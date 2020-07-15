package com.tfg.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.tfg.dao.util.GenericDaoHibernate;
import com.tfg.dao.util.InstanceNotFoundException;
import com.tfg.model.Region;

@Repository("regionDao")
public class RegionDaoHibernate extends GenericDaoHibernate<Region, Long>
		implements RegionDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<Region> getRegions(Long countryId) {
		return getSession()
				.createQuery(
						"SELECT r FROM Region r WHERE r.country.countryId = :countryId ORDER BY r.name")
				.setParameter("countryId", countryId).list();
	}

	@Override
	public long getRegionFromCity(long cityId) throws InstanceNotFoundException {
		return (Long) getSession()
				.createQuery(
						"SELECT c.region.regionId FROM City c WHERE c.cityId = :cityId")
				.setParameter("cityId", cityId).uniqueResult();
	}

}