package com.tfg.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.tfg.dao.util.GenericDaoHibernate;
import com.tfg.model.Travel;

@Repository("travelDao")
public class TravelDaoHibernate extends
		GenericDaoHibernate<Travel, Long> implements TravelDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<Travel> getTravels(Long taxiId) {
		return getSession()
				.createQuery(
						"SELECT t FROM Travel t WHERE t.taxi.taxiId = :taxiId ORDER BY t.date")
				.setParameter("taxiId", taxiId).list();
	}

}