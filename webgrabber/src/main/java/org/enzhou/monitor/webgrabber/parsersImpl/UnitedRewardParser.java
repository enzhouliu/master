package org.enzhou.monitor.webgrabber.parsersImpl;

import java.util.ArrayList;
import java.util.List;

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

}
