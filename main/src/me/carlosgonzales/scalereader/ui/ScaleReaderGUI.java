package me.carlosgonzales.scalereader.ui;

import me.carlosgonzales.scalereader.handlers.DataWriter;
import me.carlosgonzales.scalereader.handlers.Processor;
import me.carlosgonzales.scalereader.handlers.WeightHandler;
import me.carlosgonzales.scalereader.receiver.ScaleComDevice;

import javax.print.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.io.File;
import java.net.MalformedURLException;
import java.sql.Timestamp;
import java.util.*;

/**
 * Created by Carlos on 1/23/2015.
 */
public class ScaleReaderGUI extends JFrame implements Processor, ActionListener{
	private static final String NAME = "ScaleReader";
	private static final Rectangle SIZE = new Rectangle(900, 650);
	private ScaleComDevice receiver = ScaleComDevice.getInstance(this);
	private WeightTable table;
	private Map<Double, String> dataSet = new HashMap<Double, String>();
	private String[] bounds;
	private JTextField[] topFields;
	private JTextField[] bottomFields;
	private JRadioButton topButton;
	private JRadioButton bottomButton;

	private ScaleReaderGUI(String name){
		super(name);
		setJMenuBar(new ActionBar(this));
		setSize(SIZE.width, SIZE.height);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);

		promptForBounds();
		initComponents();
	}

	public void startReceiving(){
		receiver.start();
	}

	private void promptForBounds(){
		Dimension size = new Dimension(600, 300);
		int hgap = 20;
		int vgap = 10;

		final int num = 2;

		topFields = new JTextField[num];
		for(int i = 0; i < num; i++){
			topFields[i] = new JTextField();
			topFields[i].setEnabled(true);
		}

		bottomFields = new JTextField[num];
		for(int i = 0; i < num; i++){
			bottomFields[i] = new JTextField();
			bottomFields[i].setEnabled(false);
		}

		final JDialog dialog = new JDialog();
		dialog.setModalityType(JDialog.ModalityType.APPLICATION_MODAL);

		GridLayout fullLayout = new GridLayout(2, 1);
		GridLayout topHalf = new GridLayout(1, 2);
		GridLayout topHalfLeft = new GridLayout(3, 1);
		GridLayout topHalfRight = new GridLayout(3, 1, hgap, vgap);

		GridLayout bottomHalf = new GridLayout(1, 2);
		GridLayout bottomHalfLeft = new GridLayout(3, 1);
		GridLayout bottomHalfRight = new GridLayout(3, 1, hgap, vgap);

		topButton = new JRadioButton("Input lower and upper bounds");
		bottomButton = new JRadioButton("Input midpoint and offset");

		topButton.addActionListener(this);
		bottomButton.addActionListener(this);

		dialog.setLayout(fullLayout);
		JPanel topHalfPanel = new JPanel(topHalf);
		JPanel topHalfLeftPanel = new JPanel(topHalfLeft);
		topHalfLeftPanel.add(topButton);

		JPanel topHalfRightPanel = new JPanel(topHalfRight);
		topHalfRightPanel.add(new JPanel());
		topHalfRightPanel.add(topFields[0]);
		topHalfRightPanel.add(topFields[1]);
		topHalfPanel.add(topHalfLeftPanel);
		topHalfPanel.add(topHalfRightPanel);

		JPanel bottomHalfPanel = new JPanel(bottomHalf);
		JPanel bottomHalfLeftPanel = new JPanel(bottomHalfLeft);
		bottomHalfLeftPanel.add(bottomButton);

		JPanel bottomHalfRightPanel = new JPanel(bottomHalfRight);
		bottomHalfRightPanel.add(new JPanel());
		bottomHalfRightPanel.add(bottomFields[0]);
		bottomHalfRightPanel.add(bottomFields[1]);
		bottomHalfPanel.add(bottomHalfLeftPanel);
		bottomHalfPanel.add(bottomHalfRightPanel);

		topButton.setSelected(true);
		bottomButton.setSelected(false);

		dialog.add(topHalfPanel);
		dialog.add(bottomHalfPanel);

		dialog.setSize(size);

		dialog.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				bounds = new String[num];
				JTextField fields[];

				if(topButton.isSelected())
					fields = topFields;
				else
					fields = bottomFields;

				for(int i = 0; i < num; i++) {
					bounds[i] = fields[i].getText().trim();
				}

				if(fields == bottomFields){
					double mid = Double.valueOf(fields[0].getText().trim());
					double offset = Double.valueOf(fields[1].getText().trim());

					bounds[0] = String.valueOf(mid - offset);
					bounds[1] = String.valueOf(mid + offset);
				}

				JOptionPane.showMessageDialog(null, "Using entered values for determining weight limits.");
			}
		});

		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);
	}

	private void showSaveOperation(boolean isShutdown){
		JFileChooser chooser = new JFileChooser();
		chooser.setMultiSelectionEnabled(false);
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int result = chooser.showSaveDialog(ScaleReaderGUI.this);

		if(result == JFileChooser.APPROVE_OPTION) {
			File f = chooser.getSelectedFile();
			DataWriter writer = new DataWriter(f);

			ArrayList<LinkedList<String>> data = table.getData();
			data.add(0, table.getHeaders());
			writer.writeData(data);
		}else if(result == JFileChooser.CANCEL_OPTION && isShutdown){
			System.exit(0);
		}
	}

	private void initComponents(){
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				showSaveOperation(true);
			}
		});

		table = new WeightTable(bounds[0], bounds[1]);
		table.setRowSelectionAllowed(false);
		table.setColumnSelectionAllowed(false);
		table.setFont(new Font("Tahoma", Font.PLAIN, 20));

		GridLayout layout = new GridLayout(1,3);
		setLayout(layout);
		JScrollPane container = new JScrollPane(table);
		container.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				e.getAdjustable().setValue(e.getAdjustable().getMaximum());
			}
		});

		InputMap inputMap = container.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap actionMap = container.getActionMap();

		Action enterAction = new AbstractAction() {
			public void actionPerformed(ActionEvent actionEvent){
				receiver.setDoRead(true);
			}
		};

		String action = "enterAction";

		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), action);
		actionMap.put(action, enterAction);

		add(container);
	}

	public ScaleReaderGUI(){
		this(NAME);
	}

	public static void main(String[] args){
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

	private static void createAndShowGUI(){
		try {
			Font f = new Font("Tahoma", Font.PLAIN, 24);
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			UIManager.getLookAndFeelDefaults()
					.put("defaultFont", f);

			ScaleReaderGUI main = new ScaleReaderGUI();
			main.setVisible(true);
			main.startReceiving();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	}

	public void processData(String data) {
		double weight = WeightHandler.getWeight(data);

		if(dataSet.containsKey(weight))
			return;

		String timestamp = (new Timestamp(Calendar.getInstance().getTime().getTime())).toString();

		table.add(data, timestamp);
		dataSet.put(weight, timestamp);

		//Create unique label
		if(table.isInRange(weight))
			printFile(table.getLabelFile());
	}

	public void printFile(final File f){
		if(f == null)
			return;

		PrintService service = PrintServiceLookup.lookupDefaultPrintService();
		DocPrintJob job = service.createPrintJob();
		DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE;

		Printable printable = new Printable() {
			@Override
			public int print(Graphics g, PageFormat pf, int pageIndex) {
				ImageIcon printImage = null;
				try {
					printImage = new ImageIcon(f.toURI().toURL());
				} catch (MalformedURLException e) {
					//e.printStackTrace();
				}
				Graphics2D g2d = (Graphics2D) g;
				g.translate((int) (pf.getImageableX()), (int) (pf.getImageableY()));
				if (pageIndex == 0) {
					double pageWidth = pf.getImageableWidth();
					double pageHeight = pf.getImageableHeight();
					double imageWidth = printImage.getIconWidth();
					double imageHeight = printImage.getIconHeight();
					double scaleX = pageWidth / imageWidth;
					double scaleY = pageHeight / imageHeight;
					double scaleFactor = Math.min(scaleX, scaleY);
					g2d.scale(scaleFactor, scaleFactor);
					g.drawImage(printImage.getImage(), 0, 0, null);
					return Printable.PAGE_EXISTS;
				}
				return Printable.NO_SUCH_PAGE;
			}
		};

		SimpleDoc doc = new SimpleDoc(printable, flavor, null);
		try {
			job.print(doc, null);
		} catch (PrintException e) {
			//e.printStackTrace();
		}
	}

	public void actionPerformed(ActionEvent actionEvent) {
		JRadioButton button = (JRadioButton) actionEvent.getSource();
		JRadioButton disableButton = null;

		JTextField[] enabled = null;
		JTextField[] disabled = null;

		if (button == topButton) {
			disableButton = bottomButton;
			disabled = bottomFields;
			enabled = topFields;
		} else {
			disableButton = topButton;
			disabled = topFields;
			enabled = bottomFields;
		}

		for (int i = 0; i < enabled.length; i++) {
			disabled[i].setEnabled(false);
			enabled[i].setEnabled(true);
		}

		disableButton.setSelected(false);
	}
}
