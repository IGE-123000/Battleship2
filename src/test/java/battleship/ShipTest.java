package battleship;

import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ShipTest {

    private Ship ship;

    @BeforeEach
    void setUp() {
        ship = new Barge(Compass.NORTH, new Position(5, 5));
    }

    @AfterEach
    void tearDown() {
        ship = null;
    }

    @Test
    @DisplayName("Deve criar corretamente uma barca virada a norte")
    void testConstructor() {
        assertNotNull(ship, "Error: Ship instance should not be null.");
        assertEquals("Barca", ship.getCategory(), "Error: Ship category is incorrect.");
        assertEquals(Compass.NORTH, ship.getBearing(), "Error: Ship bearing is incorrect.");
        assertEquals(1, ship.getSize(), "Error: Ship size is incorrect.");
        assertFalse(ship.getPositions().isEmpty(), "Error: Ship positions should not be empty.");
    }

    @Test
    @DisplayName("Deve devolver a categoria correta")
    void testGetCategory() {
        assertEquals("Barca", ship.getCategory(), "Error: Ship category should be 'Barca'.");
    }

    @Test
    @DisplayName("Deve devolver o tamanho correto")
    void testGetSize() {
        assertEquals(1, ship.getSize(), "Error: Ship size should be 1.");
    }

    @Test
    @DisplayName("Deve devolver a orientação correta")
    void testGetBearing() {
        assertEquals(Compass.NORTH, ship.getBearing(), "Error: Ship bearing should be NORTH.");
    }

    @Test
    @DisplayName("Deve devolver a posição inicial correta do navio")
    void testGetPosition() {
        assertEquals(new Position(5, 5), ship.getPosition());
    }

    @Test
    @DisplayName("Deve devolver a lista correta de posições ocupadas")
    void testGetPositions() {
        List<IPosition> positions = ship.getPositions();
        assertNotNull(positions, "Error: Ship positions should not be null.");
        assertEquals(1, positions.size(), "Error: Ship should have exactly one position.");
        assertEquals(5, positions.get(0).getRow(), "Error: Position's row should be 5.");
        assertEquals(5, positions.get(0).getColumn(), "Error: Position's column should be 5.");
    }

    @Test
    @DisplayName("Deve devolver posições adjacentes ao navio")
    void testGetAdjacentPositions() {
        List<IPosition> adjacentPositions = ship.getAdjacentPositions();

        assertNotNull(adjacentPositions);
        assertFalse(adjacentPositions.isEmpty());

        assertTrue(adjacentPositions.contains(new Position(4, 5)));
        assertTrue(adjacentPositions.contains(new Position(5, 6)));
        assertTrue(adjacentPositions.contains(new Position(6, 5)));
        assertTrue(adjacentPositions.contains(new Position(5, 4)));
    }

    @Test
    @DisplayName("Deve indicar que o navio ainda flutua quando não foi atingido")
    void testStillFloating1() {
        assertTrue(ship.stillFloating(), "Error: Ship should still be floating.");
    }

    @Test
    @DisplayName("Deve indicar que o navio não flutua depois de ser atingido")
    void testStillFloating2() {
        ship.getPositions().get(0).shoot();
        assertFalse(ship.stillFloating(), "Error: Ship should no longer be floating after being hit.");
    }

    @Test
    @DisplayName("Deve marcar a posição como atingida quando o tiro acerta")
    void testShoot1() {
        Position target = new Position(5, 5);
        ship.shoot(target);
        assertTrue(ship.getPositions().get(0).isHit(), "Error: Position should be marked as hit.");
    }

    @Test
    @DisplayName("Não deve marcar a posição como atingida quando o tiro falha")
    void testShoot2() {
        Position target = new Position(0, 0);
        ship.shoot(target);
        assertFalse(ship.getPositions().get(0).isHit(), "Error: Position should not be marked as hit for an invalid target.");
    }

    @Test
    @DisplayName("Deve afundar o navio marcando todas as posições como atingidas")
    void testSink() {
        ship.sink();

        for (IPosition position : ship.getPositions()) {
            assertTrue(position.isHit());
        }

        assertFalse(ship.stillFloating());
    }

    @Test
    @DisplayName("Deve indicar que ocupa uma posição válida")
    void testOccupies1() {
        Position pos = new Position(5, 5);
        assertTrue(ship.occupies(pos), "Error: Ship should occupy position (5, 5).");
    }

    @Test
    @DisplayName("Deve indicar que não ocupa uma posição inválida")
    void testOccupies2() {
        Position pos = new Position(1, 1);
        assertFalse(ship.occupies(pos), "Error: Ship should not occupy position (1, 1).");
    }

    @Test
    @DisplayName("Deve indicar que dois navios estão demasiado próximos")
    void testTooCloseToShip1() {
        Ship nearbyShip = new Barge(Compass.NORTH, new Position(5, 6));
        assertTrue(ship.tooCloseTo(nearbyShip), "Error: Ships should be too close.");
    }

    @Test
    @DisplayName("Deve indicar que dois navios não estão demasiado próximos")
    void testTooCloseToShip2() {
        Ship farShip = new Barge(Compass.NORTH, new Position(8, 8));
        assertFalse(ship.tooCloseTo(farShip), "Error: Ships should not be too close.");
    }

    @Test
    @DisplayName("Deve indicar que uma posição adjacente está demasiado próxima")
    void testTooCloseToPosition1() {
        Position pos = new Position(5, 6);
        assertTrue(ship.tooCloseTo(pos), "Error: Ship should be too close to the given position.");
    }

    @Test
    @DisplayName("Deve indicar que uma posição distante não está demasiado próxima")
    void testTooCloseToPosition2() {
        Position pos = new Position(7, 7);
        assertFalse(ship.tooCloseTo(pos), "Error: Ship should not be too close to the given position.");
    }

    @Test
    @DisplayName("Deve devolver a linha mais acima ocupada pelo navio")
    void testGetTopMostPos() {
        assertEquals(5, ship.getTopMostPos(), "Error: The topmost position should be 5.");
    }

    @Test
    @DisplayName("Deve devolver a linha mais abaixo ocupada pelo navio")
    void testGetBottomMostPos() {
        assertEquals(5, ship.getBottomMostPos(), "Error: The bottommost position should be 5.");
    }

    @Test
    @DisplayName("Deve devolver a coluna mais à esquerda ocupada pelo navio")
    void testGetLeftMostPos() {
        assertEquals(5, ship.getLeftMostPos(), "Error: The leftmost position should be 5.");
    }

    @Test
    @DisplayName("Deve devolver a coluna mais à direita ocupada pelo navio")
    void testGetRightMostPos() {
        assertEquals(5, ship.getRightMostPos(), "Error: The rightmost position should be 5.");
    }

    @Test
    @DisplayName("Deve devolver uma representação textual não vazia do navio")
    void testToString() {
        String text = ship.toString();

        assertNotNull(text);
        assertFalse(text.isBlank());
        assertTrue(text.contains("Barca"));
    }

    @Test
    @DisplayName("Deve construir uma barca corretamente")
    void testBuildShipBarge() {
        Ship builtShip = Ship.buildShip("barca", Compass.NORTH, new Position(2, 2));

        assertNotNull(builtShip);
        assertEquals("Barca", builtShip.getCategory());
    }

    @Test
    @DisplayName("Deve construir uma caravela corretamente")
    void testBuildShipCaravel() {
        Ship builtShip = Ship.buildShip("caravela", Compass.NORTH, new Position(2, 2));

        assertNotNull(builtShip);
        assertEquals("Caravela", builtShip.getCategory());
    }

    @Test
    @DisplayName("Deve construir uma nau corretamente")
    void testBuildShipCarrack() {
        Ship builtShip = Ship.buildShip("nau", Compass.NORTH, new Position(2, 2));

        assertNotNull(builtShip);
        assertEquals("Nau", builtShip.getCategory());
    }

    @Test
    @DisplayName("Deve construir uma fragata corretamente")
    void testBuildShipFrigate() {
        Ship builtShip = Ship.buildShip("fragata", Compass.NORTH, new Position(2, 2));

        assertNotNull(builtShip);
        assertEquals("Fragata", builtShip.getCategory());
    }

    @Test
    @DisplayName("Deve construir um galeão corretamente")
    void testBuildShipGalleon() {
        Ship builtShip = Ship.buildShip("galeao", Compass.NORTH, new Position(2, 2));

        assertNotNull(builtShip);
        assertEquals("Galeao", builtShip.getCategory());
    }

    @Test
    @DisplayName("Deve devolver null para tipo de navio inválido")
    void testBuildShipInvalidKind() {
        Ship builtShip = Ship.buildShip("submarino", Compass.NORTH, new Position(2, 2));

        assertNull(builtShip);
    }

    @Test
    @DisplayName("Deve manter-se a flutuar quando apenas parte do navio foi atingida")
    void testStillFloatingPartialHit() {
        Ship biggerShip = new Frigate(Compass.EAST, new Position(3, 3));

        biggerShip.getPositions().get(0).shoot();

        assertTrue(biggerShip.stillFloating());
    }

    @Test
    @DisplayName("Deve afundar completamente um navio com várias posições")
    void testSinkMultiPositionShip() {
        Ship biggerShip = new Frigate(Compass.EAST, new Position(3, 3));

        biggerShip.sink();

        for (IPosition position : biggerShip.getPositions()) {
            assertTrue(position.isHit());
        }

        assertFalse(biggerShip.stillFloating());
    }

    @Test
    @DisplayName("Deve acertar apenas na posição alvo num navio com várias posições")
    void testShootMultiPositionShip() {
        Ship biggerShip = new Frigate(Compass.EAST, new Position(3, 3));
        IPosition target = biggerShip.getPositions().get(1);

        biggerShip.shoot(target);

        assertTrue(biggerShip.getPositions().get(1).isHit());
    }

    @Test
    @DisplayName("Deve calcular corretamente a linha mais acima num navio vertical")
    void testGetTopMostPosMultiPositionShip() {
        Ship biggerShip = new Frigate(Compass.NORTH, new Position(5, 5));

        assertEquals(biggerShip.getPositions().stream().mapToInt(IPosition::getRow).min().orElseThrow(),
                biggerShip.getTopMostPos());
    }

    @Test
    @DisplayName("Deve calcular corretamente a linha mais abaixo num navio vertical")
    void testGetBottomMostPosMultiPositionShip() {
        Ship biggerShip = new Frigate(Compass.NORTH, new Position(5, 5));

        assertEquals(biggerShip.getPositions().stream().mapToInt(IPosition::getRow).max().orElseThrow(),
                biggerShip.getBottomMostPos());
    }

    @Test
    @DisplayName("Deve calcular corretamente a coluna mais à esquerda num navio horizontal")
    void testGetLeftMostPosMultiPositionShip() {
        Ship biggerShip = new Frigate(Compass.EAST, new Position(5, 5));

        assertEquals(biggerShip.getPositions().stream().mapToInt(IPosition::getColumn).min().orElseThrow(),
                biggerShip.getLeftMostPos());
    }

    @Test
    @DisplayName("Deve calcular corretamente a coluna mais à direita num navio horizontal")
    void testGetRightMostPosMultiPositionShip() {
        Ship biggerShip = new Frigate(Compass.EAST, new Position(5, 5));

        assertEquals(biggerShip.getPositions().stream().mapToInt(IPosition::getColumn).max().orElseThrow(),
                biggerShip.getRightMostPos());
    }

    @Test
    @DisplayName("Deve indicar que navios maiores estão demasiado próximos")
    void testTooCloseToMultiPositionShip() {
        Ship ship1 = new Frigate(Compass.EAST, new Position(4, 4));
        Ship ship2 = new Frigate(Compass.EAST, new Position(5, 4));

        assertTrue(ship1.tooCloseTo(ship2));
    }

    @Test
    @DisplayName("Deve indicar que navios maiores não estão demasiado próximos")
    void testNotTooCloseToMultiPositionShip() {
        Ship ship1 = new Frigate(Compass.EAST, new Position(1, 1));
        Ship ship2 = new Frigate(Compass.EAST, new Position(8, 8));

        assertFalse(ship1.tooCloseTo(ship2));
    }
}