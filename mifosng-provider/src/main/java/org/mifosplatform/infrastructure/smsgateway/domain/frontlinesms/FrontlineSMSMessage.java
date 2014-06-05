package org.mifosplatform.infrastructure.smsgateway.domain.frontlinesms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Object to describe a Frontline SMS object. 
 * Includes a sendPostRequest() method to send the SMS message
 * @author antoniocarella
 */

public class FrontlineSMSMessage {

	private final static Logger logger = LoggerFactory.getLogger(FrontlineSMSMessage.class);
	private URL obj;
	private HttpURLConnection connection;
	private String url;
	final String secret;
	final String message;
	final String recipientPhoneNo;
	final String type = "application/json"; 
	final ArrayList<HashMap<String, String>> recipients = new ArrayList<HashMap<String, String>>();

	public FrontlineSMSMessage(String secret, String message, String recipientPhoneNo, String url) {
		this.secret = secret;
		this.message = message;
		this.url = url;
		this.recipientPhoneNo = recipientPhoneNo;
		
		final HashMap<String,String> recipient = new HashMap<String, String>();
		// In FrontlineSMS's API an a phone number is of type address
		recipient.put("type","address");
		recipient.put("value",recipientPhoneNo);
		recipients.add(recipient);
	}

	public void sendPostRequest() throws IOException{

		JsonObject recipient = new JsonObject();
		recipient.addProperty("type", "address");
		recipient.addProperty("value", recipientPhoneNo);
		
		JsonArray recipients = new JsonArray();
		recipients.add(recipient);
		
		JsonObject requestBody = new JsonObject();
		requestBody.addProperty("secret", secret);
		requestBody.addProperty("message", message);
		requestBody.add("recipients", recipients);
		
		final String rawData = requestBody.toString();

		try {
			obj = new URL(url);
			
			connection = (HttpURLConnection)obj.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty( "Content-Type", type );
			connection.setRequestProperty( "Content-Length", String.valueOf(rawData.length()));
			connection.setDoOutput(true);

			OutputStream os = connection.getOutputStream();
			os.write(rawData.getBytes());
 
			int responseCode = connection.getResponseCode();
			String responseMessage = connection.getResponseMessage();
			logger.info("\nHttpURLConnection results : ");
			logger.info("Sending 'POST' request to URL : " + url);
			logger.info("Raw data: " + rawData);
			logger.info("Response Code : " + responseCode);
			logger.info("Response Message : " + responseMessage);

			BufferedReader in = new BufferedReader(
					new InputStreamReader(connection.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			logger.info(response.toString());


		} catch (MalformedURLException e) {

			throw new RuntimeException(e);

		} 

	}

}
