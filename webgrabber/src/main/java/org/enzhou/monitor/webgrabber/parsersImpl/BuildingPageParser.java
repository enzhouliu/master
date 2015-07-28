package org.enzhou.monitor.webgrabber.parsersImpl;

import java.util.ArrayList;
import java.util.List;
import org.enzhou.monitor.webgrabber.parsers.ResponseParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class BuildingPageParser implements ResponseParser<List<String>>{

	public int getMaxPage(String html){
		Document doc = Jsoup.parse(html);
		int max = 0;
		Elements paging= doc.getElementsByClass("bottom_pagination");
		if(paging.get(0) != null){
			for(Element link: paging.get(0).select("a")){
				if(link.text().matches("^-?\\d+$")){
					int page = Integer.parseInt(link.text());
					if(max < page){
						max = page;
					}
				}
			}
		}
		return max;
	}
	
	@Override
	public List<String> parse(String html) {
		List<String> result = new ArrayList<String>();
		Document doc = Jsoup.parse(html);
		for(Element item: doc.getElementsByClass("building")){
			Elements links = item.select("a");
			if(links.size() >= 0){
				result.add(links.get(0).attr("href"));
			}
		}
		
		return result;
	}
	
}
