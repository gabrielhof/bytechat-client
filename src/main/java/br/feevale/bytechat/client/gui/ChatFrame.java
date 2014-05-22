package br.feevale.bytechat.client.gui;

import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultListModel;
import javax.swing.ListSelectionModel;

import br.feevale.bytechat.builder.MessageBuilder;
import br.feevale.bytechat.client.ChatClient;
import br.feevale.bytechat.client.listener.ShutdownWindowListener;
import br.feevale.bytechat.client.listener.SwingSessionListener;
import br.feevale.bytechat.exception.ClientException;
import br.feevale.bytechat.packet.Message;
import br.feevale.bytechat.packet.UserList;
import br.feevale.bytechat.util.FileUtils;
import br.feevale.bytechat.util.User;

import com.alee.laf.WebLookAndFeel;
import com.alee.laf.list.WebList;
import com.alee.laf.rootpane.WebFrame;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.splitpane.WebSplitPane;
import com.alee.laf.text.WebEditorPane;
import com.alee.laf.text.WebTextArea;

public class ChatFrame extends WebFrame {

	private static final long serialVersionUID = -4127225470877660106L;

	private static final Dimension DEFAULT_DIMENSION = new Dimension(500, 500);
	
	private DefaultListModel<User> userListModel = new DefaultListModel<User>();
	private WebList userWebList;
	
	private String htmlTemplate;
	private StringBuilder consoleBuilder = new StringBuilder();
	private WebEditorPane console;
	
	private WebTextArea messageField;
	
	private ChatClient client;
	
	public ChatFrame(ChatClient client) {
		this.client = client;
		configure();
	}
	
	protected void configure() {
		setupChat();
		
		setIconImages (WebLookAndFeel.getImages());
		setTitle("Bytechat :)");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		addWindowListener(new ShutdownWindowListener());
		
		createComponents();
		buildLayout();
	}

	private void setupChat() {
		try {
			client.getSession().addListener(new SwingSessionListener(this));
			client.getSession().send(new UserList());
			Runtime.getRuntime().addShutdownHook(new ChatConnectionChatDown());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected void createComponents() {
		userWebList = new WebList(userListModel);
		userWebList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		userWebList.addMouseListener(new WebListMouseListener());
		
		htmlTemplate = FileUtils.getContents("template.html", true);
		console = new WebEditorPane("text/html", String.format(htmlTemplate, ""));
		console.setEditable(false);
		
		messageField = new WebTextArea();
		messageField.setLineWrap(true);
		messageField.setMargin(5);
		messageField.addKeyListener(new MessageSenderKeyListener());
	}

	protected void buildLayout() {
		WebScrollPane userListPane = new WebScrollPane(userWebList);
		
		WebScrollPane consoleScroll = new WebScrollPane(console);
		WebScrollPane messageScroll = new WebScrollPane(messageField);
		
		WebSplitPane chatPanel = new WebSplitPane(WebSplitPane.VERTICAL_SPLIT, consoleScroll, messageScroll);
		chatPanel.setDividerLocation(DEFAULT_DIMENSION.height - 100);
		
		WebSplitPane panel = new WebSplitPane(WebSplitPane.HORIZONTAL_SPLIT, userListPane, chatPanel);
		panel.setDividerLocation(150);
		
		add(panel);
		
		setSize(DEFAULT_DIMENSION);
		center();
	}
	
	public void appendMessage(String message) {
		consoleBuilder.append(message);
		console.setText(String.format(htmlTemplate, consoleBuilder.toString()));
		console.setCaretPosition(console.getDocument().getLength());
	}
	
	public DefaultListModel<User> getUserModel() {
		return userListModel;
	}
	
	class MessageSenderKeyListener extends KeyAdapter {
		
		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				if (!e.isShiftDown()) {
					try {
						e.consume();
						
						String message = messageField.getText();
						Message m = parseMessage(message);
						client.getSession().send(m);
						
						if (m.isPrivate()) {
							appendMessage(String.format("<p style='color: #75BA78;'>Você disse para <span style='color: #D4BB00;'>@</span>%s: <span style='color: black;'>%s</span></p>", m.getRecipients().get(0).getName(), m.getMessage()));
						} else {
							appendMessage(String.format("<p style='color: #75BA78;'>Você disse: <span style='color: black;'>%s</span></p>", message));
						}
						messageField.setText("");
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}
			}
		}
		
		private Message parseMessage(String message) {
			MessageBuilder builder = MessageBuilder.create();
			builder.from(client.getUser());
			
			message = message.trim();
			if (message.startsWith("@")) {
				int spacePos = message.indexOf(" ");
				builder.to(new User(spacePos > -1 ? message.substring(1, spacePos) : message));
				
				message = spacePos > -1 ? message.substring(spacePos+1) : "";
			}
			
			builder.withMessage(message);
			
			return builder.getMessage();
		}
		
	}
	
	class WebListMouseListener extends MouseAdapter {
		
		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2) {
				User user = (User) userWebList.getSelectedValue();
				if (user != null) {
					String currentMessage = messageField.getText();
					if (currentMessage.startsWith("@")) {
						int spacePos = currentMessage.indexOf(" ");
						currentMessage = (spacePos > -1 ? currentMessage.substring(spacePos+1) : "");
					}
					
					currentMessage = String.format("@%s %s", user.getName(), currentMessage);
					messageField.setText(currentMessage);
				}
			}
		}
		
	}
	
	class ChatConnectionChatDown extends Thread {
		
		@Override
		public void run() {
			try {
				client.stop();
			} catch (ClientException e) {}
		}
		
	}
}
