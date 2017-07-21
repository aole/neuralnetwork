package aole;

import javax.swing.JDialog;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.CategoryDataset;

public class Analysis extends JDialog {

	private static final long serialVersionUID = 1L;

	public Analysis(Network network) {
		CategoryDataset dataset = network.getErrorDataset();
		JFreeChart chart = ChartFactory.createLineChart("Title", "Lifetime", "Errors", dataset);
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(1000, 200));
		add(chartPanel);
		pack();
	}
}
