package br.feevale.bytechat.client.exception;

public class CommandException extends RuntimeException {

	private static final long serialVersionUID = 7208104247116947660L;
	
	public CommandException(String message) {
		super(message);
	}

}
