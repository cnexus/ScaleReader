package me.carlosgonzales.scalereader.receiver;

import gnu.io.*;
import me.carlosgonzales.scalereader.handlers.Processor;
import me.carlosgonzales.scalereader.handlers.Test;

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

	private static ScaleComDevice sInstance;
	private Processor parent;
	private AsyncSerialReader reader;
	private static boolean TESTING = false;


	private ScaleComDevice(Processor parent){
		if(Test.DEBUG) {
			reader = new AsyncSerialReader(this, null);
			return;
		}

		this.parent = parent;

		Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();
		CommPortIdentifier port = null;

		while (portEnum.hasMoreElements()){
			CommPortIdentifier portIdentifier = portEnum.nextElement();
			//if(ComParams.COM_NAME.equals(portIdentifier.getName()))
			if(portIdentifier.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				port = portIdentifier;
			}
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

	public void processData(String data) {
		// We know the structure, so throw out everything but the data

		String data2 = data.substring(ComParams.TRASH_LEN, ComParams.TRASH_LEN+ComParams.DATA_LEN).trim();
		String units = data.substring(ComParams.TRASH_LEN + ComParams.DATA_LEN,
				ComParams.TRASH_LEN + ComParams.DATA_LEN + ComParams.UNITS_LEN).trim();
		double weight = Double.valueOf(data2);
		if(weight != 0) {
			System.out.println("data = [" + weight + " | " + units +"]");
			parent.processData(weight+units);
		}
	}

	public static ScaleComDevice getInstance(Processor p){
		if(sInstance == null)
			sInstance = new ScaleComDevice(p);

		return sInstance;
	}

	public void serialEvent(SerialPortEvent serialPortEvent) { }
}
