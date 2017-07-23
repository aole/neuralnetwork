package aole;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Window extends JFrame implements NetworkListener {

	private static final long serialVersionUID = 1L;
	private Canvas canvas;
	private JTextField lblStatus = new JTextField(25);
	private JTextField txtEpoch;
	private JTextField txtPercent;

	public Window(ActionListener app, Network network) {
		network.addListener(this);
		canvas = new Canvas(network);

		Container pane = getContentPane();
		pane.add(canvas, BorderLayout.CENTER);

		JPanel statusbar = new JPanel();
		pane.add(statusbar, BorderLayout.SOUTH);

		JButton button = new JButton("Train & Test");
		button.setActionCommand("TnT");
		button.addActionListener(app);
		statusbar.add(button);

		txtEpoch = new JTextField("1", 4);
		txtEpoch.setToolTipText("Epoch");
		statusbar.add(txtEpoch);

		txtPercent = new JTextField("75", 2);
		txtPercent.setToolTipText("Train/Test Percent");
		statusbar.add(txtPercent);

		button = new JButton("Train 1000");
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

		lblStatus.setEditable(false);
		lblStatus.setFocusable(false);
		statusbar.add(lblStatus);
		
		pack();
	}

	@Override
	public void networkUpdated(String msg) {
		lblStatus.setToolTipText(msg);
		lblStatus.setText(msg);
		canvas.repaint();
	}

	public int getEpoch() {
		int epoch = 1;
		try {
			epoch = Integer.parseInt(txtEpoch.getText());
		} catch (Exception e) {
			txtEpoch.setText(epoch + "");
		}
		return epoch;
	}

	public double getPercent() {
		int percent = 75;
		try {
			percent = Integer.parseInt(txtPercent.getText());
		} catch (Exception e) {
			txtPercent.setText(percent + "");
		}
		return percent / 100.0;
	}
}
