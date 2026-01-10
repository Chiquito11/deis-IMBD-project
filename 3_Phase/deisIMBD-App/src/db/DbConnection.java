package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection {

    // Para Windows Authentication com SQL Server Express
    private static final String URL = 
        "jdbc:sqlserver://localhost\\SQLEXPRESS;" +  
        "databaseName=imbd-db;" +                  
        "user=sa;" +
        "password=user;" +         
        "encrypt=true;" +
        "trustServerCertificate=true;";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}
