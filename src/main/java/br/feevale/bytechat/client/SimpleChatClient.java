package br.feevale.bytechat.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

import br.feevale.bytechat.builder.AckBuilder;
import br.feevale.bytechat.builder.MessageBuilder;
import br.feevale.bytechat.config.Configuration;
import br.feevale.bytechat.exception.ConnectionException;
import br.feevale.bytechat.listener.SessionListener;
import br.feevale.bytechat.packet.Ack;
import br.feevale.bytechat.packet.Message;
import br.feevale.bytechat.packet.Packet;
import br.feevale.bytechat.protocol.Connection;
import br.feevale.bytechat.protocol.SocketConnection;
import br.feevale.bytechat.util.Session;
import br.feevale.bytechat.util.User;

public class SimpleChatClient {

	private Configuration configuration;

	private Session session;
	
	public SimpleChatClient(Configuration configuration) {
		this.configuration = configuration;
	}
	
	public void start() throws Exception {
		if (session == null) {
			System.out.println("Qual o seu nome?");
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			
			User user = new User();
			user.setName(reader.readLine());
			
			Socket socket = new Socket(configuration.getHost(), configuration.getPort());
			
			Connection connection = new SocketConnection(socket);
			connection.send(AckBuilder.create().user(user).getAck());
			
			session = new Session(user, connection);
			session.addListener(new BlablaListener());
			session.start();
			
			Thread thread = new Thread(new ConsoleReader());
			thread.start();
		}
	}
	
	public void stop() throws ConnectionException {
		session.stop();
		session = null;
	}
	
	class BlablaListener implements SessionListener {

		@Override
		public void packetReceived(Session session, Packet packet) {
			if (packet instanceof Message) {
				Message message = (Message) packet;
				System.out.println(String.format("%s: %s", message.getOriginator().getName(), message.getMessage()));
			} else if (packet instanceof Ack) {
				Ack ack = (Ack) packet;
				System.out.println(String.format("%s acabou de entrar", ack.getUser().getName()));
			}
		}

		@Override
		public void sessionEnded(Session session) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	class ConsoleReader implements Runnable {

		@Override
		public void run() {
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			
			while (session != null) {
				try {
					MessageBuilder builder = MessageBuilder.create();
					builder.message(reader.readLine());
					builder.originator(session.getUser());
					
					session.send(builder.getMessage());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		}
		
	}
	
}
