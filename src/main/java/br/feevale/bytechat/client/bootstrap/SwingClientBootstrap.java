package br.feevale.bytechat.client.bootstrap;

import javax.swing.SwingUtilities;

import br.feevale.bytechat.client.gui.LoginFrame;

import com.alee.laf.WebLookAndFeel;

public class SwingClientBootstrap {
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				WebLookAndFeel.install();
				WebLookAndFeel.setDecorateAllWindows(true);
				
				LoginFrame frame = new LoginFrame();
				frame.setVisible(true);
			}
		});
	}
	
}
