package com.smashup.model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;


/**
 * The persistent class for the event database table.
 * 
 */
@Entity
@Table(name="event")
@NamedQuery(name="Event.findAll", query="SELECT e FROM Event e")
public class Event implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(unique=true, nullable=false)
	private int eventid;

	@Column(nullable=false)
	private int attendeecount;

	@Column(nullable=false)
	private int eventcompanyid;

	@Column(nullable=false)
	private int eventcosponsorid;

	@Temporal(TemporalType.TIMESTAMP)
	private Date eventdate;

	@Column(nullable=false, length=100)
	private String eventduration;

	@Column(nullable=false)
	private int eventlocationid;

	@Column(nullable=false, length=100)
	private String eventname;

	@Column(nullable=false)
	private int secthemeid;

	@Column(nullable=false)
	private int themeid;

	public Event() {
	}

	public int getEventid() {
		return this.eventid;
	}

	public void setEventid(int eventid) {
		this.eventid = eventid;
	}

	public int getAttendeecount() {
		return this.attendeecount;
	}

	public void setAttendeecount(int attendeecount) {
		this.attendeecount = attendeecount;
	}

	public int getEventcompanyid() {
		return this.eventcompanyid;
	}

	public void setEventcompanyid(int eventcompanyid) {
		this.eventcompanyid = eventcompanyid;
	}

	public int getEventcosponsorid() {
		return this.eventcosponsorid;
	}

	public void setEventcosponsorid(int eventcosponsorid) {
		this.eventcosponsorid = eventcosponsorid;
	}

	public Date getEventdate() {
		return this.eventdate;
	}

	public void setEventdate(Date eventdate) {
		this.eventdate = eventdate;
	}

	public String getEventduration() {
		return this.eventduration;
	}

	public void setEventduration(String eventduration) {
		this.eventduration = eventduration;
	}

	public int getEventlocationid() {
		return this.eventlocationid;
	}

	public void setEventlocationid(int eventlocationid) {
		this.eventlocationid = eventlocationid;
	}

	public String getEventname() {
		return this.eventname;
	}

	public void setEventname(String eventname) {
		this.eventname = eventname;
	}

	public int getSecthemeid() {
		return this.secthemeid;
	}

	public void setSecthemeid(int secthemeid) {
		this.secthemeid = secthemeid;
	}

	public int getThemeid() {
		return this.themeid;
	}

	public void setThemeid(int themeid) {
		this.themeid = themeid;
	}

}