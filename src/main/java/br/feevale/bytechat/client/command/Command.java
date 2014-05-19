package br.feevale.bytechat.client.command;

import br.feevale.bytechat.exception.PacketException;
import br.feevale.bytechat.util.Session;

public interface Command {
	
	public String getCommandName();
	public String getShortCommandName();
	
	public String getDescription();
	
	public CommandType getCommandType();
	
	public void execute(Session session, String content) throws PacketException;

}
