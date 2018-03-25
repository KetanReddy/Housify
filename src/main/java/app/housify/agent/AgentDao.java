package app.housify.agent;

import app.housify.util.ExtensionsKt;
import app.housify.h2.StatementResultSet;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static app.housify.Main.connectionManager;

public class AgentDao {

    String InsertQuery = "INSERT INTO agent (ID, NAME, ADDRESS, OFFICE, SALARY, TELEPHONE) VALUES (?,?,?,?,?,?)";

    public AgentDao() {
        // If agent table doesn't exist, create it and populate
        createTable();
        loadFromCSV();
    }

    private void createTable() {
        String query = "CREATE TABLE IF NOT EXISTS agent(" +
                "ID INT PRIMARY KEY," +
                "NAME VARCHAR(255)," +
                "ADDRESS INT," +
                "OFFICE INT," +
                "SALARY INT," +
                "TELEPHONE VARCHAR(10));";
        try {
            connectionManager.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    private void loadFromCSV () {
        // load csv file
        try {
            BufferedReader br = new BufferedReader(new FileReader("./src/main/resources/data/Agent.csv"));
            String line;
            while((line = br.readLine()) != null){
                String[] split = line.split(",");
                this.addAgent(split);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Populate table
    }

    /* TODO: Need to lookup address and office when those tables get completed */
    public List<Map<String, String>> getAgents() {
        String query = "SELECT * FROM agent;";
        try (StatementResultSet srs = connectionManager.executeQuery(query)) {
            return ExtensionsKt.asArrayMap(srs.getResultSet());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<String, String> getAgentByID(String id) {
        String query = String.format("SELECT * FROM agent WHERE ID = %s;", id);
        try (StatementResultSet srs = connectionManager.executeQuery(query)) {
            return ExtensionsKt.asMap(srs.getResultSet());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void addAgent (String[] AgentObj) {
        String query = "INSERT INTO agent VALUES("+AgentObj[0]+",\'"+AgentObj[1]+"\',"+AgentObj[2]+","+AgentObj[3]+","+AgentObj[4]+",\'"+AgentObj[5]+"\');";
        System.out.println(query);
        try {
            connectionManager.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteAgent (String id) {
        String query = String.format("DELETE * FROM agent WHERE ID = %s;", id);
        System.out.println(query);
        try {
            connectionManager.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Map<String,String>> getAgentListings(String id) {
        String query = String.format("SELECT * FROM listing WHERE AGENT = %s;",id);
        try (StatementResultSet srs = connectionManager.executeQuery(query)) {
            return ExtensionsKt.asArrayMap(srs.getResultSet());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Map<String,String>> getAgentSales(String id) {
        String query = String.format("WITH agent_listing(SALEID) AS" +
                "(SELECT SALE FROM listing " +
                "WHERE AGENT = %s)" +
                "SELECT * FROM sale " +
                "WHERE sale.ID = agent_listing.SALEID;",id);
        try (StatementResultSet srs = connectionManager.executeQuery(query)) {
            return ExtensionsKt.asArrayMap(srs.getResultSet());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
