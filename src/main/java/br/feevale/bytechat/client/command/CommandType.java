package br.feevale.bytechat.client.command;

import org.apache.commons.lang.StringUtils;

public enum CommandType {
	
	MESSAGE(new MessageCommand()),
	HELP(new HelpCommand()),
	USER_LIST(new UserListCommand()),
	FILE(new FileCommand())
	;
	
	private Command command;
	
	CommandType(Command command) {
		this.command = command;
	}
	
	public String getCommandName() {
		return command.getCommandName();
	}
	
	public String getShortCommandName() {
		return command.getShortCommandName();
	}
	
	public String getDescription() {
		return command.getDescription();
	}
	
	public Command getCommand() {
		return command;
	}

	public static CommandType forCommand(String command) {
		if (StringUtils.isNotBlank(command)) {
			for (CommandType commandType : values()) {
				if (command.equals(commandType.getCommandName()) || command.equals(commandType.getShortCommandName())) {
					return commandType;
				}
			}
		}
		
		return null;
	}
}
