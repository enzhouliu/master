package org.enzhou.monitor.webgrabber.singleImpl;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.enzhou.monitor.webgrabber.UnitedGrabber;
import org.enzhou.monitor.webgrabber.single.SingleRequest;

public class UnitedRewardRequest implements SingleRequest{

	private static final Logger log = LogManager.getLogger(UnitedGrabber.class.getSimpleName());
	private final String host = "http://www.united.com";
	private String targetDate = null;
	
	public UnitedRewardRequest(String targetDate) {
		this.targetDate = targetDate;
	}


	private String loadRequestContent(String name) throws UnsupportedEncodingException, ParseException{
		InputStream in = getClass().getResourceAsStream("/"+name); 
		StringBuffer result = new StringBuffer();
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(in))){
			String line = null;
			while((line = reader.readLine()) != null){
				if(line.startsWith("//"))
					continue;
				int index = line.indexOf("=");
				String key = line.substring(0, index);
				String value = line.substring(index + 1, line.length());
				if(key.equals("ctl00$ContentInfo$Booking1$DepDateTime$Depdate$txtDptDate")){
					SimpleDateFormat df = new SimpleDateFormat("M/d/yyyy");
					Date departure = df.parse(targetDate);
					Calendar c = Calendar.getInstance();
					c.setTime(departure);
					c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
					value = df.format(c.getTime());
				}
				result.append(URLEncoder.encode(key, "UTF-8")).append("=").append(URLEncoder.encode(value, "UTF-8")).append("&");
			}
		}
		catch(IOException e){
			e.printStackTrace();
		}
		return result.toString().substring(0, result.toString().length()-1);
	}
	
	@Override
	public String sendPostRequest(String url){
		HttpURLConnection connection = null;
		try {
			String content = loadRequestContent("UnitedRewardContent");
			URL uaURL = new URL(host + url);
			connection = (HttpURLConnection)uaURL.openConnection();
			connection.setInstanceFollowRedirects(false);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("User-Agent", " Mozilla/5.0 (Windows NT 6.1; WOW64; rv:33.0) Gecko/20100101 Firefox/33.0");
			connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
			connection.setRequestProperty("Accept-Encoding", "gzip, deflate");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			connection.setRequestProperty("Content-Length", Integer.toString(content.getBytes().length));
			//connection.setRequestProperty("Cookie", cookie);
			connection.setRequestProperty("Connection", "keep-alive");
			
			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);
			
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			wr.writeBytes(content);
			wr.flush();
			wr.close();
			
			if (connection.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP   
		            || connection.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM){
//				Map<String, List<String>> header = connection.getHeaderFields();
//				for(String key: header.keySet())
//					System.out.println(key + ":" + header.get(key));
				return sendRedirectRequest(connection);
				
			}
			
			InputStream is = connection.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			String line;
			StringBuffer response = new StringBuffer();
			while((line = reader.readLine()) != null){
				response.append(line).append("\r");
			}
			reader.close();
			return response.toString();
		} catch (IOException| ParseException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage());
			e.printStackTrace();
		}
		return "";
	}
	
	private String sendRedirectRequest(HttpURLConnection conn) {
		// TODO Auto-generated method stub
		try {
			URL redirectURL = new URL(host + conn.getHeaderField("Location"));
		
			HttpURLConnection connection = (HttpURLConnection)redirectURL.openConnection();
			connection.setInstanceFollowRedirects(false);
			connection.setRequestMethod("GET");
			connection.setRequestProperty("User-Agent", " Mozilla/5.0 (Windows NT 6.1; WOW64; rv:33.0) Gecko/20100101 Firefox/33.0");
			connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
			connection.setRequestProperty("Accept-Encoding", "gzip, deflate");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			//connection.setRequestProperty("Content-Length", Integer.toString(postContentHeader.getBytes().length));
			List<String> cookies = conn.getHeaderFields().get("Set-Cookie");
			StringBuffer cookieBuffer = new StringBuffer();
			for(String cookie: cookies){
				cookieBuffer.append(cookie.split(";")[0]).append(";");
			}
			String cookieString = cookieBuffer.toString();
			
			connection.setRequestProperty("Cookie", cookieString.substring(0, cookieString.length()-1));
			connection.setRequestProperty("Connection", "keep-alive");
			
			if (connection.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP   
		            || connection.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM){
//				Map<String, List<String>> header = connection.getHeaderFields();
//				for(String key: header.keySet())
//					System.out.println(key + ":" + header.get(key));
				return sendRedirectRequest(connection);
				
			}
			InputStream is = connection.getInputStream();
			if(connection.getHeaderField("Content-Encoding").equalsIgnoreCase("gzip")){
				is = decompressStream(connection.getInputStream());
			}
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			String line;
			StringBuffer response = new StringBuffer();
			while((line = reader.readLine()) != null){
				response.append(line).append("\r");
			}
			reader.close();
			return response.toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
	private InputStream decompressStream(InputStream input) throws IOException {
	     PushbackInputStream pb = new PushbackInputStream( input, 2 ); //we need a pushbackstream to look ahead
	     byte [] signature = new byte[2];
	     pb.read( signature ); //read the signature
	     pb.unread( signature ); //push back the signature to the stream
	     if( signature[ 0 ] == (byte) 0x1f && signature[ 1 ] == (byte) 0x8b ) //check if matches standard gzip magic number
	       return new GZIPInputStream( pb );
	     else 
	       return pb;
	}

	@Override
	public String sendGetRequest(String url) {
		// TODO Auto-generated method stub
		return null;
	}

}
