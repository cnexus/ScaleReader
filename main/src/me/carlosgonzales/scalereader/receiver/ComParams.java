package me.carlosgonzales.scalereader.receiver;

import gnu.io.SerialPort;

/**
 * Created by Carlos on 1/23/2015.
 */
public final class ComParams {
	public static final String START_REC = "ST";
	public static final int BAUD = 9600;
	public static final int DATA = SerialPort.DATABITS_7;
	public static final int STOP = SerialPort.STOPBITS_1;
	public static final int PARITY = SerialPort.PARITY_NONE;
}
