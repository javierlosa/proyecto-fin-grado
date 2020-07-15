package com.tfg.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tfg.dao.util.InstanceNotFoundException;
import com.tfg.model.Address;
import com.tfg.model.City;
import com.tfg.model.Client;
import com.tfg.model.Country;
import com.tfg.model.FutureTravel;
import com.tfg.model.Region;
import com.tfg.model.Stand;
import com.tfg.model.Taxi;
import com.tfg.model.TaxiClientDto;
import com.tfg.model.TaxiState;
import com.tfg.model.Travel;
import com.tfg.service.CentralService;
import com.tfg.service.TaxiService;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

@CrossOrigin
@RestController
public class Controller {

	private TaxiService taxiService;
	private CentralService centralService;
	GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(),
			4326);

	Logger LOG = LoggerFactory.getLogger(Controller.class);

	@Autowired(required = true)
	@Qualifier(value = "taxiService")
	public void setTaxiService(TaxiService taxiService) {
		this.taxiService = taxiService;
	}

	@Autowired(required = true)
	@Qualifier(value = "centralService")
	public void setCentralService(CentralService centralService) {
		this.centralService = centralService;
	}

	@RequestMapping(value = "/taxis", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody List<Taxi> getTaxis() {
		return this.taxiService.getTaxis();
	}

	@RequestMapping(value = "/taxis/{taxiId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody Taxi getTaxi(@PathVariable("taxiId") Long taxiId)
			throws InstanceNotFoundException {
		return this.taxiService.getTaxi(taxiId);
	}

	@RequestMapping(value = "/taxis/operating", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody List<Taxi> getOperatingTaxis() {
		return this.centralService.getOperatingTaxis();
	}

	@RequestMapping(value = "/taxis/available", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody List<Taxi> getAvailableTaxis() {
		return this.centralService.getAvailableTaxis();
	}

	@RequestMapping(value = "/clients", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody List<Client> getClients() {
		return this.centralService.getClients();
	}

	@RequestMapping(value = "/clients/waiting", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody List<Client> getClientsWaiting() {
		return this.centralService.getClientsWaiting();
	}

	@RequestMapping(value = "/taxis", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public Taxi createTaxi() {
		return this.taxiService.createTaxi();
	}

	@RequestMapping(value = "/clients", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public void createClient(@RequestBody double[] location)
			throws InstanceNotFoundException {
		Point position = geometryFactory.createPoint(new Coordinate(
				location[1], location[0]));
		long addressId = this.centralService.getNearestAddress(position);
		long cityId = this.centralService.getCityFromAddress(addressId);
		long regionId = this.centralService.getRegionFromCity(cityId);
		long countryId = this.centralService.getCountryFromRegion(regionId);
		this.centralService.createClient(countryId, regionId, cityId,
				addressId, position);
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public Taxi login(@RequestBody Taxi taxi) throws InstanceNotFoundException, Exception {
		return this.taxiService.login(taxi.getTaxiId(), taxi.getPassword());
	}

	@RequestMapping(value = "/countries", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody List<Country> getCountries() {
		return this.taxiService.getCountries();
	}

	@RequestMapping(value = "/countries/{countryId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody List<Region> getRegions(
			@PathVariable("countryId") Long countryId)
			throws InstanceNotFoundException {
		return this.taxiService.getRegions(countryId);
	}

	@RequestMapping(value = "/regions/{regionId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody List<City> getCities(
			@PathVariable("regionId") Long regionId)
			throws InstanceNotFoundException {
		return this.taxiService.getCities(regionId);
	}

	@RequestMapping(value = "/cities/{cityId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody List<Address> getAddresses(
			@PathVariable("cityId") Long cityId)
			throws InstanceNotFoundException {
		return this.taxiService.getAddresses(cityId);
	}

	@RequestMapping(value = "/taxis/{taxiId}/actualstate/{state}", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
	public void updateActualStateTaxi(@PathVariable("taxiId") Long taxiId,
			@PathVariable("state") String state)
			throws InstanceNotFoundException {
		this.taxiService.updateActualStateTaxi(taxiId,
				TaxiState.valueOf(state.toUpperCase()));
	}

	@RequestMapping(value = "/taxis/{taxiId}/futurestate/{state}", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
	public void updateFutureStateTaxi(@PathVariable("taxiId") Long taxiId,
			@PathVariable("state") String state)
			throws InstanceNotFoundException {
		this.taxiService.updateFutureStateTaxi(taxiId,
				TaxiState.valueOf(state.toUpperCase()));
	}

	@RequestMapping(value = "/taxis/{taxiId}/position/{x}/{y:.+}", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
	public void updatePositionTaxi(@PathVariable("taxiId") Long taxiId,
			@PathVariable("x") Double x, @PathVariable("y") Double y)
			throws InstanceNotFoundException {
		this.centralService.updatePositionTaxi(taxiId,
				geometryFactory.createPoint(new Coordinate(y, x)));
	}

	@RequestMapping(value = "/stands", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody List<Stand> getStands() {
		return this.centralService.getStands();
	}

	@RequestMapping(value = "/taxis/{taxiId}/stands", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody List<Stand> getNearestStandsByTaxi(
			@PathVariable("taxiId") Long taxiId)
			throws InstanceNotFoundException {
		return this.taxiService.getNearestStandsByTaxi(taxiId);
	}

	@RequestMapping(value = "/stands/{standId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody List<Taxi> getTaxisByStand(
			@PathVariable("standId") Long standId)
			throws InstanceNotFoundException {
		return this.centralService.getTaxisByStand(standId);
	}

	@RequestMapping(value = "/stands/{standId}/numtaxis", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody Long getNumTaxisStand(
			@PathVariable("standId") Long standId)
			throws InstanceNotFoundException {
		return this.taxiService.getNumTaxisStand(standId);
	}

	@RequestMapping(value = "/taxis/{taxiId}/countries/{countryId}/regions/{regionId}/cities/{cityId}/addresses/{addressId}", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
	public @ResponseBody Long takeClientTo(@PathVariable("taxiId") Long taxiId,
			@PathVariable("countryId") Long countryId,
			@PathVariable("regionId") Long regionId,
			@PathVariable("cityId") Long cityId,
			@PathVariable("addressId") Long addressId)
			throws InstanceNotFoundException, Exception {
		return this.taxiService.takeClientTo(taxiId, countryId, regionId,
				cityId, addressId);
	}

	@RequestMapping(value = "/destination", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public void destinationReached(@RequestBody Map<String, Object> map)
			throws InstanceNotFoundException {
		double oY = (Double) map.get("oY");
		double oX = (Double) map.get("oX");
		double dY = (Double) map.get("dY");
		double dX = (Double) map.get("dX");
		Integer travelIdInt = (Integer) map.get("travelId");
		long travelId = travelIdInt.longValue();
		String path = (String) map.get("path");
		double distance = (Double) map.get("distance");
		Point originPoint = geometryFactory.createPoint(new Coordinate(oY, oX));
		Point destinationPoint = geometryFactory.createPoint(new Coordinate(dY,
				dX));
		WKTReader wktReader = new WKTReader();
		LineString mlsPath;
		try {
			mlsPath = (LineString) wktReader.read(path);
			mlsPath.setSRID(4326);
		} catch (ParseException e) {
			mlsPath = null;
		}
		this.taxiService.destinationReached(travelId, distance, originPoint,
				destinationPoint, mlsPath);
	}

	@RequestMapping(value = "/taxis/{taxiId}/travels", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody List<Travel> getTravels(
			@PathVariable("taxiId") Long taxiId)
			throws InstanceNotFoundException {
		return this.taxiService.getTravels(taxiId);
	}

	@RequestMapping(value = "/taxis/{taxiId}/futuretravels", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody List<FutureTravel> getFutureTravels(
			@PathVariable("taxiId") Long taxiId)
			throws InstanceNotFoundException {
		return this.taxiService.getFutureTravels(taxiId);
	}

	@RequestMapping(value = "/futuretravels", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public void createFutureTravel(@RequestBody Map<String, Object> map)
			throws InstanceNotFoundException, Exception {
		String date = (String) map.get("date");
		Integer taxiIdInt = (Integer) map.get("taxiId");
		long taxiId = taxiIdInt.longValue();
		Integer originCountryIdInt = (Integer) map.get("originCountryId");
		long originCountryId = originCountryIdInt.longValue();
		Integer originRegionIdInt = (Integer) map.get("originRegionId");
		long originRegionId = originRegionIdInt.longValue();
		Integer originCityIdInt = (Integer) map.get("originCityId");
		long originCityId = originCityIdInt.longValue();
		Integer originAddressIdInt = (Integer) map.get("originAddressId");
		long originAddressId = originAddressIdInt.longValue();
		Integer destinationCountryIdInt = (Integer) map
				.get("destinationCountryId");
		long destinationCountryId = destinationCountryIdInt.longValue();
		Integer destinationRegionIdInt = (Integer) map
				.get("destinationRegionId");
		long destinationRegionId = destinationRegionIdInt.longValue();
		Integer destinationCityIdInt = (Integer) map.get("destinationCityId");
		long destinationCityId = destinationCityIdInt.longValue();
		Integer destinationAddressIdInt = (Integer) map
				.get("destinationAddressId");
		long destinationAddressId = destinationAddressIdInt.longValue();
		this.taxiService.createFutureTravel(taxiId, originCountryId,
				originRegionId, originCityId, originAddressId,
				destinationCountryId, destinationRegionId, destinationCityId,
				destinationAddressId, date);
	}

	@RequestMapping(value = "/futuretravels/{futureTravelId}", method = RequestMethod.DELETE, produces = "application/json;charset=UTF-8")
	public void cancelFutureTravel(
			@PathVariable("futureTravelId") Long futureTravelId)
			throws InstanceNotFoundException {
		this.taxiService.cancelFutureTravel(futureTravelId);
	}

	@RequestMapping(value = "/taxis/{taxiId}/canceltravel", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
	public void cancelTravel(@PathVariable("taxiId") Long taxiId)
			throws InstanceNotFoundException {
		this.taxiService.cancelTravel(taxiId);
	}

	@RequestMapping(value = "/taxis/{taxiId}/origincountries/{originCountryId}/originregions/{originRegionId}/origincities/{originCityId}/originaddresses/{originAddressId}/destinationcountries/{destinationCountryId}/destinationregions/{destinationRegionId}/destinationcities/{destinationCityId}/destinationaddresses/{destinationAddressId}", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
	public @ResponseBody Long takeClientToFromFutureTravel(
			@PathVariable("taxiId") Long taxiId,
			@PathVariable("originCountryId") Long originCountryId,
			@PathVariable("originRegionId") Long originRegionId,
			@PathVariable("originCityId") Long originCityId,
			@PathVariable("originAddressId") Long originAddressId,
			@PathVariable("destinationCountryId") Long destinationCountryId,
			@PathVariable("destinationRegionId") Long destinationRegionId,
			@PathVariable("destinationCityId") Long destinationCityId,
			@PathVariable("destinationAddressId") Long destinationAddressId)
			throws InstanceNotFoundException {
		return this.taxiService.takeClientToFromFutureTravel(taxiId,
				originCountryId, originRegionId, originCityId, originAddressId,
				destinationCountryId, destinationRegionId, destinationCityId,
				destinationAddressId);
	}

	@RequestMapping(value = "/taxiclient", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody TaxiClientDto getTaxiIdWithTokenAndClient()
			throws Exception {
		return this.centralService.getTaxiIdWithTokenAndClient();
	}

	@RequestMapping(value = "/taxis/{taxiId}/token/{token}", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
	public void setTokenToTaxi(@PathVariable("taxiId") Long taxiId,
			@PathVariable("token") String token)
			throws InstanceNotFoundException {
		this.centralService.setTokenToTaxi(taxiId, token);
	}

	@RequestMapping(value = "/taxis/{taxiId}/clients/{clientId}/accept", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
	public void assignClientToTaxi(@PathVariable("taxiId") Long taxiId,
			@PathVariable("clientId") Long clientId)
			throws InstanceNotFoundException {
		this.centralService.assignClientToTaxi(taxiId, clientId);
	}

	@RequestMapping(value = "/taxis/{taxiId}/decline", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
	public void declineClientToTaxi(@PathVariable("taxiId") Long taxiId)
			throws InstanceNotFoundException {
		this.centralService.setTaxiToOff(taxiId);
	}

	@RequestMapping(value = "/taxis/{taxiId}/locate", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
	public void locateTaxi(@PathVariable("taxiId") Long taxiId)
			throws InstanceNotFoundException {
		this.taxiService.locateTaxi(taxiId);
	}
	//POST ya que put no permite el envio de caracteres especiales
	@RequestMapping(value = "/changepassword", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public Boolean changePassword(@RequestBody Taxi taxi) throws InstanceNotFoundException {
		return this.taxiService.changePassword(taxi.getTaxiId(), taxi.getPassword());
	}
	
	@RequestMapping(value = "/taxis/{taxiId}/cities/{cityId}", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
	public Boolean changeCity(@PathVariable("taxiId") Long taxiId, @PathVariable("cityId") Long cityId) throws InstanceNotFoundException {
		return this.taxiService.changeCity(taxiId, cityId);
	}

}
