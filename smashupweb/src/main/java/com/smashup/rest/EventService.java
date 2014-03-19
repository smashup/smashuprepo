package com.smashup.rest;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
 
@Path("/event")
@RequestScoped
public class EventService {
	
	@GET
	@Path("/{param}")
	public Response getEvent(@PathParam("param") String msg) {
 
		String output = "Event say : " + msg;
				
		
		return Response.status(200).entity(output).build();
	}

 
}