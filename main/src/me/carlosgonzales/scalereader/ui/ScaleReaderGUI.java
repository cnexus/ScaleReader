package me.carlosgonzales.scalereader.ui;

import me.carlosgonzales.scalereader.handlers.DataWriter;
import me.carlosgonzales.scalereader.handlers.Processor;
import me.carlosgonzales.scalereader.receiver.ScaleComDevice;

import javax.swing.*;
import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.sql.Timestamp;
import java.util.*;

/**
 * Created by Carlos on 1/23/2015.
 */
public class ScaleReaderGUI extends JFrame implements Processor{
	private static final String NAME = "ScaleReader";
	private static final Rectangle SIZE = new Rectangle(800, 600);
	private WeightTable table;
	private Map<Double, String> dataSet = new HashMap<Double, String>();

	private ScaleReaderGUI(String name){
		super(name);
		setJMenuBar(new ActionBar(this));
		setSize(SIZE.width, SIZE.height);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		initComponents();

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				showSaveOperation(true);
			}
		});
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
		ScaleComDevice receiver = ScaleComDevice.getInstance(this);
		receiver.start();

		table = new WeightTable("0.2", "0.8");
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
		if(dataSet.containsKey(Double.valueOf(data)))
			return;

		String timestamp = (new Timestamp(Calendar.getInstance().getTime().getTime())).toString();

		table.add(data, timestamp);
		dataSet.put(Double.valueOf(data), timestamp);
	}
}
