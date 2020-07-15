package com.tfg.dao;

import java.util.List;

import com.tfg.dao.util.GenericDao;
import com.tfg.model.FutureTravel;

public interface FutureTravelDao extends GenericDao<FutureTravel, Long> {
	
	public List<FutureTravel> getFutureTravels(Long taxiId);
	
}
