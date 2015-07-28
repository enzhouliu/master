package org.enzhou.monitor.webgrabber.models;

import org.joda.time.LocalDate;
import org.joda.time.Years;

public class RentFormular {
	
	private double constant;
	private double timePar;
	private double floorPar;
	private double bedPar;
	private double sizePar;
	
	public RentFormular(double constant, double timePar, double floorPar, double bedPar, double sizePar){
		this.constant = constant;
		this.timePar = timePar;
		this.floorPar = floorPar;
		this.bedPar = bedPar;
		this.sizePar = sizePar;
	}
	
	public int estimateRent(LocalDate date, int floor, int bed, int size){
		int yearDiff = Years.yearsBetween(date, LocalDate.now()).getYears();
		return (int)(constant + yearDiff * timePar + floor * floorPar + bed * bedPar + size * sizePar);
	}
}
