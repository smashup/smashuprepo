package com.factual.demo;

import static com.factual.driver.FactualTest.factual;

import com.factual.driver.Factual;
import com.factual.driver.Query;


public class DemoRowFilters {

	//http://api.v3.factual.com/t/places?q=starbucks&filters={%22$and%22:[{%22locality%22:%22santa%20monica%22},{%22region%22:%22ca%22},{%22country%22:%22us%22}]}
	//http://api.v3.factual.com/t/places?filters={%22country%22:%22US%22}&KEY=6FByXmxr9csjj9jZxvNs0O4vUrQsbKBb9o7xjGFQ 
   //http://developer.factual.com/get-started/
	
  public static void main(String[] args) {
    Factual factual = factual();

    // Find places whose name field starts with "Starbucks"
/*    Query q1 = new Query().field("name").beginsWith("Starbucks");
    
    System.out.println(
        factual.fetch("places", q1));

    // Find places with a blank telephone number
    Query q2 = new Query().field("tel").blank();

    System.out.println(
        factual.fetch("places", q2));
    */
    
   // http://api.v3.factual.com/t/places?filters={%22country%22:%22US%22}&KEY=6FByXmxr9csjj9jZxvNs0O4vUrQsbKBb9o7xjGFQ
    Query q3 = new Query().field("country").beginsWith("US");

    System.out.println(factual.fetch("places", q3));
        
  }

  
}
