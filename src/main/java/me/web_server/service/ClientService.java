package me.web_server.service;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import me.web_server.ServiceRequestException;
import me.web_server.dao.ClientDao;

@Service
public final class ClientService extends GenericService {
	@Autowired
	private ClientDao dao;

	public Void addClient(String username, byte[] passwordHash, String client, boolean isCompany) throws ServiceRequestException {
		return dao.handleSqlQuery(() -> dao.addClient(username, passwordHash, client, isCompany));
	}

	public Void changeClient(String username, byte[] passwordHash, String client, String newName) throws ServiceRequestException {
		return dao.handleSqlQuery(() -> dao.changeClient(username, passwordHash, client, newName));
	}

	public boolean clientExists(String username, byte[] passwordHash, String client) throws ServiceRequestException {
		return dao.handleSqlQuery(() -> dao.clientExists(username, passwordHash, client));
	}

	public Void deleteClient(String username, byte[] passwordHash, String client, String reason) throws ServiceRequestException {
		return dao.handleSqlQuery(() -> dao.deleteClient(username, passwordHash, client, reason));
	}

	public ArrayList<HashMap<String, Object>> getClientList(String username, byte[] passwordHash, int page) throws ServiceRequestException {
		return dao.handleSqlQuery(() -> dao.getClientList(username, passwordHash, page));
	}

	public int getClientListPageCount(String username, byte[] passwordHash) throws ServiceRequestException {
		return dao.handleSqlQuery(() -> dao.getClientListPageCount(username, passwordHash));
	}
}
