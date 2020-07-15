package com.tfg.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.tfg.dao.util.GenericDaoHibernate;
import com.tfg.model.Entry;

@Repository("entryDao")
public class EntryDaoHibernate extends GenericDaoHibernate<Entry, Long>
		implements EntryDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<Entry> getEntries(Long standId) {
		return getSession()
				.createQuery(
						"SELECT e FROM Entry e WHERE e.stand.standId = :standId ORDER BY e.arrival")
				.setParameter("standId", standId).list();
	}

}