package app.housify.property;

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

public class PropertyDao {

    String InsertQuery = "INSERT INTO property (ID, NUMBEDS, NUMBATHS, YEARBUIT, SQUAREFOOTAGE, ADDRESS ) VALUES (?,?,?,?,?,?)";
    PreparedStatement preparedInsert;

    public PropertyDao() {
        // If property table doesn't exist, create it and populate
        createTable();
        loadFromCSV();
    }

    private void createTable() {
        String drop = "DROP TABLE IF EXISTS property";
        String create = "CREATE TABLE IF NOT EXISTS property(" +
                "ID INT PRIMARY KEY NOT NULL," +
                "NUMBEDS INT NOT NULL," +
                "NUMBATHS INT NOT NULL," +
                "YEARBUIT INT NOT NULL," +
                "SQUAREFOOTAGE INT NOT NULL," +
                "ADDRESS INT NOT NULL," +
                "FOREIGN KEY (ADDRESS) REFERENCES address);";

        try {
            connectionManager.execute(drop);
            connectionManager.execute(create);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            preparedInsert = connectionManager.prepareStatement(InsertQuery);
        } catch (SQLException e) {
            System.err.println("Error Creating Prepared Statements for Property Table");
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
        } catch (SQLException e) {
            System.err.println("Error Inserting Property Rows");
            e.printStackTrace();
        }
        System.out.println("Loaded Property Table");
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

    private void addProperty(String[] PropertyObj) throws SQLException {
        preparedInsert.setInt(1,Integer.parseInt(PropertyObj[0]));
        preparedInsert.setInt(2,Integer.parseInt(PropertyObj[1]));
        preparedInsert.setInt(3,Integer.parseInt(PropertyObj[2]));
        preparedInsert.setInt(4,Integer.parseInt(PropertyObj[3]));
        preparedInsert.setInt(5,Integer.parseInt(PropertyObj[4]));
        preparedInsert.setInt(6,Integer.parseInt(PropertyObj[5]));
        preparedInsert.executeUpdate();

    }
}
