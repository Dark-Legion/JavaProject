package me.web_server.dao;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.StoredProcedure;

final class SqlHelper {
    private SqlHelper() {
        super();
    }

    static <T> T callFunction(Class<T> type, DataSource dataSource, String function, SqlParameter[] parameterTypes, Object... parameters) {
        StoredProcedure procedure = new StoredProcedure(dataSource, function) {};

        procedure.setFunction(true);
        procedure.setParameters(parameterTypes);

        Map<String, Object> result = procedure.execute(parameters);

        Logger logger = Logger.getGlobal();

        Iterator<?> temp1 = result.values().iterator();

        if (!temp1.hasNext()) {
            logger.severe("Database API returned empty records set!");
            
            return null;
        }
        
        Object temp2 = temp1.next();

        if (!(temp2 instanceof List)) {
            logger.severe("Database API didn't return List!");

            return null;
        }

        List<?> temp3 = (List<?>) temp2;

        if (temp3.isEmpty()) {
            logger.severe("Database API returned empty list!");

            return null;
        }

        Object temp4 = temp3.get(0);

        if (!(temp4 instanceof Map)) {
            logger.severe("Database API didn't return Map!");

            return null;
        }

        Iterator<?> temp5 = ((Map<?, ?>) temp4).values().iterator();

        if (!temp5.hasNext()) {
            logger.severe("Database API returned empty columns set!");

            return null;
        }

        try {
            return type.cast(temp5.next());
        } catch (ClassCastException exception) {
            return null;
        }
    }
}
