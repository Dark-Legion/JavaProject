package me.web_server.dao;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Types;

import org.springframework.stereotype.Repository;

@Repository
public class AuthDao extends PostgreSqlDao {
	private final static String AUTHENTICATE_ADMIN = NAME_PREFIX + "\"authenticate_admin\"(?, ?)";
	private final static String AUTHENTICATE_SELLER = NAME_PREFIX + "\"authenticate_seller\"(?, ?)";

	private final ThreadLocal<CallableStatement> authenticateAdmin = new ThreadLocal<>();
	private final ThreadLocal<CallableStatement> authenticateSeller = new ThreadLocal<>();

	private boolean authenticateUser(CallableStatement statement, String username, byte[] passwordHash) throws SQLException {
		setAuthParameters(statement, username, passwordHash, 2);

		statement.execute();

		return statement.getBoolean(1);
	}

	private CallableStatement getAuthAdmin() throws SQLException {
		CallableStatement statement = authenticateAdmin.get();

		if (statement == null) {
			statement = getDbConnection().prepareCall("{ ? = call " + AUTHENTICATE_ADMIN + " }");
			statement.registerOutParameter(1, Types.BOOLEAN);
			authenticateAdmin.set(statement);
		}

		return statement;
	}

	public boolean authenticateAdmin(String username, byte[] passwordHash) throws SQLException {
		return authenticateUser(getAuthAdmin(), username, passwordHash);
	}

	private CallableStatement getAuthSeller() throws SQLException {
		CallableStatement statement = authenticateSeller.get();

		if (statement == null) {
			statement = getDbConnection().prepareCall("{ ? = call " + AUTHENTICATE_SELLER + " }");
			statement.registerOutParameter(1, Types.BOOLEAN);
			authenticateSeller.set(statement);
		}

		return statement;
	}

	public boolean authenticateSeller(String username, byte[] passwordHash) throws SQLException {
		return authenticateUser(getAuthSeller(), username, passwordHash);
	}
}
