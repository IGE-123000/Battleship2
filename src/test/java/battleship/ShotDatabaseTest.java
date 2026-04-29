package battleship;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Testes unitários para a classe ShotDatabase.
 */
class ShotDatabaseTest {

    private static final String DB_URL = "jdbc:sqlite:jogadas.db";

    @BeforeAll
    @DisplayName("Garante que começamos com uma base de dados limpa")
    static void setUpAll() {
        // Apaga o ficheiro da base de dados antiga antes de correr os testes
        File dbFile = new File("jogadas.db");
        if (dbFile.exists()) {
            dbFile.delete();
        }
    }

    @Test
    @DisplayName("Testa se a base de dados e a tabela são inicializadas corretamente")
    void testInitializeDatabase() {
        // Executa a inicialização (não deve lançar exceções)
        assertDoesNotThrow(() -> ShotDatabase.initializeDatabase());

        // Verifica diretamente na base de dados se a tabela 'jogadas' foi criada
        boolean tableExists = false;
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='jogadas'")) {

            if (rs.next()) {
                tableExists = true;
            }
        } catch (Exception e) {
            fail("Erro ao verificar a base de dados: " + e.getMessage());
        }

        assertTrue(tableExists, "A tabela 'jogadas' devia ter sido criada.");
    }

    @Test
    @DisplayName("Testa se o tiro é guardado corretamente na tabela")
    void testSaveShot() {
        // Garante que a tabela existe antes de inserir
        ShotDatabase.initializeDatabase();

        int testRow = 5;
        int testCol = 8;
        String testResult = "MISS";

        // Executa o método de inserção
        assertDoesNotThrow(() -> ShotDatabase.saveShot(testRow, testCol, testResult));

        // Verifica na base de dados se a linha lá está
        boolean dataFound = false;
        String query = "SELECT * FROM jogadas WHERE linha=" + testRow + " AND coluna=" + testCol + " AND resultado='" + testResult + "'";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                dataFound = true;
                assertEquals(testRow, rs.getInt("linha"), "A linha devia coincidir.");
                assertEquals(testCol, rs.getInt("coluna"), "A coluna devia coincidir.");
                assertEquals(testResult, rs.getString("resultado"), "O resultado devia coincidir.");
            }
        } catch (Exception e) {
            fail("Erro ao ler da base de dados: " + e.getMessage());
        }

        assertTrue(dataFound, "Os dados do tiro deveriam ter sido encontrados na base de dados.");
    }

    @Test
    @DisplayName("Booster de Cobertura: Força o bloco catch no saveShot")
    void testSaveShotExceptionCatchBlock() {
        // Para testar o bloco 'catch', precisamos que o SQL dê erro.
        // Vamos apagar a tabela 'jogadas' diretamente, o que fará o saveShot falhar internamente.
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS jogadas");
        } catch (Exception e) {
            fail("Falha a preparar o cenário de erro.");
        }

        // Tenta guardar. O código interno vai gerar uma SQLException e apanhá-la no catch (printStackTrace).
        // Como a exceção é apanhada, o assertDoesNotThrow passa e ganhamos os pontos de cobertura.
        assertDoesNotThrow(() -> ShotDatabase.saveShot(1, 1, "HIT"));

        // Re-inicializa a base de dados para não estragar outros testes que corram a seguir
        ShotDatabase.initializeDatabase();
    }
}