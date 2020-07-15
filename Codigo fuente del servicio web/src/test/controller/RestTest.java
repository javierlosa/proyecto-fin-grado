package controller;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.tfg.controller.Controller;
import com.tfg.dao.AddressDao;
import com.tfg.dao.CityDao;
import com.tfg.dao.StandDao;
import com.tfg.dao.util.InstanceNotFoundException;
import com.tfg.model.Address;
import com.tfg.model.City;
import com.tfg.model.Client;
import com.tfg.model.ClientState;
import com.tfg.model.Country;
import com.tfg.model.FutureTravel;
import com.tfg.model.Region;
import com.tfg.model.Stand;
import com.tfg.model.Taxi;
import com.tfg.model.TaxiClientDto;
import com.tfg.model.TaxiState;
import com.tfg.model.Travel;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "root-test-context.xml" })
@Transactional
public class RestTest {

	Taxi taxi1, taxi2, taxi3, taxi4, taxi5;
	Client client1, client2, client3;
	Point pavoRealRdaOuteiro, estTrenRtnd, osRosales, matogrande, calleBarcelona;
	double[] pavoRealRdaOuteiroCoords, estTrenRtndCoords, osRosalesCoords, matograndeCoords, calleBarcelonaCoords;
	GeometryFactory geometryFactory;
	List<Client> clients;
	List<Country> countries;
	List<Region> regions;
	List<City> cities;
	List<Address> addresses;
	Stand stand1, stand2, stand3;
	List<Stand> stands;

	@Autowired
	private Controller controller;

	@Autowired
	private StandDao standDao;
	
	@Autowired
	private AddressDao addressDao;
	
	@Autowired
	private CityDao cityDao;

	@Before
	public void data() {
		taxi1 = controller.createTaxi();
		taxi2 = controller.createTaxi();
		taxi3 = controller.createTaxi();
		taxi4 = controller.createTaxi();
		taxi1.setActualState(TaxiState.OFF);
		taxi2.setActualState(TaxiState.AVAILABLE);
		taxi3.setActualState(TaxiState.BUSY);
		taxi4.setActualState(TaxiState.INSTAND);
		geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
		pavoRealRdaOuteiroCoords = new double[2];
		pavoRealRdaOuteiroCoords[1] = -8.42469423106243;
		pavoRealRdaOuteiroCoords[0] = 43.3677126761829;
		pavoRealRdaOuteiro = geometryFactory.createPoint(new Coordinate(
				pavoRealRdaOuteiroCoords[1], pavoRealRdaOuteiroCoords[0]));
		try {
			controller.createClient(pavoRealRdaOuteiroCoords);
			controller.createClient(pavoRealRdaOuteiroCoords);
			controller.createClient(pavoRealRdaOuteiroCoords);
		} catch (InstanceNotFoundException e) {
			e.printStackTrace();
		}
		clients = new ArrayList<Client>();
		clients.addAll(controller.getClients());
		clients.get(0).setClientState(ClientState.WAITING);
		clients.get(1).setClientState(ClientState.ASSIGNED);
		clients.get(2).setClientState(ClientState.TRAVELLING);
		taxi1.setPassword("Hola");
		countries = controller.getCountries();
		try {
			regions = controller.getRegions(1l);
			cities = controller.getCities(9l);
			addresses = controller.getAddresses(6944l);
		} catch (InstanceNotFoundException e) {
			e.printStackTrace();
		}
		taxi1.setPosition(pavoRealRdaOuteiro);
		estTrenRtndCoords = new double[2];
		estTrenRtndCoords[1] = -8.4086963;
		estTrenRtndCoords[0] = 43.3531591;
		estTrenRtnd = geometryFactory.createPoint(new Coordinate(
				estTrenRtndCoords[1], estTrenRtndCoords[0]));
		osRosalesCoords = new double[2];
		osRosalesCoords[1] = -8.4134398;
		osRosalesCoords[0] = 43.3448868;
		osRosales = geometryFactory.createPoint(new Coordinate(
				osRosalesCoords[1], osRosalesCoords[0]));
		matograndeCoords = new double[2];
		matograndeCoords[1] = -8.4044273;
		matograndeCoords[0] = 43.3410724;
		matogrande = geometryFactory.createPoint(new Coordinate(
				matograndeCoords[1], matograndeCoords[0]));
		calleBarcelonaCoords = new double[2];
		calleBarcelonaCoords[1] = -8.4228256;
		calleBarcelonaCoords[0] = 43.3631952;
		calleBarcelona = geometryFactory.createPoint(new Coordinate(
				calleBarcelonaCoords[1], calleBarcelonaCoords[0]));
		stands = controller.getStands();
		stand1 = stands.get(0);
		stand2 = stands.get(1);
		stand3 = stands.get(2);
		try {
			taxi1.setCity(cityDao.find(6944l));
			taxi2.setCity(cityDao.find(6944l));
			taxi3.setCity(cityDao.find(6944l));
		} catch (InstanceNotFoundException e) {
			e.printStackTrace();
		}
		taxi2.setPosition(osRosales);
		try {
			controller.locateTaxi(taxi2.getTaxiId());
		} catch (InstanceNotFoundException e) {
			e.printStackTrace();
		}
		taxi3.setPosition(pavoRealRdaOuteiro);
	}

	@Test
	public void testGetTaxis() {
		assertTrue(controller.getTaxis().size() == 4);
	}

	@Test
	public void testGetTaxi() throws InstanceNotFoundException {
		Taxi taxi = controller.getTaxi(taxi1.getTaxiId());
		assertTrue(controller.getTaxis().get(0) == taxi);
	}

	@Test
	public void testGetOperatingTaxis() {
		assertTrue(controller.getOperatingTaxis().size() == 1);
	}

	@Test
	public void testGetAvailableTaxis() {
		assertTrue(controller.getAvailableTaxis().size() == 0);
	}

	@Test
	public void testGetClients() {
		assertTrue(controller.getClients().size() == 2);
	}

	@Test
	public void testGetClientsWaiting() {
		assertTrue(controller.getClientsWaiting().size() == 1);
	}

	@Test
	public void testCreateTaxi() {
		controller.createTaxi();
		assertTrue(controller.getTaxis().size() == 5);
	}

	@Test
	public void testCreateClient() throws InstanceNotFoundException {
		controller.createClient(pavoRealRdaOuteiroCoords);
		assertTrue(controller.getClients().size() == 3);
	}

	@Test
	public void testLogin() throws InstanceNotFoundException, Exception {
		Taxi taxi = controller.login(taxi1);
		assertTrue(taxi != null);
	}

	@Test
	public void testGetCountries() {
		assertTrue(countries.size() == 1);
	}

	@Test
	public void testGetRegions() {
		assertTrue(regions.size() == 50);
	}

	@Test
	public void testGetCities() {
		assertTrue(cities.size() == 93);
	}

	@Test
	public void testGetAddresses() {
		assertTrue(addresses.size() == 933);
	}

	@Test
	public void testUpdateActualStateTaxi() throws InstanceNotFoundException {
		TaxiState state = taxi1.getActualState();
		assertTrue(state == TaxiState.OFF);
		controller.updateActualStateTaxi(taxi1.getTaxiId(),
				TaxiState.AVAILABLE.toString());
		assertTrue(taxi1.getActualState() == TaxiState.AVAILABLE);
	}

	@Test
	public void testUpdateFutureStateTaxi() throws InstanceNotFoundException {
		TaxiState state = taxi1.getFutureState();
		assertTrue(state == TaxiState.OFF);
		controller.updateFutureStateTaxi(taxi1.getTaxiId(),
				TaxiState.AVAILABLE.toString());
		assertTrue(taxi1.getFutureState() == TaxiState.AVAILABLE);
	}

	@Test
	public void testUpdatePositionTaxi() throws InstanceNotFoundException {
		assertTrue(taxi1.getPosition() == pavoRealRdaOuteiro);
		controller.updatePositionTaxi(taxi1.getTaxiId(), estTrenRtndCoords[0],
				estTrenRtndCoords[1]);
		assertTrue(taxi1.getPosition().compareTo(estTrenRtnd)==0);
	}

	@Test
	public void testGetStands() {
		assertTrue(stands.size() == 3);
	}

	@Test
	public void testGetNearestStandsByTaxi() throws InstanceNotFoundException {
		List<Stand> nearestStands = controller.getNearestStandsByTaxi(taxi1.getTaxiId());
		assertTrue(nearestStands.get(0)==stand3);
		assertTrue(nearestStands.get(1)==stand1);
		assertTrue(nearestStands.get(2)==stand2);
	}

	@Test
	public void testGetTaxisByStand() throws InstanceNotFoundException {
		List<Taxi> taxisByStand1 = controller.getTaxisByStand(stand1.getStandId());
		List<Taxi> taxisByStand2 = controller.getTaxisByStand(stand2.getStandId());
		List<Taxi> taxisByStand3 = controller.getTaxisByStand(stand3.getStandId());
		assertTrue(taxisByStand2.get(0)==taxi2);
		assertTrue(taxisByStand1.size()==0);
		assertTrue(taxisByStand3.size()==0);
	}

	@Test
	public void testGetNumTaxisStand() throws InstanceNotFoundException {
		List<Taxi> taxisByStand1 = controller.getTaxisByStand(stand1.getStandId());
		List<Taxi> taxisByStand2 = controller.getTaxisByStand(stand2.getStandId());
		List<Taxi> taxisByStand3 = controller.getTaxisByStand(stand3.getStandId());
		assertTrue(taxisByStand1.size()==0);
		assertTrue(taxisByStand2.size()==1);
		assertTrue(taxisByStand3.size()==0);
	}

	@Test
	public void testTakeClientToAndDestinationReachedAndGetTravels() throws InstanceNotFoundException, Exception {
		//Calle Paraguay -> 2002
		//Taxi 1 tiene cliente asignado
		taxi1.setClient(client1);
		Long viajeClienteAsignado = controller.takeClientTo(taxi1.getTaxiId(), 1l, 9l, 6944l, 2022l);
		//Taxi 2 no tiene cliente asignado y esta en parada
		List<Taxi> taxisByStand2 = controller.getTaxisByStand(stand2.getStandId());
		assertTrue(taxisByStand2.get(0)==taxi2);
		Long viajeNoClienteAsignadoYParada = controller.takeClientTo(taxi2.getTaxiId(), 1l, 9l, 6944l, 2022l);
		//Taxi 3 no tiene cliente asignado y no esta en parada
		Long viajeNoClienteAsignadoYNoParada = controller.takeClientTo(taxi3.getTaxiId(), 1l, 9l, 6944l, 2022l);
		
		HashMap<String, Object> destinationReachedMapTaxi1 = new HashMap<String, Object>();
		destinationReachedMapTaxi1.put("oX", 43.3195407);
		destinationReachedMapTaxi1.put("oY", -8.47164906);
		destinationReachedMapTaxi1.put("dX", 43.3683804);
		destinationReachedMapTaxi1.put("dY", -8.4001909);
		destinationReachedMapTaxi1.put("travelId", viajeClienteAsignado.intValue());
		destinationReachedMapTaxi1.put("distance", 7938.16406);
		destinationReachedMapTaxi1.put("path", "LINESTRING(43.3195407 -8.47164906,43.31935644 -8.47169346)");
		controller.destinationReached(destinationReachedMapTaxi1);
		HashMap<String, Object> destinationReachedMapTaxi2 = new HashMap<String, Object>();
		destinationReachedMapTaxi2.put("oX", 43.3195407);
		destinationReachedMapTaxi2.put("oY", -8.47164906);
		destinationReachedMapTaxi2.put("dX", 43.3683804);
		destinationReachedMapTaxi2.put("dY", -8.4001909);
		destinationReachedMapTaxi2.put("travelId", viajeNoClienteAsignadoYParada.intValue());
		destinationReachedMapTaxi2.put("distance", 7938.16406);
		destinationReachedMapTaxi2.put("path", "LINESTRING(43.3195407 -8.47164906,43.31935644 -8.47169346)");
		controller.destinationReached(destinationReachedMapTaxi2);
		HashMap<String, Object> destinationReachedMapTaxi3 = new HashMap<String, Object>();
		destinationReachedMapTaxi3.put("oX", 43.3195407);
		destinationReachedMapTaxi3.put("oY", -8.47164906);
		destinationReachedMapTaxi3.put("dX", 43.3683804);
		destinationReachedMapTaxi3.put("dY", -8.4001909);
		destinationReachedMapTaxi3.put("travelId", viajeNoClienteAsignadoYNoParada.intValue());
		destinationReachedMapTaxi3.put("distance", 7938.16406);
		destinationReachedMapTaxi3.put("path", "LINESTRING(43.3195407 -8.47164906,43.31935644 -8.47169346)");
		controller.destinationReached(destinationReachedMapTaxi3);
		
		List<Travel> travelsTaxi1 = controller.getTravels(taxi1.getTaxiId());
		assertTrue(travelsTaxi1.size()==1);
		assertTrue(travelsTaxi1.get(0).getTravelId()==viajeClienteAsignado);
		List<Travel> travelsTaxi2 = controller.getTravels(taxi2.getTaxiId());
		assertTrue(travelsTaxi2.size()==1);
		assertTrue(travelsTaxi2.get(0).getTravelId()==viajeNoClienteAsignadoYParada);
		List<Travel> travelsTaxi3 = controller.getTravels(taxi3.getTaxiId());
		assertTrue(travelsTaxi3.size()==1);
		assertTrue(travelsTaxi3.get(0).getTravelId()==viajeNoClienteAsignadoYNoParada);
	}

	@Test
	public void testCreateFutureTravelAndGetFutureTravelAndCancelFutureTravel() throws Exception {
		HashMap<String, Object> futureTravelMapTaxi1 = new HashMap<String, Object>();
		futureTravelMapTaxi1.put("date", "29-12-2016 23:23");
		futureTravelMapTaxi1.put("taxiId", taxi1.getTaxiId().intValue());
		futureTravelMapTaxi1.put("originCountryId", 1);
		futureTravelMapTaxi1.put("originRegionId", 9);
		futureTravelMapTaxi1.put("originCityId", 6944);
		futureTravelMapTaxi1.put("originAddressId", 1814);
		futureTravelMapTaxi1.put("destinationCountryId", 1);
		futureTravelMapTaxi1.put("destinationRegionId", 9);
		futureTravelMapTaxi1.put("destinationCityId", 6944);
		futureTravelMapTaxi1.put("destinationAddressId", 1855);
		controller.createFutureTravel(futureTravelMapTaxi1);
		List<FutureTravel> futureTravelsTaxi1 = controller.getFutureTravels(taxi1.getTaxiId());
		assertTrue(futureTravelsTaxi1.size()==1);
		controller.cancelFutureTravel(futureTravelsTaxi1.get(0).getFutureTravelId());
		futureTravelsTaxi1 = controller.getFutureTravels(taxi1.getTaxiId());
		assertTrue(futureTravelsTaxi1.size()==0);
	}

	@Test
	public void testTakeClientToFromFutureTravel() throws InstanceNotFoundException {
		assertTrue(controller.getTravels(taxi1.getTaxiId()).size()==0);
		long travelId = controller.takeClientToFromFutureTravel(taxi1.getTaxiId(), 1l, 9l, 6944l, 2002l, 1l, 9l, 6944l, 2022l);
		assertTrue(controller.getTravels(taxi1.getTaxiId()).size()==1);
		assertTrue(travelId!=0l);
	}

	@Test
	public void testGetTaxiIdWithTokenAndClient() throws Exception {
		TaxiClientDto taxiClientDto = controller.getTaxiIdWithTokenAndClient();
		assertNotNull(taxiClientDto);
	}

	@Test
	public void testSetTokenToTaxi() throws InstanceNotFoundException {
		assertNull(taxi1.getToken());
		controller.setTokenToTaxi(taxi1.getTaxiId(), "token");
		assertTrue(taxi1.getToken().compareTo("token")==0);
	}

	@Test
	public void testAssignClientToTaxiAndCancelTravel() throws InstanceNotFoundException {
		assertNull(taxi1.getClient());
		controller.assignClientToTaxi(taxi1.getTaxiId(), clients.get(0).getClientId());
		assertTrue(taxi1.getClient()!=null);
		//Si un taxi esta en parada
		assertTrue(taxi2.getClient()==null);
		controller.assignClientToTaxi(taxi2.getTaxiId(), clients.get(1).getClientId());
		assertTrue(taxi2.getClient()!=null);
		
		controller.cancelTravel(taxi1.getTaxiId());
		assertTrue(taxi1.getClient()==null);
	}

	@Test
	public void testDeclineClientToTaxi() throws InstanceNotFoundException {
		assertTrue(taxi1.getActualState()==TaxiState.OFF);
		taxi1.setActualState(TaxiState.BUSY);
		assertTrue(taxi1.getActualState()==TaxiState.BUSY);
		controller.declineClientToTaxi(taxi1.getTaxiId());
		assertTrue(taxi1.getActualState()==TaxiState.OFF);
	}

	@Test
	public void testLocateTaxi() throws InstanceNotFoundException {
		assertNull(standDao.getStandWhereTaxiIs(taxi1.getTaxiId()));
		controller.locateTaxi(taxi1.getTaxiId());
		assertNotNull(standDao.getStandWhereTaxiIs(taxi1.getTaxiId()));
	}

	@Test
	public void testChangePassword() throws InstanceNotFoundException {
		Taxi taxi = controller.createTaxi();
		assertTrue(taxi.getPassword().compareTo("")==0);
		taxi.setPassword("hola");
		assertTrue(controller.changePassword(taxi));
	}

	@Test
	public void testChangeCity() throws InstanceNotFoundException {
		assertTrue(taxi1.getCity().getCityId()==6944l);
		assertTrue(controller.changeCity(taxi1.getTaxiId(), 6908l));
		assertTrue(taxi1.getCity().getCityId()==6908l);
	}

}
