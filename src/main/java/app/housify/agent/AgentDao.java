package app.housify.agent;

import app.housify.util.ExtensionsKt;
import app.housify.h2.StatementResultSet;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static app.housify.Main.connectionManager;

public class AgentDao {

    public AgentDao() {
        // If agent table doesn't exist, create it and populate
        createTable();
    }

    private void createTable() {
        try {
            String query = "CREATE TABLE IF NOT EXISTS agent(" +
                    "ID INT PRIMARY KEY," +
                    "NAME VARCHAR(255)," +
                    "ADDRESS INT," +
                    "OFFICE INT," +
                    "SALARY INT," +
                    "TELEPHONE VARCHAR(10));";

            connectionManager.execute(query);

            // Populate table
            query = "INSERT INTO agent VALUES(0,\'Jeremiah Zucker\',0,0,100000,\'5555555555\');";
            connectionManager.execute(query);
            query = "INSERT INTO agent VALUES(1,\'Tyler Krupicka\',0,0,100000,\'5555555556\');";
            connectionManager.execute(query);
            query = "INSERT INTO agent VALUES(2,\'Ketan Reddy\',0,0,100000,\'5555555557\');";
            connectionManager.execute(query);
            query = "INSERT INTO agent VALUES(3,\'Tristan Manning\',0,0,100000,\'5555555558\');";
            connectionManager.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

}
