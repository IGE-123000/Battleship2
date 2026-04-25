package battleship;

import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FrigateTest {

	private Frigate frigate;

	@BeforeEach
	void setUp() {
		frigate = new Frigate(Compass.NORTH, new Position(5, 5));
	}

	@AfterEach
	void tearDown() {
		frigate = null;
	}

	@Test
	@DisplayName("Deve criar corretamente uma fragata orientada a norte")
	void testConstructorNorth() {
		assertNotNull(frigate);
		assertEquals("Fragata", frigate.getCategory());
		assertEquals(Compass.NORTH, frigate.getBearing());
		assertEquals(4, frigate.getSize());

		List<IPosition> positions = frigate.getPositions();
		assertEquals(4, positions.size());
		assertEquals(new Position(5, 5), positions.get(0));
		assertEquals(new Position(6, 5), positions.get(1));
		assertEquals(new Position(7, 5), positions.get(2));
		assertEquals(new Position(8, 5), positions.get(3));
	}

	@Test
	@DisplayName("Deve criar corretamente uma fragata orientada a sul")
	void testConstructorSouth() {
		frigate = new Frigate(Compass.SOUTH, new Position(5, 5));
		List<IPosition> positions = frigate.getPositions();

		assertNotNull(frigate);
		assertEquals(4, positions.size());
		assertEquals(new Position(5, 5), positions.get(0));
		assertEquals(new Position(6, 5), positions.get(1));
		assertEquals(new Position(7, 5), positions.get(2));
		assertEquals(new Position(8, 5), positions.get(3));
	}

	@Test
	@DisplayName("Deve criar corretamente uma fragata orientada a este")
	void testConstructorEast() {
		frigate = new Frigate(Compass.EAST, new Position(5, 5));
		List<IPosition> positions = frigate.getPositions();

		assertNotNull(frigate);
		assertEquals(4, positions.size());
		assertEquals(new Position(5, 5), positions.get(0));
		assertEquals(new Position(5, 6), positions.get(1));
		assertEquals(new Position(5, 7), positions.get(2));
		assertEquals(new Position(5, 8), positions.get(3));
	}

	@Test
	@DisplayName("Deve criar corretamente uma fragata orientada a oeste")
	void testConstructorWest() {
		frigate = new Frigate(Compass.WEST, new Position(5, 5));
		List<IPosition> positions = frigate.getPositions();

		assertNotNull(frigate);
		assertEquals(4, positions.size());
		assertEquals(new Position(5, 5), positions.get(0));
		assertEquals(new Position(5, 6), positions.get(1));
		assertEquals(new Position(5, 7), positions.get(2));
		assertEquals(new Position(5, 8), positions.get(3));
	}

	@Test
	@DisplayName("Deve devolver o tamanho correto")
	void testGetSize() {
		assertEquals(4, frigate.getSize());
	}

	@Test
	@DisplayName("Deve devolver a posição inicial correta")
	void testGetPosition() {
		assertEquals(new Position(5, 5), frigate.getPosition());
	}

	@Test
	@DisplayName("Deve indicar que a fragata ainda flutua sem tiros")
	void testStillFloating1() {
		assertTrue(frigate.stillFloating());
	}

	@Test
	@DisplayName("Deve indicar que a fragata ainda flutua com uma posição atingida")
	void testStillFloating2() {
		frigate.getPositions().get(0).shoot();
		assertTrue(frigate.stillFloating());
	}

	@Test
	@DisplayName("Deve indicar que a fragata não flutua quando todas as posições são atingidas")
	void testStillFloating3() {
		frigate.getPositions().forEach(IPosition::shoot);
		assertFalse(frigate.stillFloating());
	}

	@Test
	@DisplayName("Deve acertar numa posição válida da fragata")
	void testShoot1() {
		frigate.shoot(new Position(6, 5));
		assertTrue(frigate.getPositions().get(1).isHit());
	}

	@Test
	@DisplayName("Não deve acertar quando o tiro é fora da fragata")
	void testShoot2() {
		frigate.shoot(new Position(0, 0));

		for (IPosition position : frigate.getPositions()) {
			assertFalse(position.isHit());
		}
	}

	@Test
	@DisplayName("Deve afundar completamente a fragata")
	void testSink() {
		frigate.sink();

		for (IPosition position : frigate.getPositions()) {
			assertTrue(position.isHit());
		}

		assertFalse(frigate.stillFloating());
	}

	@Test
	@DisplayName("Deve indicar que ocupa uma posição válida")
	void testOccupies1() {
		assertTrue(frigate.occupies(new Position(7, 5)));
	}

	@Test
	@DisplayName("Deve indicar que não ocupa uma posição inválida")
	void testOccupies2() {
		assertFalse(frigate.occupies(new Position(5, 6)));
	}

	@Test
	@DisplayName("Deve devolver corretamente a linha mais acima")
	void testGetTopMostPos() {
		assertEquals(5, frigate.getTopMostPos());
	}

	@Test
	@DisplayName("Deve devolver corretamente a linha mais abaixo")
	void testGetBottomMostPos() {
		assertEquals(8, frigate.getBottomMostPos());
	}

	@Test
	@DisplayName("Deve devolver corretamente a coluna mais à esquerda")
	void testGetLeftMostPos() {
		assertEquals(5, frigate.getLeftMostPos());
	}

	@Test
	@DisplayName("Deve devolver corretamente a coluna mais à direita")
	void testGetRightMostPos() {
		assertEquals(5, frigate.getRightMostPos());
	}

	@Test
	@DisplayName("Deve indicar que duas fragatas estão demasiado próximas")
	void testTooCloseToShip1() {
		Frigate nearby = new Frigate(Compass.NORTH, new Position(5, 6));
		assertTrue(frigate.tooCloseTo(nearby));
	}

	@Test
	@DisplayName("Deve indicar que duas fragatas não estão demasiado próximas")
	void testTooCloseToShip2() {
		Frigate far = new Frigate(Compass.NORTH, new Position(0, 0));
		assertFalse(frigate.tooCloseTo(far));
	}

	@Test
	@DisplayName("Deve indicar que uma posição adjacente está demasiado próxima")
	void testTooCloseToPosition1() {
		assertTrue(frigate.tooCloseTo(new Position(5, 6)));
	}

	@Test
	@DisplayName("Deve indicar que uma posição distante não está demasiado próxima")
	void testTooCloseToPosition2() {
		assertFalse(frigate.tooCloseTo(new Position(0, 0)));
	}

	@Test
	@DisplayName("Deve devolver posições adjacentes da fragata")
	void testGetAdjacentPositions() {
		List<IPosition> adjacent = frigate.getAdjacentPositions();

		assertNotNull(adjacent);
		assertFalse(adjacent.isEmpty());
		assertFalse(adjacent.contains(new Position(5, 5)));
		assertFalse(adjacent.contains(new Position(6, 5)));
		assertFalse(adjacent.contains(new Position(7, 5)));
		assertFalse(adjacent.contains(new Position(8, 5)));
	}

	@Test
	@DisplayName("Deve devolver uma representação textual não vazia da fragata")
	void testToString() {
		String text = frigate.toString();

		assertNotNull(text);
		assertFalse(text.isBlank());
		assertTrue(text.contains("Fragata"));
	}

	@Test
	@DisplayName("Deve lançar exceção para argumentos nulos no construtor")
	void testConstructorInvalidInput() {
		assertThrows(NullPointerException.class, () -> new Frigate(null, null));
		assertThrows(NullPointerException.class, () -> new Frigate(Compass.NORTH, null));
	}
}