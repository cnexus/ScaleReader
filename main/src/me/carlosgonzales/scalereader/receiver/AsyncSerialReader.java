package me.carlosgonzales.scalereader.receiver;

import java.io.*;

/**
 * Created by Carlos on 1/23/2015.
 */
public class AsyncSerialReader extends Thread{
	private InputStream in;
	private Processor processor;
	private boolean forceStop = false;

	public AsyncSerialReader(Processor p, InputStream in){
		processor = p;
		this.in = in;
	}

	public void run (){
		BufferedReader reader = null;

		try{
			String curr = "";
			String last = "";

			int block = 800;

			reader = new BufferedReader(new InputStreamReader(in));
			while((curr = reader.readLine()) != null && !forceStop) {
				if (curr.startsWith(ComParams.START_REC)) { // only enter if in standstill
					// block for set delay, check if before and after reads are equal
					String first = curr;
					junkRead(reader, block);
					String second = reader.readLine();

					if(first.equals(second) && !last.equals(first)) {
						// finally process if we're still receiving the same data, and it's not duplicate
						processor.processData(curr);
						last = curr;
					}

				}
			}
		}catch ( IOException e ){
		}finally{
			if(reader != null)
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	public void forceStop(){
		forceStop = true;
	}

	private static boolean hasPassed(long start, long offset){
		return System.currentTimeMillis() >= (start + offset);
	}

	private void junkRead(BufferedReader reader, long duration) throws IOException{
		long start = System.currentTimeMillis();
		while(start + duration > System.currentTimeMillis()) {
			reader.readLine();
		}
	}
}