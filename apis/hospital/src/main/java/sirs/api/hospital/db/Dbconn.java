package sirs.api.hospital.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class Dbconn {
    public Connection conn;

    public Dbconn() {
        createConnection();
    }

    public void createConnection() {
        try {
            //TODO: Passar a envvar (n esquecer adicionar no setup ansible)
            String url = "jdbc:postgresql://localhost/sirsDb?user=administrator&password=administrator";
            this.conn = DriverManager.getConnection(url);
        } catch(Exception e) {
            System.out.println("Error creating database connection:");
            e.printStackTrace();
        }
    }
}
