package me.web_server.dao;

public interface IUserDao {
    boolean authenticateAdmin(String username, byte[] passwordHash);

    boolean authenticateSeller(String username, byte[] passwordHash);
}
