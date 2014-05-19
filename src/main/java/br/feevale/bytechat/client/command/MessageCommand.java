package br.feevale.bytechat.client.command;

import br.feevale.bytechat.builder.MessageBuilder;
import br.feevale.bytechat.exception.PacketException;
import br.feevale.bytechat.util.Session;

public class MessageCommand implements Command {

	@Override
	public String getCommandName() {
		return "message";
	}
	
	@Override
	public String getShortCommandName() {
		return "m";
	}
	
	@Override
	public String getDescription() {
		return "Enviar uma mensagem a todos os usuários";
	}
	
	@Override
	public CommandType getCommandType() {
		return CommandType.MESSAGE;
	}
	
	@Override
	public void execute(Session session, String content) throws PacketException {
		content = content.replaceFirst("^\\\\m.* ", "");
		session.send(MessageBuilder.create().from(session.getUser()).withMessage(content).getMessage());
	}

}
