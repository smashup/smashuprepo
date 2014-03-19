package com.smashup.rest;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.smashup.util.NielsenAPIUtil;

@Path("/nielson")
public class NielsonAPIService {

	private NielsenAPIUtil nielsonAPIUtil = new NielsenAPIUtil();
	
	@GET
	@Path("/{param}")
	public Response getStoreForSecondaryThemesProductInTheLocation(@PathParam("param") String upc) {
		 
		String output = "upc : " + upc;
		//String upcstr = "0016000275270";
		Double latitude = 29.7904;
		Double longitude = -95.1624;
		 
		String secondaryThemeStr = nielsonAPIUtil.getStoreForSecondaryThemesProductInTheLocation(upc, latitude, longitude);
		
		Gson gson = new Gson();
		String jsonResponse = gson.toJson(secondaryThemeStr);
		System.out.println(jsonResponse);
		
		return Response.status(200).entity(jsonResponse).build();
	}
	
}
