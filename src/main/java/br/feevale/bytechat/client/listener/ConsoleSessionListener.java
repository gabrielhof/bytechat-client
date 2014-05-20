package br.feevale.bytechat.client.listener;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.feevale.bytechat.client.console.Console;
import br.feevale.bytechat.listener.AbstractSessionListener;
import br.feevale.bytechat.packet.Bind;
import br.feevale.bytechat.packet.File;
import br.feevale.bytechat.packet.Message;
import br.feevale.bytechat.packet.Unbind;
import br.feevale.bytechat.packet.UserList;
import br.feevale.bytechat.util.FileUtils;
import br.feevale.bytechat.util.Session;
import br.feevale.bytechat.util.User;

public class ConsoleSessionListener extends AbstractSessionListener {

	private Map<String, java.io.File> files = new HashMap<String, java.io.File>();
	
	@Override
	public void bindReceived(Session session, Bind bind) {
		Console.println(String.format("@<success>%s</success> acabou de entrar.", bind.getUser().getName()));
		Console.println();
	}

	@Override
	public void unbindReceived(Session source, Unbind unbind) {
		Console.println(String.format("@<success>%s</success> acabou de sair.", unbind.getUser().getName()));
		Console.println();
	}
	
	@Override
	public void messageReceived(Session session, Message message) {
		if (message.isPrivate()) {
			Console.print("<info>Mensagem privade de</info> ");
		}
		
		Console.println(String.format("@<notice>%s</notice>: <warn>%s</warn>", message.getOriginator().getName(), message.getMessage()));
	}
	
	@Override
	public void userListReceived(Session source, UserList userList) {
		List<User> users = userList.getUsers();
		
		Console.println();
		
		if (users != null && users.size() > 0) {
			if (users.size() == 1) {
				Console.println(String.format("Somente você e o usuário @<success>%s</success> estão logados!", users.get(0).getName()));
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
	
	@Override
	public void fileReceived(Session source, File file) {
		try {
			java.io.File f = null;
			
			if (!files.containsKey(file.getFileId())) {
				f = java.io.File.createTempFile("chat_temp_", "_" + file.getName());
				files.put(file.getFileId(), f);
			} else {
				f = files.get(file.getFileId());
			}
			
			OutputStream output = new FileOutputStream(f, true);
			output.write(file.getContents());
			output.flush();
			output.close();
			
			if (file.getPart() == file.getTotalParts()) {
				files.remove(file.getFileId());
				
				f = FileUtils.moveToUserDir(f, file.getName());
				Console.println(String.format("Arquivo <info>%s</info> recebido e salvo em <success>%s</success>", file.getName(), f.getAbsolutePath()));
			} else {
				int percent = ((file.getPart() * 100) / file.getTotalParts());
				Console.println(String.format("<warn>Recebendo arquivo</warn> <info>%s</info> (<success>%d%s</success>)", file.getName(), percent, "%"));
			}
		} catch (IOException e) {
		}
	}

}
