package me.carlosgonzales.scalereader.handlers;

/**
 * Created by Carlos on 1/28/2015.
 */
public class WeightHandler {
	private static final String KG = "kg";
	private static final String LBS = "lb";

	public static double getWeight(String data){
		data = data.trim();

		int index = data.indexOf(KG) == -1 ? data.indexOf(LBS) : data.indexOf(KG);
		if(index != -1)
			data = data.substring(0, index).trim();

		return Double.valueOf(data);
	}

	public static String getUnits(String data){
		data = data.trim();
		int index = data.indexOf(KG) == -1 ? data.indexOf(LBS) : data.indexOf(KG);

		return index != -1 ? data.substring(index).trim() : "";
	}
}
