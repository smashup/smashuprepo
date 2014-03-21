package com.smashup.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.jayway.jsonpath.Criteria;
import com.jayway.jsonpath.Filter;
import com.jayway.jsonpath.JsonPath;

/**
 * SmashupAPIUtil
 * 
 */
public class SmashupAPIUtil {
	private GoogleAdWordAPIUtil googleAdWordApi = new GoogleAdWordAPIUtil();

	private FactualAPIUtil factualApi = new FactualAPIUtil();
	
	private NielsenAPIUtil nielsenAPIUtil = new NielsenAPIUtil();

	public static void main(String args[]) {
		SmashupAPIUtil smashupAPIUtil = new SmashupAPIUtil();

		//String category = "CEREAL";
		//String category = "Drinks";
		String category = "Breakfast";

		//1. Get Secondary Theme
		String jsonResponse = smashupAPIUtil.getSecondaryTheme(category);
		System.out.println("Final String "+jsonResponse);
		
		//2. Get Category
		//String jsonResponse = smashupAPIUtil.getCategory();
		//System.out.println("Final String "+jsonResponse);
		
		//[{"category":"DRINK","imageurl":"http://ecx.images-amazon.com/images/I/51XtLqsnIoL._SY300_.jpg"}, {"category":"WATER","imageurl":"http://ecx.images-amazon.com/images/I/31GodgVT8bL.jpg"}]
		
		/*
		Gson gson = new Gson();
		
		Map mapcat = new HashMap();
		
		Map map = new HashMap();
		map.put("category", "DRINK");
		map.put("imageurl", "http://ecx.images-amazon.com/images/I/51XtLqsnIoL._SY300_.jpg");
		
		mapcat.put("category", map);
		
		map = new HashMap();
		map.put("category", "DRINK");
		map.put("imageurl", "http://ecx.images-amazon.com/images/I/51XtLqsnIoL._SY3001_.jpg");
				
		mapcat.put("category", map);
		
		map = new HashMap();
		map.put("category2", "DRINK");
		map.put("imageurl", "http://ecx.images-amazon.com/images/I/51XtLqsnIoL._SY3002_.jpg");
		
		mapcat.put("category2", map);
						
		List<Map> mapList = new ArrayList<Map>(mapcat.values());
		
		System.out.println(gson.toJson(mapList));*/
		

	}

	public String getCategory() {

		String jsonResponse = nielsenAPIUtil.getCategory();
		
		return jsonResponse;
	}
	
	public String getSecondaryTheme(String category) {

		// 1. Get the product from google ad word
		List<String> productList = googleAdWordApi.getProductByCategory(category);

		List<String> imagesList = new ArrayList<String>();

		// 2. Get the product and image from Factual
		Map secThemes  = new HashMap();
		Gson gson = new Gson();
		
		Map mapcat = new HashMap(); 
		
		for (String product : productList) {
			System.out.println("Fo Product : "+product);

			String jsonResponse = factualApi.getProduct(product);

			Filter filter = Filter.filter(Criteria.where("image_urls").exists(true));
		
			//Working List all category
			List<Object> tmp = JsonPath.read(jsonResponse, "$..data[?]", filter); 
			
			for (Object obj : tmp) {
				
				System.out.println(obj.toString());
				String jsonres = obj.toString();
				
				List<String> imageUrlList = JsonPath.read(jsonres, "$..image_urls");
				List<String> categoryList = JsonPath.read(jsonres, "$..category");
				
				/*if(imageUrlList.toString().contains("TopRight")) {
					System.out.println("Found TopRight ");
					continue ;
				}*/
				
				String catStrStrip = categoryList.toString();
				
				//Create a map
				Map map = new HashMap();
				map.put("category", catStrStrip);
				map.put("imageurl", imageUrlList.toString().split(",")[0]);
														
				//Add In the List
				mapcat.put(catStrStrip, map);	
		
			}
		}
				
		return gson.toJson(new ArrayList(mapcat.values()));
	}
	
	
}
