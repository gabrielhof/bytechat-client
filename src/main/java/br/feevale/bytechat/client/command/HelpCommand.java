package br.feevale.bytechat.client.command;

import br.feevale.bytechat.client.console.Console;
import br.feevale.bytechat.exception.PacketException;
import br.feevale.bytechat.util.Session;

public class HelpCommand implements Command {

	private static final String HELP_MESSAGE = "<notice>\\%s</notice>, <notice>\\%s</notice> - <info>%s</info>";
	
	@Override
	public String getCommandName() {
		return "help";
	}
	
	@Override
	public String getShortCommandName() {
		return "h";
	}
	
	@Override
	public CommandType getCommandType() {
		return CommandType.HELP;
	}
	
	@Override
	public String getDescription() {
		return "Listar todos os comandos existentes";
	}
	
	@Override
	public void execute(Session session, String content) throws PacketException {
		Console.println();
		Console.println("Você pode utilizar os seguintes comandos:");
		Console.println();
		
		for (CommandType command : CommandType.values()) {
			Console.println(String.format(HELP_MESSAGE, command.getCommandName(), command.getShortCommandName(), command.getDescription()));
		}
		
		Console.println();
	}

}
