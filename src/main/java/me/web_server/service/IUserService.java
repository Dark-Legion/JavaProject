package me.web_server.service;

public interface IUserService {
    boolean authenticateAdmin(String username, byte[] passwordHash);

	boolean authenticateSeller(String username, byte[] passwordHash);
}
