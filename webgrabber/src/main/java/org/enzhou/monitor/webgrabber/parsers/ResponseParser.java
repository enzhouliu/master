package org.enzhou.monitor.webgrabber.parsers;

public interface ResponseParser<T> {
	public T parse(String html);
}
