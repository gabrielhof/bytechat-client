package br.feevale.bytechat.client.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import org.fusesource.jansi.AnsiConsole;


public class Console {

	private static PrintStream out = new ColorFullPrintStream(AnsiConsole.out);
	
	public static void info(String message) {
		println(String.format("<info>%s</info>", message));
	}
	
	public static void notice(String message) {
		println(String.format("<notice>%s</notice>", message));
	}
	
	public static void success(String message) {
		println(String.format("<success>%s</success>", message));
	}
	
	public static void warn(String message) {
		println(String.format("<warn>%s</warn>", message));
	}
	
	public static void error(String message) {
		println(String.format("<error>%s</error>", message));
	}
	
	public static void question(String message) {
		println(String.format("<question>%s</question>", message));
	}

	public static void print(String message) {
		out.print(message);
	}
	
	public static void println() {
		out.println();
	}
	
	public static void println(String message) {
		out.println(message);
	}
	
	public static boolean askBoolean(String question) {
		String answer = ask(question, new BooleanValidator()).trim().toLowerCase();
		return Arrays.asList("true", "sim", "yes").contains(answer);
	}
	
	public static String ask(String question) {
		return ask(question, (ConsoleResponseValidator) null);
	}
	
	public static String ask(String question, String... availableAnswers) {
		return ask(question, new StringArrayValidator(availableAnswers));
	}
	
	public static String ask(String question, ConsoleResponseValidator validator) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			question(question);
			
			String answer = reader.readLine();
			
			if (validator != null && !validator.isValid(answer)) {
				warn("Resposta inválida. As respostas possíveis são: " + validator.getPossibleAnswers());
				answer = ask(question, validator);
			}
			
			return answer;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	static class StringArrayValidator implements ConsoleResponseValidator {

		private List<String> availableAnswers;
		
		public StringArrayValidator(String... availableAnswers) {
			this(Arrays.asList(availableAnswers));
		}
		
		public StringArrayValidator(List<String> availableAnswers) {
			this.availableAnswers = availableAnswers;
		}
		
		@Override
		public boolean isValid(String response) {
			return availableAnswers.contains(response.trim().toLowerCase());
		}

		@Override
		public String getPossibleAnswers() {
			return availableAnswers.toString();
		}
		
	}
	
	static class BooleanValidator extends StringArrayValidator {
		
		public BooleanValidator() {
			super("true", "false", "sim", "nao", "yes", "no");
		}
		
	}
}
