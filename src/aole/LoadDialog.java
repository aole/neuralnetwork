package aole;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
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

		txtFile = new JTextField("C:\\Users\\baole\\Downloads\\iris.data", 40);
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
					tableModel.fireTableStructureChanged();
					String s = null;
					for (int i = 0; i < data.get(0).length; i++) {
						s = "Column " + (i + 1);
						targetModel.addElement(s);
					}
					targetModel.setSelectedItem(s);
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

	public int[][] getData() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getHidden() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int[][] getTarget() {
		// TODO Auto-generated method stub
		return null;
	}

}
