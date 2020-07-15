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

import org.hibernate.annotations.Immutable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vividsolutions.jts.geom.MultiPolygon;

@Entity
@Immutable
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class City {

	private long cityId;
	private String name;
	@JsonIgnore
	private MultiPolygon location;
	@JsonIgnore
	private Region region;
	@JsonIgnore
	private Set<Address> addresses = new HashSet<Address>();
	
	public City() {
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long getCityId() {
		return cityId;
	}

	public void setCityId(long cityId) {
		this.cityId = cityId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(columnDefinition = "geometry(MultiPolygon,4326)")
	public MultiPolygon getLocation() {
		return location;
	}

	public void setLocation(MultiPolygon location) {
		this.location = location;
	}

	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	@JoinColumn(name = "region")
	public Region getRegion() {
		return region;
	}

	public void setRegion(Region region) {
		this.region = region;
	}

	@OneToMany(mappedBy = "city")
	public Set<Address> getAddresses() {
		return addresses;
	}

	public void setAddresses(Set<Address> addresses) {
		this.addresses = addresses;
	}

}
