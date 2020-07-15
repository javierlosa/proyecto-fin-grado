package com.tfg.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.tfg.dao.util.GenericDaoHibernate;
import com.tfg.model.City;
import com.tfg.model.Stand;
import com.tfg.model.Taxi;
import com.vividsolutions.jts.geom.Point;

@Repository("standDao")
public class StandDaoHibernate extends GenericDaoHibernate<Stand, Long>
		implements StandDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<Stand> getStands() {
		return getSession().createQuery("FROM Stand").list();
	}

	@Override
	public Long getNumTaxisStand(Long standId) {
		return (Long) getSession()
				.createQuery(
						"SELECT COUNT(t) FROM Stand s JOIN Entry e ON s.standId = e.stand.standId JOIN Taxi t ON e.taxi.taxiId = t.taxiId WHERE s.standId = :standId")
				.setParameter("standId", standId).uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Stand> getNearestStandsByTaxi(Long taxiId) {
		//Busco el taxi, su posicion, coges la ciudad en la que esta y miras que sea la misma que la de las paradas
		Taxi t = (Taxi) getSession().createQuery("SELECT t FROM Taxi t WHERE t.taxiId = :taxiId").setParameter("taxiId", taxiId).uniqueResult();
		//City c = (City) getSession().createQuery("SELECT c FROM City c WHERE ST_Contains(c.location, :position) = true").setParameter("position", position).uniqueResult();
		Long cityId = t.getCity().getCityId();
		return getSession()
				.createQuery(
						"SELECT s FROM Stand s JOIN Address a ON s.address.addressId = a.addressId JOIN City c ON a.city.cityId = c.cityId "
								+ "WHERE c.cityId = :cityId ORDER BY ST_Distance((SELECT position FROM Taxi WHERE taxiId = :taxiId),s.location) ASC")
				.setParameter("taxiId", taxiId).setParameter("cityId", cityId).setMaxResults(5).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Taxi> getTaxisByStand(Long standId) {
		return getSession()
				.createQuery(
						"SELECT t FROM Stand s JOIN Entry e ON s.standId = e.stand.standId JOIN Taxi t ON e.taxi.taxiId = t.taxiId WHERE s.standId = :standId")
				.setParameter("standId", standId).list();
	}
	
	@Override
	public Stand getStandWhereTaxiIs(Long taxiId) {
		return (Stand) getSession()
				.createQuery(
						"SELECT s FROM Stand s JOIN Entry e ON s.standId = e.stand.standId JOIN Taxi t ON e.taxi.taxiId = t.taxiId WHERE t.taxiId = :taxiId")
				.setParameter("taxiId", taxiId).setMaxResults(1).uniqueResult();
	}

}