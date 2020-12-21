package me.web_server.dao;

import java.sql.SQLException;

import me.web_server.SqlQueryException;

public interface IUserDao {
    interface SqlQueryCallable<T> {
        T call() throws SQLException;
    }

    <T> T handleSqlQuery(SqlQueryCallable<T> callable) throws SqlQueryException;

    Void addClient(String username, byte[] passwordHash, String client, boolean isCompany) throws SQLException;

    Void addProduct(String username, byte[] passwordHash, String product, double price) throws SQLException;

    Void addUser(String username, byte[] passwordHash, String newUsername, byte[] newPasswordHash, boolean isAdmin)
        throws SQLException;

    boolean authenticateAdmin(String username, byte[] passwordHash) throws SQLException;

    boolean authenticateSeller(String username, byte[] passwordHash) throws SQLException;

    Object getClientList(String username, byte[] passwordHash, int page) throws SQLException;
}
