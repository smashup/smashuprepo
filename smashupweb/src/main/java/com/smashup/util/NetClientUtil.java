package com.smashup.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.jayway.jsonpath.JsonPath;

public class NetClientUtil implements Configuration {

	public static void main(String[] args) {

		NetClientUtil netClientUtil = new NetClientUtil();

		// netClientUtil.getResponseNielsonApi(String upc, float latitude, float
		// longitude);
		String upc = "0016000275270";
		Double latitude = 29.7904;
		Double longitude = -95.1624;
		String jsonResponse = netClientUtil.getStoreForSecondaryThemesProductInTheLocation(upc, latitude, longitude);

		List<String> phoneList = JsonPath.read(jsonResponse, "$..Phone");
		for (String string : phoneList) {
			System.out.println(phoneList);
		}

	}

	public String getStoreForSecondaryThemesProductInTheLocation(String upc, Double latitude, Double longitude) {

		// URL url = new URL("https://nielsen.api.tibco.com/StoreAvailability/v1?product_id=0016000275270&lat=29.7904&long=-95.1624&apikey=1504-71e5a462-f1d2-48c-a518-0a3bd9ba1916");
		String tmpURL = String.format("%s%s?product_id=%s&lat=%f&long=%f&apikey=%s",BASEHACKATHLONHOST, "StoreAvailability/v1", upc, latitude,longitude, APIKEY);
		return getJsonString(tmpURL);

	}

	public String searchProducts(String productName) {

		String tmpURL = String.format("%s%s?search=%s&apikey=%s", BASEHACKATHLONHOST, "Products/v1/", productName, APIKEY);
		return getJsonString(tmpURL);
	}

	// Get all the stores frequented by dominant demography
	public String getStoresBydemography(String dominatrace,
			String dominatageGroup) {
		String tmpURL = String.format("%s%s?agegroup=%s&race=%s&apikey=%s", BASEHACKATHLONHOST, "Stores/v1/demographic/", dominatageGroup, dominatrace, APIKEY);
		return getJsonString(tmpURL);
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

}
