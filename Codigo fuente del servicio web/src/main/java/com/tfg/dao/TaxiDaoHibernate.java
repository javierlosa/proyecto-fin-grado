package com.tfg.dao;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.tfg.dao.util.GenericDaoHibernate;
import com.tfg.model.City;
import com.tfg.model.Entry;
import com.tfg.model.Stand;
import com.tfg.model.Taxi;
import com.tfg.model.TaxiState;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;

@Repository("taxiDao")
public class TaxiDaoHibernate extends GenericDaoHibernate<Taxi, Long> implements
		TaxiDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<Taxi> getTaxis() {
		return getSession().createQuery("FROM Taxi").list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Taxi> getOperatingTaxis() {
		return getSession().createQuery(
				"SELECT t FROM Taxi t WHERE t.actualState <> 0 AND t.actualState <> 3").list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Taxi> getAvailableTaxis() {
		return getSession().createQuery(
				"SELECT t FROM Taxi t WHERE t.actualState = 1").list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Taxi getNearestAvailableTaxi(Point position) throws Exception {
		City c = (City) getSession().createQuery("SELECT c FROM City c WHERE ST_Contains(c.location, :position) = true").setParameter("position", position).uniqueResult();
		MultiPolygon location = c.getLocation();
		Taxi t = (Taxi) getSession()
				.createQuery(
						"SELECT t FROM Taxi t WHERE t.actualState = 1 AND ST_Contains(:location, t.position) = true ORDER BY ST_Distance(:position, t.position)").setMaxResults(1)
				.setParameter("position", position).setParameter("location", location).uniqueResult();
		Double distanceToTaxi = (double) 999999999;
		if (t!=null) {
			Long taxiId = t.getTaxiId();
			distanceToTaxi = (Double) getSession()
					.createSQLQuery(
							"SELECT ST_Distance(:position, t.position) as distance FROM Taxi t WHERE t.taxiId = :taxiId").addScalar("distance").setParameter("position", position).setParameter("taxiId", taxiId).uniqueResult();
		}
		
		Long cityId = c.getCityId();
		List<Stand> stands = (List<Stand>) getSession()
				.createQuery(
						"SELECT s FROM Stand s JOIN Address a ON s.address.addressId = a.addressId JOIN City c ON a.city.cityId = c.cityId "
								+ "WHERE c.cityId = :cityId ORDER BY ST_Distance(:position, s.location) ASC")
				.setParameter("position", position).setParameter("cityId", cityId).list();
		Long standId = (long) 0;
		for (Stand aux: stands) {
			if (aux.getEntries().isEmpty()) {
			} else {
				Set<Entry> entries = aux.getEntries();
				for (Entry entry: entries) {
					if (entry.getTaxi().getActualState().compareTo(TaxiState.INSTAND)==0) {
						standId = aux.getStandId();
					}
				}
			}
		}
		Double distanceToStand;
		if (standId==0) {
			distanceToStand = (double) 999999999;
		} else {
			distanceToStand = (Double) getSession()
					.createSQLQuery(
							"SELECT ST_Distance(:position, s.location) as distance FROM Stand s WHERE s.standId = :standId").addScalar("distance").setParameter("position", position).setParameter("standId", standId).uniqueResult();
		}
		if (distanceToTaxi < distanceToStand) {
			return t;
		} else  if (distanceToTaxi > distanceToStand){
			List<Taxi> taxisFromStand = (List<Taxi>) getSession()
			.createQuery(
					"SELECT e.taxi FROM Entry e WHERE e.stand.standId = :standId").setParameter("standId", standId).list();
			for (Taxi taxiFromStand: taxisFromStand) {
				if (taxiFromStand.getActualState().compareTo(TaxiState.INSTAND)==0){
					return taxiFromStand;
				}
			}
			return null;
		} else {
			return null;
		}
	}

}