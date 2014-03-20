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

	public static void main(String args[]) {
		SmashupAPIUtil smashupAPIUtil = new SmashupAPIUtil();

		//String category = "CEREAL";
		//String category = "Drinks";
		String category = "Breakfast";

		String jsonResponse = smashupAPIUtil.getSecondaryTheme(category);
		System.out.println("Final String "+jsonResponse);
	}

	public String getSecondaryTheme(String category) {

		// 1. Get the product from google ad word
		List<String> productList = googleAdWordApi.getProductByCategory(category);

		List<String> imagesList = new ArrayList<String>();

		// 2. Get the product and image from Factual
		Map secThemes  = new HashMap();
		Gson gson = new Gson();
		
		for (String product : productList) {
			System.out.println(product);

			String jsonResponse = factualApi.getProduct(product);

			System.out.println(jsonResponse);

			Filter filter = Filter.filter(Criteria.where("image_urls").exists(true));
		
			//Working List all category
			//List<String> tmp = JsonPath.read(jsonResponse, "$..category");
			List<Object> tmp = JsonPath.read(jsonResponse, "$..data[?]", filter); 
			 
			
			for (Object obj : tmp) {
				
				System.out.println(obj.toString());
				String jsonres = obj.toString();
				
				List<String> imageUrlList = JsonPath.read(jsonres, "$..image_urls");
				List<String> categoryList = JsonPath.read(jsonres, "$..category");
				
				if(imageUrlList.toString().contains("TopRight")) {
					System.out.println("Found TopRight ");
					continue ;
				}
				
				System.out.println("Got 0 ******"+imageUrlList.toString().split(",")[0]);
				
				System.out.println(imageUrlList.toString());
				System.out.println(categoryList.toString());
				
				if(secThemes.containsKey(categoryList.toString())){
					continue;
				}else {
					System.out.println("Before Map "+ categoryList.toString() + " "+ imageUrlList.toString().split(",")[0]);
					String catStrStrip = categoryList.toString();//.substring(1, categoryList.toString().length()-1);
					System.out.println("catStrStrip "+catStrStrip);
					
					secThemes.put(catStrStrip,imageUrlList.toString().split(",")[0]);
				}
			} 

		}
				
		return gson.toJson(secThemes);
	}
	
	
}

class Abc{
	String category;
	String image_urls;
	
	public Abc(String category, String image_urls) {
		super();
		this.category = category;
		this.image_urls = image_urls;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getImage_urls() {
		return image_urls;
	}
	public void setImage_urls(String image_urls) {
		this.image_urls = image_urls;
	}
	
}