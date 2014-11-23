package org.enzhou.emailservice;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Hello world!
 *
 */
public class GmailService 
{
	private final String username;
	private final String password;
	private final Session session;
	
	public GmailService(final String username, final String password){
		this.username = username;
		this.password = password;
		
		Properties props = new Properties();
    	props.put("mail.smtp.auth", "true");
    	props.put("mail.smtp.starttls.enable", "true");
    	props.put("mail.smtp.host", "smtp.gmail.com");
    	props.put("mail.smtp.port", "587");
    	
    	session = Session.getDefaultInstance(props, new Authenticator(){

			protected PasswordAuthentication getPasswordAuthentication(){
    			
    			return new PasswordAuthentication(username, password);
    		}
    	});
	}
	
	
	public boolean sendEmail(String aliasFrom, String to, String subject, String content){
		Message message = new MimeMessage(session);
		try {
			message.setFrom(new InternetAddress(username, "Enzhou Monitor"));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
			message.setSubject(subject);
			message.setText(content);
			Transport.send(message);
		} catch (UnsupportedEncodingException|MessagingException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	
	
    public static void main( String[] args ) throws AddressException, MessagingException, UnsupportedEncodingException
    {
    	GmailService email = new GmailService("enzhouliu@gmail.com", "84212905a~pig");
    	email.sendEmail("Enzhou Liu", "9177556028@tmomail.net", "reminder", "time to go!!");
    	email.sendEmail("Enzhou Liu", "9177556028@tmomail.net", "reminder1", "time to go!!");
    	email.sendEmail("Enzhou Liu", "9177556028@tmomail.net", "reminder2", "time to go!!");
    }
}
