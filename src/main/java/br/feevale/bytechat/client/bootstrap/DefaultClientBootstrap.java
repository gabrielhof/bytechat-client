package br.feevale.bytechat.client.bootstrap;

import br.feevale.bytechat.client.ChatClient;
import br.feevale.bytechat.client.SimpleChatClient;
import br.feevale.bytechat.client.console.Console;
import br.feevale.bytechat.client.console.ConsoleObserver;
import br.feevale.bytechat.config.Configuration;
import br.feevale.bytechat.exception.ClientException;
import br.feevale.bytechat.util.User;

public class DefaultClientBootstrap {
	
	private Configuration configuration;
	private ChatClient chatClient;
	
	private ConsoleObserver consoleObserver;
	
	protected DefaultClientBootstrap() {
		configuration = createConfig();
		chatClient = new SimpleChatClient(configuration);
		
		consoleObserver = new ConsoleObserver(chatClient);
	}
	
	void init() {
		String name = Console.ask("Qual seu nome?");
		
		User user = new User();
		user.setName(name);
		
		chatClient.setUser(user);
		
		startChat();
		welcomeMessage();
		
		consoleObserver.start();
		
		Runtime.getRuntime().addShutdownHook(new UnbindAndShutdown());
	}
	
	private void startChat() {
		try {
			chatClient.start();
		} catch (ClientException e) {
			e.printStackTrace();
		}
	}
	
	private void welcomeMessage() {
		Console.println();
		Console.println(String.format("Olá <success>%s</success> :)", chatClient.getUser().getName()));
		
		Console.println();
		Console.println("Esse é o bytechat. Escreva algo e essa mensagem sera enviada para outros usuários.");
		Console.info("Utilize \\help para ver a lista de comandos possíveis.");
		Console.info("Utilize \\users para ver a lista de usuários logados.");
		Console.println();
	}
	
	private Configuration createConfig() {
		Configuration configuration = new Configuration();
		configuration.setHost("localhost");
		configuration.setPort(8080);

		return configuration;
	}
	
	public static void main(String[] args) throws Exception {
		new DefaultClientBootstrap().init();
	}
	
	class UnbindAndShutdown extends Thread {
		
		@Override
		public void run() {
			try {
				consoleObserver.stop();
				chatClient.stop();
			} catch (ClientException e) {}
		}
		
	}

}
