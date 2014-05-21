package br.feevale.bytechat.client.listener;

import br.feevale.bytechat.client.gui.ChatFrame;
import br.feevale.bytechat.listener.AbstractSessionListener;
import br.feevale.bytechat.packet.Message;
import br.feevale.bytechat.util.Session;

public class SwingSessionListener extends AbstractSessionListener {

	private ChatFrame chatFrame;
	
	public SwingSessionListener(ChatFrame chatFrame) {
		this.chatFrame = chatFrame;
	}
	
	@Override
	public void messageReceived(Session source, Message message) {
		chatFrame.appendMessage(String.format("<p style='color: #3B5998;'>%s disse: <span style='color: black;'>%s</span></p>", message.getOriginator().getName(), message.getMessage()));
	}
	
}
