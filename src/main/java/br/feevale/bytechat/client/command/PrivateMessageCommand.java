package br.feevale.bytechat.client.command;

import java.util.ArrayList;
import java.util.List;

import br.feevale.bytechat.builder.MessageBuilder;
import br.feevale.bytechat.client.exception.CommandException;
import br.feevale.bytechat.exception.PacketException;
import br.feevale.bytechat.util.Session;
import br.feevale.bytechat.util.User;

public class PrivateMessageCommand implements Command {

	@Override
	public String getCommandName() {
		return "pm";
	}

	@Override
	public String getShortCommandName() {
		return "pm";
	}

	@Override
	public String getDescription() {
		return "Enviar mensagem privada para usuários. Uso: \\pm @usuario1 @usuario2 mensagem";
	}

	@Override
	public CommandType getCommandType() {
		return null;
	}

	@Override
	public void execute(Session session, String content) throws PacketException {
		content = content.replaceFirst("^\\\\pm ", "");
		
		MessageBuilder messageBuilder = MessageBuilder.create();
		messageBuilder.from(session.getUser());
		messageBuilder.to(parseUsers(content));
		messageBuilder.withMessage(parseMessage(content));
		
		session.send(messageBuilder.getMessage());
	}

	private List<User> parseUsers(String content) {
		List<User> users = new ArrayList<User>();
		
		String[] splitted = content.split(" ");
		for (String item : splitted) {
			if (item.startsWith("@")) {
				users.add(new User(item.substring(1)));
			} else {
				break;
			}
		}
		
		if (users.size() <= 0) {
			throw new CommandException("Você precisa especificar pelo menos um usuário.");
		}
		
		return users;
	}

	private String parseMessage(String content) {
		StringBuilder message = new StringBuilder();
		boolean atFound = false;
		
		String[] splitted = content.split(" ");
		for (String item : splitted) {
			if (!atFound && item.startsWith("@")) {
				continue;
			} else if (!atFound) {
				atFound = true;
			}
			
			message.append(item).append(" ");
		}
		
		return message.toString();
	}
}
