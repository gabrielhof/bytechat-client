package br.feevale.bytechat.client.listener;

import java.util.List;

import br.feevale.bytechat.client.console.Console;
import br.feevale.bytechat.listener.AbstractSessionListener;
import br.feevale.bytechat.packet.Bind;
import br.feevale.bytechat.packet.Message;
import br.feevale.bytechat.packet.Unbind;
import br.feevale.bytechat.packet.UserList;
import br.feevale.bytechat.util.Session;
import br.feevale.bytechat.util.User;

public class ConsoleSessionListener extends AbstractSessionListener {

	@Override
	public void bindReceived(Session session, Bind bind) {
		Console.println(String.format("<warn>@</warn><success>%s</success> acabou de entrar.", bind.getUser().getName()));
		Console.println();
	}

	@Override
	public void unbindReceived(Session source, Unbind unbind) {
		Console.println(String.format("<warn>@</warn><success>%s</success> acabou de sair.", unbind.getUser().getName()));
		Console.println();
	}
	
	@Override
	public void messageReceived(Session session, Message message) {
		Console.println(String.format("<warn>@</warn><notice>%s</notice>: <warn>%s</warn>", message.getOriginator().getName(), message.getMessage()));
	}
	
	@Override
	public void userListReceived(Session source, UserList userList) {
		List<User> users = userList.getUsers();
		
		Console.println();
		
		if (users != null && users.size() > 0) {
			if (users.size() == 1) {
				Console.println(String.format("Somente você e o usuário <warn>@</warn><success>%s</success> estão logados!", users.get(0).getName()));
			} else {
				Console.println("Esses são os usuários logados:");
				
				for (User user : users) {
					Console.println(String.format("<warn>@</warn><success>%s</success>", user.getName()));
				}
			}
		} else {
			Console.println("<warn>Você é o único usuário logado :(</warn> <question>[forever alone]</question>");
		}
		
		Console.println();
	}

}
