package org.enzhou.monitor.webgrabber.models;

public class SaleListing {
	private String name;
	private int price;
	private BedType bed = BedType.Unknown;
	private int size;
	private boolean inConstract;
	private int tax;
	private int maintenance;
	private double capRatio;
	private int floor;
	
	public int getFloor() {
		return floor;
	}
	public void setFloor(int floor) {
		this.floor = floor;
	}
	public double getCapRatio() {
		return capRatio;
	}
	public void setCapRatio(double capRatio) {
		this.capRatio = capRatio;
	}
	public int getTax() {
		return tax;
	}
	public void setTax(int tax) {
		this.tax = tax;
	}
	public int getMaintenance() {
		return maintenance;
	}
	public void setMaintenance(int maintenance) {
		this.maintenance = maintenance;
	}
	public boolean isInConstract() {
		return inConstract;
	}
	public void setInConstract(boolean isInConstract) {
		this.inConstract = isInConstract;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}

	public BedType getBed() {
		return bed;
	}
	public void setBed(BedType baths) {
		this.bed = baths;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
}
