package org.enzhou.monitor.webgrabber.parsersImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;
import org.enzhou.monitor.webgrabber.models.BedType;
import org.enzhou.monitor.webgrabber.models.BuildingData;
import org.enzhou.monitor.webgrabber.models.RentFormular;
import org.enzhou.monitor.webgrabber.models.RentalListing;
import org.enzhou.monitor.webgrabber.models.SaleListing;
import org.enzhou.monitor.webgrabber.parsers.ResponseParser;
import org.enzhou.monitor.webgrabber.single.SingleRequest;
import org.enzhou.monitor.webgrabber.singleImpl.DefaultRequest;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class SingleBuildingParser implements ResponseParser<BuildingData>{
	
	private SingleRequest requestor;
	private String baseUrl;
	private int priceLimit;
	
	public SingleBuildingParser(String baseUrl, int priceLimit){
		this.requestor = new DefaultRequest();
		this.baseUrl = baseUrl;
		this.priceLimit = priceLimit;
	}

	@Override
	public BuildingData parse(String html){
		Document document = Jsoup.parse(html);
		BuildingData result = new BuildingData();
		
		//basic info
		Elements mainInfo = document.getElementsByClass("main-info");
		if(mainInfo.size() > 0){
			result.setName(mainInfo.get(0).child(0).text());
			result.setAddress(mainInfo.get(0).child(1).text());
		}
		
		//building facts
		Pattern avgPattern = Pattern.compile("\\$(.*?) per ft");
		Pattern totalFloors = Pattern.compile("(\\d+) stories");
		Elements buildingFacts = document.getElementsByClass("building_facts");
		if(buildingFacts.size() > 0){
			Elements infos = buildingFacts.get(0).select("tr");
			for(Element row: infos){
				if(row.childNodeSize() >= 2){
					if(row.child(0).text().equals("Facts")){
						Matcher matcher = totalFloors.matcher(row.child(1).text());
						if(matcher.find())
							result.setNumOfFloors(Integer.parseInt(matcher.group(1).trim()));
					}
					if(row.child(0).text().equals("Sales Listings")){
						Matcher matcher = avgPattern.matcher(row.child(1).text());
						if(matcher.find())
							result.setPricePerFt(Integer.parseInt(matcher.group(1).replace(",", "").trim()));
					}
					else if(row.child(0).text().equals("Rentals Listings")){
						Matcher matcher = avgPattern.matcher(row.child(1).text());
						if(matcher.find())
							result.setPricePerFt(Integer.parseInt(matcher.group(1)));
					}
				}
			}
		}
		
		//listing
		HashMap<String, BedType> bathMap = new HashMap<>();
		bathMap.put("studio", BedType.Studio);
		bathMap.put("1", BedType.OneBed);
		bathMap.put("2", BedType.TwoBed);
		bathMap.put("3", BedType.ThreeBed);
		//current listing
		Pattern roomPattern = Pattern.compile(", (.*?) bed");
		Pattern roomPattern2 = Pattern.compile("^(.*?) bed");
		Pattern sizePattern = Pattern.compile("(.*?) ft");
		Pattern taxPattern = Pattern.compile("Monthly Taxes: \\$(.*?)$");
		Pattern ccPattern = Pattern.compile("Common Charges: \\$(.*?) ");
		Elements listing = document.getElementsByClass("listings_table_container");
		if(listing.size() > 0){
			for(Element row: listing.get(0).select("tbody").get(0).select("tr")){
				SaleListing unit = new SaleListing();
				if(row.childNodeSize() >= 5){
					unit.setName(row.child(0).select("a").get(0).text());
					int floor = extracFloor(result, unit.getName());
					unit.setFloor(floor == 0? result.getNumOfFloors()/2: floor);
					String detailUrl = row.child(0).select("a").attr("href");
					if(row.getElementsByClass("price").size() > 0){
						unit.setPrice(Integer.parseInt(row.getElementsByClass("price").get(0).text().replace("$", "").replace(",", "").replace("\u00a0", "").trim()));
					}
					if(this.priceLimit > 0 && unit.getPrice() > this.priceLimit)
						continue;
					Matcher matcher = roomPattern.matcher(row.child(2).text());
					if(matcher.find()){
						String beds = matcher.group(1).trim();
						if(bathMap.containsKey(beds)){
							unit.setBed(bathMap.get(beds));
						}
						else{
							unit.setBed(BedType.Huge);
						}
					}
					else{
						matcher = roomPattern2.matcher(row.child(2).text());
						if(matcher.find()){
							String beds = matcher.group(1).trim();
							if(bathMap.containsKey(beds)){
								unit.setBed(bathMap.get(beds));
							}
							else{
								unit.setBed(BedType.Huge);
							}
						}
						else if(row.child(2).text().contains("studio")){
							unit.setBed(BedType.Studio);
						}
					}
					matcher = sizePattern.matcher(row.child(4).text());
					if(matcher.find()){
						unit.setSize(Integer.parseInt(matcher.group(1).replace(",", "").trim()));
					}
					
					String detail = this.requestor.sendGetRequest(this.baseUrl + detailUrl);
					Document detailDoc = Jsoup.parse(detail);
					for(Element info: detailDoc.getElementsByClass("details_info")){
						if(info.childNodeSize() > 1 && info.child(0).text().equals("Monthly Charges")){
							matcher = taxPattern.matcher(info.text());
							if(matcher.find()){
								String tax = matcher.group(1).replace(",", "").trim();
								if(!tax.isEmpty())
									unit.setTax(Integer.parseInt(tax));
							}
							matcher = ccPattern.matcher(info.text());
							if(matcher.find()){
								String cc = matcher.group(1).replace(",", "").trim();
								if(!cc.isEmpty())
									unit.setMaintenance(Integer.parseInt(cc));
							}
						}
					}
					result.getListing().add(unit);
				}
			}
		}
		
		for(Element tab: document.getElementsByClass("tabset-content")){
			String url = tab.attr("se:url");
			if(!url.isEmpty() && url.contains("show_rentals")){
				html = requestor.sendGetRequest(baseUrl + url);
				document = Jsoup.parse(html);
				break;
			}
		}
		
		//past rentals
		Pattern bedPattern = Pattern.compile("(.*?) bed");
		Elements rentals = document.getElementsByClass("past-rentals-table");
		if(rentals.size() > 0){
			for(Element row: rentals.get(0).select("tbody").get(0).select("tr")){
				RentalListing rental = new RentalListing();
				String date = row.child(0).text().split(" ")[0];
				DateTimeFormatter format = DateTimeFormat.forPattern("MM/dd/yyyy");
				LocalDate rentalDate = format.parseLocalDate(date);
				LocalDate today = LocalDate.now();
				if(Days.daysBetween(rentalDate, today).getDays() < 24*30){
					rental.setDate(rentalDate);
					rental.setName(row.child(1).attr("data-sort-value"));
					Matcher matcher;
					int floor = extracFloor(result, rental.getName());
					if(floor == 0)
						continue;
					rental.setFloor(floor);
					if(!row.child(2).attr("data-sort-value").isEmpty())
						rental.setPrice(Integer.parseInt(row.child(2).attr("data-sort-value")));
					matcher = bedPattern.matcher(row.child(3).text());
					if(matcher.find()){
						String beds = matcher.group(1);
						if(bathMap.containsKey(beds)){
							rental.setBed(bathMap.get(beds));
						}
						else if(row.child(3).text().contains("studio")){
							rental.setBed(BedType.Studio);
						}
					}
					if(!row.child(3).attr("data-sort-value").isEmpty())
						rental.setSize(Integer.parseInt(row.child(5).attr("data-sort-value")));
					result.getRentals().add(rental);
				}				
			}
			buildFormular(result);
		}
		
		
		return result;
	}

	private void buildFormular(BuildingData result) {
		List<RentalListing> data = preProcess(result);
		if(data.size() > 5){
			List<RentalListing> rentals = new ArrayList<RentalListing>();

			double[] y;
			double[][] x;
			int count = 1;
			boolean dateFlag=false, floorFlag=false, bedFlag=false, sizeFlag=true;
			int firstFloor = 0;
			BedType firstBed = BedType.Unknown;
			int firstYear = 100;	
			for(RentalListing rental: data){
				if(firstYear == 100){
					firstYear = Years.yearsBetween(LocalDate.now(), rental.getDate()).getYears();
				}
				else if(dateFlag && Years.yearsBetween(LocalDate.now(), rental.getDate()).getYears() != firstYear){
					dateFlag = true;
					count++;
				}
				if(firstFloor == 0)
					firstFloor = rental.getFloor();
				else if(!floorFlag && rental.getFloor() != firstFloor){
					floorFlag = true;
					count++;
				}
				if(firstBed == BedType.Unknown)
					firstBed = rental.getBed();
				else if(!bedFlag && rental.getBed() != firstBed){
					bedFlag = true;
					count++;
				}
				if(sizeFlag && rental.getSize() == 0){
					sizeFlag = false;
					count--;
				}
			}
			//remove duplication
			for(RentalListing rental: data){
				boolean flag = true;
				for(RentalListing existing: rentals){
					if((!dateFlag || Years.yearsBetween(existing.getDate(), LocalDate.now()).getYears() == Years.yearsBetween(rental.getDate(), LocalDate.now()).getYears()) 
							&& (!floorFlag || existing.getFloor() == rental.getFloor())
							&& (!bedFlag || existing.getBed() == rental.getBed()) && (!sizeFlag || existing.getSize() == rental.getSize())){
						flag = false;
						break;
					}
				}
				if(flag)
					rentals.add(rental);
			}
			if(rentals.size() < 5)
				return;
			
			if(count == 0 || rentals.size() == 0)
				return;
			x = new double[rentals.size()][count];
			y = new double[rentals.size()];
			
			for(int i=0; i<rentals.size(); i++){
				y[i] = rentals.get(i).getPrice();
				int index = 0;
				if(dateFlag)
					x[i][index++] = Years.yearsBetween(LocalDate.now(), rentals.get(i).getDate()).getYears();
				if(floorFlag)
					x[i][index++] = rentals.get(i).getFloor();
				if(bedFlag)
					x[i][index++] = rentals.get(i).getBed().getValue();
				if(sizeFlag)
					x[i][index++] = rentals.get(i).getSize();
			}
			OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();
			regression.newSampleData(y, x);
			double[] beta = regression.estimateRegressionParameters();
			int index = 1;
			double datePar=0, floorPar=0, bedPar=0, sizePar=0;
			if(dateFlag)
				datePar = beta[index++];
			if(floorFlag)
				floorPar = beta[index++];
			if(bedFlag)
				bedPar = beta[index++];
			if(sizeFlag)
				sizePar = beta[index];
			if(floorPar >= 0)
				result.setFormular(new RentFormular(beta[0], datePar, floorPar, bedPar, sizePar));
		}
	}

	private List<RentalListing> preProcess(BuildingData result) {
		List<RentalListing> data = new ArrayList<RentalListing>();
		List<RentalListing> dataWithSize = new ArrayList<RentalListing>();
		for(RentalListing rental: result.getRentals()){
			if(rental.getDate() == null || rental.getBed() == null || rental.getBed() == BedType.Unknown || rental.getFloor() == 0 || rental.getPrice() == 0)
				continue;
			data.add(rental);
			if(rental.getSize() > 0)
				dataWithSize.add(rental);
		}
		if(dataWithSize.size()/data.size() > 0.7 && dataWithSize.size() > 5)
			return dataWithSize;
		else
			return data;
	}

	private int extracFloor(BuildingData result, String rental) {
		Pattern floorPattern = Pattern.compile("^#?(\\d+)");
		Matcher matcher = floorPattern.matcher(rental);
		if(matcher.find()){
			String floor = matcher.group(1);
			if(floor.length() <= 2)
				return Integer.parseInt(floor);
			else if(floor.length() == 3)
				return Integer.parseInt(floor.substring(0, 1));
			else if(floor.length() == 4)
				return Integer.parseInt(floor.substring(0, 2));
		}
		else if(rental.contains("PH")){
			return result.getNumOfFloors();
		}
		else if(rental.contains("GROUND")){
			return 1;
		}
		return 0;
	}


}
