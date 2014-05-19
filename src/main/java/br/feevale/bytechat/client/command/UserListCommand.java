package br.feevale.bytechat.client.command;

import br.feevale.bytechat.exception.PacketException;
import br.feevale.bytechat.packet.UserList;
import br.feevale.bytechat.util.Session;

public class UserListCommand implements Command {

	@Override
	public String getCommandName() {
		return "users";
	}

	@Override
	public String getShortCommandName() {
		return "u";
	}

	@Override
	public CommandType getCommandType() {
		return CommandType.USER_LIST;
	}
	
	@Override
	public String getDescription() {
		return "Exibir todos os usuários logados";
	}
	
	@Override
	public void execute(Session session, String content) throws PacketException {
		session.send(new UserList());
	}

}
