package me.web_server.dao;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.stereotype.Repository;

@Repository
public class ClientDao extends PostgreSqlDao {
	private final static String ADD_CLIENT = NAME_PREFIX + "\"add_client\"(?, ?, ?, ?)";
	private final static String CHANGE_CLIENT = NAME_PREFIX + "\"change_client\"(?, ?, ?, ?)";
	private final static String DELETE_CLIENT = NAME_PREFIX + "\"delete_client\"(?, ?, ?, ?)";
	private final static String GET_CLIENT_LIST = NAME_PREFIX + "\"get_client_list\"(?, ?, ?)";
	private final static String GET_CLIENT_LIST_PAGE_COUNT = NAME_PREFIX + "\"get_client_list_page_count\"(?, ?)";

	private final ThreadLocal<PreparedStatement> addClient = new ThreadLocal<>();
	private final ThreadLocal<PreparedStatement> changeClient = new ThreadLocal<>();
	private final ThreadLocal<PreparedStatement> deleteClient = new ThreadLocal<>();
	private final ThreadLocal<PreparedStatement> getClientList = new ThreadLocal<>();
	private final ThreadLocal<CallableStatement> getClientListPageCount = new ThreadLocal<>();

	private PreparedStatement getAddClient() throws SQLException {
		PreparedStatement statement = addClient.get();

		if (statement == null) {
			statement = getDbConnection().prepareStatement("call " + ADD_CLIENT + ";");
			addClient.set(statement);
		}

		return statement;
	}

	public Void addClient(String username, byte[] passwordHash, String client, boolean isCompany) throws SQLException {
		PreparedStatement statement = getAddClient();

		setAuthParameters(statement, username, passwordHash, 1);
		statement.setString(3, client);
		statement.setBoolean(4, isCompany);

		statement.execute();

		return null;
	}

	private PreparedStatement getChangeClient() throws SQLException {
		PreparedStatement statement = changeClient.get();

		if (statement == null) {
			statement = getDbConnection().prepareStatement("call " + CHANGE_CLIENT + ";");
			changeClient.set(statement);
		}

		return statement;
	}

	public Void changeClient(String username, byte[] passwordHash, String client, String newName) throws SQLException {
		PreparedStatement statement = getChangeClient();

		setAuthParameters(statement, username, passwordHash, 1);
		statement.setString(3, client);
		statement.setString(4, newName);

		statement.execute();

		return null;
	}

	private PreparedStatement getDeleteClient() throws SQLException {
		PreparedStatement statement = deleteClient.get();

		if (statement == null) {
			statement = getDbConnection().prepareStatement("call " + DELETE_CLIENT + ";");
			deleteClient.set(statement);
		}

		return statement;
	}

	public Void deleteClient(String username, byte[] passwordHash, String client, String reason) throws SQLException {
		PreparedStatement statement = getDeleteClient();

		setAuthParameters(statement, username, passwordHash, 1);
		statement.setString(3, client);
		statement.setString(4, reason);

		statement.execute();

		return null;
	}

	private PreparedStatement getGetClientList() throws SQLException {
		PreparedStatement statement = getClientList.get();

		if (statement == null) {
			statement = getDbConnection().prepareStatement("select * from " + GET_CLIENT_LIST + ";");
			getClientList.set(statement);
		}

		return statement;
	}

	public ArrayList<HashMap<String, Object>> getClientList(String username, byte[] passwordHash, int page) throws SQLException {
		PreparedStatement statement = getGetClientList();

		setAuthParameters(statement, username, passwordHash, 1);
		statement.setInt(3, page);

		return mapResultSet(statement.executeQuery());
	}

	private CallableStatement getGetClientListPageCount() throws SQLException {
		CallableStatement statement = getClientListPageCount.get();

		if (statement == null) {
			statement = getDbConnection().prepareCall("{ ? = call " + GET_CLIENT_LIST_PAGE_COUNT + " }");
			statement.registerOutParameter(1, Types.INTEGER);
			getClientListPageCount.set(statement);
		}

		return statement;
	}

	public int getClientListPageCount(String username, byte[] passwordHash) throws SQLException {
		CallableStatement statement = getGetClientListPageCount();

		setAuthParameters(statement, username, passwordHash, 2);

		statement.execute();

		return statement.getInt(1);
	}
}
