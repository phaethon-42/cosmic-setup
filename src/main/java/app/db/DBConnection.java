package app.db;

import javax.sql.rowset.serial.SerialArray;
import java.sql.*;

/**
 * Created by Phaethon on 08-Feb-17
 */
public class DBConnection {

    private Connection connection;

    public DBConnection() throws SQLException {
        String dbUrl = System.getenv("JDBC_DATABASE_URL");
        connection = DriverManager.getConnection(dbUrl);
    }

    public void close() throws SQLException {
        connection.close();
    }

    public ResultSet query(String sql, Object[] params) throws SQLException {
        PreparedStatement statement = getPreparedStatement(sql, params);
        return statement.executeQuery();
    }

    public void executeDml(String sql, Object[] params) throws SQLException {
        PreparedStatement preparedStatement = getPreparedStatement(sql, params);
        preparedStatement.execute();
    }

    private PreparedStatement getPreparedStatement(String sql, Object[] params) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(sql);
        if (params != null) {
            for (int i = 1; i < params.length + 1; ++i) {
                Object param = params[i - 1];
                if (param instanceof Object[]) {
                    Array array = connection.createArrayOf("text", (Object[]) param);
                    statement.setObject(i, array);
                } else {
                    statement.setObject(i, param);
                }
            }
        }
        return statement;
    }

    private Connection getConnection() throws SQLException {
        return connection;
    }
}
