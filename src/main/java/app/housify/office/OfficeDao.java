package app.housify.office;

import app.housify.h2.StatementResultSet;
import app.housify.util.ExtensionsKt;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static app.housify.Main.connectionManager;

public class OfficeDao {

    public OfficeDao() {
        // If office table doesn't exist, create it and populate
        createTable();
        loadFromCSV();
    }

    private void createTable() {
        try {
            String query = "CREATE TABLE IF NOT EXISTS office(" +
                    "LOCATION VARCHAR(127) PRIMARY KEY," +
                    "NAME VARCHAR(255)," +
                    "ADDRESS INT," +
                    "MANAGER INT);";

            connectionManager.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadFromCSV() {
        // load csv file
        try {
            BufferedReader br = new BufferedReader(new FileReader("./src/main/resources/data/Office.csv"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] split = line.split(",");
                this.addOffice(split);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Populate table
    }

    public List<Map<String, String>> getOffices() {
        String query = "SELECT * FROM office;";
        try (StatementResultSet srs = connectionManager.executeQuery(query)) {
            return ExtensionsKt.asArrayMap(srs.getResultSet());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<String, String> getOfficeByID(String id) {
        String query = String.format("SELECT * FROM office WHERE ID = %s;", id);
        try (StatementResultSet srs = connectionManager.executeQuery(query)) {
            return ExtensionsKt.asMap(srs.getResultSet());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void addOffice(String[] OfficeObj) {
        String query = "INSERT INTO office VALUES(\'"
                + OfficeObj[0]
                + "\',\'" + OfficeObj[1]
                + "\'," + OfficeObj[2]
                + "," + OfficeObj[3] + ");";
        System.out.println(query);
        try {
            connectionManager.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}