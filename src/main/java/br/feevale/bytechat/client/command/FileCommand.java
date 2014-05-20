package br.feevale.bytechat.client.command;

import java.io.File;

import br.feevale.bytechat.client.console.Console;
import br.feevale.bytechat.client.exception.CommandException;
import br.feevale.bytechat.exception.PacketException;
import br.feevale.bytechat.util.PacketUtils;
import br.feevale.bytechat.util.Session;

public class FileCommand implements Command {

	@Override
	public String getCommandName() {
		return "file";
	}

	@Override
	public String getShortCommandName() {
		return "f";
	}

	@Override
	public String getDescription() {
		return "Enviar um arquivo para todos os usuários";
	}

	@Override
	public CommandType getCommandType() {
		return CommandType.FILE;
	}

	@Override
	public void execute(Session session, String content) throws PacketException {
		File file = validateFile(content);
		
		try {
			PacketUtils.sendSerializedFile(session, file);
			Console.success("Arquivo enviado com sucesso!");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private File validateFile(String content) {
		String[] splitted = content.split(" ");
		if (splitted.length <= 1) {
			throw new CommandException("O segundo parâmetro deve ser caminho para o arquivo a ser enviado.");
		}
		
		File file = new File(splitted[1]);
		if (!file.exists()) {
			throw new CommandException(String.format("O arquivo %s não existe", file.getName()));
		}
		
		if (file.isDirectory()) {
			throw new CommandException(String.format("O arquivo que você forneceu é uma pasta."));
		}
		
		return file;
	}

}
