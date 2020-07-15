package com.tfg.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tfg.dao.AddressDao;
import com.tfg.dao.CityDao;
import com.tfg.dao.ClientDao;
import com.tfg.dao.CountryDao;
import com.tfg.dao.EntryDao;
import com.tfg.dao.FutureTravelDao;
import com.tfg.dao.RegionDao;
import com.tfg.dao.StandDao;
//import com.tfg.dao.StateDao;
import com.tfg.dao.TaxiDao;
import com.tfg.dao.TravelDao;
import com.tfg.dao.util.InstanceNotFoundException;
import com.tfg.model.Address;
import com.tfg.model.City;
import com.tfg.model.Client;
import com.tfg.model.ClientState;
import com.tfg.model.Country;
import com.tfg.model.Entry;
import com.tfg.model.FutureTravel;
import com.tfg.model.Region;
import com.tfg.model.Stand;
import com.tfg.model.Taxi;
import com.tfg.model.TaxiState;
import com.tfg.model.Travel;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.Point;

@Service("taxiService")
@Transactional
public class TaxiServiceImpl implements TaxiService {

	@Autowired
	private TaxiDao taxiDao;

	@Autowired
	private ClientDao clientDao;

	@Autowired
	private CountryDao countryDao;

	@Autowired
	private RegionDao regionDao;

	@Autowired
	private CityDao cityDao;

	@Autowired
	private AddressDao addressDao;

	@Autowired
	private FutureTravelDao futureTravelDao;

	@Autowired
	private TravelDao travelDao;

	@Autowired
	private StandDao standDao;

	@Autowired
	private EntryDao entryDao;

	@Override
	public Taxi createTaxi() {
		Taxi taxi = new Taxi();
		this.taxiDao.save(taxi);
		return taxi;
	}

	@Override
	@Transactional(readOnly = true)
	public Taxi login(Long taxiId, String password) throws Exception {
		Taxi taxi = taxiDao.find(taxiId);
		String password2 = password.trim();

		if (password2.compareTo(taxi.getPassword()) != 0) {
			throw new Exception("Incorrect Password");
		}
		return taxi;
	}

	@Override
	public void updateActualStateTaxi(Long taxiId, TaxiState actualState)
			throws InstanceNotFoundException {
		Taxi taxi = taxiDao.find(taxiId);
		taxi.setActualState(actualState);
		this.taxiDao.save(taxi);
		Stand s = standDao.getStandWhereTaxiIs(taxiId);
		if (s != null) {
			Entry entry = s.getEntries().iterator().next();
			this.entryDao.remove(entry.getEntryId());
		}
	}

	@Override
	public void updateFutureStateTaxi(Long taxiId, TaxiState futureState)
			throws InstanceNotFoundException {
		Taxi taxi = taxiDao.find(taxiId);
		taxi.setFutureState(futureState);
		this.taxiDao.save(taxi);
	}

	@Override
	public void updatePositionTaxi(Long taxiId, Point position)
			throws InstanceNotFoundException {
		Taxi taxi = taxiDao.find(taxiId);
		taxi.setPosition(position);
		this.taxiDao.save(taxi);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Taxi> getTaxis() {
		return this.taxiDao.getTaxis();
	}

	@Override
	@Transactional(readOnly = true)
	public Taxi getTaxi(Long taxiId) throws InstanceNotFoundException {
		return this.taxiDao.find(taxiId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Country> getCountries() {
		return this.countryDao.getCountries();
	}

	@Override
	@Transactional(readOnly = true)
	public List<Region> getRegions(Long countryId)
			throws InstanceNotFoundException {
		return this.regionDao.getRegions(countryId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<City> getCities(Long regionId) throws InstanceNotFoundException {
		return this.cityDao.getCities(regionId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Address> getAddresses(Long cityId)
			throws InstanceNotFoundException {
		return this.addressDao.getAddresses(cityId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Stand> getNearestStandsByTaxi(Long taxiId)
			throws InstanceNotFoundException {
		return this.standDao.getNearestStandsByTaxi(taxiId);
	}

	@Override
	@Transactional(readOnly = true)
	public Long getNumTaxisStand(Long standId) throws InstanceNotFoundException {
		return this.standDao.getNumTaxisStand(standId);
	}

	@Override
	public Long takeClientTo(Long taxiId, Long countryId, Long regionId,
			Long cityId, Long addressId) throws InstanceNotFoundException,
			Exception {
		Taxi taxi = taxiDao.find(taxiId);
		Client client = null;
		if (taxi.getClient() == null) {
			Stand s = standDao.getStandWhereTaxiIs(taxiId);
			Address address;
			if (s != null) {
				address = s.getAddress();
			} else {
				long addressIdAux = addressDao.getNearestAddress(taxi
						.getPosition());
				address = addressDao.find(addressIdAux);
			}
			City city = address.getCity();
			Region region = city.getRegion();
			Country country = region.getCountry();
			client = new Client(country, region, city, address,
					Calendar.getInstance(), taxi.getPosition());
			this.clientDao.save(client);
			if (s != null) {
				System.out.println("Numero de paradas: "
						+ standDao.getStands().size());
				System.out.println("Numero de entradas en la parada: "
						+ s.getStandId() + " -> " + s.getEntries().size());
				Entry entry = s.getEntries().iterator().next();
				this.entryDao.remove(entry.getEntryId());
			}
		} else {
			client = taxi.getClient();
		}
		client.setClientState(ClientState.TRAVELLING);
		Calendar now = Calendar.getInstance();
		Country country = countryDao.find(countryId);
		Region region = regionDao.find(regionId);
		City city = cityDao.find(cityId);
		Address address = addressDao.find(addressId);
		Travel travel = new Travel(now, client.getOriginCountry(),
				client.getOriginRegion(), client.getOriginCity(),
				client.getOriginAddress(), country, region, city, address, taxi);
		this.travelDao.save(travel);
		taxi.setActualState(TaxiState.BUSY);
		taxi.setClient(client);
		this.taxiDao.save(taxi);
		return travel.getTravelId();
	}

	@Override
	public void destinationReached(Long travelId, double distance,
			Point originPoint, Point destinationPoint, LineString path)
			throws InstanceNotFoundException {
		Travel travel = travelDao.find(travelId);
		Taxi taxi = travel.getTaxi();
		taxi.setActualState(taxi.getFutureState());
		travel.setDistance(distance);
		travel.setOriginPoint(originPoint);
		travel.setDestinationPoint(destinationPoint);
		travel.setPath(path);
		this.travelDao.save(travel);
		clientDao.remove(taxi.getClient().getClientId());
		taxi.setClient(null);
		this.taxiDao.save(taxi);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Travel> getTravels(Long taxiId)
			throws InstanceNotFoundException {
		return this.travelDao.getTravels(taxiId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<FutureTravel> getFutureTravels(Long taxiId)
			throws InstanceNotFoundException {
		return this.futureTravelDao.getFutureTravels(taxiId);
	}

	@Override
	public void createFutureTravel(Long taxiId, Long originCountryId,
			Long originRegionId, Long originCityId, Long originAddressId,
			Long destinationCountryId, Long destinationRegionId,
			Long destinationCityId, Long destinationAddressId, String date)
			throws InstanceNotFoundException, Exception {
		Calendar dateAsCalendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
		try {
			Date aux = sdf.parse(date);
			dateAsCalendar.setTime(aux);
		} catch (ParseException e) {
			throw new Exception("Error parsing");
		}
		Country originCountry = countryDao.find(originCountryId);
		Region originRegion = regionDao.find(originRegionId);
		City originCity = cityDao.find(originCityId);
		Address originAddress = addressDao.find(originAddressId);
		Country destinationCountry = countryDao.find(destinationCountryId);
		Region destinationRegion = regionDao.find(destinationRegionId);
		City destinationCity = cityDao.find(destinationCityId);
		Address destinationAddress = addressDao.find(destinationAddressId);
		Taxi taxi = taxiDao.find(taxiId);
		FutureTravel futureTravel = new FutureTravel(dateAsCalendar,
				originCountry, originRegion, originCity, originAddress,
				destinationCountry, destinationRegion, destinationCity,
				destinationAddress, taxi);
		this.futureTravelDao.save(futureTravel);

	}

	@Override
	public void cancelFutureTravel(Long futureTravelId)
			throws InstanceNotFoundException {
		this.futureTravelDao.remove(futureTravelId);
	}

	@Override
	public void cancelTravel(Long taxiId) throws InstanceNotFoundException {
		Taxi taxi = taxiDao.find(taxiId);
		Client client = taxi.getClient();
		client.setClientState(ClientState.WAITING);
		this.clientDao.save(client);
		taxi.setActualState(TaxiState.AVAILABLE);
		taxi.setClient(null);
		this.taxiDao.save(taxi);
	}

	@Override
	public Long takeClientToFromFutureTravel(Long taxiId, Long originCountryId,
			Long originRegionId, Long originCityId, Long originAddressId,
			Long destinationCountryId, Long destinationRegionId,
			Long destinationCityId, Long destinationAddressId)
			throws InstanceNotFoundException {
		Taxi taxi = taxiDao.find(taxiId);
		Calendar now = Calendar.getInstance();
		Client client = new Client(countryDao.find(originCountryId),
				regionDao.find(originRegionId), cityDao.find(originCityId),
				addressDao.find(originAddressId), now, taxi.getPosition());
		client.setClientState(ClientState.TRAVELLING);
		this.clientDao.save(client);
		Country country = countryDao.find(destinationCountryId);
		Region region = regionDao.find(destinationRegionId);
		City city = cityDao.find(destinationCityId);
		Address address = addressDao.find(destinationAddressId);
		Travel travel = new Travel(now, client.getOriginCountry(),
				client.getOriginRegion(), client.getOriginCity(),
				client.getOriginAddress(), country, region, city, address, taxi);
		this.travelDao.save(travel);
		taxi.setActualState(TaxiState.BUSY);
		taxi.setClient(client);
		this.taxiDao.save(taxi);
		return travel.getTravelId();
	}

	@Override
	public void locateTaxi(Long taxiId) throws InstanceNotFoundException {
		List<Stand> stands = standDao.getNearestStandsByTaxi(taxiId);
		Stand nearestStand = stands.get(0);
		Taxi taxi = taxiDao.find(taxiId);
		if (taxi.getActualState().compareTo(TaxiState.INSTAND) == 0) {
		} else {
			taxi.setActualState(TaxiState.INSTAND);
			this.taxiDao.save(taxi);
			Entry entry = new Entry(taxi, nearestStand, Calendar.getInstance());
			this.entryDao.save(entry);
		}
	}

	@Override
	public Boolean changePassword(Long taxiId, String password)
			throws InstanceNotFoundException {
		boolean done = false;
		Taxi taxi = taxiDao.find(taxiId);
		String password2 = password.trim();
		taxi.setPassword(password2);
		done = true;
		return done;
	}

	@Override
	public Boolean changeCity(Long taxiId, Long cityId)
			throws InstanceNotFoundException {
		boolean done = false;
		Taxi taxi = taxiDao.find(taxiId);
		City city = cityDao.find(cityId);
		taxi.setCity(city);
		done = true;
		return done;
	}

}
