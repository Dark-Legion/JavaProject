package me.web_server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService implements IUserService {
    @Autowired
    private IUserDao dao;

    @Override
    public boolean authenticateAdmin(String username, byte[] passwordHash) {
        return dao.authenticateAdmin(username, passwordHash);
    }

    @Override
    public boolean authenticateSeller(String username, byte[] passwordHash) {
        return dao.authenticateSeller(username, passwordHash);
    }
}
