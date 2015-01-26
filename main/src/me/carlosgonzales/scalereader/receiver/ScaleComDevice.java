package me.carlosgonzales.scalereader.receiver;

import gnu.io.*;
import me.carlosgonzales.scalereader.handlers.Processor;

import java.io.IOException;
import java.util.Enumeration;

/**
 * Created by Carlos on 1/23/2015.
 */

public class ScaleComDevice implements SerialPortEventListener, Processor {
	/**
	 * Serial input structure
	 *
	 * s1 s1 , s2 s2 , s3 d d d d d d d s4 cr lf
	 *
	 * S1:	weight status, ST=standstill, US=not standstill, OL=overload
	 * S2:	weight mode, GS=gross mode NT=net mode
	 * S3:	sign of positive or negative, "+" or "-"
	 * S4:	units, "kg" or "lb"
	 *
	 * d:	weight data
	 * cr:	carriage return
	 * lf:	line feed
	 *
	 * total full read (starting with "ST"): 17 bits
	 *
	 */

	private static final String COM_NAME = "COM6";
	private static ScaleComDevice sInstance;
	private Processor parent;
	private AsyncSerialReader reader;


	private ScaleComDevice(Processor parent){
		this.parent = parent;

		Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();
		CommPortIdentifier port = null;

		while (portEnum.hasMoreElements()){
			CommPortIdentifier portIdentifier = portEnum.nextElement();
			if(COM_NAME.equals(portIdentifier.getName()))
				port = portIdentifier;
		}

		if(port != null){
			try {
				int time = 2000;
				CommPort commPort = port.open(this.getClass().getName(), time);
				SerialPort serialPort = (SerialPort) commPort;
				serialPort.setSerialPortParams(ComParams.BAUD,
						ComParams.DATA,
						ComParams.STOP,
						ComParams.PARITY);

				serialPort.enableReceiveTimeout(time / 2);

				reader = new AsyncSerialReader(this, serialPort.getInputStream());
			} catch (PortInUseException e) {
				e.printStackTrace();
			} catch (UnsupportedCommOperationException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void start(){
		reader.start();
	}

	public void stop(){
		reader.forceStop();
	}

	public static void main(String[] args){
		ScaleComDevice rec = new ScaleComDevice(null);
		rec.start();
	}

	public void processData(String data) {
		// We know the structure, so throw out everything but the data
		int trash = 7;
		int dataLen = 7;
		data = data.substring(trash, trash+dataLen).trim();

		double weight = Double.valueOf(data);
		if(weight != 0) {
			System.out.println("data = [" + weight + "]");
			parent.processData(data);
		}
	}

	public static ScaleComDevice getInstance(Processor p){
		if(sInstance == null)
			sInstance = new ScaleComDevice(p);

		return sInstance;
	}

	public void serialEvent(SerialPortEvent serialPortEvent) { }
}
