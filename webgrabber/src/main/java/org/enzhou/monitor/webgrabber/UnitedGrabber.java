package org.enzhou.monitor.webgrabber;


import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.enzhou.emailservice.GmailService;
import org.enzhou.emailservice.utils.MobileProvider;
import org.enzhou.monitor.webgrabber.models.RewardType;
import org.enzhou.monitor.webgrabber.parsersImpl.UnitedRewardParser;
import org.enzhou.monitor.webgrabber.singleImpl.UnitedRewardRequest;


public class UnitedGrabber {
	
	private static final Logger log = LogManager.getLogger(UnitedGrabber.class.getSimpleName());

//	private static void setProxy(){
//		System.setProperty("http.proxyHost", "127.0.0.1");
//	    System.setProperty("https.proxyHost", "127.0.0.1");
//	    System.setProperty("http.proxyPort", "8888");
//	    System.setProperty("https.proxyPort", "8888");
//	}
	
	/***
	 * run method will invoke search the united airline reward ticket availability on specific date. (From Beijing to NYC)
	 * @param targetDate the date you want to monitor on which in format of M/d/yyyy
	 */
	public void run(String targetDate){
		//Grabber.setProxy();	
		UnitedRewardRequest request = new UnitedRewardRequest(targetDate);
		
		String response = request.sendPostRequest();
//		System.out.println(response);
		
//		ResponseParser<List<UnitedRewardAvailability>> parser = new UnitedRewardParser();
//		List<UnitedRewardAvailability> result = parser.parse(response);
		UnitedRewardParser parser = new UnitedRewardParser();
		RewardType result;
		try {
			result = parser.parseCalender(response, targetDate);
		
		
//		for(UnitedRewardAvailability avail: result){
//			System.out.println(avail.isAvaiable() + ":" + avail.getMiles());
//		}
//		if(result.get(1).isAvaiable()){
//			GmailService email = new GmailService("enzhouliu@gmail.com", "84212905a~pig");
//			email.sendTextMessage("9177556028", MobileProvider.TMobile, "Flight available now!", "Please go and book the ticket!!");
//		}
			if(result == RewardType.BusinessSaver || result == RewardType.BothSaver){
				GmailService email = new GmailService("enzhouliu@gmail.com", "84212905a~pig");
				email.sendTextMessage("9177556028", MobileProvider.TMobile, "Flight available for " + targetDate + " now!", "Please go and book the ticket!!");
				email.sendEmail("United Reminder", "enzhou.important@gmail.com", "Flight available for " + targetDate + " now!", "Please go and book the ticket!!");
				log.info(String.format("BusinessSaver is available now!!!", targetDate.toString(), result.toString()));
			}
			else{
				log.info(String.format("BusinessSaver is not available, current status for %s is %s", targetDate.toString(), result.toString()));
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws UnsupportedEncodingException{
		UnitedGrabber grabber = new UnitedGrabber();
		grabber.run("1/24/2015");
		
	}

}
