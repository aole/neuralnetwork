package aole;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;

public class LoadDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private boolean isOK;
	private ArrayList<String[]> data = new ArrayList<>();

	private JTextField txtFile;

	private JTable table;

	private AbstractTableModel tableModel;
	private JComboBox<String> cboTarget;

	private DefaultComboBoxModel<String> targetModel;

	public LoadDialog(Window window) {
		super(window, "Load and Create Network from CSV", ModalityType.APPLICATION_MODAL);
		setLayout(new BorderLayout());

		JPanel panelFile = new JPanel(new BorderLayout());
		add(panelFile, BorderLayout.NORTH);

		txtFile = new JTextField("iris.data", 40);
		panelFile.add(txtFile, BorderLayout.CENTER);

		JPanel panelFileBtn = new JPanel();
		panelFile.add(panelFileBtn, BorderLayout.EAST);

		JButton btn = new JButton("...");
		panelFileBtn.add(btn);
		btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser();
				int returnVal = jfc.showOpenDialog(getParent());
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					txtFile.setText(jfc.getSelectedFile().getAbsolutePath());
				}
			}
		});

		btn = new JButton("Load");
		panelFileBtn.add(btn);
		btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				data.clear();
				targetModel.removeAllElements();
				BufferedReader br = null;
				try {
					String line = "";
					String cvsSplitBy = ",";
					br = new BufferedReader(new FileReader(txtFile.getText()));
					while ((line = br.readLine()) != null) {
						if (line.trim().equals(""))
							continue;
						data.add(line.split(cvsSplitBy));
					}
					String s = null;
					for (int i = 0; i < data.get(0).length; i++) {
						s = "Column " + (i + 1);
						targetModel.addElement(s);
					}
					targetModel.setSelectedItem(s);
				} catch (FileNotFoundException fnfe) {
					JOptionPane.showMessageDialog(null, "File not found! " + txtFile.getText(), "Load CSV",
							JOptionPane.ERROR_MESSAGE);
				} catch (IOException e1) {
					e1.printStackTrace();
				} finally {
					if (br != null)
						try {
							br.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
				}
				tableModel.fireTableStructureChanged();
			}
		});

		JPanel panelExit = new JPanel();
		add(panelExit, BorderLayout.SOUTH);

		btn = new JButton("OK");
		panelExit.add(btn);
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				isOK = true;
				dispose();
			}
		});

		btn = new JButton("Cancel");
		panelExit.add(btn);
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				isOK = false;
				dispose();
			}
		});

		btn = new JButton("Shuffle");
		panelExit.add(btn);
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Collections.shuffle(data);
				tableModel.fireTableDataChanged();
			}
		});

		isOK = false;

		tableModel = new AbstractTableModel() {

			private static final long serialVersionUID = 1L;

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				String value = "";
				if (columnIndex == 0)
					return rowIndex + 1;
				try {
					value = data.get(rowIndex)[columnIndex - 1];
				} catch (Exception e) {
					System.out.println("Error getValueAt():" + rowIndex + "," + columnIndex);
				}
				return value;
			}

			@Override
			public String getColumnName(int column) {
				if (column == 0)
					return "Sr";

				return "Column " + column;
			}

			@Override
			public int getRowCount() {
				return data.size();
			}

			@Override
			public int getColumnCount() {
				int count = 0;
				try {
					count = data.get(0).length + 1;
				} catch (Exception e) {
				}
				return count;
			}
		};

		table = new JTable(tableModel);
		JScrollPane sp = new JScrollPane(table);
		JPanel pnlTable = new JPanel(new BorderLayout());
		add(pnlTable, BorderLayout.CENTER);
		pnlTable.add(sp, BorderLayout.CENTER);

		final JPanel pnlDataOptions = new JPanel();

		pnlTable.add(pnlDataOptions, BorderLayout.SOUTH);

		pnlDataOptions.add(new JLabel("Target"));

		targetModel = new DefaultComboBoxModel<String>();
		cboTarget = new JComboBox<>(targetModel);
		pnlDataOptions.add(cboTarget);

		pack();
	}

	public boolean isOKtoLoad() {
		return isOK;
	}

	public void setNetwork(Network network) {
		//Collections.shuffle(data);

		int target = targetModel.getIndexOf(targetModel.getSelectedItem());
		double inputdata[][] = new double[data.size()][];
		int targetdata[][] = new int[data.size()][];
		int numinput = data.get(0).length - 1;

		// create target dictionary
		Hashtable<String, Integer> d = new Hashtable<>();

		for (String[] row : data) {
			d.put(row[target], 0);
		}
		int numoutput = 0;
		for (String key : d.keySet()) {
			d.put(key, numoutput++);
		}

		// parse input and output data
		for (int row = 0; row < data.size(); row++) {
			String rowdata[] = data.get(row);
			inputdata[row] = new double[numinput];
			targetdata[row] = new int[numoutput];
			int i = 0;
			for (int col = 0; col < rowdata.length; col++) {
				if (col != target) {
					inputdata[row][i] = Double.parseDouble(rowdata[col]);
					i++;
				} else {
					for (int j = 0; j < numoutput; j++) {
						targetdata[row][j] = 0;
					}
					targetdata[row][d.get(rowdata[col])] = 1;
				}
			}
		}

		network.init(inputdata, numinput, (String[]) d.keySet().toArray(new String[0]), targetdata);
	}

}
