package org.enzhou.monitor.webgrabber.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.joda.time.LocalDate;

public class BuildingData {
	private String name;
	private String address;
	private int pricePerFt;
	private int rentPerFt;
	private List<SaleListing> listing = new ArrayList<SaleListing>();
	private List<RentalListing> rentals = new ArrayList<RentalListing>();
	private int[] avgRental = new int[]{0,0,0,0,0};
	private RentFormular formular;
	
	public RentFormular getFormular() {
		return formular;
	}
	public void setFormular(RentFormular formular) {
		this.formular = formular;
	}
	private int numOfFloors;
	
	public int getNumOfFloors() {
		return numOfFloors;
	}
	public void setNumOfFloors(int numOfFloors) {
		this.numOfFloors = numOfFloors;
	}
	public int[] getAvgRental() {
		return avgRental;
	}
	public void setAvgRental(int[] avgRental) {
		this.avgRental = avgRental;
	}
	public List<RentalListing> getRentals() {
		return rentals;
	}
	public void setRentals(List<RentalListing> rentals) {
		this.rentals = rentals;
	}
	private HashMap<BedType, Integer> rentInfo = new HashMap<>();
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public int getPricePerFt() {
		return pricePerFt;
	}
	public void setPricePerFt(int pricePerFt) {
		this.pricePerFt = pricePerFt;
	}
	public int getRentPerFt() {
		return rentPerFt;
	}
	public void setRentPerFt(int rentPerFt) {
		this.rentPerFt = rentPerFt;
	}
	public List<SaleListing> getListing() {
		return listing;
	}
	public void setListing(List<SaleListing> listing) {
		this.listing = listing;
	}
	public HashMap<BedType, Integer> getRentInfo() {
		return rentInfo;
	}
	public void setRentInfo(HashMap<BedType, Integer> rentInfo) {
		this.rentInfo = rentInfo;
	}
	
	@Override
	public String toString(){
		StringBuffer buffer = new StringBuffer();
//		buffer.append(this.name);
//		buffer.append("\n");
//		buffer.append(this.address + "\n");
//		buffer.append(this.pricePerFt);
//		buffer.append(this.rentPerFt + "\n");
		for(SaleListing sale: this.listing){
			buffer.append(sale.getName() + ";");
			buffer.append(String.format("%.4f", sale.getCapRatio())+";");
			buffer.append(sale.getBed().toString() + ";");
			buffer.append(String.format(Locale.US, "%1$,d", sale.getPrice()) + ";");
			buffer.append(String.format(Locale.US, "%1$,d", sale.getTax()) + ";");
			buffer.append(String.format(Locale.US, "%1$,d", sale.getMaintenance()) + ";");
			buffer.append(String.format(Locale.US, "%1$,d", this.avgRental[sale.getBed().getValue()]) + ";");
			if(this.formular != null)
				buffer.append(String.format(Locale.US, "%1$,d", this.formular.estimateRent(LocalDate.now(), sale.getFloor(), sale.getBed().getValue(), sale.getSize()))+ ";");
			else
				buffer.append("Unknown;");
			buffer.append(sale.getSize() + "\n");
		}
		return buffer.toString();
	}
}
