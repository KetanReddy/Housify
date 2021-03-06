package app.housify.agent;

import app.housify.util.ExtensionsKt;
import app.housify.h2.StatementResultSet;

import java.io.*;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static app.housify.Main.connectionManager;

public class AgentDao {

    String InsertQuery = "INSERT INTO agent (ID, NAME, ADDRESS, OFFICE, SALARY, TELEPHONE) VALUES (?,?,?,?,?,?)";
    PreparedStatement preparedInsert;

    public AgentDao() {
        // If agent table doesn't exist, create it and populate
        createTable();
        loadFromCSV();
    }

    private void createTable() {
        String drop = "DROP TABLE IF EXISTS agent";
        String create = "CREATE TABLE IF NOT EXISTS agent(" +
                "ID INT PRIMARY KEY NOT NULL," +
                "NAME VARCHAR(255) NOT NULL," +
                "ADDRESS INT NOT NULL," +
                "OFFICE INT NOT NULL," +
                "SALARY INT NOT NULL," +
                "TELEPHONE VARCHAR(10) NOT NULL," +
                "FOREIGN KEY (ADDRESS) REFERENCES address," +
                "FOREIGN KEY (OFFICE) REFERENCES office);";
        try {
            connectionManager.execute(drop);
            connectionManager.execute(create);
        } catch (SQLException e) {
            System.err.println("Error Creating Agent Table");
            e.printStackTrace();
        }

        try {
            preparedInsert = connectionManager.prepareStatement(InsertQuery);
        } catch (SQLException e) {
            System.err.println("Error Creating Prepared Statements for Agent Table");
            e.printStackTrace();
        }

    }
    private void loadFromCSV () {
        // try and get file
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("./src/main/resources/data/Agent.csv"));
        } catch (FileNotFoundException e1) {
            InputStream is = getClass().getResourceAsStream("/data/Agent.csv");
            br = new BufferedReader((new InputStreamReader(is)));
        }
        // read csv file
        try {
            String line;
            // Populate table
            while((line = br.readLine()) != null){
                String[] split = line.split(",");
                this.addAgent(split);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Error Inserting Agent into Table");
            e.printStackTrace();
        }
        System.out.println("Loaded Agent Table");
    }

    public List<Map<String, String>> getAgents() {
        String query = "WITH agent_list(agent_id, agent_name, agent_telephone, office_name) AS (" +
                "SELECT agent.id, agent.name, agent.telephone, office.name " +
                "FROM agent INNER JOIN office ON agent.office = office.id)" +
                "SELECT * FROM agent_list;";
        try (StatementResultSet srs = connectionManager.executeQuery(query)) {
            return ExtensionsKt.asArrayMap(srs.getResultSet());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<String, String> getAgentInfo(String id) {
        String query = String.format("WITH agent_info(name, office, telephone, address, salary) AS (" +
                "SELECT agent.name, office.name, agent.telephone," +
                "CONCAT(address.street, ' ', address.city, ', ', address.state, ' ', address.zip)," +
                "agent.salary FROM (agent " +
                "INNER JOIN office ON agent.office = office.id " +
                "INNER JOIN address ON agent.address = address.id" +
                ") WHERE agent.id = %d) " +
                "SELECT * FROM agent_info;", Integer.valueOf(id));
        try (StatementResultSet srs = connectionManager.executeQuery(query)) {
            return ExtensionsKt.asMap(srs.getResultSet());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void addAgent (String[] AgentObj) throws SQLException {
        preparedInsert.setInt(1,Integer.parseInt(AgentObj[0]));
        preparedInsert.setString(2,AgentObj[1]);
        preparedInsert.setInt(3,Integer.parseInt(AgentObj[2]));
        preparedInsert.setInt(4,Integer.parseInt(AgentObj[3]));
        preparedInsert.setInt(5,Integer.parseInt(AgentObj[4]));
        preparedInsert.setString(6,AgentObj[5]);
        preparedInsert.executeUpdate();
    }

    private void deleteAgent (String id) {
        String query = String.format("DELETE * FROM agent WHERE ID = %s;", id);
        try {
            connectionManager.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
