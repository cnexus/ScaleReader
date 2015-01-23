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

			int block = 2500;


			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			while((curr = reader.readLine()) != null) {
				if (curr.startsWith(ComParams.START_REC)) { // only enter if in standstill
					// block for set delay, check if before and after reads are equal
					String first = curr;

					junkRead(reader, block);

					String second = reader.readLine();


					if(first != null && first.equals(second)) {
						// finally process if we're still receiving the same data

						processor.processData(curr);
					}

				}
			}
		}
		catch ( IOException e ){
			e.printStackTrace();
		}
	}

	private static boolean hasPassed(long start, long offset){
		return System.currentTimeMillis() >= (start + offset);
	}

	private void junkRead(BufferedReader reader, long duration) throws IOException{
		long start = System.currentTimeMillis();
		while(start + duration < System.currentTimeMillis())
			reader.readLine();
	}
}