package app.housify.h2;

import java.sql.*;

public class H2ConnectionManager implements AutoCloseable {

    private Connection connection;

    public H2ConnectionManager(String location,
                               String user,
                               String password) throws ClassNotFoundException, SQLException {
        //This tells it to use the h2 driver
        Class.forName("org.h2.Driver");

        String url = "jdbc:h2:" + location;
        connection = DriverManager.getConnection(url, user, password);
    }

    public void execute(PreparedStatement statement) throws SQLException {
        statement.executeQuery();
    }
    public void execute(String query) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute(query);
        }
    }
    /** Ensure to use with try/resource block to maximize chances of closing objects **/
    public StatementResultSet executeQuery(String query) throws SQLException {
        Statement statement = connection.createStatement();
        return new StatementResultSet(statement, statement.executeQuery(query));
    }
    public PreparedStatement prepareStatement(String statement) throws SQLException {
        return connection.prepareStatement(statement);
    }

    @Override
    public void close() throws Exception {
        connection.close();
    }
}
