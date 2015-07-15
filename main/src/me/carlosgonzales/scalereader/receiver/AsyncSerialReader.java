package me.carlosgonzales.scalereader.receiver;

import me.carlosgonzales.scalereader.handlers.Processor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Carlos on 1/23/2015.
 */
public class AsyncSerialReader extends Thread{
	private static final boolean TESTING = true;
	private InputStream in;
	private Processor processor;
	private boolean forceStop = false;
	private boolean doRead = false;

	public AsyncSerialReader(Processor p, InputStream in){
		processor = p;
		this.in = in;
	}

	public void run() {
		BufferedReader reader = null;

		try {
			String curr;

			reader = new BufferedReader(new InputStreamReader(in));
			while ((curr = reader.readLine()) != null && !forceStop) {
				if (doRead) {
					// block for set delay, check if before and after reads are equal
					String first = curr;
					//junkRead(reader, block);
					String second = reader.readLine();

					if (first.equals(second)) {
						// finally process if we're still receiving the same data, and it's not duplicate
						processor.processData(curr);
					}

					doRead = !doRead;
				}
			}
		} catch (IOException e) {
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void forceStop(){
		forceStop = true;
	}
	public void setDoRead(boolean read) {
		doRead = read;
	}
}