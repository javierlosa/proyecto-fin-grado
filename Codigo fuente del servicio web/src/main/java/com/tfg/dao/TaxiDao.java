package com.tfg.dao;

import java.util.List;

import com.tfg.dao.util.GenericDao;
import com.tfg.model.Taxi;
import com.vividsolutions.jts.geom.Point;

public interface TaxiDao extends GenericDao<Taxi, Long> {
	
	public List<Taxi> getTaxis();

	public List<Taxi> getOperatingTaxis();
	
	public List<Taxi> getAvailableTaxis();

	public Taxi getNearestAvailableTaxi(Point position) throws Exception;
	
}
