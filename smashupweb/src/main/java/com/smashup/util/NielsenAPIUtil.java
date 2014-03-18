package com.smashup.util;
/*
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;*/
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

//javax.ws.rs.core.Response


public class NielsenAPIUtil implements Configuration {
	
	/*private  Client  nielsenRestclient  = null;
	private WebTarget target;
	public String getStoreForSecondaryThemesProductInTheLocation(String upc, float latitude, float longitude){
		 //initialize REST end point 
		 nielsenRestclient= ClientBuilder.newClient();
		 String t = String.format( "%s%s?product_id=%s&lat=%f&long=%f&apikey=%s",BASEHACKATHLONHOST,"StoreAvailability/v1" ,upc,latitude,longitude, APIKEY );
		 System.out.println("before calling " + t);
		 target = nielsenRestclient.target(t);
		
		// target = nielsenRestclient.target("https://nielsen.api.tibco.com/StoreAvailability/v1?product_id=0016000275270&lat=29.7904&long=-95.1624&apikey=1504-71e5a462-f1d2-4f8c-a518-0a3bd9ba1916");
		 Invocation.Builder invocationBuilder =
	                target.request(MediaType.APPLICATION_JSON);
	        Response response = invocationBuilder.get();
	        String jsonResponse= (String) response.getEntity(); //.readEntity(String.class);
		 System.out.println(jsonResponse);
		 return jsonResponse;
	 }
	 
	 public String searchProducts (String productName) {
		//initialize REST end point 
		 nielsenRestclient= ClientBuilder.newClient();
		 String t = String.format( "%s%s?search=%s&apikey=%s",BASEHACKATHLONHOST,"Products/v1/" ,productName,  APIKEY );
		 //System.out.println("before calling " + t);
		 target = nielsenRestclient.target(t);
		
		// target = nielsenRestclient.target("https://nielsen.api.tibco.com/StoreAvailability/v1?product_id=0016000275270&lat=29.7904&long=-95.1624&apikey=1504-71e5a462-f1d2-4f8c-a518-0a3bd9ba1916");
		 Invocation.Builder invocationBuilder =
	                target.request(MediaType.APPLICATION_JSON);
	        Response response = invocationBuilder.get();
	        String jsonResponse = (String) response.getEntity();//response.readEntity(String.class);
		 System.out.println(jsonResponse);
		 return jsonResponse;
	 }
	 
	 // Get all the stores frequented by dominant demography 
	 public String getStoresBydemography(String dominatrace, String dominatageGroup){
		 nielsenRestclient= ClientBuilder.newClient();
		 String t = String.format( "%s%s?agegroup=%s&race=%s&apikey=%s",BASEHACKATHLONHOST,"Stores/v1/demographic/" ,dominatageGroup, dominatrace, APIKEY );
		 //System.out.println("before calling " + t);
		 target = nielsenRestclient.target(t);
		
		// target = nielsenRestclient.target("https://nielsen.api.tibco.com/StoreAvailability/v1?product_id=0016000275270&lat=29.7904&long=-95.1624&apikey=1504-71e5a462-f1d2-4f8c-a518-0a3bd9ba1916");
		 Invocation.Builder invocationBuilder =
	                target.request(MediaType.APPLICATION_JSON);
	        Response response = invocationBuilder.get();
	        String jsonResponse = (String) response.getEntity();//response.readEntity(String.class);
		 System.out.println(jsonResponse);
		 return jsonResponse;
	 }
	 */
	
	
}
