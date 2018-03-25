package app.housify.sale;

import app.housify.h2.StatementResultSet;
import app.housify.util.ExtensionsKt;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static app.housify.Main.connectionManager;

public class SaleDao {

    public SaleDao() {
        // If sale table doesn't exist, create it and populate
        createTable();
        loadFromCSV();
    }

    private void createTable() {
        try {
            String query = "CREATE TABLE IF NOT EXISTS sale(" +
                    "ID INT PRIMARY KEY," +
                    "LISTING INT," +
                    "BUYER INT," +
                    "PRICE NUMERIC(10,2)," +
                    "DATE BIGINT);";

            connectionManager.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*TODO Create a derived commission() function*/

    private void loadFromCSV() {
        // load csv file
        try {
            BufferedReader br = new BufferedReader(new FileReader("./src/main/resources/data/Sale.csv"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] split = line.split(",");
                this.addSale(split);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Populate table
    }

    public List<Map<String, String>> getSales() {
        String query = "SELECT * FROM sale;";
        try (StatementResultSet srs = connectionManager.executeQuery(query)) {
            return ExtensionsKt.asArrayMap(srs.getResultSet());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<String, String> getSaleByID(String id) {
        String query = String.format("SELECT * FROM sale WHERE ID = %s;", id);
        try (StatementResultSet srs = connectionManager.executeQuery(query)) {
            return ExtensionsKt.asMap(srs.getResultSet());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void addSale(String[] SaleObj) {
        String query = "INSERT INTO sale VALUES("
                + SaleObj[0]
                + "," + SaleObj[1]
                + "," + SaleObj[2]
                + "," + SaleObj[3]
                + "," + SaleObj[4] + ");";
        System.out.println(query);
        try {
            connectionManager.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
