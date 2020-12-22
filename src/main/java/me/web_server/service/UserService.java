package me.web_server.service;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import me.web_server.SqlQueryException;
import me.web_server.dao.UserDao;

@Service
public class UserService {
    public interface SqlQueryCallable {
        Object call() throws SqlQueryException;
    }

    @Autowired
    private UserDao dao;

    public HashMap<String, Object> handleRestRequest(SqlQueryCallable callable) throws SqlQueryException {
        Object result_data = callable.call();

        HashMap<String, Object> result = new HashMap<>();

        result.put("success", true);
        result.put("error", null);
        result.put("result", result_data);

        return result;
    }

    public Void addClient(String username, byte[] passwordHash, String client, boolean isCompany) throws SqlQueryException {
        return dao.handleSqlQuery(() -> dao.addClient(username, passwordHash, client, isCompany));
    }

    public Void addProduct(String username, byte[] passwordHash, String product, double price) throws SqlQueryException {
        return dao.handleSqlQuery(() -> dao.addProduct(username, passwordHash, product, price));
    }

    public Void addUser(String username, byte[] passwordHash, String user, byte[] hash, boolean isAdmin)
        throws SqlQueryException {
        return dao.handleSqlQuery(() -> dao.addUser(username, passwordHash, user, hash, isAdmin));
    }

    public boolean authenticateAdmin(String username, byte[] passwordHash) throws SqlQueryException {
        return dao.handleSqlQuery(() -> dao.authenticateAdmin(username, passwordHash));
    }

    public boolean authenticateSeller(String username, byte[] passwordHash) throws SqlQueryException {
        return dao.handleSqlQuery(() -> dao.authenticateSeller(username, passwordHash));
    }

    public Object getClientList(String username, byte[] passwordHash, int page) throws SqlQueryException {
        return dao.handleSqlQuery(() -> dao.getClientList(username, passwordHash, page));
    }

    public int getClientListPageCount(String username, byte[] passwordHash) throws SqlQueryException {
        return dao.handleSqlQuery(() -> dao.getClientListPageCount(username, passwordHash));
    }
}
