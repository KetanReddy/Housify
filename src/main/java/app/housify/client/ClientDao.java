package app.housify.client;

import java.io.*;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static app.housify.Main.connectionManager;

public class ClientDao {

    String InsertQuery = "INSERT INTO client (ID, NAME, ADDRESS, TELEPHONE) VALUES (?,?,?,?)";
    PreparedStatement preparedInsert;

    public ClientDao() {
        // If client table doesn't exist, create it and populate
        createTable();
        loadFromCSV();
    }

    private void createTable() {
        String drop = "DROP TABLE IF EXISTS client";
        String create = "CREATE TABLE IF NOT EXISTS client(" +
                "ID INT PRIMARY KEY NOT NULL," +
                "NAME VARCHAR(255) NOT NULL," +
                "ADDRESS INT NOT NULL," +
                "TELEPHONE VARCHAR(10) NOT NULL," +
                "FOREIGN KEY (ADDRESS) REFERENCES address);";
        try {
            connectionManager.execute(drop);
            connectionManager.execute(create);
        } catch (SQLException e) {
            System.err.println("Error Creating Client Table");
            e.printStackTrace();
        }

        try {
            preparedInsert = connectionManager.prepareStatement(InsertQuery);
        } catch (SQLException e) {
            System.err.println("Error Creating Prepared Statements for Client Table");
            e.printStackTrace();
        }
    }

    private void loadFromCSV() {
        // try and get file
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("./src/main/resources/data/Client.csv"));
        } catch (FileNotFoundException e1) {
            InputStream is = getClass().getResourceAsStream("/data/Client.csv");
            br = new BufferedReader((new InputStreamReader(is)));
        }
        // read csv file
        try {
            String line;
            // Populate table
            while ((line = br.readLine()) != null) {
                String[] split = line.split(",");
                this.addClient(split);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Error Inserting Client Rows");
            e.printStackTrace();
        }
        System.out.println("Loaded Client Table");
    }

    private void addClient(String[] ClientObj) throws SQLException {
        preparedInsert.setInt(1,Integer.parseInt(ClientObj[0]));
        preparedInsert.setString(2,ClientObj[1]);
        preparedInsert.setInt(3,Integer.parseInt(ClientObj[2]));
        preparedInsert.setString(4,ClientObj[3]);
        preparedInsert.executeUpdate();
    }
}