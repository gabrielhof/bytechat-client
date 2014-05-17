package br.feevale.bytechat.client.console;

public interface ConsoleResponseValidator {

	public boolean isValid(String response);
	
	public String getPossibleAnswers();
}
