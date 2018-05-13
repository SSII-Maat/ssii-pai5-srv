package src;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class Main {
    public static final Logger log = LogManager.getLogger("A1");

    public static void main(String[] args) throws InterruptedException {
        String log4jConfigPath = "log4j.properties";
        PropertyConfigurator.configure(log4jConfigPath);

        Thread serverThread = new Thread(new Server());
        serverThread.start();

        try {
            // Juntamos ambos threads
            serverThread.join();
        } catch(InterruptedException ie) {
            Main.log.warn(ie.getMessage());
            throw ie;
        }
    }
    
    public static void createNewDatabase(String fileName) {
    	 
        String url = "jdbc:sqlite:C:/sqlite/db/" + fileName;
 
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
            }
 
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public static void createNewTable() {
        // SQLite connection string
        String url = "jdbc:sqlite:C://sqlite/db/tests.db";
        
        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS resources (\n"
                + "	id integer PRIMARY KEY,\n"
                + "	name text NOT NULL,\n"
                + "	quantity real\n"
                + ");";
        
        try (Connection conn = DriverManager.getConnection(url);
                Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
