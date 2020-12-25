package me.web_server.dao;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.stereotype.Repository;

@Repository
public class ProductDao extends PostgreSqlDao {
	private final static String ADD_PRODUCT = NAME_PREFIX + "\"add_product\"(?, ?, ?, ?)";
	private final static String CHANGE_PRODUCT = NAME_PREFIX + "\"change_product\"(?, ?, ?, ?, ?)";
	private final static String DELETE_PRODUCT = NAME_PREFIX + "\"delete_product\"(?, ?, ?, ?)";
	private final static String GET_PRODUCT_LIST = NAME_PREFIX + "\"get_product_list\"(?, ?, ?)";
	private final static String GET_PRODUCT_LIST_PAGE_COUNT = NAME_PREFIX + "\"get_product_list_page_count\"(?, ?)";

	private final ThreadLocal<PreparedStatement> addProduct = new ThreadLocal<>();
	private final ThreadLocal<PreparedStatement> changeProduct = new ThreadLocal<>();
	private final ThreadLocal<PreparedStatement> deleteProduct = new ThreadLocal<>();
	private final ThreadLocal<PreparedStatement> getProductList = new ThreadLocal<>();
	private final ThreadLocal<CallableStatement> getProductListPageCount = new ThreadLocal<>();

	private PreparedStatement getAddProduct() throws SQLException {
		PreparedStatement statement = addProduct.get();

		if (statement == null) {
			statement = getDbConnection().prepareStatement("call " + ADD_PRODUCT + ";");
			addProduct.set(statement);
		}

		return statement;
	}

	public Void addProduct(String username, byte[] passwordHash, String product, double price) throws SQLException {
		PreparedStatement statement = getAddProduct();

		setAuthParameters(statement, username, passwordHash, 1);

		statement.setString(3, product);
		statement.setDouble(4, price);

		statement.execute();

		return null;
	}

	private PreparedStatement getChangeProduct() throws SQLException {
		PreparedStatement statement = changeProduct.get();

		if (statement == null) {
			statement = getDbConnection().prepareStatement("call " + CHANGE_PRODUCT + ";");
			changeProduct.set(statement);
		}

		return statement;
	}

	public Void changeProduct(String username, byte[] passwordHash, String product, String newName, Double newPrice) throws SQLException {
		PreparedStatement statement = getChangeProduct();

		setAuthParameters(statement, username, passwordHash, 1);

		statement.setString(3, product);
		statement.setString(4, newName);
		statement.setDouble(5, newPrice);

		statement.execute();

		return null;
	}

	private PreparedStatement getDeleteProduct() throws SQLException {
		PreparedStatement statement = deleteProduct.get();

		if (statement == null) {
			statement = getDbConnection().prepareStatement("call " + DELETE_PRODUCT + ";");
			deleteProduct.set(statement);
		}

		return statement;
	}

	public Void deleteProduct(String username, byte[] passwordHash, String product, String reason) throws SQLException {
		PreparedStatement statement = getDeleteProduct();

		setAuthParameters(statement, username, passwordHash, 1);

		statement.setString(3, product);
		statement.setString(4, reason);

		statement.execute();

		return null;
	}

	private PreparedStatement getGetProductList() throws SQLException {
		PreparedStatement statement = getProductList.get();

		if (statement == null) {
			statement = getDbConnection().prepareStatement("select * from " + GET_PRODUCT_LIST + ";");
			getProductList.set(statement);
		}

		return statement;
	}

	public ArrayList<HashMap<String, Object>> getProductList(String username, byte[] passwordHash, int page) throws SQLException {
		PreparedStatement statement = getGetProductList();

		setAuthParameters(statement, username, passwordHash, 1);
		statement.setInt(3, page);

		return mapResultSet(statement.executeQuery());
	}

	private CallableStatement getGetProductListPageCount() throws SQLException {
		CallableStatement statement = getProductListPageCount.get();

		if (statement == null) {
			statement = getDbConnection().prepareCall("{ ? = call " + GET_PRODUCT_LIST_PAGE_COUNT + " }");
			statement.registerOutParameter(1, Types.INTEGER);
			getProductListPageCount.set(statement);
		}

		return statement;
	}

	public int getProductListPage(String username, byte[] passwordHash) throws SQLException {
		CallableStatement statement = getGetProductListPageCount();

		setAuthParameters(statement, username, passwordHash, 2);

		statement.execute();

		return statement.getInt(1);
	}
}
