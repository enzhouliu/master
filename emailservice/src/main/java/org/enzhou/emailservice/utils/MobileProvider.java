package org.enzhou.emailservice.utils;

public enum MobileProvider {
	TMobile("@tmomail.net"),
	Verizon("@vtext.com"),
	Sprint("@@messaging.sprintpcs.com"),
	ATT("@txt.att.net");
	
	private final String email;
	
	private MobileProvider(final String email){
		this.email = email;
	}
	
	@Override
	public String toString(){
		return email;
	}
	
}
