package app.housify.listing;

import app.housify.h2.StatementResultSet;
import app.housify.util.ExtensionsKt;

import java.io.*;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static app.housify.Main.connectionManager;

public class ListingDao {

    PreparedStatement preparedInsert;
    String InsertQuery = "INSERT INTO listing (ID, SELLER, PRICE, SALE, DATE, AGENT, OFFICE, PROPERTY) VALUES (?,?,?,?,?,?,?,?)";
    String listingQuery = "WITH listings(address, price, date, seller, numbeds, numbaths, yearbuilt, squarefootage) AS (" +
            "SELECT CONCAT(address.street, ' ', address.city, ', ', address.state, ' ', address.zip), " +
            "listing.price, listing.date, seller.name, property.numbeds, property.numbaths, property.yearbuilt, property.squarefootage FROM (listing " +
            "INNER JOIN property ON listing.property = property.id " +
            "INNER JOIN address ON property.address = address.id " +
            "INNER JOIN client AS seller ON listing.seller = seller.id " +
            ") %s ) SELECT * FROM listings;";
    String searchQuery = "WITH listings(address, price, date, seller, numbeds, numbaths, yearbuilt, squarefootage) AS (" +
            "SELECT CONCAT(address.street, ' ', address.city, ', ', address.state, ' ', address.zip)," +
            "listing.price, listing.date, seller.name, property.numbeds, property.numbaths, property.yearbuilt, property.squarefootage FROM (listing " +
            "INNER JOIN property ON listing.property = property.id " +
            "INNER JOIN address ON property.address = address.id " +
            "INNER JOIN client AS seller ON listing.seller = seller.id " +
            ") WHERE listing.id IN (" +
            "WITH params(id) AS" +
            "(SELECT listing.id, address.city as city, listing.price as min_price, listing.price as max_price, property.numbaths as num_baths, property.numbeds as num_beds, " +
            "property.squarefootage as sq_ft, property.yearbuilt as year_built FROM (listing " +
            "INNER JOIN property ON listing.property = property.id " +
            "INNER JOIN address ON property.address = address.id " +
            " ) ) SELECT id FROM params %s ) ) SELECT * FROM listings;";

    public ListingDao() {
        // If listing table doesn't exist, create it and populate
        createTable();
        loadFromCSV();
    }

    private void createTable() {
        String drop = "DROP TABLE IF EXISTS listing";
        String create = "CREATE TABLE IF NOT EXISTS listing(" +
                "ID INT PRIMARY KEY NOT NULL," +
                "SELLER INT NOT NULL," +
                "PRICE NUMERIC(10,2) NOT NULL," +
                "SALE INT," +
                "DATE BIGINT NOT NULL," +
                "AGENT INT NOT NULL," +
                "OFFICE INT NOT NULL," +
                "PROPERTY INT NOT NULL," +
                "FOREIGN KEY (SELLER) REFERENCES client," +
                "FOREIGN KEY (SALE) REFERENCES sale," +
                "FOREIGN KEY (AGENT) REFERENCES agent," +
                "FOREIGN KEY (OFFICE) REFERENCES office," +
                "FOREIGN KEY (PROPERTY) REFERENCES property);";

        try {
            connectionManager.execute(drop);
            connectionManager.execute(create);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            preparedInsert = connectionManager.prepareStatement(InsertQuery);
        } catch (SQLException e) {
            System.err.println("Error Creating Prepared Statements for Listing Table");
            e.printStackTrace();
        }
    }

    private void loadFromCSV() {
        // try and get file
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("./src/main/resources/data/Listing.csv"));
        } catch (FileNotFoundException e1) {
            InputStream is = getClass().getResourceAsStream("/data/Listing.csv");
            br = new BufferedReader((new InputStreamReader(is)));
        }
        // read csv file
        try {
            String line;
            while ((line = br.readLine()) != null) {
                String[] split = line.split(",");
                this.addListing(split);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Error Inserting Listing Rows");
            e.printStackTrace();
        }
        System.out.println("Loaded Listing Table");
    }

    private List<Map<String, String>> getActiveListings(String where) {
        if (where != null && !where.isEmpty()) {
            where += " AND listing.sale IS NULL";
        } else {
            where = "WHERE listing.sale IS NULL";
        }
        String query = String.format(listingQuery, where);
        try (StatementResultSet srs = connectionManager.executeQuery(query)) {
            return ExtensionsKt.asArrayMap(srs.getResultSet());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Map<String, String>> getActiveListings() {
        return getActiveListings(null);
    }
    public List<Map<String, String>> getAgentActiveListings(String id) {
        String where = String.format("WHERE listing.agent = %d", Integer.valueOf(id));
        return getActiveListings(where);
    }

    public List<Map<String, String>> getOfficeActiveListings(String id) {
        String where = String.format("WHERE listing.office = %d", Integer.valueOf(id));
        return getActiveListings(where);
    }

    public List<Map<String, String>> getOfficeAgentActiveListings(String officeId, String agentId) {
        String where = String.format("WHERE listing.office = %d AND listing.agent = %d",
                Integer.valueOf(officeId), Integer.valueOf(agentId));
        return getActiveListings(where);
    }

    private List<Map<String, String>> searchActiveListings(String where) {
        if (where != null && !where.isEmpty()) {
            where += " AND listing.sale IS NULL";
        } else {
            where = "WHERE listing.sale IS NULL";
        }
        String query = String.format(searchQuery, where);
        try (StatementResultSet srs = connectionManager.executeQuery(query)) {
            return ExtensionsKt.asArrayMap(srs.getResultSet());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Map<String, String>> searchActiveListings(Map<String, String> criteria) {
        String where = "";
        if (!criteria.isEmpty()) {
            ArrayList<String> constraints = new ArrayList<>();
            ArrayList<String> lowerboundKeys = new ArrayList<>();
            lowerboundKeys.add("min_price");
            lowerboundKeys.add("sq_ft");
            ArrayList<String> upperboundKeys = new ArrayList<>();
            upperboundKeys.add("max_price");
            for (Map.Entry<String, String> entry : criteria.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (key == null || key.isEmpty() || value == null || value.isEmpty()) continue;

                String operator = "";
                // Check for special constraints
                if (lowerboundKeys.contains(key)) {
                    operator = ">=";
                } else if (upperboundKeys.contains(key)) {
                    operator = "<=";
                } else if (!key.matches("^(ic|_).*$")) {
                    operator = "=";
                }
                if (!operator.isEmpty())
                    constraints.add(String.format("%s %s '%s'", key, operator, value));
            }
            System.out.println(constraints);

            if (!constraints.isEmpty())
                where = "WHERE " + String.join(" AND ", constraints);
        }
        return searchActiveListings(where);
    }

    private void addListing(String[] ListingObj) throws SQLException {
        preparedInsert.setInt(1,Integer.parseInt(ListingObj[0]));
        preparedInsert.setInt(2,Integer.parseInt(ListingObj[1]));
        preparedInsert.setInt(3,Integer.parseInt(ListingObj[2]));
        if (ListingObj[3].equals("")) {
            preparedInsert.setNull(4,Types.NULL);
        } else {
            preparedInsert.setInt(4,Integer.parseInt(ListingObj[3]));
        }
        preparedInsert.setLong(5,Long.parseLong(ListingObj[4]));
        preparedInsert.setInt(6,Integer.parseInt(ListingObj[5]));
        preparedInsert.setInt(7,Integer.parseInt(ListingObj[6]));
        preparedInsert.setInt(8,Integer.parseInt(ListingObj[7]));
        preparedInsert.executeUpdate();
    }
}
