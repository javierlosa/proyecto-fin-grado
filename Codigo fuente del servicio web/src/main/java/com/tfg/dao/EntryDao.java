package com.tfg.dao;

import java.util.List;

import com.tfg.dao.util.GenericDao;
import com.tfg.model.Entry;

public interface EntryDao extends GenericDao<Entry, Long> {
	
	public List<Entry> getEntries(Long standId);
	
}
