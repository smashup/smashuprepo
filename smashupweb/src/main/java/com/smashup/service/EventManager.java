package com.smashup.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import com.smashup.model.Event;

@Stateless
@Named
public class EventManager {

	@Inject
	private EntityManager em;
	
	public List<Event> getAllEvent(){
		return (List<Event>) em.createNamedQuery("Event.findAll");	
	}
}
