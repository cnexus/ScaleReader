package me.carlosgonzales.scalereader.receiver;

import me.carlosgonzales.scalereader.handlers.Processor;
import me.carlosgonzales.scalereader.handlers.Test;

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

	public void run(){
		if(Test.DEBUG) {
			runTesting();
			return;
		}

		BufferedReader reader = null;

		try{
			String curr = "";
			String last = "";

			int block = 600;

			reader = new BufferedReader(new InputStreamReader(in));
			while((curr = reader.readLine()) != null && !forceStop) {
				if(doRead) {
					// block for set delay, check if before and after reads are equal
					String first = curr;
					//junkRead(reader, block);
					String second = reader.readLine();

					if (first.equals(second) && !last.equals(first)) {
						// finally process if we're still receiving the same data, and it's not duplicate
						processor.processData(curr);
						last = curr;
					}

					doRead = !doRead;
				}
			}
		}catch ( IOException e ){
		}finally{
			if(reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void runTesting(){
		String value1 = "ST34567   0.11kg";
		String value2 = "ST34567   0.34kg";
		String value3 = "ST34567   0.93kg";

		int count = 1;

		while(true){
			if(count == 1)
				processor.processData(value1);
			else if(count == 2)
				processor.processData(value2);
			else if(count == 3);
				processor.processData(value3);

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				//e.printStackTrace();
			}

			count = count >= 3 ? 1: count++;
		}
	}

	public void forceStop(){
		forceStop = true;
	}
	public void setDoRead(boolean read){
		doRead = read;
	}

	private void junkRead(BufferedReader reader, long duration) throws IOException{
		long start = System.currentTimeMillis();
		while(start + duration > System.currentTimeMillis()) {
			reader.readLine();
		}
	}
}