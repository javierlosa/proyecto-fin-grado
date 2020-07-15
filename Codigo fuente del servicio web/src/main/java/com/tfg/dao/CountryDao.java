package com.tfg.dao;

import java.util.List;

import com.tfg.dao.util.GenericDao;
import com.tfg.dao.util.InstanceNotFoundException;
import com.tfg.model.Country;

public interface CountryDao extends GenericDao<Country, Long> {

	public List<Country> getCountries();

	public long getCountryFromRegion(long regionId)
			throws InstanceNotFoundException;

}
