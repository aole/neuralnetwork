package aole;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

public class Main implements ActionListener {
	private static double[][] data = { { 0, 0 }, { 0, 1 }, { 1, 0 }, { 1, 1 } };
	private static int[][] target = { { 0 }, { 1 }, { 1 }, { 1 } };
	int count = 1;
	private Network network;
	private Window window;

	public static void main(String[] args) {
		Main m = new Main();
		m.start();

	}

	private void start() {
		network = new Network(data, 2, target);

		window = new Window(this, network);
		window.setTitle("Neural Network");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		switch (ae.getActionCommand()) {
		case "1000":
			network.trainTo(1000, 101);
			break;
		case "next":
			network.trainTo(1, 1);
			break;
		case "reset":
			network.resetWeights();
			window.networkUpdated();
			break;
		case "load":
			LoadDialog ld = new LoadDialog(window);
			ld.setVisible(true);
			if (ld.isOKtoLoad()) {
				ld.setNetwork(network);
				window.networkUpdated();
			}
			break;
		case "analysis":
			(new Analysis(network)).setVisible(true);
			break;
		}
	}
}
