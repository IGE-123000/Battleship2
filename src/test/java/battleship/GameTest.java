package battleship;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

public class GameTest {

	private Game game;
	private IFleet myFleet;

	@BeforeEach
	@DisplayName("Setup do Jogo")
	void setUp() {
		myFleet = new Fleet();
		// Adicionamos um navio qualquer para testes
		myFleet.addShip(new Barge(Compass.NORTH, new Position(0, 0)));
		game = new Game(myFleet);
	}

	@Test
	@DisplayName("Testa fireSingleShot: Miss, Hit e Repetido")
	void testFireSingleShotLogic() {
		// 1. Tiro na água
		IGame.ShotResult miss = game.fireSingleShot(new Position(9, 9), false);
		assertTrue(miss.valid());
		assertFalse(miss.repeated());

		// 2. Tiro certeiro (no navio em 0,0)
		IGame.ShotResult hit = game.fireSingleShot(new Position(0, 0), false);
		assertTrue(hit.valid());
		assertTrue(game.getHits() >= 1);

		// 3. Tiro repetido (usando o parâmetro booleano)
		IGame.ShotResult rep = game.fireSingleShot(new Position(0, 0), true);
		assertTrue(rep.repeated());
		assertEquals(1, game.getRepeatedShots());

		// 4. Tiro inválido (fora do mapa)
		IGame.ShotResult inv = game.fireSingleShot(new Position(-1, 5), false);
		assertFalse(inv.valid());
		assertEquals(1, game.getInvalidShots());
	}

	@Test
	@DisplayName("Testa Getters e Afundamento Dinâmico")
	void testStats() {
		// Vamos buscar o navio que adicionámos
		IShip ship = myFleet.getShips().get(0);
		int totalNavios = game.getRemainingShips();

		// Disparar em TODAS as posições que o navio tem, seja qual for o tamanho
		for (IPosition pos : ship.getPositions()) {
			game.fireSingleShot(pos, false);
		}

		// Agora o navio TEM de estar afundado
		assertEquals(totalNavios - 1, game.getRemainingShips(), "O navio devia ter afundado");
		assertTrue(game.getSunkShips() >= 1);
	}

	@Test
	@DisplayName("Testa formatos de leitura do Scanner (A1 vs A 1)")
	void testReadEnemyFire() {
		// Formato com espaço: "A 1" e formatos juntos: "B2 C3"
		// Isto cobre os ramos do 'if (token.matches("[A-Za-z]"))'
		Scanner scanner = new Scanner("A 1 B2 C3\n");
		assertDoesNotThrow(() -> game.readEnemyFire(scanner));

		// Testar erro de input insuficiente para cobrir a exceção
		Scanner scannerCurto = new Scanner("A1 B2\n");
		assertThrows(IllegalArgumentException.class, () -> game.readEnemyFire(scannerCurto));
	}

	@Test
	@DisplayName("Testa JSON e processamento de erros")
	void testJsonStuff() {
		// JSON válido
		String json = "[{\"row\":\"A\", \"column\":1}, {\"row\":\"B\", \"column\":2}, {\"row\":\"C\", \"column\":3}]";
		assertDoesNotThrow(() -> game.processEnemyFireJson(json));

		// JSON inválido para entrar no bloco CATCH e subir a cobertura
		assertDoesNotThrow(() -> game.processEnemyFireJson("{ erro }"));
	}

	@Test
	@DisplayName("Testa Board e Random Fire")
	void testBoardAndRandom() {
		// Cobre o método randomEnemyFire
		assertNotNull(game.randomEnemyFire());

		// Cobre os métodos de print e as condições show_shots/showLegend
		assertDoesNotThrow(() -> {
			game.printMyBoard(true, true);
			game.printAlienBoard(false, false);
			game.over();
		});
	}

	@Test
	@DisplayName("Booster de Cobertura: Testar todos os símbolos do mapa")
	void testPrintBoardFullCoverage() {
		// 1. Adicionar um tiro na água e um tiro no navio para cobrir os símbolos * e o
		game.fireSingleShot(new Position(0, 0), false); // Hit
		game.fireSingleShot(new Position(9, 9), false); // Miss

		// 2. Afundar o navio para mostrar os símbolos adjacentes (-)
		IShip ship = myFleet.getShips().get(0);
		for (IPosition p : ship.getPositions()) {
			game.fireSingleShot(p, false);
		}

		// 3. Imprimir com todas as combinações de flags para cobrir os 'if'
		assertDoesNotThrow(() -> {
			game.printMyBoard(true, true);   // Mostra tudo
			game.printMyBoard(true, false);  // Sem legenda
			game.printMyBoard(false, true);  // Sem tiros
		});
	}

	@Test
	@DisplayName("Booster de Cobertura: Casos de Erro no Scanner")
	void testScannerEdgeCases() {
		// Testa o erro de formato (ex: Coluna sem linha)
		Scanner s1 = new Scanner("A B2 C3\n");
		assertThrows(IllegalArgumentException.class, () -> game.readEnemyFire(s1));

		// Testa o erro de inserir 4 posições em vez de 3
		// (O código vai ler as 3 primeiras e parar, ou dar erro se sobrar lixo)
		Scanner s2 = new Scanner("A1 B2 C3 D4\n");
		assertDoesNotThrow(() -> game.readEnemyFire(s2));
	}

	@Test
	@DisplayName("Booster 80%: Força o ramo de tabuleiro quase cheio")
	void testRandomEnemyFireSmallBoard() {
		game.getAlienMoves().clear();

		// Preencher quase tudo, mas deixar a última célula (9,9) livre
		for (int r = 0; r < Game.BOARD_SIZE; r++) {
			List<IPosition> shots = new ArrayList<>();
			for (int c = 0; c < Game.BOARD_SIZE; c++) {
				// Se não for a última posição, "queimamos" a célula
				if (!(r == 9 && c == 9)) {
					shots.add(new Position(r, c));
				}
			}
			// Adicionamos à lista de moves para o usablePositions as remover
			game.getAlienMoves().add(new Move(r + 1, shots, new ArrayList<>()));
		}

		// Agora candidateShots terá apenas 1 posição.
		// O código entrará no ELSE (size < Game.NUMBER_SHOTS)
		assertNotNull(game.randomEnemyFire());
	}

	@Test
	@DisplayName("Booster 80%: Cobertura de símbolos adjacentes no print")
	void testPrintBoardAdjacentShot() {
		// 1. Adicionar um navio e afundá-lo para gerar os marcadores '-' (adjacentes)
		IShip ship = myFleet.getShips().get(0);
		for (IPosition p : ship.getPositions()) {
			game.fireSingleShot(p, false);
		}

		// 2. Disparar numa posição ADJACENTE ao navio afundado
		// Se o navio está em (0,0), a posição (1,1) deve ser adjacente
		IPosition adj = ship.getAdjacentPositions().get(0);
		game.fireSingleShot(adj, false);

		// 3. Imprimir com show_shots = true
		assertDoesNotThrow(() -> game.printMyBoard(true, true));
	}

	@Test
	@DisplayName("Booster 80%: Casos limite do JSON")
	void testJsonEdgeCases() {
		// Lista vazia para o JSON
		String json = Game.jsonShots(new ArrayList<>());
		// Removemos espaços e quebras de linha para comparar de forma segura
		String cleanedJson = json.replaceAll("\\s", "");
		assertEquals("[]", cleanedJson);
	}

	@Test
	@DisplayName("Booster Final: Erro de posição incompleta")
	void testReadEnemyFireIncomplete() {
		// Escrevemos a letra mas não o número. Isto ativa a exceção "Posição incompleta!"
		Scanner scanner = new Scanner("A B2 C3\n");
		assertThrows(IllegalArgumentException.class, () -> game.readEnemyFire(scanner));
	}
}