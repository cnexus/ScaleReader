package me.carlosgonzales.scalereader.ui;

import me.carlosgonzales.scalereader.receiver.Processor;
import me.carlosgonzales.scalereader.receiver.SerialReceiver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

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
		this.setJMenuBar(new ActionBar(this));
		this.setSize(SIZE.width, SIZE.height);
		this.setResizable(false);

		initComponents();
	}

	private void initComponents(){
		SerialReceiver receiver = SerialReceiver.getInstance(this);
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
