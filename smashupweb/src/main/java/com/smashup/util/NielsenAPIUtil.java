package com.smashup.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jayway.jsonpath.JsonPath;

public class NielsenAPIUtil implements Configuration {
	
	
	public static void main(String[] args) {

		NielsenAPIUtil nielsonAPIUtil = new NielsenAPIUtil();

		// netClientUtil.getResponseNielsonApi(String upc, float latitude, float
		// longitude);
		/*String upc = "0049000059892"; // form example
		Double latitude = 29.7904;
		Double longitude = -95.1624;
		String jsonResponse = nielsonAPIUtil.getStoreForSecondaryThemesProductInTheLocation(upc, latitude, longitude);

		List<String> phoneList = JsonPath.read(jsonResponse, "$..Phone");
		for (String string : phoneList) {
			System.out.println(phoneList);
		}

		//Step1 : Product Serch
		String jsonResponse1 = nielsonAPIUtil.searchProducts("Breakfast%20Cereals");  //Soft%20Drinks
		System.out.println(jsonResponse1);
		
		List<String> upcList = JsonPath.read(jsonResponse1, "$..UPC");
		
		//latitude = 41.887694;
		//longitude = -87.641783;
		
		latitude = 29.7904;
		longitude = -95.1624;
		
		for (String string : upcList) {
			//System.out.println(string);
			
			
			//Step2 : Get latitude longitude from first page
			//upc = "0035000538000";
			//upc = string; //"0035000680679";		
			upc = "0049000059892"; 
			
			String tmp = nielsonAPIUtil.getStoreForSecondaryThemesProductInTheLocation(upc, latitude, longitude);
			System.out.println(tmp);
		}*/
		
		//Get De				
					
		Map<String, Map<String, Integer>> white18_24 = nielsonAPIUtil.getAllStoresBydemography(Configuration.RACE_WHITE, Configuration.AGE_18_24);

		System.out.println(white18_24.get(Configuration.RACE_WHITE+Configuration.AGE_18_24));

		
		System.out.println("Done");
		
	}

	public String getStoreForSecondaryThemesProductInTheLocation(String upc, Double latitude, Double longitude) {

		// URL url = new URL("https://nielsen.api.tibco.com/StoreAvailability/v1?product_id=0016000275270&lat=29.7904&long=-95.1624&apikey=1504-71e5a462-f1d2-48c-a518-0a3bd9ba1916");
		//String tmpURL = String.format("%s%s?product_id=%s&lat=%f&long=%f&apikey=%s",BASEHACKATHLONHOST, "StoreAvailability/v1", upc, latitude,longitude, APIKEY);
		String tmpURL = String.format("%s%s?product_id=%s&lat=%f&long=%f&apikey=%s&distance=%d",BASEHACKATHLONHOST, "StoreAvailability/v1", upc, latitude,longitude, APIKEY,50);
		return getJsonString(tmpURL);

	}

	public String searchProducts(String productName) {

		String tmpURL = String.format("%s%s?search=%s&apikey=%s", BASEHACKATHLONHOST, "Products/v1/", productName, APIKEY);
		return getJsonString(tmpURL);
	}

	// Get all the stores frequented by dominant demography
	/*public String getStoresBydemography(String dominatrace, String dominatageGroup) {
		String tmpURL = String.format("%s%s?agegroup=%s&race=%s&apikey=%s", BASEHACKATHLONHOST, "Stores/v1/demographic/", dominatageGroup, dominatrace, APIKEY);
		return getJsonString(tmpURL);
	}*/
	
	public String getStoresBydemography(String dominatrace, String dominatageGroup, int pageNo, int pageSize){

		 String tmpURL = null;
		 if ( pageNo == -1 || pageSize == -1){
			 tmpURL = String.format( "%s%s?agegroup=%s&race=%s&apikey=%s",BASEHACKATHLONHOST,"Stores/v1/demographic/" ,dominatageGroup, dominatrace, APIKEY );	 
		 }else {
			 tmpURL = String.format( "%s%s?agegroup=%s&race=%s&apikey=%s&pagesize=%d&pageno=%d",BASEHACKATHLONHOST,"Stores/v1/demographic/" ,dominatageGroup, dominatrace, APIKEY, pageSize, pageNo );
		 }

		 return getJsonString(tmpURL);
	 }

	
	// Get all the stores frequented by dominant demography
	
	public Map<String, Map<String, Integer>> getAllStoresBydemography(String dominatrace, String dominatageGroup){
		 String response = getStoresBydemography(dominatrace,dominatageGroup, 1,25);
		 Map<String, Map<String, Integer>> storeByRace = new HashMap<String, Map<String, Integer>>(); 
		 Map<String, Integer> hm  = new HashMap<String, Integer>( );
		 List<String> pageSize = JsonPath.read(response, "$..TotalPages");
		 String size = pageSize.get(0);
		 int pages  =  Integer.parseInt(size);
		 List<String> storename = JsonPath.read(response, "$..StoreName");
		 manageCount(hm,storename);
		 storeByRace.put(dominatrace+dominatageGroup,hm);
		 String r;  
		 for (int page =2; page<= pages  ; page++)
		 {
			  r = getStoresBydemography(dominatrace,dominatageGroup, page,25);
			  storename = JsonPath.read(r, "$..StoreName");
			  manageCount(hm,storename);
			  storeByRace.put(dominatrace+dominatageGroup,hm);

		 };

		 return storeByRace;
	 }
	
	 private void manageCount(Map<String, Integer> hm , List<String> storename){
		 for (String name : storename){
			 if (hm.containsKey(name) ){
				 int count = (Integer)hm.get(name).intValue();
				 hm.put(name, ++count);
			 }else {
				 hm.put(name, Integer.valueOf(1));
			 }

		 }
		 return;
	 }
	
	private String getJsonString(String urlStr) {
		StringBuffer sb = null;
		String output = null;

		try {

			// TODO
			System.getProperties().put("https.proxyHost", HTTPS_PROXYHOST);
			System.getProperties().put("https.proxyPort", HTTPS_PROXYPORT);

			URL url = new URL(urlStr);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			System.out.println("Output from Server .... \n");
			sb = new StringBuffer();
			while ((output = br.readLine()) != null) {
				sb.append(output);
			}

			System.out.println("Done ");

			conn.disconnect();

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	
	
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
