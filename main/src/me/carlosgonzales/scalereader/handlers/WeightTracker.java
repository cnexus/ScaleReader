package me.carlosgonzales.scalereader.handlers;

import java.util.LinkedList;

/**
 * Created by Carlos on 1/23/2015.
 */
public class WeightTracker {
	public static final String PASS = "PASS";
	public static final String FAIL = "FAIL";

	private int cartonNum;
	private double low;
	private double high;

	public WeightTracker(double low, double high){
		this.low = low;
		this.high = high;
	}

	public void setRange(double low, double high){
		this.low = low;
		this.high = high;
	}

	public int getCartonNum(){
		return cartonNum;
	}

	public boolean inRange(double weight){
		return weight >= low && weight <= high;
	}

	private String getStatus(String weight){
		String ret = PASS;

		if(!inRange(Double.valueOf(weight)))
			ret = FAIL;

		return ret;
	}

	public LinkedList<String> getEntryFor(String weight, String time){
		LinkedList<String> entry = new LinkedList<String>();
		String[] timeParts = time.trim().split(" ");

		double weightVal = WeightHandler.getWeight(weight);

		String carton = inRange(weightVal) ? String.valueOf(++cartonNum) : " -X-";

		entry.add(carton);
		entry.add(String.valueOf(weightVal));
		entry.add(WeightHandler.getUnits(weight));
		for(String s: timeParts)
			entry.add(s);

		entry.add(getStatus(String.valueOf(weightVal)));
		entry.add(String.valueOf(low));
		entry.add(String.valueOf(high));
		return entry;
	}
}
