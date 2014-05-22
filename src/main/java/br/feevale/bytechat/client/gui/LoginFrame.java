package br.feevale.bytechat.client.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingUtilities;

import org.apache.commons.lang.StringUtils;

import br.feevale.bytechat.client.ChatClient;
import br.feevale.bytechat.client.SimpleChatClient;
import br.feevale.bytechat.client.listener.ShutdownWindowListener;
import br.feevale.bytechat.config.Configuration;
import br.feevale.bytechat.exception.PacketFailedException;
import br.feevale.bytechat.util.User;

import com.alee.extended.layout.TableLayout;
import com.alee.extended.panel.CenterPanel;
import com.alee.extended.progress.WebProgressOverlay;
import com.alee.laf.WebLookAndFeel;
import com.alee.laf.button.WebButton;
import com.alee.laf.optionpane.WebOptionPane;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.rootpane.WebFrame;
import com.alee.laf.text.WebTextField;

public class LoginFrame extends WebFrame {

	private static final long serialVersionUID = -3848008163996198317L;
	
	private WebTextField usernameField;
	
	private WebProgressOverlay progressOverlay;
	private WebButton loginButton;

	public LoginFrame() {
		configure();
	}

	protected void configure() {
		setResizable(false);
		setIconImages (WebLookAndFeel.getImages());
		setTitle("Bytechat :)");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		addWindowListener(new ShutdownWindowListener());
		
		createComponents();
		buildLayout();
	}

	protected void createComponents() {
		usernameField = new WebTextField(20);
		usernameField.setInputPrompt("Digite seu nome de usuário");
		usernameField.setHideInputPromptOnFocus(false);
		
		loginButton = new WebButton("Login");
		progressOverlay  = new WebProgressOverlay();
		progressOverlay.setComponent(loginButton);
		
		loginButton.addActionListener(new LoginAction());
	}
	
	private void buildLayout() {
		TableLayout layout = new TableLayout(new double[][]{{TableLayout.FILL }, {TableLayout.PREFERRED, TableLayout.PREFERRED}});
		WebPanel panel = new WebPanel(layout);
		panel.setMargin(15, 30, 15, 30);
		
		panel.add(usernameField, "0,0");
		panel.add(new CenterPanel(progressOverlay), "0,1");
		
		add(panel);
		
		pack();
		center();
	}
	
	class LoginAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			final String username = usernameField.getText();
			if (StringUtils.isBlank(username)) {
				WebOptionPane.showMessageDialog(LoginFrame.this, "Por favor, digite um usuário", "Aviso!", WebOptionPane.WARNING_MESSAGE);
				return;
			}
			
			progressOverlay.setShowLoad(true);
			loginButton.setText("Logando...");
			usernameField.setFocusable(false);
			
			SwingUtilities.invokeLater(new Runnable() {
				
				@Override
				public void run() {
					try {
						Configuration configuration = Configuration.fromProperties();
						
						ChatClient client = new SimpleChatClient(configuration);
						client.setUser(new User(username));
						client.start();
						
						LoginFrame.this.setVisible(false);
						ChatFrame frame = new ChatFrame(client);
						frame.setVisible(true);
						
						return;
					} catch (PacketFailedException ex) {
						WebOptionPane.showMessageDialog(null, "Esse usuário já esta em uso, escolha outro :D", "Aviso!", WebOptionPane.INFORMATION_MESSAGE);
					} catch (Exception ex) {
						ex.printStackTrace();
						WebOptionPane.showMessageDialog(null, "Não foi possível conectar no nosso servidor.", "Erro!", WebOptionPane.ERROR_MESSAGE);
					}
					
					usernameField.setFocusable(true);
					progressOverlay.setShowLoad(false);
					loginButton.setText("Login");
				}
			});
			
			
		}
		
	}
}
