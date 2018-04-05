package app.housify.sale;

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

public class SaleDao {

    String InsertQuery = "INSERT INTO sale (ID, LISTING, SELLER, BUYER, PRICE, DATE ) VALUES (?,?,?,?,?,?)";
    PreparedStatement preparedInsert;

    public SaleDao() {
        // If sale table doesn't exist, create it and populate
        createTable();
        loadFromCSV();
    }

    private void createTable() {
        String drop = "DROP TABLE IF EXISTS sale";
        String create = "CREATE TABLE IF NOT EXISTS sale(" +
                "ID INT PRIMARY KEY NOT NULL," +
                "LISTING INT NOT NULL," +
                "SELLER INT NOT NULL," +
                "BUYER INT NOT NULL," +
                "PRICE NUMERIC(10,2) NOT NULL," +
                "DATE BIGINT NOT NULL," +
                "FOREIGN KEY (LISTING) REFERENCES listing," +
                "FOREIGN KEY (SELLER) REFERENCES client," +
                "FOREIGN KEY (BUYER) REFERENCES client);";

        try {
            connectionManager.execute(drop);
            connectionManager.execute(create);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            preparedInsert = connectionManager.prepareStatement(InsertQuery);
        } catch (SQLException e) {
            System.err.println("Error Creating Prepared Statements for Sale Table");
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
        } catch (SQLException e) {
            System.err.println("Error Inserting Sale Rows");
            e.printStackTrace();
        }
        System.out.println("Loaded Sale Table");
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

    private void addSale(String[] SaleObj) throws SQLException {
        preparedInsert.setInt(1,Integer.parseInt(SaleObj[0]));
        preparedInsert.setInt(2,Integer.parseInt(SaleObj[1]));
        preparedInsert.setInt(3,Integer.parseInt(SaleObj[2]));
        preparedInsert.setInt(4,Integer.parseInt(SaleObj[3]));
        preparedInsert.setInt(5,Integer.parseInt(SaleObj[4]));
        preparedInsert.setLong(6,Long.parseLong(SaleObj[5]));
        preparedInsert.executeUpdate();
    }
}
