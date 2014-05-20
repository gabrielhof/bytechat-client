package br.feevale.bytechat.client.listener;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ShutdownWindowListener extends WindowAdapter {

	@Override
	public void windowClosed(WindowEvent e) {
		System.exit(0);
	}

}
