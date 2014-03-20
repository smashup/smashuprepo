package com.smashup.rest;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.smashup.util.GoogleAdWordAPIUtil;

@Path("/googleadword")
public class GoogleAPIService {

	private GoogleAdWordAPIUtil googleAdWordApi = new GoogleAdWordAPIUtil();
	
	@GET
	@Path("/{param}")
	public Response getSecondaryTheme(@PathParam("param") String searchStr) {
		 
		String output = "Search string : " + searchStr;
		List<String> secondaryThemeList = googleAdWordApi.getProductByCategory(searchStr);
		
		Gson gson = new Gson();
		String tmp = gson.toJson(secondaryThemeList);
		System.out.println(tmp);
		
		return Response.status(200).entity(tmp).build(); 
	}
	
}
