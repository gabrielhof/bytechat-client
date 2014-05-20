package br.feevale.bytechat.client.bootstrap;

import javax.swing.SwingUtilities;

import com.alee.extended.layout.TableLayout;
import com.alee.laf.WebLookAndFeel;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.rootpane.WebDialog;
import com.alee.laf.text.WebTextField;

public class SwingClientBootstrap {
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				WebLookAndFeel.install();
				
				WebDialog dialog = new WebDialog();
				dialog.setModal(true);
				
				TableLayout layout = new TableLayout();
				WebPanel panel = new WebPanel(layout);
				
				panel.add ( new WebLabel ( "Nickname", WebLabel.TRAILING ), "0,0" );
				panel.add ( new WebTextField ( 15 ), "1,0" );
				
				dialog.add(panel);
				dialog.setVisible(true);
			}
		});
		
		Runtime.getRuntime().addShutdownHook(new UnbindAndShutdown());
	}
	
	static class UnbindAndShutdown extends Thread {
		
		@Override
		public void run() {
//			try {
//				consoleObserver.stop();
//				chatClient.stop();
//			} catch (ClientException e) {}
		}
		
	}
}
