package battleship;

import javax.swing.*;
import java.awt.*;

public class BoardGUI extends JFrame {

    private static final int BOARD_SIZE = 10;
    private JButton[][] cells = new JButton[BOARD_SIZE][BOARD_SIZE];

    public BoardGUI() {
        setTitle("Battleship Board");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new GridLayout(BOARD_SIZE, BOARD_SIZE));

        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {

                JButton cell = new JButton();
                cell.setBackground(Color.BLUE);
                cell.setOpaque(true);
                cell.setBorder(BorderFactory.createLineBorder(Color.BLACK));

                cells[r][c] = cell;
                add(cell);
            }
        }

        setVisible(true);
    }

    public void updateBoard(IFleet fleet, java.util.List<IMove> moves) {

        // limpar tabuleiro
        for (int r = 0; r < BOARD_SIZE; r++)
            for (int c = 0; c < BOARD_SIZE; c++)
                cells[r][c].setBackground(Color.BLUE);

        // desenhar navios
        for (IShip ship : fleet.getShips()) {
            for (IPosition pos : ship.getPositions()) {
                cells[pos.getRow()][pos.getColumn()].setBackground(Color.GRAY);
            }
        }

        // desenhar tiros
        for (IMove move : moves) {
            for (IPosition shot : move.getShots()) {

                if (!shot.isInside()) continue;

                int r = shot.getRow();
                int c = shot.getColumn();

                IShip ship = fleet.shipAt(shot);

                if (ship != null)
                    cells[r][c].setBackground(Color.RED);   // HIT
                else
                    cells[r][c].setBackground(Color.WHITE); // MISS
            }
        }
    }
}
