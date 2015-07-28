package org.enzhou.monitor.webgrabber.single;

public interface SingleRequest {
	public String sendPostRequest(String url);
	public String sendGetRequest(String url);
}
