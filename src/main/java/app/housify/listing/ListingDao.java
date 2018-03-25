package app.housify.listing;

import app.housify.h2.StatementResultSet;
import app.housify.util.ExtensionsKt;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static app.housify.Main.connectionManager;

public class ListingDao {

    public ListingDao() {
        // If listing table doesn't exist, create it and populate
        createTable();
        loadFromCSV();
    }

    private void createTable() {
        try {
            String query = "CREATE TABLE IF NOT EXISTS listing(" +
                    "ID INT PRIMARY KEY," +
                    "SELLER INT," +
                    "PRICE NUMERIC(10,2)," +
                    "SALE INT," +
                    "DATE BIGINT," +
                    "AGENT INT," +
                    "OFFICE INT," +
                    "PROPERTY INT);";

            connectionManager.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadFromCSV() {
        // load csv file
        try {
            BufferedReader br = new BufferedReader(new FileReader("./src/main/resources/data/Listing.csv"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] split = line.split(",");
                this.addListing(split);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Populate table
    }

    public List<Map<String, String>> getListings() {
        String query = "SELECT * FROM listing;";
        try (StatementResultSet srs = connectionManager.executeQuery(query)) {
            return ExtensionsKt.asArrayMap(srs.getResultSet());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<String, String> getListingByID(String id) {
        String query = String.format("SELECT * FROM listing WHERE ID = %s;", id);
        try (StatementResultSet srs = connectionManager.executeQuery(query)) {
            return ExtensionsKt.asMap(srs.getResultSet());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void addListing(String[] ListingObj) {
        String query = "INSERT INTO listing VALUES("
                + ListingObj[0]
                + "," + ListingObj[1]
                + "," + ListingObj[2]
                + "," + ListingObj[3]
                + "," + ListingObj[4]
                + "," + ListingObj[5]
                + "," + ListingObj[6]
                + "," + ListingObj[7] + ");";
        System.out.println(query);
        try {
            connectionManager.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
