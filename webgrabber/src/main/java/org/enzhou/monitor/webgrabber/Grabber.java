package org.enzhou.monitor.webgrabber;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.enzhou.monitor.webgrabber.models.UnitedRewardAvailability;
import org.enzhou.monitor.webgrabber.parsers.ResponseParser;
import org.enzhou.monitor.webgrabber.parsersImpl.UnitedRewardParser;
import org.enzhou.monitor.webgrabber.single.SingleRequest;
import org.enzhou.monitor.webgrabber.singleImpl.UnitedRewardRequest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

public class Grabber {
	
	
	
	
	

	public static void main(String[] args) throws UnsupportedEncodingException{
		
		System.setProperty("http.proxyHost", "127.0.0.1");
	    System.setProperty("https.proxyHost", "127.0.0.1");
	    System.setProperty("http.proxyPort", "8888");
	    System.setProperty("https.proxyPort", "8888");
	    
		SingleRequest request = new UnitedRewardRequest();
		String response = request.sendPostRequest();
		
		ResponseParser<List<UnitedRewardAvailability>> parser = new UnitedRewardParser();
		List<UnitedRewardAvailability> result = parser.parse(response);
	}

}
