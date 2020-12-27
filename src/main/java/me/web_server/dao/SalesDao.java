package me.web_server.dao;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Pattern;

import org.springframework.stereotype.Repository;

import me.web_server.model.SaleUnit;

@Repository
public class SalesDao extends PostgreSqlDao {
	private final static String ADD_SALE = NAME_PREFIX + "\"add_sale\"(?, ?, ?, ?)";
	private final static String GET_SALES_REPORT = NAME_PREFIX + "\"get_sales_report\"(?, ?, ?, ?, ?)";
	private final static String GET_SALES_REPORT_PAGE_COUNT = NAME_PREFIX + "\"get_sales_report_page_count\"(?, ?, ?, ?)";
	private final static String GET_SALES_REPORT_FOR_SELLER = NAME_PREFIX + "\"get_sales_report_for_seller\"(?, ?, ?, ?, ?, ?)";
	private final static String GET_SALES_REPORT_FOR_SELLER_PAGE_COUNT = NAME_PREFIX + "\"get_sales_report_for_seller_page_count\"(?, ?, ?, ?, ?)";

	private final ThreadLocal<PreparedStatement> addSale = new ThreadLocal<>();
	private final ThreadLocal<PreparedStatement> getSalesReport = new ThreadLocal<>();
	private final ThreadLocal<CallableStatement> getSalesReportPageCount = new ThreadLocal<>();
	private final ThreadLocal<PreparedStatement> getSalesReportForSeller = new ThreadLocal<>();
	private final ThreadLocal<CallableStatement> getSalesReportForSellerPageCount = new ThreadLocal<>();

	protected SalesDao() {
		typeMapping.put(
			Pattern.compile("^\\((?:\\\".+\\\"|.+),(?:\\d+(?:\\.(?:\\d+)?)?|\\.\\d+),\\d+\\)$"),
			(String value) -> {
				value = value.substring(1, value.length() - 1);

				int indexRight = value.lastIndexOf(',', value.length());

				int indexLeft = value.lastIndexOf(',', indexRight - 1);

				String left = value.substring((value.startsWith("\"") ? 1 : 0), indexLeft - (value.startsWith("\"") ? 1 : 0));
				String middle = value.substring(indexLeft + 1, indexRight);
				String right = value.substring(indexRight + 1);

				left = left.replaceAll("\"\"", "\"");
				left = left.replaceAll("\\\\\\\\", "\\\\");

				return SaleUnit.load(left, Double.parseDouble(middle), Integer.parseInt(right));
			}
		);
	}

	private java.sql.Date toSqlDate(Date date) {
		return new java.sql.Date(date.getTime());
	}

	private PreparedStatement getAddSale() throws SQLException {
		PreparedStatement statement = addSale.get();

		if (statement == null) {
			statement = getDbConnection().prepareStatement("call " + ADD_SALE + ";");
			addSale.set(statement);
		}

		return statement;
	}

	public Void addSale(
		String username,
		byte[] passwordHash,
		String client,
		SaleUnit[] saleUnits
	) throws SQLException {
		PreparedStatement statement = getAddSale();

		setAuthParameters(statement, username, passwordHash, 1);
		statement.setString(3, client);
		statement.setArray(4, getDbConnection().createArrayOf("\"application\".\"sale_unit_type\"", saleUnits));

		statement.execute();

		return null;
	}

	private PreparedStatement getGetSalesReport() throws SQLException {
		PreparedStatement statement = getSalesReport.get();

		if (statement == null) {
			statement = getDbConnection().prepareStatement("select * from " + GET_SALES_REPORT + ";");
			addSale.set(statement);
		}

		return statement;
	}

	public ArrayList<HashMap<String, Object>> getSalesReport(
		String username,
		byte[] passwordHash,
		Date start,
		Date end,
		int page
	) throws SQLException {
		PreparedStatement statement = getGetSalesReport();

		setAuthParameters(statement, username, passwordHash, 1);
		statement.setDate(3, toSqlDate(start));
		statement.setDate(4, toSqlDate(end));
		statement.setInt(5, page);

		return mapResultSet(statement.executeQuery());
	}

	private CallableStatement getGetSalesReportPageCount() throws SQLException {
		CallableStatement statement = getSalesReportPageCount.get();

		if (statement == null) {
			statement = getDbConnection().prepareCall("{ ? = call " + GET_SALES_REPORT_PAGE_COUNT + " }");
			statement.registerOutParameter(1, Types.INTEGER);
			addSale.set(statement);
		}

		return statement;
	}

	public int getSalesReportPageCount(String username, byte[] passwordHash, Date start, Date end) throws SQLException {
		CallableStatement statement = getGetSalesReportPageCount();

		setAuthParameters(statement, username, passwordHash, 2);
		statement.setDate(4, toSqlDate(start));
		statement.setDate(5, toSqlDate(end));

		statement.execute();

		return statement.getInt(1);
	}

	private PreparedStatement getGetSalesReportForSeller() throws SQLException {
		PreparedStatement statement = getSalesReportForSeller.get();

		if (statement == null) {
			statement = getDbConnection().prepareStatement("select * from " + GET_SALES_REPORT_FOR_SELLER + ";");
			addSale.set(statement);
		}

		return statement;
	}

	public ArrayList<HashMap<String, Object>> getSalesReportForSeller(
		String username,
		byte[] passwordHash,
		String seller,
		Date start,
		Date end,
		int page
	) throws SQLException {
		PreparedStatement statement = getGetSalesReportForSeller();

		setAuthParameters(statement, username, passwordHash, 1);
		statement.setString(3, seller);
		statement.setDate(4, toSqlDate(start));
		statement.setDate(5, toSqlDate(end));
		statement.setInt(6, page);

		return mapResultSet(statement.executeQuery());
	}

	private CallableStatement getGetSalesReportForSellerPageCount() throws SQLException {
		CallableStatement statement = getSalesReportForSellerPageCount.get();

		if (statement == null) {
			statement = getDbConnection().prepareCall("{ ? = call " + GET_SALES_REPORT_FOR_SELLER_PAGE_COUNT + " }");
			statement.registerOutParameter(1, Types.INTEGER);
			addSale.set(statement);
		}

		return statement;
	}

	public int getSalesReportForSellerPageCount(
		String username,
		byte[] passwordHash,
		String seller,
		Date start,
		Date end
	) throws SQLException {
		CallableStatement statement = getGetSalesReportForSellerPageCount();

		setAuthParameters(statement, username, passwordHash, 2);
		statement.setString(4, seller);
		statement.setDate(5, toSqlDate(start));
		statement.setDate(6, toSqlDate(end));

		statement.execute();

		return statement.getInt(1);
	}
}
