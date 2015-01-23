package me.carlosgonzales.scalereader.receiver;

import java.io.*;
import java.util.Map;
import java.util.Set;

/**
 * Created by Carlos on 1/23/2015.
 */
public class DataWriter {
	private File file;
	public DataWriter(File file){
		this.file = file;
	}

	public void writeData(Map<Double, String> values){
		PrintWriter writer = null;

		try {
			writer = new PrintWriter(new BufferedWriter(new FileWriter(file, file.exists())));

			Set<Double> keys = values.keySet();

			for(Double d: keys){
				String timestamp = values.get(d);
				writer.println(d + "\t" + timestamp);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			writer.close();
		}
	}
}
