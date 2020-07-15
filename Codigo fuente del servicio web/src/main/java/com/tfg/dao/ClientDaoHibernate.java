package com.tfg.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.tfg.dao.util.GenericDaoHibernate;
import com.tfg.model.Client;

@Repository("clientDao")
public class ClientDaoHibernate extends GenericDaoHibernate<Client, Long>
		implements ClientDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<Client> getClients() {
		return getSession().createQuery("FROM Client c WHERE c.clientState <> 2").list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Client> getClientsWaiting() {
		return getSession().createQuery(
				"SELECT c FROM Client c WHERE c.clientState = 0").list();
	}

	@Override
	public Client getFirstClient() throws Exception {
		Client client = (Client) getSession()
				.createQuery("SELECT c FROM Client c WHERE c.clientState = 0")
				.setMaxResults(1).uniqueResult();
		if (client!=null) {
			return client;
		} else {
			throw new Exception("No client found");
		}
		
	}

}