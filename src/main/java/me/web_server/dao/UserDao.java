package me.web_server.dao;

import java.sql.Types;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

@Repository
public class UserDao extends JdbcDaoSupport implements IUserDao {
    private static final SqlParameter[] AUTH_USER_PARAMETERS;
    
    static {
        AUTH_USER_PARAMETERS = new SqlParameter[] {
            new SqlParameter("username_arg", Types.VARCHAR),
            new SqlParameter("password_hash_arg", Types.VARBINARY),
        };
    }

    @Autowired
    DataSource dataSource;

    @PostConstruct
    private void initialize() {
        setDataSource(dataSource);
    }

    @Override
    public boolean authenticateAdmin(String username, byte[] passwordHash) {
        Boolean result = SqlHelper.callFunction(
            Boolean.class,
            dataSource,
            "\"private\".\"authenticate_admin\"",
            AUTH_USER_PARAMETERS,
            username,
            passwordHash
        );

        if (result == null) {
            return false;
        }
        
        return result;
    }

    @Override
    public boolean authenticateSeller(String username, byte[] passwordHash) {
        Boolean result = SqlHelper.callFunction(
            Boolean.class,
            dataSource,
            "\"private\".\"authenticate_seller\"",
            AUTH_USER_PARAMETERS,
            username,
            passwordHash
        );

        if (result == null) {
            return false;
        }
        
        return result;
    }
}
