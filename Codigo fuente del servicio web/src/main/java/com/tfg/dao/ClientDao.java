package com.tfg.dao;

import java.util.List;

import com.tfg.dao.util.GenericDao;
import com.tfg.model.Client;

public interface ClientDao extends GenericDao<Client, Long> {
	
	public List<Client> getClients();
	
	public List<Client> getClientsWaiting();

	public Client getFirstClient() throws Exception;
	
}
