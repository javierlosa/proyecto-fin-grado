package com.tfg.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.scaleset.geo.geojson.GeometryDeserializer;
import com.scaleset.geo.geojson.GeometrySerializer;
import com.vividsolutions.jts.geom.Point;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Stand {

	private long standId;
	private String name;
	private Point location;
	@JsonIgnore
	private Address address;
	@JsonIgnore
	private Set<Entry> entries = new HashSet<Entry>();
	
	public Stand() {
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long getStandId() {
		return standId;
	}

	public void setStandId(long standId) {
		this.standId = standId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	@JoinColumn(name = "address")
	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	@OneToMany(mappedBy = "stand")
	public Set<Entry> getEntries() {
		return entries;
	}

	public void setEntries(Set<Entry> entries) {
		this.entries = entries;
	}

}
