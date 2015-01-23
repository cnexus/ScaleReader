package me.carlosgonzales.scalereader.receiver;

import java.io.*;

/**
 * Created by Carlos on 1/23/2015.
 */
public class AsyncSerialReader implements Runnable{
	private InputStream in;
	private Processor processor;

	public AsyncSerialReader(Processor p, InputStream in){
		processor = p;
		this.in = in;
	}

	public void run (){
		try{
			String curr = "";
			String last = "";


			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			while((curr = reader.readLine()) != null) {
				if (curr.startsWith(ComParams.START_REC) && !last.equals(curr)) { // only enter if in standstill
					processor.processData(curr);
				}
				
				last = curr;
			}
		}
		catch ( IOException e ){
			e.printStackTrace();
		}
	}

	private static boolean hasPassed(long start, long offset){
		return System.currentTimeMillis() >= (start + offset);
	}
}