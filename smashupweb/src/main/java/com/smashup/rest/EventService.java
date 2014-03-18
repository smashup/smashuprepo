package com.smashup.rest;

import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import com.smashup.service.EventManager;
 
@Path("/event")
@RequestScoped
public class EventService {
 	
	@Inject
	private EventManager eventManager;
	
	@Inject
	private Logger log;
	
	@GET
	@Path("/{param}")
	public Response getMsg(@PathParam("param") String msg) {
 
		String output = "Event say : " + msg;
		
		/*
		 List<Event> eventList = eventManager.getAllEvent();
		for (Event event : eventList) {
			log.info("event "+event.getEventid());
		}*/
		
		return Response.status(200).entity(output).build();
	}
	
	public EventManager getEventManager() {
		return eventManager;
	}

	public void setEventManager(EventManager eventManager) {
		this.eventManager = eventManager;
	}	
 
}