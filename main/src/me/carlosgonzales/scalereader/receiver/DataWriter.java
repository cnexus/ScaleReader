package me.carlosgonzales.scalereader.receiver;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by Carlos on 1/23/2015.
 */
public class DataWriter {
	private File file;
	public DataWriter(File file){
		this.file = file;
	}

	public void writeData(ArrayList<LinkedList<String>> values){
		PrintWriter writer = null;

		try {
			writer = new PrintWriter(new BufferedWriter(new FileWriter(file, file.exists())));

			LinkedList<String> headers = values.get(0);

			for(int i = 1; i < values.size(); i++){
				writeLine(writer, values.get(i));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			writer.close();
		}
	}

	private void writeLine(PrintWriter pw, LinkedList<String> entry){
		StringBuffer line = new StringBuffer();
		for(int j = 0; j < entry.size(); j++)
			line.append(entry.get(j) + "\t");

		pw.println(line.toString().trim());
	}
}
