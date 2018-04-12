package app.housify.client;

import app.housify.h2.StatementResultSet;
import app.housify.util.ExtensionsKt;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static app.housify.Main.connectionManager;

public class ClientDao {

    String InsertQuery = "INSERT INTO client (ID, NAME, ADDRESS, TELEPHONE) VALUES (?,?,?,?)";
    PreparedStatement preparedInsert;

    public ClientDao() {
        // If client table doesn't exist, create it and populate
        createTable();
        loadFromCSV();
    }

    private void createTable() {
        String drop = "DROP TABLE IF EXISTS client";
        String create = "CREATE TABLE IF NOT EXISTS client(" +
                "ID INT PRIMARY KEY NOT NULL," +
                "NAME VARCHAR(255) NOT NULL," +
                "ADDRESS INT NOT NULL," +
                "TELEPHONE VARCHAR(10) NOT NULL," +
                "FOREIGN KEY (ADDRESS) REFERENCES address);";
        try {
            connectionManager.execute(drop);
            connectionManager.execute(create);
        } catch (SQLException e) {
            System.err.println("Error Creating Client Table");
            e.printStackTrace();
        }

        try {
            preparedInsert = connectionManager.prepareStatement(InsertQuery);
        } catch (SQLException e) {
            System.err.println("Error Creating Prepared Statements for Client Table");
            e.printStackTrace();
        }
    }

    private void loadFromCSV() {
        // load csv file
        try {
            BufferedReader br = new BufferedReader(new FileReader("./src/main/resources/data/Client.csv"));
            String line;
            // Populate table
            while ((line = br.readLine()) != null) {
                String[] split = line.split(",");
                this.addClient(split);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Error Inserting Client Rows");
            e.printStackTrace();
        }
        System.out.println("Loaded Client Table");
    }

    public List<Map<String, String>> getClients() {
        String query = "SELECT * FROM client;";
        try (StatementResultSet srs = connectionManager.executeQuery(query)) {
            return ExtensionsKt.asArrayMap(srs.getResultSet());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<String, String> getClientByID(String id) {
        String query = String.format("SELECT * FROM client WHERE ID = %s;", id);
        try (StatementResultSet srs = connectionManager.executeQuery(query)) {
            return ExtensionsKt.asMap(srs.getResultSet());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*TODO: Find sales client was a part of, as well as their current listings*/

    public Map<String, String> getAddress(String id) {
        String query = String.format("SELECT address.ID," +
                "STREET,CITY,STATE,ZIP FROM address, client " +
                "WHERE client.ID = %s AND client.ADDRESS = address.ID;", id);
        try (StatementResultSet srs = connectionManager.executeQuery(query)) {
            return ExtensionsKt.asMap(srs.getResultSet());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void addClient(String[] ClientObj) throws SQLException {
        preparedInsert.setInt(1,Integer.parseInt(ClientObj[0]));
        preparedInsert.setString(2,ClientObj[1]);
        preparedInsert.setInt(3,Integer.parseInt(ClientObj[2]));
        preparedInsert.setString(4,ClientObj[3]);
        preparedInsert.executeUpdate();
    }
}