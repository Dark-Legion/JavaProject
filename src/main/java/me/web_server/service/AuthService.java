package me.web_server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import me.web_server.ServiceRequestException;
import me.web_server.dao.AuthDao;

@Service
public final class AuthService extends GenericService {
	@Autowired
	private AuthDao dao;

	public Boolean authenticateAdmin(String username, byte[] passwordHash)
		throws ServiceRequestException {
		return dao.handleSqlQuery(() -> dao.authenticateAdmin(username, passwordHash));
	}

	public Boolean authenticateSeller(String username, byte[] passwordHash)
		throws ServiceRequestException {
		return dao.handleSqlQuery(() -> dao.authenticateSeller(username, passwordHash));
	}
}
