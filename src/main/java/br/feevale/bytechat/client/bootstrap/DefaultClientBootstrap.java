package br.feevale.bytechat.client.bootstrap;

import br.feevale.bytechat.client.SimpleChatClient;
import br.feevale.bytechat.config.Configuration;

public class DefaultClientBootstrap {
	
	public static void main(String[] args) throws Exception {
		Configuration configuration = new Configuration();
		configuration.setHost("localhost");
		configuration.setPort(8080);
		
		SimpleChatClient client = new SimpleChatClient(configuration);
		client.start();
	}

}
