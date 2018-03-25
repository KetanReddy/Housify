package app.housify.client;

import app.housify.h2.StatementResultSet;
import app.housify.util.ExtensionsKt;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static app.housify.Main.connectionManager;

public class ClientDao {

    public ClientDao() {
        // If client table doesn't exist, create it and populate
        createTable();
        loadFromCSV();
    }

    private void createTable() {
        try {
            String query = "CREATE TABLE IF NOT EXISTS client(" +
                    "ID INT PRIMARY KEY," +
                    "NAME VARCHAR(255)," +
                    "ADDRESS INT," +
                    "TELEPHONE VARCHAR(10));";

            connectionManager.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadFromCSV() {
        // load csv file
        try {
            BufferedReader br = new BufferedReader(new FileReader("./src/main/resources/data/Client.csv"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] split = line.split(",");
                this.addClient(split);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Populate table
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

    private void addClient(String[] ClientObj) {
        String query = "INSERT INTO client VALUES(" + ClientObj[0] + ",\'" + ClientObj[1] + "\'," + ClientObj[2] + ",\'" + ClientObj[3] + "\');";
        System.out.println(query);
        try {
            connectionManager.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}