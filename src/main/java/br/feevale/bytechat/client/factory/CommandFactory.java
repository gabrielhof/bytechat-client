package br.feevale.bytechat.client.factory;

import br.feevale.bytechat.client.command.Command;

public abstract class CommandFactory {
	
	public abstract Command create(String commandString);
	
	public static CommandFactory getDefault() {
		return new EnumCommandFactory();
	}

}
