package br.feevale.bytechat.client.console;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import br.feevale.bytechat.client.ChatClient;
import br.feevale.bytechat.client.command.Command;
import br.feevale.bytechat.client.exception.CommandException;
import br.feevale.bytechat.client.factory.CommandFactory;
import br.feevale.bytechat.exception.ClientException;
import br.feevale.bytechat.util.Session;

public class ConsoleObserver {

	private CommandFactory commandFactory = CommandFactory.getDefault();
	
	private ChatClient client;
	private Session session;
	
	private Thread consoleReaderThread;
	
	public ConsoleObserver(ChatClient client) {
		this.client = client;
	}
	
	public void start() {
		if (consoleReaderThread == null) {
			try {
				this.session = client.getSession();
			} catch (ClientException e) {}
			
			consoleReaderThread = new Thread(new ConsoleReader());
			consoleReaderThread.start();
		}
	}
	
	public void stop() {
		consoleReaderThread = null;
		Thread.currentThread().interrupt();
	}
	
	class ConsoleReader implements Runnable {

		@Override
		public void run() {
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			while (consoleReaderThread != null && client.isRunning()) {
				try {
					String line = reader.readLine();
					
					Command command = commandFactory.create(line);
					
					if (command != null) {
						command.execute(session, line);
					}
				} catch (CommandException e) {
					Console.error(e.getMessage());
				} catch (Exception e) {
					if (session.getConnection().isClosed()) {
						stop();
						
						try {
							client.stop();
						} catch (Exception e2) {}
					} else {
						e.printStackTrace();
					}
				}
			}
		}
		
	}
	
}
