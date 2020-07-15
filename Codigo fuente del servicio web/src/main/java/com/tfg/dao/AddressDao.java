package com.tfg.dao;

import java.util.List;

import com.tfg.dao.util.GenericDao;
import com.tfg.model.Address;
import com.vividsolutions.jts.geom.Point;

public interface AddressDao extends GenericDao<Address, Long> {
	
	public List<Address> getAddresses(Long cityId);

	public long getNearestAddress(Point position);
	
}
