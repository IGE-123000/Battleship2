package battleship;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class PdfExporter {

    public static void exportMovesToPdf(List<IMove> moves, String fileName) {
        Document document = new Document();

        try {
            PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();

            document.add(new Paragraph("Historico de Jogadas - Battleship2"));
            document.add(new Paragraph(" "));

            if (moves == null || moves.isEmpty()) {
                document.add(new Paragraph("Ainda nao existem jogadas registadas."));
            } else {
                for (int i = 0; i < moves.size(); i++) {
                    IMove move = moves.get(i);
                    StringBuilder line = new StringBuilder();
                    line.append("Jogada ").append(i + 1).append(": ");

                    List<IPosition> shots = move.getShots();
                    for (int j = 0; j < shots.size(); j++) {
                        IPosition shot = shots.get(j);
                        line.append(shot.toString());
                        if (j < shots.size() - 1) {
                            line.append(", ");
                        }
                    }

                    document.add(new Paragraph(line.toString()));
                }
            }

            System.out.println("PDF gerado com sucesso: " + fileName);

        } catch (DocumentException | IOException e) {
            System.out.println("Erro ao gerar PDF: " + e.getMessage());
        } finally {
            document.close();
        }
    }
}