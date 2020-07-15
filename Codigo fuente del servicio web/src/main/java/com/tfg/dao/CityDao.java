package com.tfg.dao;

import java.util.List;

import com.tfg.dao.util.GenericDao;
import com.tfg.dao.util.InstanceNotFoundException;
import com.tfg.model.City;


public interface CityDao extends GenericDao<City, Long> {

	public List<City> getCities(Long regionId);

	public long getCityFromAddress(long addressId)
			throws InstanceNotFoundException;

}
