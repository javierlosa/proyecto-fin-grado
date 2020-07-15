package com.tfg.dao;

import java.util.List;

import com.tfg.dao.util.GenericDao;
import com.tfg.model.Stand;
import com.tfg.model.Taxi;

public interface StandDao extends GenericDao<Stand, Long> {

	public List<Stand> getStands();

	public List<Stand> getNearestStandsByTaxi(Long taxiId);

	public Long getNumTaxisStand(Long standId);
	
	public List<Taxi> getTaxisByStand(Long standId);

	public Stand getStandWhereTaxiIs(Long taxiId);

}
