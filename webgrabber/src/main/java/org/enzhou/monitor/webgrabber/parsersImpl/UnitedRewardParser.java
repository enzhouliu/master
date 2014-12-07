package org.enzhou.monitor.webgrabber.parsersImpl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.enzhou.monitor.webgrabber.models.RewardType;
import org.enzhou.monitor.webgrabber.models.UnitedRewardAvailability;
import org.enzhou.monitor.webgrabber.parsers.ResponseParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class UnitedRewardParser implements ResponseParser<List<UnitedRewardAvailability>>{

	@Override
	public List<UnitedRewardAvailability> parse(String html) {
		List<UnitedRewardAvailability> result = new ArrayList<>();
		Document doc = Jsoup.parse(html);
		//System.out.println(doc.toString());
		Elements elements = doc.getElementsByClass("rewardResults");
		if(elements.size() > 0){
			Element table = elements.get(0);
			Elements rewardPrices = table.getElementsByClass("tdRewardPrice");
			
			
			for(Element column: rewardPrices){
				UnitedRewardAvailability avail = new UnitedRewardAvailability();
				Elements miles = column.getElementsByClass("divMileage");
				if(miles.size() > 0){
					avail.setAvaiable(!miles.get(0).text().isEmpty());
					avail.setMiles(miles.get(0).text());
					result.add(avail);
				}
			}
		}
		return result;
	}
	
	public RewardType parseCalender(String html, String date) throws ParseException{
		SimpleDateFormat dfinput = new SimpleDateFormat("MM/dd/yyy");
		SimpleDateFormat dfoutput = new SimpleDateFormat("MMMM/dd/yyyy");
		Date dateInfo = dfinput.parse(date);
		String[] TargetDate = dfoutput.format(dateInfo).split("/");
		String targetMonth = TargetDate[0];
		String targetDay = TargetDate[1];
		Document doc = Jsoup.parse(html);
		Elements elements = doc.getElementsByClass("Calendar");
		for(Element calendar: elements){
			Element tbody = calendar.child(0);
			Element tr = tbody.child(0);
			Element th = tr.child(0);
			if(th.text().contains(targetMonth)){
				Elements trs = tbody.children(); 
				for(int i = 3; i< trs.size(); i++){
					Element row = trs.get(i);
					for(Element td: row.children()){
						String day = td.child(0).text();
						if(day.equals(targetDay)){
							String color = td.attr("style");
							switch(color){
								case "background:#9cf;": return RewardType.BusinessSaver;
								case "background:#fff;": return RewardType.Standard;
								case "background:#ff9;": return RewardType.EconomySaver;
								case "background:#9f9;": return RewardType.BothSaver;
							}
						}
					}
				}
			}
		}
		return null;
	}
}
