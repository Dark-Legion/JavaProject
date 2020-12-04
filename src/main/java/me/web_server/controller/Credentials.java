package me.web_server.controller;

import me.web_server.service.IUserService;

public class Credentials {
    private Boolean admin;
    private String username;
    private byte[] passwordHash;

    public Credentials() {
        super();

        admin = null;
        username = null;
        passwordHash = null;
    }

    public Credentials(boolean admin) {
        super();

        this.admin = admin;
        this.username = null;
        this.passwordHash = null;
    }

    public Credentials(boolean admin, String username, byte[] passwordHash) {
        super();

        this.admin = Boolean.valueOf(admin);
        this.username = username;
        this.passwordHash = passwordHash;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public byte[] getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(byte[] passwordHash) {
        this.passwordHash = passwordHash;
    }

    public boolean authenticateAdmin(IUserService userService) {
        if (username != null && passwordHash != null) {
            return userService.authenticateAdmin(username, passwordHash);
        } else {
            return false;
        }
    }
}
