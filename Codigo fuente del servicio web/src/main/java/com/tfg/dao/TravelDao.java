package com.tfg.dao;

import java.util.List;

import com.tfg.dao.util.GenericDao;
import com.tfg.model.Travel;

public interface TravelDao extends GenericDao<Travel, Long> {
	
	public List<Travel> getTravels(Long taxiId);
	
}
