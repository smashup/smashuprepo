package com.smashup.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Named;

import com.smashup.model.Event;

@Stateless
@Named
public class EventManager {

	//@Inject
	//private EntityManager em = new EntityManager();
	
	public List<Event> getAllEvent(){
		return null;//(List<Event>) em.createNamedQuery("Event.findAll");	
	}
}
