package me.web_server.service;

import java.util.HashMap;

import me.web_server.SqlQueryException;

public interface IUserService {
    interface Callable {
        Object call() throws SqlQueryException;
    }

    HashMap<String, Object> handleRestRequest(Callable callable) throws SqlQueryException;

    Void addClient(String username, byte[] passwordHash, String client, boolean isCompany) throws SqlQueryException;
   
    Void addProduct(String username, byte[] passwordHash, String product, double price) throws SqlQueryException;
   
    Void addUser(String username, byte[] passwordHash, String user, byte[] hash, boolean isAdmin) throws SqlQueryException;

    boolean authenticateAdmin(String username, byte[] passwordHash) throws SqlQueryException;

    boolean authenticateSeller(String username, byte[] passwordHash) throws SqlQueryException;

    Object getClientList(String username, byte[] passwordHash, int page) throws SqlQueryException;
}
