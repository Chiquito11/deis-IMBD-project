import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestConnection {

    private static final String URL = 
        "jdbc:sqlserver://localhost\\SQLEXPRESS;" +  
        "databaseName=imbd-db;" +                  
        "user=sa;" +
        "password=user;" +         
        "encrypt=true;" +
        "trustServerCertificate=true;";

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(URL)) {
            System.out.println("CONECTADO COM SUCESSO USANDO SQL AUTH (sa)! 🎉");
        } catch (SQLException e) {
            System.out.println("Erro:");
            e.printStackTrace();
        }
    }
}