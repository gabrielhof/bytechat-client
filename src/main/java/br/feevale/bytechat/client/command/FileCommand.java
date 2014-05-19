package br.feevale.bytechat.client.command;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;

import br.feevale.bytechat.client.console.Console;
import br.feevale.bytechat.exception.PacketException;
import br.feevale.bytechat.util.Session;

public class FileCommand implements Command {

	private static final int FILE_SPLIT_SIZE = (1024 * 1024);
	
	@Override
	public String getCommandName() {
		return "file";
	}

	@Override
	public String getShortCommandName() {
		// TODO Auto-generated method stub
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
		
		if (file != null) {
			try {
				br.feevale.bytechat.packet.File f = new br.feevale.bytechat.packet.File();
				
				int totalParts = new BigDecimal(file.length()).divide(new BigDecimal(FILE_SPLIT_SIZE), RoundingMode.CEILING).intValue();
				
				f.setFileId(String.valueOf(file.getAbsolutePath().hashCode() + (100 * Math.random())));
				f.setName(file.getName());
				f.setContentType(Files.probeContentType(Paths.get(file.getAbsolutePath())));
				f.setTotalParts(totalParts);
				
				byte[] buffer = new byte[FILE_SPLIT_SIZE];
				int current = 0;
				InputStream input = new FileInputStream(file);
				
				while (input.read(buffer) > -1) {
					f.setPart(++current);
					f.setContents(buffer);
					
					session.send(f);
				}
				
				input.close();
				Console.success("Arquivo enviado com sucesso!");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private File validateFile(String content) {
		String[] splitted = content.split(" ");
		if (splitted.length <= 1) {
			Console.error("O segundo parâmetro deve ser caminho para o arquivo a ser enviado.");
			return null;
		}
		
		File file = new File(splitted[1]);
		if (!file.exists()) {
			Console.error(String.format("O arquivo %s não existe", file.getName()));
			return null;
		}
		
		if (file.isDirectory()) {
			Console.error("O arquivo que você forneceu é uma pasta.");
			return null;
		}
		
		return file;
	}

}
