package app.housify.sale;

import app.housify.h2.StatementResultSet;
import app.housify.util.ExtensionsKt;

import java.io.*;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static app.housify.Main.connectionManager;

public class SaleDao {

    PreparedStatement preparedInsert;
    String InsertQuery = "INSERT INTO sale (ID, BUYER, PRICE, DATE) VALUES (?,?,?,?)";
    String saleQuery = "WITH agent_sales(address, price, date, buyer, commission) AS (" +
            "SELECT CONCAT(address.street, ' ', address.city, ', ', address.state, ' ', address.zip)," +
            "sale.price, sale.date, buyer.name, sale.commission FROM (sale " +
            "INNER JOIN client AS buyer ON sale.buyer = buyer.id " +
            "INNER JOIN listing ON listing.sale = sale.id " +
            "INNER JOIN property ON listing.property = property.id " +
            "INNER JOIN address ON property.address = address.id" +
            ") %s ) " +
            "SELECT * FROM agent_sales ORDER BY date DESC;";
    String metricsQuery = "SELECT CAST(AVG(sale.date - listing.date) as BIGINT) as avg_time, " +
            "CAST(AVG(sale.price) as NUMERIC(10, 2)) as avg_price, " +
            "COUNT(sale.id) as total_sales FROM sale INNER JOIN listing " +
            "ON sale.id = listing.sale %s;";

    public SaleDao() {
        // If sale table doesn't exist, create it and populate
        createTable();
        loadFromCSV();
    }

    private void createTable() {
        String drop = "DROP TABLE IF EXISTS sale";
        String create = "CREATE TABLE IF NOT EXISTS sale(" +
                "ID INT PRIMARY KEY NOT NULL," +
                "BUYER INT NOT NULL," +
                "PRICE NUMERIC(10,2) NOT NULL," +
                "COMMISSION NUMERIC(10,2) AS 0.1 * PRICE," +
                "DATE BIGINT NOT NULL," +
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

    private void loadFromCSV() {
        // try and get file
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("./src/main/resources/data/Sale.csv"));
        } catch (FileNotFoundException e1) {
            InputStream is = getClass().getResourceAsStream("/data/Sale.csv");
            br = new BufferedReader((new InputStreamReader(is)));
        }
        // read csv file
        try {
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

    private List<Map<String,String>> getSales(String where) {
        String query = String.format(saleQuery, where);
        try (StatementResultSet srs = connectionManager.executeQuery(query)) {
            return ExtensionsKt.asArrayMap(srs.getResultSet());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Map<String,String>> getAgentSales(String id) {
        String where = String.format("WHERE listing.agent = %d", Integer.valueOf(id));
        return getSales(where);
    }

    public List<Map<String,String>> getOfficeSales(String id) {
        String where = String.format("WHERE listing.office = %d", Integer.valueOf(id));
        return getSales(where);
    }

    public List<Map<String,String>> getOfficeAgentSales(String officeId, String agentId) {
        String where = String.format("WHERE listing.office = %d AND listing.agent = %d",
                Integer.valueOf(officeId), Integer.valueOf(agentId));
        return getSales(where);
    }

    public Map<String,String> getMetrics() {
        return getMetrics("");
    }

    public Map<String,String> getMetrics(String where) {
        String q = String.format(metricsQuery, where);
        try (StatementResultSet srs = connectionManager.executeQuery(q)) {
            return ExtensionsKt.asMap(srs.getResultSet());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<String,String> getOfficeMetrics(String id) {
        String where = String.format("WHERE listing.office = %d", Integer.valueOf(id));
        return getMetrics(where);
    }

    public Map<String,String> getAgentMetrics(String id) {
        String where = String.format("WHERE listing.agent = %d", Integer.valueOf(id));
        return getMetrics(where);
    }

    public Map<String,String> getOfficeAgentMetrics(String officeId, String agentId) {
        String where = String.format("WHERE listing.office = %d AND listing.agent = %d",
                Integer.valueOf(officeId), Integer.valueOf(agentId));
        return getMetrics(where);
    }

    private void addSale(String[] SaleObj) throws SQLException {
        preparedInsert.setInt(1,Integer.parseInt(SaleObj[0]));
        preparedInsert.setInt(2,Integer.parseInt(SaleObj[1]));
        preparedInsert.setInt(3,Integer.parseInt(SaleObj[2]));
        preparedInsert.setLong(4,Long.parseLong(SaleObj[3]));
        preparedInsert.executeUpdate();
    }
}
