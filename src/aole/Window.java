package aole;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Window extends JFrame implements NetworkListener {

	private static final long serialVersionUID = 1L;
	private Canvas canvas;
	private JLabel lblStatus = new JLabel();

	public Window(ActionListener app, Network network) {
		network.addListener(this);
		canvas = new Canvas(network);

		Container pane = getContentPane();
		pane.add(canvas, BorderLayout.CENTER);

		JPanel statusbar = new JPanel();
		pane.add(statusbar, BorderLayout.SOUTH);

		JButton button = new JButton("Train 1000");
		button.setActionCommand("1000");
		button.addActionListener(app);
		statusbar.add(button);

		button = new JButton("Train Next");
		button.setActionCommand("next");
		button.addActionListener(app);
		statusbar.add(button);

		button = new JButton("Analysis");
		button.setActionCommand("analysis");
		button.addActionListener(app);
		statusbar.add(button);

		button = new JButton("Reset");
		button.setActionCommand("reset");
		button.addActionListener(app);
		statusbar.add(button);

		button = new JButton("Load");
		button.setActionCommand("load");
		button.addActionListener(app);
		statusbar.add(button);

		statusbar.add(lblStatus);
		pack();
	}

	@Override
	public void networkUpdated(int epoch, int target, double error) {
		canvas.repaint();
		lblStatus.setText(epoch + " | " + target + ".. " + Math.round(Math.abs(error) * 100) + "%");
	}

	public void networkUpdated() {
		canvas.repaint();
	}
}
