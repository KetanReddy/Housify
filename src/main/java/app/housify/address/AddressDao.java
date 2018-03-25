package app.housify.address;

import app.housify.h2.StatementResultSet;
import app.housify.util.ExtensionsKt;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static app.housify.Main.connectionManager;

public class AddressDao {

    public AddressDao() {
        // If address table doesn't exist, create it and populate
        createTable();
        loadFromCSV();
    }

    private void createTable() {
        try {
            String query = "CREATE TABLE IF NOT EXISTS address(" +
                    "ID INT PRIMARY KEY," +
                    "STREET VARCHAR(255)," +
                    "CITY VARCHAR(255)," +
                    "STATE VARCHAR(127)," +
                    "ZIP INT);";

            connectionManager.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void loadFromCSV () {
        // load csv file
        try {
            BufferedReader br = new BufferedReader(new FileReader("./src/main/resources/data/Address.csv"));
            String line;
            while((line = br.readLine()) != null){
                String[] split = line.split(",");
                this.addAddress(split);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Populate table
    }

    public List<Map<String, String>> getAddresses() {
        String query = "SELECT * FROM address;";
        try (StatementResultSet srs = connectionManager.executeQuery(query)) {
            return ExtensionsKt.asArrayMap(srs.getResultSet());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<String, String> getAddressByID(String id) {
        String query = String.format("SELECT * FROM address WHERE ID = %s;", id);
        try (StatementResultSet srs = connectionManager.executeQuery(query)) {
            return ExtensionsKt.asMap(srs.getResultSet());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void addAddress (String[] AddressObj) {
        String query = "INSERT INTO address VALUES("+AddressObj[0]+",\'"+AddressObj[1]+"\',\'"+AddressObj[2]+"\',\'"+AddressObj[3]+"\',"+AddressObj[4]+");";
        System.out.println(query);
        try {
            connectionManager.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}










