package battleship;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

/**
 * Testes unitários para a classe Fleet.
 * Focado em atingir 100% de Branch Coverage.
 */
public class FleetTest {

	private Fleet fleet;

	@BeforeEach
	@DisplayName("Inicializa uma frota vazia antes de cada teste")
	void setUp() {
		fleet = new Fleet();
	}

	@AfterEach
	@DisplayName("Limpa a referência da frota")
	void tearDown() {
		fleet = null;
	}

	@Test
	@DisplayName("Garante que a frota começa vazia")
	void testConstructor() {
		assertNotNull(fleet.getShips());
		assertTrue(fleet.getShips().isEmpty());
	}

	@Test
	@DisplayName("Testa a geração de frota aleatória com 11 navios")
	void testCreateRandom() {
		IFleet randomFleet = Fleet.createRandom();
		assertNotNull(randomFleet);
		// O array shipTypes no código original contém exatamente 11 strings
		assertEquals(11, randomFleet.getShips().size());
	}

	@Test
	@DisplayName("Testa isInsideBoard: Cobertura total de todos os limites (N, S, E, W)")
	void testIsInsideBoardBoundaries() {
		// BOARD_SIZE é 10 (posições 0 a 9). Cada teste abaixo falha uma parte da condição &&
		assertFalse(fleet.addShip(new Barge(Compass.NORTH, new Position(-1, 5))), "Falha X < 0");
		assertFalse(fleet.addShip(new Barge(Compass.NORTH, new Position(10, 5))), "Falha X > 9");
		assertFalse(fleet.addShip(new Barge(Compass.NORTH, new Position(5, -1))), "Falha Y < 0");
		assertFalse(fleet.addShip(new Barge(Compass.NORTH, new Position(5, 10))), "Falha Y > 9");
	}

	@Test
	@DisplayName("Testa getShipsLike: Ramos verdadeiro e falso no loop")
	void testGetShipsLike() {
		IShip b = new Barge(Compass.NORTH, new Position(1, 1));
		fleet.addShip(b);

		// Branch Verdadeiro: categoria existe (usamos a categoria real do objeto)
		String cat = b.getCategory();
		List<IShip> found = fleet.getShipsLike(cat);
		assertFalse(found.isEmpty());

		// Branch Falso: percorre o loop mas o 'if' da categoria é sempre falso
		assertTrue(fleet.getShipsLike("CategoriaInexistente").isEmpty());
	}

	@Test
	@DisplayName("Testa shipAt: Ramos verdadeiro e falso no loop")
	void testShipAt() {
		IShip s = new Barge(Compass.NORTH, new Position(1, 1));
		fleet.addShip(s);

		// Branch Verdadeiro: encontra o navio
		assertEquals(s, fleet.shipAt(new Position(1, 1)));

		// Branch Falso: percorre a lista mas nenhuma posição coincide
		assertNull(fleet.shipAt(new Position(9, 9)));
	}

	@Test
	@DisplayName("Testa getFloatingShips e getSunkShips: Ramos opostos")
	void testSunkAndFloating() {
		Barge b = new Barge(Compass.NORTH, new Position(1, 1));
		fleet.addShip(b);

		// Estado inicial: a flutuar
		assertEquals(1, fleet.getFloatingShips().size());
		assertTrue(fleet.getSunkShips().isEmpty());

		// Afundar o navio disparando na sua única posição
		b.getPositions().get(0).shoot();

		// Estado final: afundado
		assertTrue(fleet.getFloatingShips().isEmpty());
		assertEquals(1, fleet.getSunkShips().size());
	}

	@Test
	@DisplayName("Testa métodos de impressão para cobertura de linhas")
	void testPrints() {
		fleet.addShip(new Barge(Compass.NORTH, new Position(1, 1)));
		assertDoesNotThrow(() -> {
			fleet.printStatus();
			fleet.printAllShips();
			fleet.printFloatingShips();
			fleet.printShipsByCategory("barca");
			fleet.printShips(fleet.getShips());
		});
	}
}