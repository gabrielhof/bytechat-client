package br.feevale.bytechat.client.listener;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import br.feevale.bytechat.client.gui.ChatFrame;
import br.feevale.bytechat.listener.AbstractSessionListener;
import br.feevale.bytechat.packet.Bind;
import br.feevale.bytechat.packet.File;
import br.feevale.bytechat.packet.Message;
import br.feevale.bytechat.packet.Unbind;
import br.feevale.bytechat.packet.UserList;
import br.feevale.bytechat.util.FileUtils;
import br.feevale.bytechat.util.Session;
import br.feevale.bytechat.util.User;

import com.alee.extended.panel.CenterPanel;
import com.alee.extended.window.WebProgressDialog;
import com.alee.laf.label.WebLabel;
import com.alee.laf.optionpane.WebOptionPane;
import com.alee.managers.notification.NotificationIcon;
import com.alee.managers.notification.NotificationManager;
import com.alee.managers.notification.WebNotificationPopup;

public class SwingSessionListener extends AbstractSessionListener {

	private ChatFrame chatFrame;
	
	private Map<String, FileDialog> files = new HashMap<String, FileDialog>();
	
	public SwingSessionListener(ChatFrame chatFrame) {
		this.chatFrame = chatFrame;
	}
	
	@Override
	public void bindReceived(Session source, final Bind bind) {
		showInfo(String.format("@%s acabou de entrar", bind.getUser().getName()));
		chatFrame.getUserModel().addElement(bind.getUser());
	}
	
	@Override
	public void unbindReceived(Session source, Unbind unbind) {
		showInfo(String.format("@%s acabou de sair", unbind.getUser().getName()));
		chatFrame.getUserModel().removeElement(unbind.getUser());
	}
	
	@Override
	public void userListReceived(Session source, UserList userList) {
		chatFrame.getUserModel().removeAllElements();
		
		for (User user : userList.getUsers()) {
			if (!chatFrame.getUserModel().contains(user)) {
				chatFrame.getUserModel().addElement(user);
			}
		}
	}
	
	@Override
	public void fileReceived(Session source, File file) {
		try {
			FileDialog dialog = null;
			
			if (!files.containsKey(file.getFileId())) {
				WebProgressDialog progress = new WebProgressDialog("Baixando arquivo");
				progress.setMaximum(file.getTotalParts());
				
				java.io.File f = java.io.File.createTempFile("chat_temp_", "_" + file.getName());
				dialog = new FileDialog(f,progress);
				files.put(file.getFileId(), dialog);
			} else {
				dialog = files.get(file.getFileId());
			}
			
			OutputStream output = new FileOutputStream(dialog.getFile(), true);
			output.write(file.getContents());
			output.flush();
			output.close();
			
			dialog.getProgress().setProgress(file.getPart());
			if (file.getPart() == file.getTotalParts()) {
				files.remove(file.getFileId());
				
				java.io.File f = FileUtils.moveToUserDir(dialog.getFile(), file.getName());
				dialog.getProgress().dispose();
				WebOptionPane.showMessageDialog(null, String.format("Arquivo %s recebido e salvo em %s", file.getName(), f.getAbsolutePath()));
			}
		} catch (IOException e) {
		}
	}
	
	@Override
	public void messageReceived(Session source, Message message) {
		if (!message.isPrivate()) {
			chatFrame.appendMessage(String.format("<p style='color: #3B5998;'><span style='color: #D4BB00;'>@</span>%s disse: <span style='color: black;'>%s</span></p>", message.getOriginator().getName(), message.getMessage()));
		} else {
			chatFrame.appendMessage(String.format("<p style='color: red;'>(mensagem privada) @%s disse: %s</p>", message.getOriginator().getName(), message.getMessage()));
		}
	}
	
	private void showInfo(String message) {
		WebNotificationPopup popup = new WebNotificationPopup();
		popup.setIcon(NotificationIcon.information);
		popup.setDisplayTime(2000);
		popup.setContent(new CenterPanel(new WebLabel(message)));
		
		NotificationManager.showNotification(chatFrame, popup);
	}
	
	class FileDialog {
		
		private java.io.File file;
		private WebProgressDialog progress;
		
		public FileDialog(java.io.File file, WebProgressDialog progress) {
			this.file = file;
			this.progress = progress;
		}
		
		public java.io.File getFile() {
			return file;
		}

		public WebProgressDialog getProgress() {
			return progress;
		}
	}
	
}
