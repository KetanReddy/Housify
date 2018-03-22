package app.housify;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class H2ConnectionManager {

    private String url;
    private String user;
    private String password;

    H2ConnectionManager(String location,
                        String user,
                        String password) throws ClassNotFoundException {
        //This tells it to use the h2 driver
        Class.forName("org.h2.Driver");

        this.url = "jdbc:h2:" + location;
        this.user = user;
        this.password = password;
    }

    public Connection createConnection() {
        try {
            //creates the connection
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            //You should handle this better
            e.printStackTrace();
            return null;
        }
    }

}
