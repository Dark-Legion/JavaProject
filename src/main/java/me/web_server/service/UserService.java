package me.web_server.service;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import me.web_server.SqlQueryException;
import me.web_server.dao.IUserDao;

@Service
public class UserService implements IUserService {
    @Autowired
    private IUserDao dao;

    @Override
    public HashMap<String, Object> handleRestRequest(Callable callable) throws SqlQueryException {
        Object result_data = callable.call();

        HashMap<String, Object> result = new HashMap<>();

        result.put("success", true);
        result.put("error", null);
        result.put("result", result_data);

        return result;
    }

    @Override
    public Void addClient(String username, byte[] passwordHash, String client, boolean isCompany) throws SqlQueryException {
        return dao.handleSqlQuery(() -> dao.addClient(username, passwordHash, client, isCompany));
    }

    @Override
    public Void addProduct(String username, byte[] passwordHash, String product, double price) throws SqlQueryException {
        return dao.handleSqlQuery(() -> dao.addProduct(username, passwordHash, product, price));
    }

    @Override
    public Void addUser(String username, byte[] passwordHash, String user, byte[] hash, boolean isAdmin)
        throws SqlQueryException {
        return dao.handleSqlQuery(() -> dao.addUser(username, passwordHash, user, hash, isAdmin));
    }

    @Override
    public boolean authenticateAdmin(String username, byte[] passwordHash) throws SqlQueryException {
        return dao.handleSqlQuery(() -> dao.authenticateAdmin(username, passwordHash));
    }

    @Override
    public boolean authenticateSeller(String username, byte[] passwordHash) throws SqlQueryException {
        return dao.handleSqlQuery(() -> dao.authenticateSeller(username, passwordHash));
    }

    @Override
    public Object getClientList(String username, byte[] passwordHash, int page) throws SqlQueryException {
        return dao.handleSqlQuery(() -> dao.getClientList(username, passwordHash, page));
    }
}
