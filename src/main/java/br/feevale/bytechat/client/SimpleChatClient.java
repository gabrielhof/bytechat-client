package br.feevale.bytechat.client;

import br.feevale.bytechat.builder.UnbindBuilder;
import br.feevale.bytechat.client.console.Console;
import br.feevale.bytechat.client.factory.ClientSessionFactory;
import br.feevale.bytechat.config.Configuration;
import br.feevale.bytechat.exception.ClientAlreadyStartedException;
import br.feevale.bytechat.exception.ClientException;
import br.feevale.bytechat.exception.ClientNotStartedException;
import br.feevale.bytechat.exception.PacketException;
import br.feevale.bytechat.exception.PacketFailedException;
import br.feevale.bytechat.util.Session;
import br.feevale.bytechat.util.User;

public class SimpleChatClient implements ChatClient {

	private static final String ALREADY_STARTED_MESSAGE = "O cliente ja esta connectado em %s:%d";
	
	private ClientSessionFactory sessionFactory;
	
	private Configuration configuration;
	private User user;

	private Session session;
	
	public SimpleChatClient() {
		this(null);
	}
	
	public SimpleChatClient(Configuration configuration) {
		this.configuration = configuration;
		this.sessionFactory = ClientSessionFactory.getDefault();
	}
	
	@Override
	public void setConfiguration(Configuration configuration) throws ClientException {
		if (isRunning()) {
			throw new ClientAlreadyStartedException(String.format(ALREADY_STARTED_MESSAGE, configuration.getHost(), configuration.getPort()));
		}
		
		this.configuration = configuration;
	}
	
	@Override
	public Configuration getConfiguration() {
		return configuration;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
	public User getUser() {
		return user;
	}
	
	@Override
	public Session getSession() throws ClientException {
		if (!isRunning()) {
			throw new ClientNotStartedException("O cliente ainda não foi iniciado.");
		}
		
		return session;
	}
	
	@Override
	public void start() throws ClientException {
		if (configuration == null) {
			throw new NullPointerException("A configuracao não pode ser nula");
		}
		
		if (user == null) {
			throw new NullPointerException("O usuario não pode ser nulo.");
		}
		
		if (isRunning()) {
			throw new ClientAlreadyStartedException(String.format(ALREADY_STARTED_MESSAGE, configuration.getHost(), configuration.getPort()));
		}
		
		try {
			session = sessionFactory.create(configuration, user);
			session.start();
		} catch (PacketFailedException e) {
			throw e;
		} catch (ClientException e) {
			Console.error(String.format("Não foi possível conectar no endereço %s:%d", configuration.getHost(), configuration.getPort()));
			throw e;
		}
	}
	
	@Override
	public void stop() throws ClientException {
		if (session == null) {
			throw new ClientNotStartedException("O cliente ainda nao foi iniciado");
		}
		
		if (!session.getConnection().isClosed()) {
			try {
				session.send(UnbindBuilder.create().user(session.getUser()).getUnbind());
			} catch (PacketException e) {}
		}
		
		session.stop();
		session = null;
	}
	
	@Override
	public boolean isRunning() {
		return session != null;
	}

}