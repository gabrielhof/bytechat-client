package br.feevale.bytechat.client.gui;

import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import br.feevale.bytechat.builder.MessageBuilder;
import br.feevale.bytechat.client.ChatClient;
import br.feevale.bytechat.client.listener.ShutdownWindowListener;
import br.feevale.bytechat.client.listener.SwingSessionListener;
import br.feevale.bytechat.exception.ClientException;
import br.feevale.bytechat.util.FileUtils;

import com.alee.laf.WebLookAndFeel;
import com.alee.laf.rootpane.WebFrame;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.splitpane.WebSplitPane;
import com.alee.laf.text.WebEditorPane;
import com.alee.laf.text.WebTextArea;

public class ChatFrame extends WebFrame {

	private static final long serialVersionUID = -4127225470877660106L;

	private static final Dimension DEFAULT_DIMENSION = new Dimension(300, 500);
	
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
			Runtime.getRuntime().addShutdownHook(new ChatConnectionChatDown());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected void createComponents() {
		htmlTemplate = FileUtils.getContents("template.html", true);
		
		console = new WebEditorPane("text/html", String.format(htmlTemplate, ""));
		console.setEditable(false);
		
		messageField = new WebTextArea();
		messageField.setLineWrap(true);
		messageField.setMargin(5);
		messageField.addKeyListener(new MessageSenderKeyListener());
	}

	protected void buildLayout() {
		WebScrollPane consoleScroll = new WebScrollPane(console);
		WebScrollPane messageScroll = new WebScrollPane(messageField);
		
		WebSplitPane panel = new WebSplitPane(WebSplitPane.VERTICAL_SPLIT, consoleScroll, messageScroll);
		panel.setDividerLocation(DEFAULT_DIMENSION.height - 100);
		add(panel);
		
		setSize(DEFAULT_DIMENSION);
		center();
	}
	
	public void appendMessage(String message) {
		consoleBuilder.append(message);
		console.setText(String.format(htmlTemplate, consoleBuilder.toString()));
		console.setCaretPosition(console.getDocument().getLength());
	}
	
	class MessageSenderKeyListener extends KeyAdapter {
		
		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				if (!e.isShiftDown()) {
					try {
						e.consume();
						
						String message = messageField.getText();
						client.getSession().send(MessageBuilder.create().from(client.getUser()).withMessage(message).getMessage());
						
						appendMessage(String.format("<p style='color: #75BA78;'>Você disse: <span style='color: black;'>%s</span></p>", message));
						messageField.setText("");
					} catch (Exception e2) {
						e2.printStackTrace();
					}
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
