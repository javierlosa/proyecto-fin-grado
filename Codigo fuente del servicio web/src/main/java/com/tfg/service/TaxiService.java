package com.tfg.service;

import java.util.List;

import com.tfg.dao.util.InstanceNotFoundException;
import com.tfg.model.Address;
import com.tfg.model.City;
import com.tfg.model.Country;
import com.tfg.model.FutureTravel;
import com.tfg.model.Region;
import com.tfg.model.Stand;
import com.tfg.model.TaxiState;
import com.tfg.model.Taxi;
import com.tfg.model.Travel;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.Point;

public interface TaxiService {

	public Taxi createTaxi();

	public Taxi login(Long taxiId, String password)
			throws InstanceNotFoundException, Exception;

	public void updateActualStateTaxi(Long taxiId, TaxiState actualState)
			throws InstanceNotFoundException;

	public void updateFutureStateTaxi(Long taxiId, TaxiState futureState)
			throws InstanceNotFoundException;

	public List<Taxi> getTaxis();

	public Taxi getTaxi(Long taxiId) throws InstanceNotFoundException;

	public void updatePositionTaxi(Long taxiId, Point position)
			throws InstanceNotFoundException;

	public List<Country> getCountries();

	public List<Region> getRegions(Long countryId)
			throws InstanceNotFoundException;

	public List<City> getCities(Long regionId) throws InstanceNotFoundException;

	public List<Address> getAddresses(Long cityId)
			throws InstanceNotFoundException;

	public List<Stand> getNearestStandsByTaxi(Long taxiId)
			throws InstanceNotFoundException;

	public Long getNumTaxisStand(Long standId) throws InstanceNotFoundException;

	public Long takeClientTo(Long taxiId, Long countryId, Long regionId,
			Long cityId, Long addressId) throws InstanceNotFoundException, Exception;

	public void destinationReached(Long travelId, double distance,
			Point originPoint, Point destinationPoint, LineString mlsPath)
			throws InstanceNotFoundException;

	public List<Travel> getTravels(Long taxiId)
			throws InstanceNotFoundException;

	public List<FutureTravel> getFutureTravels(Long taxiId)
			throws InstanceNotFoundException;

	public void createFutureTravel(Long taxiId, Long originCountryId,
			Long originRegionId, Long originCityId, Long originAddressId,
			Long destinationCountryId, Long destinationRegionId,
			Long destinationCityId, Long destinationAddressId, String date)
			throws InstanceNotFoundException, Exception;

	public void cancelFutureTravel(Long futureTravelId)
			throws InstanceNotFoundException;

	public void cancelTravel(Long travelId) throws InstanceNotFoundException;

	public Long takeClientToFromFutureTravel(Long taxiId, Long originCountryId,
			Long originRegionId, Long originCityId, Long originAddressId,
			Long destinationCountryId, Long destinationRegionId,
			Long destinationCityId, Long destinationAddressId)
			throws InstanceNotFoundException;

	public void locateTaxi(Long taxiId) throws InstanceNotFoundException;

	public Boolean changePassword(Long taxiId, String password) throws InstanceNotFoundException;
	
	public Boolean changeCity(Long taxiId, Long cityId) throws InstanceNotFoundException;

}
