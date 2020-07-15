package com.tfg.dao;

import java.util.List;

import com.tfg.dao.util.GenericDao;
import com.tfg.dao.util.InstanceNotFoundException;
import com.tfg.model.Region;

public interface RegionDao extends GenericDao<Region, Long> {

	public List<Region> getRegions(Long countryId);

	public long getRegionFromCity(long cityId) throws InstanceNotFoundException;

}
