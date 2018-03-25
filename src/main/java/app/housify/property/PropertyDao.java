package app.housify.property;

import app.housify.h2.StatementResultSet;
import app.housify.util.ExtensionsKt;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static app.housify.Main.connectionManager;

public class PropertyDao {

    public PropertyDao() {
        // If property table doesn't exist, create it and populate
        createTable();
        loadFromCSV();
    }

    private void createTable() {
        try {
            String query = "CREATE TABLE IF NOT EXISTS property(" +
                    "ID INT PRIMARY KEY," +
                    "NUMBEDS INT," +
                    "NUMBATHS INT," +
                    "YEARBUIT INT," +
                    "SQUAREFOOTAGE INT" +
                    "ADDRESS INT);";

            connectionManager.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadFromCSV() {
        // load csv file
        try {
            BufferedReader br = new BufferedReader(new FileReader("./src/main/resources/data/Property.csv"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] split = line.split(",");
                this.addProperty(split);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Populate table
    }

    public List<Map<String, String>> getPropertys() {
        String query = "SELECT * FROM property;";
        try (StatementResultSet srs = connectionManager.executeQuery(query)) {
            return ExtensionsKt.asArrayMap(srs.getResultSet());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<String, String> getPropertyByID(String id) {
        String query = String.format("SELECT * FROM property WHERE ID = %s;", id);
        try (StatementResultSet srs = connectionManager.executeQuery(query)) {
            return ExtensionsKt.asMap(srs.getResultSet());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void addProperty(String[] PropertyObj) {
        String query = "INSERT INTO property VALUES("
                + PropertyObj[0]
                + "," + PropertyObj[1]
                + "," + PropertyObj[2]
                + "," + PropertyObj[3]
                + "," + PropertyObj[4]
                + "," + PropertyObj[5] + ");";
        System.out.println(query);
        try {
            connectionManager.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
