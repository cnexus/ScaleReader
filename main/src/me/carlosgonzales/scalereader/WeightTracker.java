package me.carlosgonzales.scalereader;

import java.util.LinkedList;

/**
 * Created by Carlos on 1/23/2015.
 */
public class WeightTracker {
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

	private boolean inRange(double weight){
		return weight >= low && weight <= high;
	}

	private String getStatus(String weight){
		String ret = "PASS";

		if(!inRange(Double.valueOf(weight)))
			ret = "FAIL";

		return ret;
	}

	public LinkedList<String> getEntryFor(String weight, String time){
		LinkedList<String> entry = new LinkedList<String>();
		String[] timeParts = time.trim().split(" ");

		String carton = inRange(Double.valueOf(weight)) ? String.valueOf(++cartonNum) : " -X-";

		entry.add(carton);
		entry.add(weight);
		for(String s: timeParts)
			entry.add(s);

		entry.add(getStatus(weight));
		return entry;
	}
}
