package battleship;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class ShotDatabase {

    private static final String DB_URL = "jdbc:sqlite:jogadas.db";

    public static void initializeDatabase() {

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            String sql = """
                    CREATE TABLE IF NOT EXISTS jogadas (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        linha INTEGER,
                        coluna INTEGER,
                        resultado TEXT
                    )
                    """;

            stmt.execute(sql);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveShot(int row, int column, String result) {

        String sql = "INSERT INTO jogadas(linha, coluna, resultado) VALUES(?,?,?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, row);
            pstmt.setInt(2, column);
            pstmt.setString(3, result);

            pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
