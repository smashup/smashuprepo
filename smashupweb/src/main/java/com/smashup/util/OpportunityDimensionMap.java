package com.smashup.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jayway.jsonpath.JsonPath;

public class OpportunityDimensionMap {
	Map storeByRace = null;
	
	public OpportunityDimensionMap(boolean refreshData ){
		if (refreshData){
			// Call Nielsen API to load the data 
			storeByRace = new HashMap(); 
			NielsenAPIUtil na = new NielsenAPIUtil();			
			
			String jsonResponse = na.getStoresBydemography(Configuration.RACE_WHITE, Configuration.AGE_18_24);
			List<String> storename=  JsonPath.read(jsonResponse, "$..StoreName");
		}
	}
	
	

}
