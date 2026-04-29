package battleship;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class TasksTest {

    private String captureOutput(Runnable action) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(out));
        try {
            action.run();
        } finally {
            System.setOut(originalOut);
        }
        return out.toString();
    }

    private String runMenuWithInput(String input) {
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        java.io.InputStream originalIn = System.in;
        PrintStream originalOut = System.out;

        System.setIn(in);
        System.setOut(new PrintStream(out));

        try {
            Tasks.menu();
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }

        return out.toString();
    }

    @Test
    @DisplayName("menuHelp deve imprimir ajuda com os comandos principais")
    void menuHelpPrintsCommands() {
        String output = captureOutput(Tasks::menuHelp);

        assertTrue(output.contains("AJUDA DO MENU"));
        assertTrue(output.contains("gerafrota"));
        assertTrue(output.contains("lefrota"));
        assertTrue(output.contains("estado"));
        assertTrue(output.contains("mapa"));
        assertTrue(output.contains("rajada"));
        assertTrue(output.contains("simula"));
        assertTrue(output.contains("tiros"));
        assertTrue(output.contains("desisto"));
        assertTrue(output.contains("pdf"));
    }

    @Test
    @DisplayName("menu deve terminar com desisto")
    void menuExitImmediately() {
        String output = runMenuWithInput("desisto");

        assertTrue(output.contains("AJUDA DO MENU"));
        assertTrue(output.contains("Bons ventos!"));
    }

    @Test
    @DisplayName("menu deve tratar comando desconhecido")
    void menuUnknownCommand() {
        String output = runMenuWithInput("xyz desisto");

        assertTrue(output.contains("Que comando é esse???"));
        assertTrue(output.contains("Bons ventos!"));
    }

    @Test
    @DisplayName("menu deve voltar a mostrar ajuda quando recebe ajuda")
    void menuHelpCommand() {
        String output = runMenuWithInput("ajuda desisto");

        int occurrences = output.split("AJUDA DO MENU", -1).length - 1;
        assertTrue(occurrences >= 2);
        assertTrue(output.contains("Bons ventos!"));
    }

    @Test
    @DisplayName("menu deve ignorar comandos protegidos por null sem falhar")
    void menuNullGuardCommands() {
        String output = runMenuWithInput("estado mapa tiros rajada simula ajuda desisto");

        assertTrue(output.contains("AJUDA DO MENU"));
        assertTrue(output.contains("Bons ventos!"));
    }

    @Test
    @DisplayName("menu deve executar gerafrota sem falhar")
    void menuGenerateFleet() {
        String output = runMenuWithInput("gerafrota desisto");

        assertTrue(output.contains("Bons ventos!"));
    }

    @Test
    @DisplayName("menu deve executar status mapa e tiros após gerafrota")
    void menuGenerateFleetThenStatusMapAndTiros() {
        String output = runMenuWithInput("""
                gerafrota
                estado
                mapa
                tiros
                desisto
                """);

        assertTrue(output.contains("Bons ventos!"));
    }

    @Test
    @DisplayName("menu deve executar rajada após gerafrota")
    void menuRajadaAfterGenerateFleet() {
        String output = runMenuWithInput("""
                gerafrota
                rajada A1 B2 C3
                desisto
                """);

        assertTrue(output.contains("Bons ventos!"));
    }

    @Test
    @DisplayName("menu deve executar pdf depois de gerar frota")
    void menuPdfAfterGenerateFleet() {
        File file = new File("jogadas.pdf");
        if (file.exists()) {
            assertTrue(file.delete());
        }

        String output = runMenuWithInput("""
                gerafrota
                pdf
                desisto
                """);

        assertTrue(output.contains("Bons ventos!"));
        assertTrue(file.exists());
        assertTrue(file.length() > 0);

        assertTrue(file.delete());
    }

    @Test
    @DisplayName("menu deve entrar em lefrota e falhar com navio inválido")
    void menuReadFleetInvalidShip() {
        assertThrows(AssertionError.class, () -> runMenuWithInput("""
                lefrota
                Banana 0 0 N
                """));
    }

    @Test
    @DisplayName("readPosition deve ler coordenadas numéricas")
    void readPosition() {
        Scanner in = new Scanner("2 3");

        Position position = Tasks.readPosition(in);

        assertNotNull(position);
        assertEquals(2, position.getRow());
        assertEquals(3, position.getColumn());
    }

    @Test
    @DisplayName("readPosition deve falhar com scanner nulo")
    void readPositionNullScanner() {
        assertThrows(AssertionError.class, () -> Tasks.readPosition(null));
    }

    @Test
    @DisplayName("readClassicPosition deve ler formato compacto")
    void readClassicPositionCompact() {
        Scanner in = new Scanner("A3");

        IPosition position = Tasks.readClassicPosition(in);

        assertNotNull(position);
        assertEquals(0, position.getRow());
        assertEquals(2, position.getColumn());
        assertEquals("A3", position.toString());
    }

    @Test
    @DisplayName("readClassicPosition deve ler formato separado")
    void readClassicPositionSeparated() {
        Scanner in = new Scanner("B 4");

        IPosition position = Tasks.readClassicPosition(in);

        assertNotNull(position);
        assertEquals(1, position.getRow());
        assertEquals(3, position.getColumn());
        assertEquals("B4", position.toString());
    }

    @Test
    @DisplayName("readClassicPosition deve aceitar minúsculas")
    void readClassicPositionLowercase() {
        Scanner in = new Scanner("c5");

        IPosition position = Tasks.readClassicPosition(in);

        assertNotNull(position);
        assertEquals(2, position.getRow());
        assertEquals(4, position.getColumn());
        assertEquals("C5", position.toString());
    }

    @Test
    @DisplayName("readClassicPosition deve falhar quando não há tokens")
    void readClassicPositionNoTokens() {
        Scanner in = new Scanner("");

        assertThrows(IllegalArgumentException.class, () -> Tasks.readClassicPosition(in));
    }

    @Test
    @DisplayName("readClassicPosition deve falhar com formato inválido")
    void readClassicPositionInvalidFormat() {
        Scanner in = new Scanner("33A");

        assertThrows(IllegalArgumentException.class, () -> Tasks.readClassicPosition(in));
    }

    @Test
    @DisplayName("readClassicPosition deve falhar com primeira parte inválida e segunda numérica")
    void readClassicPositionInvalidSeparatedFormat() {
        Scanner in = new Scanner("AA 3");

        assertThrows(IllegalArgumentException.class, () -> Tasks.readClassicPosition(in));
    }

    @Test
    @DisplayName("readShip deve falhar para tipo de navio inválido")
    void readShipInvalidKind() {
        Scanner in = new Scanner("Banana 1 1 N");

        assertThrows(AssertionError.class, () -> Tasks.readShip(in));
    }

    @Test
    @DisplayName("readShip deve falhar com scanner nulo")
    void readShipNullScanner() {
        assertThrows(AssertionError.class, () -> Tasks.readShip(null));
    }

    @Test
    @DisplayName("readShip deve falhar com input incompleto")
    void readShipIncompleteInput() {
        Scanner in = new Scanner("Barge 0 0");

        assertThrows(NoSuchElementException.class, () -> Tasks.readShip(in));
    }

    @Test
    @DisplayName("buildFleet deve falhar com scanner nulo")
    void buildFleetNullScanner() {
        assertThrows(AssertionError.class, () -> Tasks.buildFleet(null));
    }

    @Test
    @DisplayName("buildFleet deve falhar com navio inválido")
    void buildFleetInvalidShipThrows() {
        Scanner in = new Scanner("Banana 0 0 N");

        assertThrows(AssertionError.class, () -> Tasks.buildFleet(in));
    }

    @Test
    @DisplayName("buildFleet deve falhar com input incompleto")
    void buildFleetIncompleteInputThrows() {
        Scanner in = new Scanner("Barge 0 0");

        assertThrows(NoSuchElementException.class, () -> Tasks.buildFleet(in));
    }
}