package app.housify.office;

import app.housify.h2.StatementResultSet;
import app.housify.util.ExtensionsKt;

import java.io.*;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static app.housify.Main.connectionManager;

public class OfficeDao {

    String InsertQuery = "INSERT INTO office (ID, NAME, ADDRESS, MANAGER) VALUES (?,?,?,?)";
    PreparedStatement preparedInsert;

    public OfficeDao() {
        // If office table doesn't exist, create it and populate
        createTable();
        loadFromCSV();
    }

    private void createTable() {
        String drop = "DROP TABLE IF EXISTS office";
        String create = "CREATE TABLE IF NOT EXISTS office(" +
                "ID INT PRIMARY KEY NOT NULL," +
                "NAME VARCHAR(255) NOT NULL," +
                "ADDRESS INT NOT NULL," +
                "MANAGER VARCHAR(255) NOT NULL," +
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
            System.err.println("Error Creating Prepared Statements for Office Table");
            e.printStackTrace();
        }
        System.out.println("Loaded Office Table");
    }

    private void loadFromCSV() {
        // try and get file
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("./src/main/resources/data/Office.csv"));
        } catch (FileNotFoundException e1) {
            InputStream is = getClass().getResourceAsStream("/data/Office.csv");
            br = new BufferedReader((new InputStreamReader(is)));
        }
        // read csv file
        try {
            String line;
            while ((line = br.readLine()) != null) {
                String[] split = line.split(",");
                this.addOffice(split);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Error Inserting Office Rows");
            e.printStackTrace();
        }
        System.out.println("Loaded Office Table");
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

    private void addOffice(String[] OfficeObj) throws SQLException {
        preparedInsert.setInt(1,Integer.parseInt(OfficeObj[0]));
        preparedInsert.setString(2,OfficeObj[1]);
        preparedInsert.setInt(3,Integer.parseInt(OfficeObj[2]));
        preparedInsert.setString(4,OfficeObj[3]);
        preparedInsert.executeUpdate();
    }
}