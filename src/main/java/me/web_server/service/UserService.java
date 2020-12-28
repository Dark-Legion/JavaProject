package me.web_server.service;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import me.web_server.ServiceRequestException;
import me.web_server.dao.UserDao;

@Service
public final class UserService extends GenericService {
	@Autowired
	private UserDao dao;

	public Void addUser(String username, byte[] passwordHash, String newName, byte[] newPasswordHash, boolean isAdmin)
		throws ServiceRequestException {
		return dao.handleSqlQuery(() -> dao.addUser(username, passwordHash, newName, newPasswordHash, isAdmin));
	}

	public Void changeUser(String username, byte[] passwordHash, String user, String newName, byte[] newPasswordHash)
		throws ServiceRequestException {
		return dao.handleSqlQuery(() -> dao.changeUser(username, passwordHash, user, newName, newPasswordHash));
	}

	public Void changeUserPassword(String username, byte[] passwordHash, byte[] newPasswordHash)
		throws ServiceRequestException {
		return dao.handleSqlQuery(() -> dao.changeUserPassword(username, passwordHash, newPasswordHash));
	}

	public Void deleteUser(String username, byte[] passwordHash, String user, String reason)
		throws ServiceRequestException {
		return dao.handleSqlQuery(() -> dao.deleteUser(username, passwordHash, user, reason));
	}

	public ArrayList<HashMap<String, Object>> getUserList(String username, byte[] passwordHash, int page)
		throws ServiceRequestException {
		return dao.handleSqlQuery(() -> dao.getUserList(username, passwordHash, page));
	}

	public int getUserListPageCount(String username, byte[] passwordHash)
		throws ServiceRequestException {
		return dao.handleSqlQuery(() -> dao.getUserListPageCount(username, passwordHash));
	}

	public boolean sellerExists(String username, byte[] passwordHash, String seller)
		throws ServiceRequestException {
		return dao.handleSqlQuery(() -> dao.sellerExists(username, passwordHash, seller));
	}

	public boolean userExists(String username, byte[] passwordHash, String user)
		throws ServiceRequestException {
		return dao.handleSqlQuery(() -> dao.userExists(username, passwordHash, user));
	}
}
