package me.carlosgonzales.scalereader.ui;

import me.carlosgonzales.scalereader.handlers.LabelFactory;
import me.carlosgonzales.scalereader.handlers.WeightTracker;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * Created by Carlos on 1/23/2015.
 */
public class WeightTable extends JTable {
	private HashSet<File> labelsCreated = new HashSet<File>();
	private WeightTableModel model;
	private WeightTracker tracker;

	public WeightTable(String low, String high){
		model = new WeightTableModel();
		tracker = new WeightTracker(Double.valueOf(low.trim()), Double.valueOf(high.trim()));

		this.setModel(model);
		this.setRowHeight(40);

		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);

		for(int i = 0; i < model.getColumnCount(); i++)
			getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
	}

	public boolean isInRange(double weight){
		return tracker.inRange(weight);
	}

	public LinkedList<String> getHeaders(){
		return new LinkedList<String>(Arrays.asList(model.headers));
	}

	public WeightTracker getTracker(){
		return tracker;
	}

	public void add(String weight, String timestamp) {
		model.add(tracker.getEntryFor(weight, timestamp));
	}

	public File getLabelFile(){
		int last = tracker.getCartonNum();
		LinkedList<String> data = model.getValueAt(last - 1);

		if(WeightTracker.FAIL.equals(data.getLast()))
			return null;

		String[] fields = new String[4];
		fields[0] = "Carton: " + last;
		fields[1] = "Weight: " + data.get(1) + data.get(2);
		fields[2] = data.get(3).trim() + " " + data.get(4);
		fields[3] = "Status: " + data.getLast();

		File f = LabelFactory.createLabel(fields);

		if(labelsCreated.contains(f))
			return null;

		labelsCreated.add(f);
		return f;
	}

	public ArrayList<LinkedList<String>> getData(){
		return model.data;
	}

	private class WeightTableModel extends AbstractTableModel{
		private String[] headers = {"Carton #", "Weight", "Units", "Date", "Time", "Status", "Lower", "Upper"};
		private ArrayList<LinkedList<String>> data = new ArrayList<LinkedList<String>>();
		public String getColumnName(int col) {
			return headers[col].toString();
		}
		public int getRowCount() {
			return data.size();
		}

		public int getColumnCount() {
			return headers.length;
		}

		public LinkedList<String> getValueAt(int row){
			return data.get(row);
		}

		public Object getValueAt(int row, int col) {
			return data.get(row).get(col);
		}
		public boolean isCellEditable(int row, int col){
			return false;

		}
		public void setValueAt(Object value, int row, int col) {
			data.get(row).set(col, value.toString());
			fireTableCellUpdated(row, col);
		}

		public void add(LinkedList<String> entry){
			data.add(entry);
			fireTableRowsInserted(data.size() - 1, data.size() - 1);
		}
	}
}
