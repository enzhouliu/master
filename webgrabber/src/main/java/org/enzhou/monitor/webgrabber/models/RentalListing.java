package org.enzhou.monitor.webgrabber.models;

import java.util.Date;

import org.joda.time.LocalDate;

public class RentalListing {
	private String name;
	private LocalDate date;
	private int price;
	private int size;
	private BedType beds = BedType.Unknown;
	private int floor;
	
	public int getFloor() {
		return floor;
	}
	public void setFloor(int floor) {
		this.floor = floor;
	}
	public BedType getBed() {
		return beds;
	}
	public void setBed(BedType baths) {
		this.beds = baths;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
}
