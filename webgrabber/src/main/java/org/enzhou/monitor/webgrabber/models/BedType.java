package org.enzhou.monitor.webgrabber.models;

public enum BedType 
{
	Studio(0), OneBed(1), TwoBed(2), ThreeBed(3), Huge(4), Unknown(5);
	
	private int value;
	private BedType(int value){
		this.value = value;
	}
	
	public int getValue(){
		return value;
	}
}
