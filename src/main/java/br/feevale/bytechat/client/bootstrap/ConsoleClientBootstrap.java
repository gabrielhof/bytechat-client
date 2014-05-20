package br.feevale.bytechat.client.bootstrap;

import br.feevale.bytechat.client.ChatClient;
import br.feevale.bytechat.client.SimpleChatClient;
import br.feevale.bytechat.client.console.Console;
import br.feevale.bytechat.client.console.ConsoleObserver;
import br.feevale.bytechat.client.listener.ConsoleSessionListener;
import br.feevale.bytechat.config.Configuration;
import br.feevale.bytechat.exception.PacketFailedException;
import br.feevale.bytechat.exception.ClientException;
import br.feevale.bytechat.packet.Fail;
import br.feevale.bytechat.packet.FailType;
import br.feevale.bytechat.util.User;

public class ConsoleClientBootstrap {
	
	private Configuration configuration;
	private ChatClient chatClient;
	
	private ConsoleObserver consoleObserver;
	
	protected ConsoleClientBootstrap() {
		configuration = createConfig();
		chatClient = new SimpleChatClient(configuration);
		
		consoleObserver = new ConsoleObserver(chatClient);
	}
	
	void init() {
		String name = Console.ask("Qual seu nome? (espaços serão removidos)");
		
		User user = new User();
		user.setName(name.replaceAll(" ", ""));
		
		chatClient.setUser(user);
		
		startChat();
		welcomeMessage();
		
		consoleObserver.start();
		
		Runtime.getRuntime().addShutdownHook(new UnbindAndShutdown());
	}
	
	private void startChat() {
		try {
			chatClient.start();
			chatClient.getSession().addListener(new ConsoleSessionListener());
		} catch (PacketFailedException e) { 
			treatFail(e.getFail());
		} catch (ClientException e) {
			e.printStackTrace();
		}
	}
	
	private void treatFail(Fail fail) {
		if (fail.getFailType() == FailType.USERNAME_TAKEN) {
			Console.println();
			Console.error(String.format("O usuário %s já esta sendo utilizado.", chatClient.getUser().getName()));
			String username = Console.ask("Por favor, escolha outro nome (espaços serão removidos):");
			
			chatClient.getUser().setName(username.replaceAll(" ", ""));
			startChat();
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
		new ConsoleClientBootstrap().init();
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
