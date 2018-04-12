package app.housify.address;

import app.housify.h2.StatementResultSet;
import app.housify.util.ExtensionsKt;

import java.io.*;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static app.housify.Main.connectionManager;

public class AddressDao {

    String InsertQuery = "INSERT INTO address (ID, STREET, CITY, STATE, ZIP) VALUES (?,?,?,?,?)";
    PreparedStatement preparedInsert;

    public AddressDao() {
        // If address table doesn't exist, create it and populate
        createTable();
        loadFromCSV();
    }

    private void createTable() {
        String drop = "DROP TABLE IF EXISTS address";
        String create = "CREATE TABLE IF NOT EXISTS address(" +
                "ID INT PRIMARY KEY NOT NULL," +
                "STREET VARCHAR(255) NOT NULL," +
                "CITY VARCHAR(255) NOT NULL," +
                "STATE VARCHAR(127) NOT NULL," +
                "ZIP INT NOT NULL);";
        try {
            connectionManager.execute(drop);
            connectionManager.execute(create);
        } catch (SQLException e) {
            System.err.println("Error Creating Address Table");
            e.printStackTrace();
        }

        try {
            preparedInsert = connectionManager.prepareStatement(InsertQuery);
        } catch (SQLException e) {
            System.err.println("Error Creating Prepared Statements for Address Table");
            e.printStackTrace();
        }
    }
    private void loadFromCSV () {
        // try and get file
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("./src/main/resources/data/Address.csv"));
        } catch (FileNotFoundException e1) {
            InputStream is = getClass().getResourceAsStream("/data/Address.csv");
            br = new BufferedReader((new InputStreamReader(is)));
        }
        // read csv file
        try {
            String line;
            // Populate table
            while((line = br.readLine()) != null){
                String[] split = line.split(",");
                this.addAddress(split);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Error Inserting Address Rows");
            e.printStackTrace();
        }
        System.out.println("Loaded Address Table");
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

    private void addAddress (String[] AddressObj) throws SQLException {
        preparedInsert.setInt(1,Integer.parseInt(AddressObj[0]));
        preparedInsert.setString(2,AddressObj[1]);
        preparedInsert.setString(3,AddressObj[2]);
        preparedInsert.setString(4,AddressObj[3]);
        preparedInsert.setString(5,AddressObj[4]);
        preparedInsert.executeUpdate();
    }

}










