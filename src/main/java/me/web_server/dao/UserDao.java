package me.web_server.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import me.web_server.SqlQueryException;

@Repository
public class UserDao extends JdbcDaoSupport {
    public interface SqlQueryCallable<T> {
        T call() throws SQLException;
    }

    private final static Logger LOGGER = Logger.getLogger(UserDao.class.getName());

    private final static String NAME_PREFIX = "\"application\".";

    private final static String ADD_CLIENT_FUNCTION = NAME_PREFIX + "\"add_client\"(?, ?, ?, ?)";
    private final static String ADD_PRODUCT_FUNCTION = NAME_PREFIX + "\"add_product\"(?, ?, ?, ?)";
    private final static String ADD_USER_FUNCTION = NAME_PREFIX + "\"add_user\"(?, ?, ?, ?, ?)";
    private final static String AUTHENTICATE_ADMIN_FUNCTION = NAME_PREFIX + "\"authenticate_admin\"(?, ?)";
    private final static String AUTHENTICATE_SELLER_FUNCTION = NAME_PREFIX + "\"authenticate_seller\"(?, ?)";
    private final static String GET_CLIENT_LIST_FUNCTION = NAME_PREFIX + "\"get_client_list\"(?, ?, ?)";
    private final static String GET_CLIENT_LIST_PAGE_COUNT_FUNCTION = NAME_PREFIX + "\"get_client_list_page_count\"(?, ?)";

    private Connection connection;

    private final ThreadLocal<PreparedStatement> addClient;
    private final ThreadLocal<PreparedStatement> addProduct;
    private final ThreadLocal<PreparedStatement> addUser;
    private final ThreadLocal<CallableStatement> authenticateAdmin;
    private final ThreadLocal<CallableStatement> authenticateSeller;
    private final ThreadLocal<PreparedStatement> getClientList;
    private final ThreadLocal<CallableStatement> getClientListPageCount;

    public UserDao() {
        addClient = new ThreadLocal<>();
        addProduct = new ThreadLocal<>();
        addUser = new ThreadLocal<>();
        authenticateAdmin = new ThreadLocal<>();
        authenticateSeller = new ThreadLocal<>();
        getClientList = new ThreadLocal<>();
        getClientListPageCount = new ThreadLocal<>();
    }

    @Autowired
    DataSource dataSource;

    @PostConstruct
    private void initialize() throws SQLException {
        setDataSource(dataSource);

        connection = dataSource.getConnection();
    }

    private static boolean isInternalError(SQLException exception) {
        return !exception.getMessage().matches("^ERROR\\: .+\\s+Where\\: .+ at RAISE$");
    }

    private static String getSqlExceptionMessage(SQLException exception) {
        return exception.getMessage().replaceAll("(?:ERROR\\: |\\s+Where\\: .* at RAISE$)", "");
    }

    public <T> T handleSqlQuery(SqlQueryCallable<T> callable) throws SqlQueryException {
        try {
            return callable.call();
        } catch (SQLException exception) {
            if (isInternalError(exception)) {
                LOGGER.severe("Error occured while authenticating seller! [" + exception.getMessage() + "]");
                
                throw new SqlQueryException("Internal error occured!");
            } else {
                throw new SqlQueryException(getSqlExceptionMessage(exception));
            }
        }
    }

    private static void setAuthUser(PreparedStatement statement, String username, int offset) throws SQLException {
        statement.setString(offset, username);
    }

    private static void setAuthPassword(PreparedStatement statement, byte[] passwordHash, int offset) throws SQLException {
        statement.setBytes(offset, passwordHash);
    }

    private static void setAuthParameters(PreparedStatement statement, String username, byte[] passwordHash, int offset) throws SQLException {
        setAuthUser(statement, username, offset);
        setAuthPassword(statement, passwordHash, offset + 1);
    }

    private PreparedStatement getAddClient() throws SQLException {
        PreparedStatement statement = addClient.get();

        if (statement == null) {
            statement = connection.prepareStatement("call " + ADD_CLIENT_FUNCTION + ";");
            addUser.set(statement);
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

    private PreparedStatement getAddProduct() throws SQLException {
        PreparedStatement statement = addProduct.get();

        if (statement == null) {
            statement = connection.prepareStatement("call " + ADD_PRODUCT_FUNCTION + ";");
            addUser.set(statement);
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

    private PreparedStatement getAddUser() throws SQLException {
        PreparedStatement statement = addUser.get();

        if (statement == null) {
            statement = connection.prepareStatement("call " + ADD_USER_FUNCTION + ";");
            addUser.set(statement);
        }

        return statement;
    }

    public Void addUser(String username, byte[] passwordHash, String newUsername, byte[] newPasswordHash,
            boolean isAdmin) throws SQLException {
        PreparedStatement statement = getAddUser();

        setAuthParameters(statement, username, passwordHash, 1);

        setAuthParameters(statement, newUsername, newPasswordHash, 3);

        statement.setBoolean(5, isAdmin);

        statement.execute();

        return null;
    }

    private boolean authenticateUser(CallableStatement statement, String username, byte[] passwordHash) throws SQLException {
        setAuthParameters(statement, username, passwordHash, 2);

        statement.execute();

        return statement.getBoolean(1);
    }

    private CallableStatement getAuthAdmin() throws SQLException {
        CallableStatement statement = authenticateAdmin.get();

        if (statement == null) {
            statement = connection.prepareCall("{ ? = call " + AUTHENTICATE_ADMIN_FUNCTION + " }");
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
            statement = connection.prepareCall("{ ? = call " + AUTHENTICATE_SELLER_FUNCTION + " }");
            authenticateSeller.set(statement);
        }

        return statement;
    }

    public boolean authenticateSeller(String username, byte[] passwordHash) throws SQLException {
        return authenticateUser(getAuthSeller(), username, passwordHash);
    }

    private PreparedStatement getGetClientList() throws SQLException {
        PreparedStatement statement = getClientList.get();

        if (statement == null) {
            statement = connection.prepareStatement("select * from " + GET_CLIENT_LIST_FUNCTION + ";");
            getClientList.set(statement);
        }

        return statement;
    }

    public Map<String, List<Object>> getClientList(String username, byte[] passwordHash, int page) throws SQLException {
        PreparedStatement statement = getGetClientList();

        setAuthParameters(statement, username, passwordHash, 1);

        statement.setInt(3, page);

        ResultSet set = statement.executeQuery();
        
        HashMap<String, List<Object>> map = new HashMap<>();

        int columns = set.getMetaData().getColumnCount();

        for (int z = 0; z < columns; ++z) {
            map.put(set.getMetaData().getColumnName(z + 1), new ArrayList<>());
        }

        while (set.next()) {
            for (String key : map.keySet()) {
                map.get(key).add(set.getObject(key));
            }
        }
        
        return map;
    }

    private CallableStatement getGetClientListPageCount() throws SQLException {
        CallableStatement statement = getClientListPageCount.get();

        if (statement == null) {
            statement = connection.prepareCall("{ ? = call " + GET_CLIENT_LIST_PAGE_COUNT_FUNCTION + " }");
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
