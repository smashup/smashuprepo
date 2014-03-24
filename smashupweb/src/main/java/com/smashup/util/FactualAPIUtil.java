package com.smashup.util;

//import static com.smashup.factual.driver.FactualTest.factual;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.yaml.snakeyaml.Yaml;

import com.factual.driver.Factual;
import com.factual.driver.Query;
import com.factual.driver.ReadResponse;
import com.google.common.io.Closeables;

public class FactualAPIUtil {
	private Factual factual;
	private static File AUTH = null;

	// http://www.factual.com/data/t/products-cpg-nutrition#q=0016000275270

	// http://api.v3.factual.com/t/places?q=starbucks&filters={%22$and%22:[{%22locality%22:%22santa%20monica%22},{%22region%22:%22ca%22},{%22country%22:%22us%22}]}
	public static void main(String[] args) {
		//getCPG();
		FactualAPIUtil factualApi = new FactualAPIUtil();
		//factualApi.getProduct("Skin");
		//factualApi.getProduct("Drink");
		//factualApi.getProduct("Energy Drinks");
		//factualApi.getProduct("Soft Drinks");
		//factualApi.getProduct("Sports Drinks");
		
		factualApi.getProduct("Cereal & Grain");
		factualApi.getProduct("Breakfast Cereals");
		
		//Google Ad Word
		//Underage Drinking & Underage Alcohol Abuse
		///Alcohol Free Drink Mixers
		//Energy Drinks
		//Soft Drinks
		//Sports Drinks
		
	}
	
	public String getProduct(String category) {
		//http://www.factual.com/data/t/products-cpg-nutrition#filters={"$and":[{"category":{"$eq":"SKIN+CARE"}}]}
	
		Factual factual = factual();
	    
		// Search for products containing the word "shampoo"
	    Query query = new Query().only("image_urls", "category").search(category);
	    //query.field("image_urls");
	    ReadResponse resp = factual.fetch("products-cpg", query);
	    //System.out.println(resp.getJson());
	    
	    return resp.getJson();
	    
	}
	
	private static void getCPG() {

		Factual factual = factual();

		/*
		 * Query q = new Query() .only("name", "address") .search("cafe")
		 * .within(new Circle(34.06018, -118.41835, 5000))
		 * .field("postcode").isEqual("90067")
		 * .field("category").isEqual("Food & Beverage") .limit(25);
		 */

		Query q = new Query()
		// .only("name", "address").search("cpg")
				// .search("nutrition")
				// .within(new Circle(34.06018, -118.41835, 5000))
				// .field("postcode").isEqual("72956")
				// .field("category").isEqual("Food & Beverage")
				// .field("factual_id").isEqual("0016000275270")
				// .field("factual_id").isEqual("3c1b0589-f8a0-4d4c-98cc-21878b845f8b")
				.limit(10);

		// Run the query on Factual's "places" table
		ReadResponse resp = factual.fetch("places", q);

		// Print out each record
		for (Map<?, ?> record : resp.getData()) {
			System.out.println(record);
		}

	}
	
	private static Map loadMapFromYaml(File file) {
	    InputStream is = null;
	    try {
	      is = FileUtils.openInputStream(file);
	      return (Map) (new Yaml()).load(is);
	    } catch (IOException e) {
	      throw new RuntimeException(e);
	    } finally {
	      Closeables.closeQuietly(is);
	    }
	  }

	  public static Factual factual() {
		
		//TODO
		System.getProperties().put("http.proxyHost", "172.28.184.18");
		System.getProperties().put("http.proxyPort", "8080");
			
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	    URL resource = classLoader.getResource("factual-auth.yaml");

	    AUTH = new File(resource.getPath());

	    
	    if (!AUTH.exists()) {
	      //fail("You must provide " + AUTH);
	      System.err.println("You must provide " + AUTH);
	      throw new IllegalStateException("Could not find " + AUTH);
	    } else {
	      Map auth = loadMapFromYaml(AUTH);
	      return new Factual((String) auth.get("key"), (String) auth.get("secret"));
	    }
	  }
		
}
