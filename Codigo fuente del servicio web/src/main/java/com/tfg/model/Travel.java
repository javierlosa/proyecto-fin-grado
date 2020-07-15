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
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.Point;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Travel {

	private long travelId;
	private Calendar date;
	private Country originCountry;
	private Region originRegion;
	private City originCity;
	private Address originAddress;
	private Country destinationCountry;
	private Region destinationRegion;
	private City destinationCity;
	private Address destinationAddress;
	private double distance;
	private Point originPoint;
	private Point destinationPoint;
	private LineString path;
	private Taxi taxi;

	public Travel() {
	}

	public Travel(Calendar date, Country originCountry, Region originRegion,
			City originCity, Address originAddress, Country destinationCountry,
			Region destinationRegion, City destinationCity,
			Address destinationAddress, Taxi taxi) {
		this.date = date;
		this.originCountry = originCountry;
		this.originRegion = originRegion;
		this.originCity = originCity;
		this.originAddress = originAddress;
		this.destinationCountry = destinationCountry;
		this.destinationRegion = destinationRegion;
		this.destinationCity = destinationCity;
		this.destinationAddress = destinationAddress;
		this.taxi = taxi;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long getTravelId() {
		return travelId;
	}

	public void setTravelId(long travelId) {
		this.travelId = travelId;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Calendar getDate() {
		return date;
	}

	public void setDate(Calendar date) {
		this.date = date;
	}

	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "originCountry")
	public Country getOriginCountry() {
		return originCountry;
	}

	public void setOriginCountry(Country originCountry) {
		this.originCountry = originCountry;
	}

	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "originRegion")
	public Region getOriginRegion() {
		return originRegion;
	}

	public void setOriginRegion(Region originRegion) {
		this.originRegion = originRegion;
	}

	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "originCity")
	public City getOriginCity() {
		return originCity;
	}

	public void setOriginCity(City originCity) {
		this.originCity = originCity;
	}

	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "originAddress")
	public Address getOriginAddress() {
		return originAddress;
	}

	public void setOriginAddress(Address originAddress) {
		this.originAddress = originAddress;
	}

	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "destinationCountry")
	public Country getDestinationCountry() {
		return destinationCountry;
	}

	public void setDestinationCountry(Country destinationCountry) {
		this.destinationCountry = destinationCountry;
	}

	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "destinationRegion")
	public Region getDestinationRegion() {
		return destinationRegion;
	}

	public void setDestinationRegion(Region destinationRegion) {
		this.destinationRegion = destinationRegion;
	}

	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "destinationCity")
	public City getDestinationCity() {
		return destinationCity;
	}

	public void setDestinationCity(City destinationCity) {
		this.destinationCity = destinationCity;
	}

	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "destinationAddress")
	public Address getDestinationAddress() {
		return destinationAddress;
	}

	public void setDestinationAddress(Address destinationAddress) {
		this.destinationAddress = destinationAddress;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	@JsonSerialize(using = GeometrySerializer.class)
	@JsonDeserialize(using = GeometryDeserializer.class)
	@Column(columnDefinition = "geometry(Point,4326)")
	public Point getOriginPoint() {
		return originPoint;
	}

	public void setOriginPoint(Point originPoint) {
		this.originPoint = originPoint;
	}

	@JsonSerialize(using = GeometrySerializer.class)
	@JsonDeserialize(using = GeometryDeserializer.class)
	@Column(columnDefinition = "geometry(Point,4326)")
	public Point getDestinationPoint() {
		return destinationPoint;
	}

	public void setDestinationPoint(Point destinationPoint) {
		this.destinationPoint = destinationPoint;
	}

	@JsonSerialize(using = GeometrySerializer.class)
	@JsonDeserialize(using = GeometryDeserializer.class)
	@Column(columnDefinition = "geometry(LineString,4326)")
	public LineString getPath() {
		return path;
	}

	public void setPath(LineString path) {
		this.path = path;
	}

	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "taxi")
	public Taxi getTaxi() {
		return taxi;
	}

	public void setTaxi(Taxi taxi) {
		this.taxi = taxi;
	}

}
