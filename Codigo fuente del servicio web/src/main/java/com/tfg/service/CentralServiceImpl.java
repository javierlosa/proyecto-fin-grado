package com.tfg.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tfg.dao.AddressDao;
import com.tfg.dao.CityDao;
import com.tfg.dao.ClientDao;
import com.tfg.dao.CountryDao;
import com.tfg.dao.EntryDao;
import com.tfg.dao.RegionDao;
import com.tfg.dao.StandDao;
import com.tfg.dao.TaxiDao;
import com.tfg.dao.util.InstanceNotFoundException;
import com.tfg.model.Address;
import com.tfg.model.City;
import com.tfg.model.Client;
import com.tfg.model.ClientState;
import com.tfg.model.Country;
import com.tfg.model.Entry;
import com.tfg.model.Region;
import com.tfg.model.Stand;
import com.tfg.model.Taxi;
import com.tfg.model.TaxiClientDto;
import com.tfg.model.TaxiState;
import com.vividsolutions.jts.geom.Point;

@Service("centralService")
@Transactional
public class CentralServiceImpl implements CentralService {

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
	private TaxiDao taxiDao;

	@Autowired
	private StandDao standDao;
	
	@Autowired
	private EntryDao entryDao;

	@Override
	public Client createClient(Long country, Long region, Long city,
			Long address, Point location) throws InstanceNotFoundException {
		Country originCountry = countryDao.find(country);
		Region originRegion = regionDao.find(region);
		City originCity = cityDao.find(city);
		Address originAddress = addressDao.find(address);
		Client client = new Client(originCountry, originRegion, originCity,
				originAddress, Calendar.getInstance(), location);
		this.clientDao.save(client);
		return client;
	}

	@Override
	public void removeClient(Long id) throws InstanceNotFoundException {
		this.clientDao.remove(id);
	}

	@Override
	@Transactional(readOnly = true)
	public Client getClient(Long clientId) throws InstanceNotFoundException {
		return this.clientDao.find(clientId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Client> getClients() {
		return this.clientDao.getClients();
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
	public List<Stand> getStands() {
		return this.standDao.getStands();
	}

	@Override
	@Transactional(readOnly = true)
	public List<Taxi> getTaxisByStand(Long standId)
			throws InstanceNotFoundException {
		return this.standDao.getTaxisByStand(standId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Taxi> getOperatingTaxis() {
		return this.taxiDao.getOperatingTaxis();
	}

	@Override
	@Transactional(readOnly = true)
	public List<Taxi> getAvailableTaxis() {
		return this.taxiDao.getAvailableTaxis();
	}

	@Override
	@Transactional(readOnly = true)
	public List<Client> getClientsWaiting() {
		return this.clientDao.getClientsWaiting();
	}

	@Override
	@Transactional(readOnly = true)
	public long getNearestAddress(Point position) {
		return this.addressDao.getNearestAddress(position);
	}

	@Override
	@Transactional(readOnly = true)
	public long getCityFromAddress(long addressId)
			throws InstanceNotFoundException {
		return this.cityDao.getCityFromAddress(addressId);
	}

	@Override
	@Transactional(readOnly = true)
	public long getRegionFromCity(long cityId) throws InstanceNotFoundException {
		return this.regionDao.getRegionFromCity(cityId);
	}

	@Override
	@Transactional(readOnly = true)
	public long getCountryFromRegion(long regionId)
			throws InstanceNotFoundException {
		return this.countryDao.getCountryFromRegion(regionId);
	}

	@Override
	public TaxiClientDto getTaxiIdWithTokenAndClient() throws Exception {
		Client client;
		client = this.clientDao.getFirstClient();
		Taxi taxi;
		taxi = this.taxiDao.getNearestAvailableTaxi(client.getLocation());
		//Si no hay taxis en la zona (ni andando ni en una parada) quitamos al cliente
		while (taxi==null) {
			this.clientDao.remove(client.getClientId());
			client = this.clientDao.getFirstClient();
			taxi = this.taxiDao.getNearestAvailableTaxi(client.getLocation());
		}
		return new TaxiClientDto(taxi.getTaxiId(), taxi.getToken(),
				client.getClientId(), client.getOriginCountry().getName(),
				client.getOriginRegion().getName(), client.getOriginCity()
						.getName(), client.getOriginAddress().getName());

	}

	@Override
	public void setTokenToTaxi(Long taxiId, String token)
			throws InstanceNotFoundException {
		Taxi taxi = taxiDao.find(taxiId);
		taxi.setToken(token);
		taxiDao.save(taxi);
	}

	@Override
	public void assignClientToTaxi(Long taxiId, Long clientId)
			throws InstanceNotFoundException {
		Client client = this.clientDao.find(clientId);
		Taxi taxi = this.taxiDao.find(taxiId);
		Entry oldestEntry;
		if ((client != null) && (taxi != null)) {
			taxi.setClient(client);
			client.setClientState(ClientState.ASSIGNED);
			taxi.setActualState(TaxiState.BUSY);
			this.clientDao.save(client);
			this.taxiDao.save(taxi);
			Stand s = standDao.getStandWhereTaxiIs(taxiId);
			if (s!=null) {
				List<Entry> entries = new ArrayList<Entry>();
				entries.addAll(s.getEntries());
				oldestEntry = entries.get(0);
				for (Entry entry: entries) {
					if (entry.getArrival().before(oldestEntry.getArrival())) {
						oldestEntry = entry;
					}
				}
				this.entryDao.remove(oldestEntry.getEntryId());
			}
		}
	}

	@Override
	public void setTaxiToOff(Long taxiId) throws InstanceNotFoundException {
		Taxi taxi = this.taxiDao.find(taxiId);
		taxi.setActualState(TaxiState.OFF);
		this.taxiDao.save(taxi);
	}
}
