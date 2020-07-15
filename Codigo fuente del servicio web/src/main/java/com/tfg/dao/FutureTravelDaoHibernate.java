package com.tfg.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.tfg.dao.util.GenericDaoHibernate;
import com.tfg.model.FutureTravel;

@Repository("futureTravelDao")
public class FutureTravelDaoHibernate extends
		GenericDaoHibernate<FutureTravel, Long> implements FutureTravelDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<FutureTravel> getFutureTravels(Long taxiId) {
		return getSession()
				.createQuery(
						"SELECT t FROM FutureTravel t WHERE t.taxi.taxiId = :taxiId ORDER BY t.date")
				.setParameter("taxiId", taxiId).list();
	}

}