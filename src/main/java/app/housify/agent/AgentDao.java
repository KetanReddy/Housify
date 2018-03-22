package app.housify.agent;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

import static app.housify.Main.connectionManager;

public class AgentDao {

    public List<Agent> getAgents() {
        return Arrays.asList(
                new Agent(0, "name", 0, 0, 55555, "5555555555"),
                new Agent(1, "name", 0, 0, 55555, "5555555555")
        );
    }

    public Agent getAgentByID(String id) {
        return new Agent(Integer.valueOf(id), "name", 0, 0, 55555, "5555555555");
    }

    // Possible H2 Solution
    public Agent getAgentByIDH2(String id) {
        String query = "TODO WRITE ACTUAL QUERY;";
        try (Connection conn = connectionManager.createConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(query);

            if (result.next()) {
                // TODO: Figure out correct indices
                return new Agent(
                        result.getInt(1),
                        result.getString(2),
                        result.getInt(3),
                        result.getInt(4),
                        result.getInt(5),
                        result.getString(6)
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
