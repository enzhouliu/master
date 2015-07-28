package org.enzhou.monitor.webgrabber.singleImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.enzhou.monitor.webgrabber.single.SingleRequest;

public class DefaultRequest implements SingleRequest{

	private final String USER_AGENT = "Mozilla/5.0";
	
	@Override
	public String sendPostRequest(String url) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String sendGetRequest(String url){
		StringBuffer result = new StringBuffer();
		try{
			URL destination = new URL(url);
			HttpURLConnection conn = (HttpURLConnection)destination.openConnection();
			conn.setRequestMethod("GET");
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			
			String line = "";
			while((line = rd.readLine()) != null){
				result.append(line);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return result.toString();
	}

}
