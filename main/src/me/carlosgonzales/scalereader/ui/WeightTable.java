package me.carlosgonzales.scalereader.ui;

import me.carlosgonzales.scalereader.WeightTracker;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by Carlos on 1/23/2015.
 */
public class WeightTable extends JTable {
	private WeightTableModel model;
	private WeightTracker tracker;

	public WeightTable(String low, String high){
		model = new WeightTableModel();
		tracker = new WeightTracker(Double.valueOf(low), Double.valueOf(high));

		this.setModel(model);
		this.setRowHeight(40);

		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);

		for(int i = 0; i < model.getColumnCount(); i++)
			getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
	}

	public LinkedList<String> getHeaders(){
		LinkedList<String> l =  new LinkedList();
		for(int i = 0; i < l.size(); i++)
			l.add(model.headers[i]);

		return l;
	}

	public void add(String weight, String timestamp) {
		model.add(tracker.getEntryFor(weight, timestamp));
	}

	public ArrayList<LinkedList<String>> getData(){
		return model.data;
	}

	private class WeightTableModel extends AbstractTableModel{
		private String[] headers = {"Carton #", "Weight", "Date", "Time", "Status"};
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
