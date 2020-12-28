package me.web_server.dao;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.stereotype.Repository;

@Repository
public class UserDao extends PostgreSqlDao {
	private final static String ADD_USER = NAME_PREFIX + "\"add_user\"(?, ?, ?, ?, ?)";
	private final static String CHANGE_USER_NAME = NAME_PREFIX + "\"change_user\"(?, ?, ?, ?, ?)";
	private final static String CHANGE_USER_PASSWORD = NAME_PREFIX + "\"change_user_password\"(?, ?, ?)";
	private final static String DELETE_USER = NAME_PREFIX + "\"delete_user\"(?, ?, ?, ?)";
	private final static String GET_USER_LIST = NAME_PREFIX + "\"get_user_list\"(?, ?, ?)";
	private final static String GET_USER_LIST_PAGE_COUNT = NAME_PREFIX + "\"get_user_list_page_count\"(?, ?)";
	private final static String SELLER_EXISTS = NAME_PREFIX + "\"seller_exists\"(?, ?, ?)";
	private final static String USER_EXISTS = NAME_PREFIX + "\"user_exists\"(?, ?, ?)";

	private final ThreadLocal<PreparedStatement> addUser = new ThreadLocal<>();
	private final ThreadLocal<PreparedStatement> changeUserName = new ThreadLocal<>();
	private final ThreadLocal<PreparedStatement> changeUserPassword = new ThreadLocal<>();
	private final ThreadLocal<PreparedStatement> deleteUser = new ThreadLocal<>();
	private final ThreadLocal<PreparedStatement> getUserList = new ThreadLocal<>();
	private final ThreadLocal<CallableStatement> getUserListPageCount = new ThreadLocal<>();
	private final ThreadLocal<CallableStatement> sellerExists = new ThreadLocal<>();
	private final ThreadLocal<CallableStatement> userExists = new ThreadLocal<>();

	private PreparedStatement getAddUser() throws SQLException {
		PreparedStatement statement = addUser.get();

		if (statement == null) {
			statement = getDbConnection().prepareCall("call " + ADD_USER + ";");
			addUser.set(statement);
		}

		return statement;
	}

	public Void addUser(String username, byte[] passwordHash, String newName, byte[] newPasswordHash,
			boolean isAdmin) throws SQLException {
		PreparedStatement statement = getAddUser();

		setAuthParameters(statement, username, passwordHash, 1);
		setAuthParameters(statement, newName, newPasswordHash, 3);
		statement.setBoolean(5, isAdmin);

		statement.execute();

		return null;
	}

	private PreparedStatement getChangeUserName() throws SQLException {
		PreparedStatement statement = changeUserName.get();

		if (statement == null) {
			statement = getDbConnection().prepareStatement("call " + CHANGE_USER_NAME + ";");
			changeUserName.set(statement);
		}

		return statement;
	}

	public Void changeUser(String username, byte[] passwordHash, String user, String newName, byte[] newPasswordHash) throws SQLException {
		PreparedStatement statement = getChangeUserName();

		setAuthParameters(statement, username, passwordHash, 1);
		statement.setString(3, user);
		statement.setString(4, newName);
		statement.setBytes(5, newPasswordHash);

		statement.execute();

		return null;
	}

	private PreparedStatement getChangeUserPassword() throws SQLException {
		PreparedStatement statement = changeUserPassword.get();

		if (statement == null) {
			statement = getDbConnection().prepareStatement("call " + CHANGE_USER_PASSWORD + ";");
			changeUserPassword.set(statement);
		}

		return statement;
	}

	public Void changeUserPassword(String username, byte[] passwordHash, byte[] newPasswordHash) throws SQLException {
		PreparedStatement statement = getChangeUserPassword();

		setAuthParameters(statement, username, passwordHash, 1);
		statement.setBytes(3, newPasswordHash);

		statement.execute();

		return null;
	}

	private PreparedStatement getDeleteUser() throws SQLException {
		PreparedStatement statement = deleteUser.get();

		if (statement == null) {
			statement = getDbConnection().prepareStatement("call " + DELETE_USER + ";");
			deleteUser.set(statement);
		}

		return statement;
	}

	public Void deleteUser(String username, byte[] passwordHash, String user, String reason) throws SQLException {
		PreparedStatement statement = getDeleteUser();

		setAuthParameters(statement, username, passwordHash, 1);
		statement.setString(3, user);
		statement.setString(4, reason);

		statement.execute();

		return null;
	}

	private PreparedStatement getGetUserList() throws SQLException {
		PreparedStatement statement = getUserList.get();

		if (statement == null) {
			statement = getDbConnection().prepareStatement("select * from " + GET_USER_LIST + ";");
			getUserList.set(statement);
		}

		return statement;
	}

	public ArrayList<HashMap<String, Object>> getUserList(String username, byte[] passwordHash, int page) throws SQLException {
		PreparedStatement statement = getGetUserList();

		setAuthParameters(statement, username, passwordHash, 1);
		statement.setInt(3, page);

		return mapResultSet(statement.executeQuery());
	}

	private CallableStatement getGetUserListPageCount() throws SQLException {
		CallableStatement statement = getUserListPageCount.get();

		if (statement == null) {
			statement = getDbConnection().prepareCall("{ ? = call " + GET_USER_LIST_PAGE_COUNT + " }");
			statement.registerOutParameter(1, Types.INTEGER);
			getUserListPageCount.set(statement);
		}

		return statement;
	}

	public int getUserListPageCount(String username, byte[] passwordHash) throws SQLException {
		CallableStatement statement = getGetUserListPageCount();

		setAuthParameters(statement, username, passwordHash, 2);

		statement.execute();

		return statement.getInt(1);
	}

	private CallableStatement getSellerExists() throws SQLException {
		CallableStatement statement = sellerExists.get();

		if (statement == null) {
			statement = getDbConnection().prepareCall("{ ? = call " + SELLER_EXISTS + " }");
			statement.registerOutParameter(1, Types.BOOLEAN);
			sellerExists.set(statement);
		}

		return statement;
	}

	public boolean sellerExists(String username, byte[] passwordHash, String seller) throws SQLException {
		CallableStatement statement = getSellerExists();

		setAuthParameters(statement, username, passwordHash, 2);
		statement.setString(4, seller);

		statement.execute();

		return statement.getBoolean(1);
	}

	private CallableStatement getUserExists() throws SQLException {
		CallableStatement statement = userExists.get();

		if (statement == null) {
			statement = getDbConnection().prepareCall("{ ? = call " + USER_EXISTS + " }");
			statement.registerOutParameter(1, Types.BOOLEAN);
			userExists.set(statement);
		}

		return statement;
	}

	public boolean userExists(String username, byte[] passwordHash, String user) throws SQLException {
		CallableStatement statement = getUserExists();

		setAuthParameters(statement, username, passwordHash, 2);
		statement.setString(4, user);

		statement.execute();

		return statement.getBoolean(1);
	}
}
