package com.smashup.util;

import static com.smashup.factual.driver.FactualTest.factual;

import java.util.Map;

import com.factual.driver.Factual;
import com.factual.driver.Query;
import com.factual.driver.ReadResponse;

public class FactualAPIUtil {

	// http://www.factual.com/data/t/products-cpg-nutrition#q=0016000275270

	// http://api.v3.factual.com/t/places?q=starbucks&filters={%22$and%22:[{%22locality%22:%22santa%20monica%22},{%22region%22:%22ca%22},{%22country%22:%22us%22}]}
	public static void main(String[] args) {
		//getCPG();
		FactualAPIUtil factualApi = new FactualAPIUtil();
		factualApi.getProduct("Skin");
	}
	
	private void getProduct(String category) {
			//http://www.factual.com/data/t/products-cpg-nutrition#filters={"$and":[{"category":{"$eq":"SKIN+CARE"}}]}
	
		Factual factual = factual();

	    System.out.println(
	        factual.fetch("places", new Query().limit(3)));
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
		// .only("name", "address")
				.search("cpg")
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
	
		
}
