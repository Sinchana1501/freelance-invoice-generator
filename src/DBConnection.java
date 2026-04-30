import java.sql.*;

public class DBConnection {
    private static final String URL  = "jdbc:mysql://localhost:3306/scriptmint_db?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "your_password"; // <-- CHANGE THIS

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
