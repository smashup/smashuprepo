package com.smashup.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.smashup.util.SmashupAPIUtil;

/**
 * SmashupAPI
 */
@Path("/smashupapi")
public class SmashupAPIService {
	
	private SmashupAPIUtil smashupAPIUtil = new SmashupAPIUtil();
	 
	/*@GET
	@Path("/{param}")
	public Response getSecondaryTheme(@PathParam("param") String category) {
	
		String jsonResponse = smashupAPIUtil.getSecondaryTheme(category);
		System.out.println("SecondaryTheme "+jsonResponse);
		
		return Response.status(200).entity(jsonResponse).build();
	}
	
	@GET
	@Path("/getcategory")
	public Response getCategory() {
	
		String jsonResponse = smashupAPIUtil.getCategory();
		System.out.println("category ::"+jsonResponse);
		
		return Response.status(200).entity(jsonResponse).build();
	}*/
	
	

	@GET
	@Path("/category")
	public Response getSecondaryTheme(@QueryParam("param") String category) {
	
		System.out.println("Category in SmashupAPIService -"+category);

		try{
			String jsonResponse = smashupAPIUtil.getSecondaryTheme(category);
			System.out.println("SecondaryTheme "+jsonResponse);
			return Response.status(200).entity(jsonResponse).build();
		}catch(Exception e){
			//TODO if any exception show some default values
			//String test = "[{'category':'DRINK','imageurl':'http://ecx.images-amazon.com/images/I/51XtLqsnIoL._SY300_.jpg'}]";
			String test = null;
			if(category.equalsIgnoreCase("DRINK")){
				test = "[{\"category\":\"DRINK\",\"imageurl\":\"http://ecx.images-amazon.com/images/I/51XtLqsnIoL._SY300_.jpg\"}]";
			} else if(category.equalsIgnoreCase("HEALTH")){
				test = "[{\"category\":\"JUICES\",\"imageurl\":\"http://ecx.images-amazon.com/images/I/41G9RKB3WBL._SL500_AA300_.jpg\"},{\"category\":\"NUTRITIONAL\",\"imageurl\":\"http://ecx.images-amazon.com/images/I/21Twj3Y83jL._SL500_AA300_.jpg\"}]";
			} else if(category.equalsIgnoreCase("BREAKFAST")){
				test = "[{\"category\":\"JUICES\",\"imageurl\":\"http://ecx.images-amazon.com/images/I/41G9RKB3WBL._SL500_AA300_.jpg\"},{\"category\":\"NUTRITIONAL\",\"imageurl\":\"http://ecx.images-amazon.com/images/I/21Twj3Y83jL._SL500_AA300_.jpg\"}]";
			}
			
		}
		
		return null;
	}	
	
	@GET
	@Path("/theme")
	public Response getMainTheme() {
	
		try{		
			String jsonResponse = smashupAPIUtil.getCategory();
			return Response.status(200).entity(jsonResponse).build();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return null;
	}	
	
}
