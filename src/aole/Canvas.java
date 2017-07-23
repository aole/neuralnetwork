package aole;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;

import javax.swing.JPanel;

public class Canvas extends JPanel {

	private static final long serialVersionUID = 1L;

	private Network network;
	private static Stroke[] strokes = { new BasicStroke(1), new BasicStroke(1), new BasicStroke(2), new BasicStroke(3),
			new BasicStroke(4), new BasicStroke(5), new BasicStroke(6), new BasicStroke(7), new BasicStroke(8),
			new BasicStroke(9), new BasicStroke(10) };
	private static Color nodeFillColor = new Color(0, 0, 0, 100);

	public Canvas(Network n) {
		network = n;
		setPreferredSize(new Dimension(800, 600));
	}

	public void paintComponent(Graphics gr) {
		Graphics2D g = (Graphics2D) gr;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		int w = getWidth();
		int h = getHeight();

		g.setColor(Color.white);
		g.fillRect(0, 0, w, h);

		int numcols = network.getNumLayers();
		int coldelta = w / (numcols + 1);

		g.setColor(Color.black);
		int colstart = coldelta;
		// draw nodes
		for (Layer l : network.layers) {
			int numrows = l.getNumNodes();
			int rowdelta = h / (numrows + 1);
			int rowstart = rowdelta;
			for (Node n : l.nodes) {
				n.setLocation(colstart, rowstart);
				rowstart += rowdelta;
			}
			colstart += coldelta;
		}
		// draw connections
		for (Layer l : network.layers) {
			for (int nn = 0; nn < l.nodes.size(); nn++) {
				Node n = l.nodes.get(nn);
				// for (Node n : l.nodes) {
				for (int no = 0; no < n.outNodes.size(); no++) {
					Node o = n.outNodes.get(no);
					double weight = n.weights.get(no);
					if (weight < 0)
						g.setColor(Color.red);
					else
						g.setColor(Color.lightGray);
					int linesize = (int) Math.abs(weight) * 2;
					linesize = linesize < 1 ? 1 : linesize;
					linesize = linesize > 10 ? 10 : linesize;
					g.setStroke(strokes[linesize]);
					g.drawLine(n.locx, n.locy, o.locx, o.locy);
					//String sweight = (double) ((int) (weight * 100)) / 100.0 + "";
					//g.setColor(Color.black);
					//g.drawString(sweight, n.locx + (o.locx - n.locx) / 4, n.locy + (o.locy - n.locy) / 4);
				}
				drawNode(g, n, l.type, nn);
			}
		}
	}

	private void drawNode(Graphics2D g, Node n, int type, int nn) {
		g.setStroke(strokes[1]);
		g.setColor(Color.white);
		g.fillRect(n.locx - 20, n.locy - 10, 40, 20);
		g.setColor(nodeFillColor);
		g.fillRect(n.locx - 20, n.locy - 10, (int) (40 * n.output), 20);
		g.setColor(Color.black);
		g.drawRect(n.locx - 20, n.locy - 10, 40, 20);

		String output = "B";
		if (!n.isBias)
			output = (double) ((int) (n.output * 100) / 100.0) + "";

		g.setColor(Color.black);
		if (type == Layer.INPUT && !n.isBias)
			g.drawString(output, n.locx - 50, n.locy + 5);
		else
			g.drawString(output, n.locx - 10, n.locy + 5);

		if (type == Layer.OUTPUT) {
			String label = n.label;
			if (network.currentRow != -1)
				if (network.target[network.currentRow][nn] == 1)
					label += " *";
			g.drawString(label, n.locx + 25, n.locy + 5);
		}
	}
}
