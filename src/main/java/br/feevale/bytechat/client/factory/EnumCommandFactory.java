package br.feevale.bytechat.client.factory;

import java.util.HashMap;
import java.util.Map;

import br.feevale.bytechat.client.command.Command;
import br.feevale.bytechat.client.command.CommandType;
import br.feevale.bytechat.client.console.Console;

public class EnumCommandFactory extends CommandFactory {

	private Map<String, Command> commands = new HashMap<String, Command>();
	
	@Override
	public Command create(String commandString) {
		if (!commandString.startsWith("\\")) {
			return CommandType.MESSAGE.getCommand();
		}
		
		int indexOfSpace = commandString.indexOf(" ");
		String command = indexOfSpace < 0 ? commandString.substring(1) : commandString.substring(1, indexOfSpace);
		command = command.toLowerCase();
		
		Command c = null;
		
		if (!commands.containsKey(command)) {
			CommandType commandType = CommandType.forCommand(command);
			commands.put(command, c = (commandType == null ? null : commandType.getCommand()));
		} else {
			c = commands.get(command);
		}
		
		if (c == null) {
			Console.error(String.format("Comando não encontrado: \\%s", command));
		}
		
		return c;
	}

}
