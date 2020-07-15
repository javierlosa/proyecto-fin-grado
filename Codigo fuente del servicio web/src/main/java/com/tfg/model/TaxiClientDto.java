package com.tfg.model;

public class TaxiClientDto {

	private long taxiId;
	private String token;
	private long clientId;
	private String country;
	private String region;
	private String city;
	private String address;
	
	public TaxiClientDto(long taxiId, String token, long clientId,
			String country, String region, String city, String address) {
		this.taxiId = taxiId;
		this.token = token;
		this.clientId = clientId;
		this.country = country;
		this.region = region;
		this.city = city;
		this.address = address;
	}

	public long getTaxiId() {
		return taxiId;
	}

	public void setTaxiId(long taxiId) {
		this.taxiId = taxiId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public long getClientId() {
		return clientId;
	}

	public void setClientId(long clientId) {
		this.clientId = clientId;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

}
