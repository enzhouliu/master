package org.enzhou.monitor.webgrabber;

import org.enzhou.monitor.webgrabber.models.BedType;
import org.enzhou.monitor.webgrabber.models.BuildingData;
import org.enzhou.monitor.webgrabber.models.RentalListing;
import org.enzhou.monitor.webgrabber.models.SaleListing;
import org.enzhou.monitor.webgrabber.parsers.ResponseParser;
import org.enzhou.monitor.webgrabber.parsersImpl.SingleBuildingParser;
import org.enzhou.monitor.webgrabber.single.SingleRequest;
import org.enzhou.monitor.webgrabber.singleImpl.DefaultRequest;
import org.joda.time.LocalDate;

public class BuildingRunner implements Runnable{

	private String baseUrl;
	private String url;
	private SingleRequest request;
	private int priceLimit;
	
	public BuildingRunner(String baseUrl, String url, int priceLimit){
		this.url = url;
		this.priceLimit = priceLimit;
		this.baseUrl = baseUrl;
		this.request = new DefaultRequest();
	}
	@Override
	public void run() {
		ResponseParser<BuildingData> parser = new SingleBuildingParser(this.baseUrl, priceLimit);
		String html = request.sendGetRequest(baseUrl + url);
		BuildingData result = parser.parse(html);
		
		calculateCapRatio(result);
		StreetEasyGrabber.writeFile(result);
		System.out.println(result.getName() + " " + result.getAddress());
	}
	
	private void calculateCapRatio(BuildingData building){
		//get avgRent
		calAverageRent(building);
		
		if(building.getFormular() != null){
			for(SaleListing sales: building.getListing()){
				if(sales.getPrice() > 0 && sales.getTax() >= 0 && sales.getMaintenance() > 0){
					int rent = building.getFormular().estimateRent(LocalDate.now(), sales.getFloor(), sales.getBed().getValue(), sales.getSize());
					sales.setCapRatio(12 * ((double)(rent - sales.getTax() - sales.getMaintenance()))/sales.getPrice());
				}
			}
		}
		else{
			for(SaleListing sales: building.getListing()){
				if(sales.getPrice() > 0 && sales.getTax() >= 0 && sales.getMaintenance() > 0 && building.getAvgRental()[sales.getBed().getValue()] > 0){
					sales.setCapRatio(12 * ((double)(building.getAvgRental()[sales.getBed().getValue()] - sales.getTax() - sales.getMaintenance()))/sales.getPrice());
				}
			}
		}
	}
	private void calAverageRent(BuildingData building) {
		int[] rentals = new int[]{0,0,0,0,0,0};
		int[] counts = new int[]{0,0,0,0,0,0};
		for(RentalListing rental: building.getRentals()){
			if(rental.getPrice() > 0 && rental.getBed() != BedType.Unknown){
				rentals[rental.getBed().getValue()] += rental.getPrice();
				counts[rental.getBed().getValue()] += 1;
			}
		}
		int[] avgRent = new int[]{0,0,0,0,0,0};
		for(int i=0; i<5; i++){
			if(counts[i] > 0)
				avgRent[i] = rentals[i]/counts[i];
		}
		building.setAvgRental(avgRent);
	}

}
