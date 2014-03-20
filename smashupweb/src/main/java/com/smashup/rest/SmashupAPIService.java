package com.smashup.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

/**
 * SmashupAPI
 */
@Path("/smashupapi")
public class SmashupAPIService {
	
	@GET
	@Path("/{param}")
	public Response getSecondaryTheme(@PathParam("param") String category) {
	
		
		
		return null;
	}	
	
}
