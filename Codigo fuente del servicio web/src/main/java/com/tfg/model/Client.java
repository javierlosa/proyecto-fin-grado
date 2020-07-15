package com.tfg.model;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.scaleset.geo.geojson.GeometryDeserializer;
import com.scaleset.geo.geojson.GeometrySerializer;
import com.vividsolutions.jts.geom.Point;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Client {

	private long clientId;
	private Country originCountry;
	private Region originRegion;
	private City originCity;
	private Address originAddress;
	private Calendar entry;
	private Point location;
	private ClientState clientState;

	public Client() {
	}
	
	public Client(Country originCountry, Region originRegion, City originCity,
			Address originAddress, Calendar entry, Point location) {
		this.originCountry = originCountry;
		this.originRegion = originRegion;
		this.originCity = originCity;
		this.originAddress = originAddress;
		this.entry = entry;
		this.location = location;
		this.clientState = ClientState.WAITING;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long getClientId() {
		return clientId;
	}

	public void setClientId(long clientId) {
		this.clientId = clientId;
	}

	@ManyToOne(optional=false, fetch=FetchType.EAGER)
	@JoinColumn(name = "originCountry")
	public Country getOriginCountry() {
		return originCountry;
	}

	public void setOriginCountry(Country originCountry) {
		this.originCountry = originCountry;
	}

	@ManyToOne(optional=false, fetch=FetchType.EAGER)
	@JoinColumn(name = "originRegion")
	public Region getOriginRegion() {
		return originRegion;
	}

	public void setOriginRegion(Region originRegion) {
		this.originRegion = originRegion;
	}

	@ManyToOne(optional=false, fetch=FetchType.EAGER)
	@JoinColumn(name = "originCity")
	public City getOriginCity() {
		return originCity;
	}

	public void setOriginCity(City originCity) {
		this.originCity = originCity;
	}

	@ManyToOne(optional=false, fetch=FetchType.EAGER)
	@JoinColumn(name = "originAddress")
	public Address getOriginAddress() {
		return originAddress;
	}

	public void setOriginAddress(Address originAddress) {
		this.originAddress = originAddress;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Calendar getEntry() {
		return entry;
	}

	public void setEntry(Calendar entry) {
		this.entry = entry;
	}

	@JsonSerialize(using = GeometrySerializer.class)
	@JsonDeserialize(using = GeometryDeserializer.class)
	@Column(columnDefinition = "geometry(Point,4326)")
	public Point getLocation() {
		return location;
	}

	public void setLocation(Point location) {
		this.location = location;
	}

	public ClientState getClientState() {
		return clientState;
	}

	public void setClientState(ClientState clientState) {
		this.clientState = clientState;
	}

}
