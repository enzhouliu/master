package org.enzhou.monitor.webgrabber;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.enzhou.monitor.webgrabber.models.BedType;
import org.enzhou.monitor.webgrabber.models.BuildingData;
import org.enzhou.monitor.webgrabber.models.RentalListing;
import org.enzhou.monitor.webgrabber.models.SaleListing;
import org.enzhou.monitor.webgrabber.parsers.ResponseParser;
import org.enzhou.monitor.webgrabber.parsersImpl.BuildingPageParser;
import org.enzhou.monitor.webgrabber.parsersImpl.SingleBuildingParser;
import org.enzhou.monitor.webgrabber.single.SingleRequest;
import org.enzhou.monitor.webgrabber.singleImpl.DefaultRequest;

public class StreetEasyGrabber {
	private static final Logger log = LogManager.getLogger(StreetEasyGrabber.class.getSimpleName());
	
	private static BufferedWriter out = null;
	
	public static void writeInit(String fileName){
		File file = new File(fileName);
		try {
			out = new BufferedWriter(new FileWriter(file));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void close(){
		if(out != null)
			try {
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	public static synchronized void  writeFile(BuildingData data){
		try {
			System.out.println(data.toString());
			out.write(data.toString());
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static synchronized void  writeFile(String data){
		try {
			out.write(data);
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void runBuilding(String baseUrl, String location){
		BufferedWriter out = null;
		ExecutorService executor = Executors.newFixedThreadPool(10);
		try{
			SingleRequest request = new DefaultRequest();
			String html = request.sendGetRequest(baseUrl + "/condo-buildings/" + location);
			
			BuildingPageParser parser = new BuildingPageParser();
			int maxPage = parser.getMaxPage(html);
			
			writeFile("Name;Cap Ratio;Bedroom Type;Sell Price;Monthly Tax;Mantenance;Avergge Rental;Estimate Rental;Size\n");
			for(int i=1; i<=maxPage; i++){
				String response = request.sendGetRequest(baseUrl + "/condo-buildings/" + location + "?page=" + Integer.toString(i));
				List<String> result = parser.parse(response);
				for(String link: result){
					BuildingRunner runner = new BuildingRunner(baseUrl, link, 1500000);
					executor.execute(runner);
				}
			}
			executor.shutdown();
			while (!executor.isTerminated()) {
				Thread.sleep(1000);
			}
			StreetEasyGrabber.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			if(out != null)
				try {
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
	
	public static void main(String[] args){
		StreetEasyGrabber.writeInit("C:\\tmp\\output.csv");
		new StreetEasyGrabber().runBuilding("http://streeteasy.com", "manhattan");
		
//		ExecutorService executor = Executors.newFixedThreadPool(1);
//		StreetEasyGrabber.writeInit("C:\\tmp\\output.csv");
//		BuildingRunner runner = new BuildingRunner("http://streeteasy.com", "/building/bryant-park-tower", 1500000);
//		executor.execute(runner);
		
//		Pattern pattern = Pattern.compile("^#?(\\d+)");
//		Matcher matcher = pattern.matcher("#17R - 50 Riverside Boulevard");
//		if(matcher.find()){
//			System.out.println(matcher.group(1));
//		}else{
//			System.out.println("asdfasdf");
//		}
	}
}
