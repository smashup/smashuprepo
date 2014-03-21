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

		// String category = "CEREAL";
		// String category = "Drinks";
		String category = "Breakfast";

		// 1. Get Secondary Theme
		String jsonResponse = smashupAPIUtil.getSecondaryTheme(category);
		System.out.println("Final String " + jsonResponse);

		// 2. Get Category
		// String jsonResponse = smashupAPIUtil.getCategory();
		// System.out.println("Final String "+jsonResponse);

		// [{"category":"DRINK","imageurl":"http://ecx.images-amazon.com/images/I/51XtLqsnIoL._SY300_.jpg"},
		// {"category":"WATER","imageurl":"http://ecx.images-amazon.com/images/I/31GodgVT8bL.jpg"}]

		
		/* Gson gson = new Gson();
		 
		 Map mapcat = new HashMap();
		 
		  
		  Map map = new HashMap(); map.put("category", "DRINK1");
		  map.put("imageurl","http://ecx.images-amazon.com/images/I/51XtLqsnIoL._SY300_.jpg");
		  
		  mapcat.put("category", map);
		  
		  map = new HashMap(); map.put("category", "DRINK2");
		  map.put("imageurl",
		  "http://ecx.images-amazon.com/images/I/51XtLqsnIoL._SY3001_.jpg");
		  
		  mapcat.put("category", map);
		  
		  map = new HashMap(); map.put("category2", "DRINK3");
		  map.put("imageurl","http://ecx.images-amazon.com/images/I/51XtLqsnIoL._SY3002_.jpg");
		  
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
		Map secThemes = new HashMap();
		Gson gson = new Gson();

		Map mapcat = new HashMap();
		
		try {

			
			for (String product : productList) {
				System.out.println("Fo Product : " + product);

				String jsonResponse = factualApi.getProduct(product);

				Filter filter = Filter.filter(Criteria.where("image_urls")
						.exists(true));

				// Working List all category
				List<Object> tmp = JsonPath.read(jsonResponse, "$..data[?]",
						filter);

				for (Object obj : tmp) {

					System.out.println(obj.toString());
					String jsonres = obj.toString();

					List<String> imageUrlList = JsonPath.read(jsonres,"$..image_urls");
					List<String> categoryList = JsonPath.read(jsonres, "$..category");

					String imageUrlStr = imageUrlList.toString();					
					String categoryStr = categoryList.toString();
					
					/*if(imageUrlStr.contains("TopRight")) {
						System.out.println("Found TopRight ");
						continue ; 
					}*/
					
					System.out.println(imageUrlStr);
					System.out.println(categoryStr);
					if(imageUrlStr.contains("SL500_AA300_.jpg")){
						System.out.println("Found");
					}
					
					//get fisrt image
					String imageUrlFinal = null;
					String categoryFinal = null;
					/*if(imageUrlStr.split("\",\"").length > 0){
						//Remove the brackets
						imageUrlFinal = imageUrlStr.substring(3,imageUrlStr.length()-3);
					} else {
						imageUrlFinal = imageUrlStr.substring(3,imageUrlStr.length()-3);
					}*/
					imageUrlFinal = imageUrlStr.substring(3,imageUrlStr.length()-3);
					
					imageUrlFinal = imageUrlFinal.split("\",\"")[0];	
					categoryFinal = categoryStr.substring(2,categoryStr.length()-2);	
											
								
					// Create a map
					Map map = new HashMap();
					map.put("category", categoryFinal);
					map.put("imageurl", imageUrlFinal);

					// Add In the List
					mapcat.put(categoryFinal, map);

				}
			}
		} catch (Exception e) {
			System.out.println(e);
		} finally {

		}

		return gson.toJson(new ArrayList(mapcat.values()));
	}

}
