package battleship;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PdfExporterTest {

    private String captureOutput(Runnable action) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(out));
        try {
            action.run();
        } finally {
            System.setOut(original);
        }
        return out.toString();
    }

    private IMove mockMove(List<IPosition> shots) {
        return (IMove) Proxy.newProxyInstance(
                IMove.class.getClassLoader(),
                new Class[]{IMove.class},
                (proxy, method, args) -> {
                    String name = method.getName();

                    if ("getShots".equals(name)) {
                        return shots;
                    }
                    if ("toString".equals(name)) {
                        return "MockMove";
                    }
                    if ("hashCode".equals(name)) {
                        return System.identityHashCode(proxy);
                    }
                    if ("equals".equals(name)) {
                        return proxy == args[0];
                    }

                    Class<?> returnType = method.getReturnType();
                    if (returnType.equals(boolean.class)) return false;
                    if (returnType.equals(int.class)) return 0;
                    if (returnType.equals(long.class)) return 0L;
                    if (returnType.equals(double.class)) return 0.0;
                    if (returnType.equals(float.class)) return 0.0f;
                    if (returnType.equals(short.class)) return (short) 0;
                    if (returnType.equals(byte.class)) return (byte) 0;
                    if (returnType.equals(char.class)) return '\0';

                    return null;
                }
        );
    }

    @Test
    @DisplayName("exportMovesToPdf deve criar PDF quando moves é null")
    void exportMovesToPdfWithNullMoves() throws Exception {
        Path filePath = Files.createTempFile("pdfexporter-null-", ".pdf");
        File file = filePath.toFile();

        String output = captureOutput(() ->
                assertDoesNotThrow(() -> PdfExporter.exportMovesToPdf(null, file.getAbsolutePath()))
        );

        assertTrue(file.exists());
        assertTrue(file.length() > 0);
        assertTrue(output.contains("PDF gerado com sucesso"));

        file.delete();
    }

    @Test
    @DisplayName("exportMovesToPdf deve criar PDF quando moves está vazia")
    void exportMovesToPdfWithEmptyMoves() throws Exception {
        Path filePath = Files.createTempFile("pdfexporter-empty-", ".pdf");
        File file = filePath.toFile();

        String output = captureOutput(() ->
                assertDoesNotThrow(() -> PdfExporter.exportMovesToPdf(List.of(), file.getAbsolutePath()))
        );

        assertTrue(file.exists());
        assertTrue(file.length() > 0);
        assertTrue(output.contains("PDF gerado com sucesso"));

        file.delete();
    }

    @Test
    @DisplayName("exportMovesToPdf deve criar PDF quando existem jogadas")
    void exportMovesToPdfWithMoves() throws Exception {
        Path filePath = Files.createTempFile("pdfexporter-moves-", ".pdf");
        File file = filePath.toFile();

        IMove move1 = mockMove(List.of(
                new Position(0, 0),
                new Position(1, 1),
                new Position(2, 2)
        ));

        IMove move2 = mockMove(List.of(
                new Position(3, 3),
                new Position(4, 4)
        ));

        List<IMove> moves = List.of(move1, move2);

        String output = captureOutput(() ->
                assertDoesNotThrow(() -> PdfExporter.exportMovesToPdf(moves, file.getAbsolutePath()))
        );

        assertTrue(file.exists());
        assertTrue(file.length() > 0);
        assertTrue(output.contains("PDF gerado com sucesso"));

        file.delete();
    }

    @Test
    @DisplayName("exportMovesToPdf deve tratar erro de escrita")
    void exportMovesToPdfWithInvalidPath() throws Exception {
        Path directory = Files.createTempDirectory("pdfexporter-dir-");

        String output = captureOutput(() ->
                assertDoesNotThrow(() -> PdfExporter.exportMovesToPdf(List.of(), directory.toString()))
        );

        assertTrue(output.contains("Erro ao gerar PDF"));

        Files.deleteIfExists(directory);
    }
}