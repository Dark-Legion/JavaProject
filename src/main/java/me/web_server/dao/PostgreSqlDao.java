package me.web_server.dao;

import java.sql.SQLException;

public abstract class PostgreSqlDao extends GenericDao {
	@Override
	protected boolean isInternalError(SQLException exception) {
		return !exception.getMessage().matches("^ERROR\\: .+\\s+Where\\: .+ at RAISE$");
	}

	@Override
	protected String getSqlExceptionMessage(SQLException exception) {
		return exception.getMessage().replaceAll("(?:ERROR\\: |\\s+Where\\: .* at RAISE$)", "");
	}
}
