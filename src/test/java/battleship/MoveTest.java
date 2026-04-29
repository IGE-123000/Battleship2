package battleship;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MoveTest {

    @Test
    @DisplayName("Deve devolver uma representação textual correta da jogada")
    void testToString() {
        List<IPosition> shots = new ArrayList<>();
        shots.add(new Position(0, 0));
        shots.add(new Position(1, 1));

        List<IGame.ShotResult> results = new ArrayList<>();

        Move move = new Move(3, shots, results);

        String text = move.toString();

        assertTrue(text.contains("number=3"));
        assertTrue(text.contains("shots=2"));
        assertTrue(text.contains("results=0"));
    }

    @Test
    @DisplayName("Deve devolver o número correto da jogada")
    void getNumber() {
        Move move = new Move(5, new ArrayList<>(), new ArrayList<>());

        assertEquals(5, move.getNumber());
    }

    @Test
    @DisplayName("Deve devolver a lista correta de tiros")
    void getShots() {
        List<IPosition> shots = new ArrayList<>();
        shots.add(new Position(0, 0));
        shots.add(new Position(2, 3));

        Move move = new Move(1, shots, new ArrayList<>());

        assertEquals(shots, move.getShots());
        assertEquals(2, move.getShots().size());
        assertEquals(new Position(0, 0), move.getShots().get(0));
        assertEquals(new Position(2, 3), move.getShots().get(1));
    }

    @Test
    @DisplayName("Deve devolver a lista correta de resultados dos tiros")
    void getShotResults() {
        List<IGame.ShotResult> results = new ArrayList<>();

        Move move = new Move(1, new ArrayList<>(), results);

        assertEquals(results, move.getShotResults());
        assertTrue(move.getShotResults().isEmpty());
    }

    @Test
    @DisplayName("Deve processar corretamente um tiro repetido e um tiro na água")
    void processEnemyFire() throws Exception {
        List<IGame.ShotResult> results = List.of(
                new IGame.ShotResult(true, true, null, false),
                new IGame.ShotResult(true, false, null, false)
        );

        Move move = new Move(2, new ArrayList<>(), results);

        String json = move.processEnemyFire(false);

        ObjectMapper mapper = new ObjectMapper();
        Map<?, ?> parsed = mapper.readValue(json, Map.class);

        assertEquals(1, parsed.get("validShots"));
        assertEquals(1, parsed.get("repeatedShots"));
        assertEquals(1, parsed.get("missedShots"));
        assertEquals(Game.NUMBER_SHOTS - 2, parsed.get("outsideShots"));

        List<?> sunkBoats = (List<?>) parsed.get("sunkBoats");
        List<?> hitsOnBoats = (List<?>) parsed.get("hitsOnBoats");

        assertTrue(sunkBoats.isEmpty());
        assertTrue(hitsOnBoats.isEmpty());
    }

    @Test
    @DisplayName("Deve registar acertos num barco que não foi afundado")
    void processEnemyFireWithHitOnBoat() throws Exception {
        IShip frigate = fakeShip("Frigate");

        List<IGame.ShotResult> results = List.of(
                new IGame.ShotResult(true, false, frigate, false)
        );

        Move move = new Move(4, new ArrayList<>(), results);

        String json = move.processEnemyFire(false);

        ObjectMapper mapper = new ObjectMapper();
        Map<?, ?> parsed = mapper.readValue(json, Map.class);

        assertEquals(1, parsed.get("validShots"));
        assertEquals(0, parsed.get("repeatedShots"));
        assertEquals(0, parsed.get("missedShots"));
        assertEquals(Game.NUMBER_SHOTS - 1, parsed.get("outsideShots"));

        List<?> sunkBoats = (List<?>) parsed.get("sunkBoats");
        List<?> hitsOnBoats = (List<?>) parsed.get("hitsOnBoats");

        assertTrue(sunkBoats.isEmpty());
        assertEquals(1, hitsOnBoats.size());

        Map<?, ?> hit = (Map<?, ?>) hitsOnBoats.get(0);
        assertEquals("Frigate", hit.get("type"));
        assertEquals(1, hit.get("hits"));
    }

    @Test
    @DisplayName("Deve registar barcos afundados corretamente")
    void processEnemyFireWithSunkBoat() throws Exception {
        IShip ship = fakeShip("Ship");

        List<IGame.ShotResult> results = List.of(
                new IGame.ShotResult(true, false, ship, true)
        );

        Move move = new Move(6, new ArrayList<>(), results);

        String json = move.processEnemyFire(false);

        ObjectMapper mapper = new ObjectMapper();
        Map<?, ?> parsed = mapper.readValue(json, Map.class);

        assertEquals(1, parsed.get("validShots"));
        assertEquals(0, parsed.get("repeatedShots"));
        assertEquals(0, parsed.get("missedShots"));
        assertEquals(Game.NUMBER_SHOTS - 1, parsed.get("outsideShots"));

        List<?> sunkBoats = (List<?>) parsed.get("sunkBoats");
        List<?> hitsOnBoats = (List<?>) parsed.get("hitsOnBoats");

        assertEquals(1, sunkBoats.size());
        assertTrue(hitsOnBoats.isEmpty());

        Map<?, ?> sunk = (Map<?, ?>) sunkBoats.get(0);
        assertEquals("Ship", sunk.get("type"));
        assertEquals(1, sunk.get("count"));
    }

    @Test
    @DisplayName("Deve ignorar tiros inválidos")
    void processEnemyFireWithInvalidShot() throws Exception {
        List<IGame.ShotResult> results = List.of(
                new IGame.ShotResult(false, false, null, false)
        );

        Move move = new Move(7, new ArrayList<>(), results);

        String json = move.processEnemyFire(false);

        ObjectMapper mapper = new ObjectMapper();
        Map<?, ?> parsed = mapper.readValue(json, Map.class);

        assertEquals(0, parsed.get("validShots"));
        assertEquals(0, parsed.get("repeatedShots"));
        assertEquals(0, parsed.get("missedShots"));
        assertEquals(Game.NUMBER_SHOTS, parsed.get("outsideShots"));

        List<?> sunkBoats = (List<?>) parsed.get("sunkBoats");
        List<?> hitsOnBoats = (List<?>) parsed.get("hitsOnBoats");

        assertTrue(sunkBoats.isEmpty());
        assertTrue(hitsOnBoats.isEmpty());
    }

    @Test
    @DisplayName("Deve processar corretamente quando existem apenas tiros repetidos em modo verbose")
    void processEnemyFireOnlyRepeatedVerbose() throws Exception {
        List<IGame.ShotResult> results = List.of(
                new IGame.ShotResult(true, true, null, false),
                new IGame.ShotResult(true, true, null, false)
        );

        Move move = new Move(8, new ArrayList<>(), results);

        String json = move.processEnemyFire(true);

        ObjectMapper mapper = new ObjectMapper();
        Map<?, ?> parsed = mapper.readValue(json, Map.class);

        assertEquals(0, parsed.get("validShots"));
        assertEquals(2, parsed.get("repeatedShots"));
        assertEquals(0, parsed.get("missedShots"));
        assertEquals(Game.NUMBER_SHOTS - 2, parsed.get("outsideShots"));
    }

    @Test
    @DisplayName("Deve contar vários tiros na água")
    void processEnemyFireWithMultipleMisses() throws Exception {
        List<IGame.ShotResult> results = List.of(
                new IGame.ShotResult(true, false, null, false),
                new IGame.ShotResult(true, false, null, false)
        );

        Move move = new Move(9, new ArrayList<>(), results);

        String json = move.processEnemyFire(true);

        ObjectMapper mapper = new ObjectMapper();
        Map<?, ?> parsed = mapper.readValue(json, Map.class);

        assertEquals(2, parsed.get("validShots"));
        assertEquals(0, parsed.get("repeatedShots"));
        assertEquals(2, parsed.get("missedShots"));
        assertEquals(Game.NUMBER_SHOTS - 2, parsed.get("outsideShots"));
    }

    @Test
    @DisplayName("Deve acumular vários acertos no mesmo tipo de barco")
    void processEnemyFireWithMultipleHitsOnSameBoat() throws Exception {
        IShip frigate = fakeShip("Frigate");

        List<IGame.ShotResult> results = List.of(
                new IGame.ShotResult(true, false, frigate, false),
                new IGame.ShotResult(true, false, frigate, false)
        );

        Move move = new Move(10, new ArrayList<>(), results);

        String json = move.processEnemyFire(false);

        ObjectMapper mapper = new ObjectMapper();
        Map<?, ?> parsed = mapper.readValue(json, Map.class);

        assertEquals(2, parsed.get("validShots"));
        assertEquals(0, parsed.get("repeatedShots"));
        assertEquals(0, parsed.get("missedShots"));

        List<?> hitsOnBoats = (List<?>) parsed.get("hitsOnBoats");
        assertEquals(1, hitsOnBoats.size());

        Map<?, ?> hit = (Map<?, ?>) hitsOnBoats.get(0);
        assertEquals("Frigate", hit.get("type"));
        assertEquals(2, hit.get("hits"));
    }

    @Test
    @DisplayName("Deve acumular vários barcos afundados do mesmo tipo")
    void processEnemyFireWithMultipleSunkBoatsSameType() throws Exception {
        IShip frigate = fakeShip("Frigate");

        List<IGame.ShotResult> results = List.of(
                new IGame.ShotResult(true, false, frigate, true),
                new IGame.ShotResult(true, false, frigate, true)
        );

        Move move = new Move(11, new ArrayList<>(), results);

        String json = move.processEnemyFire(false);

        ObjectMapper mapper = new ObjectMapper();
        Map<?, ?> parsed = mapper.readValue(json, Map.class);

        assertEquals(2, parsed.get("validShots"));
        assertEquals(0, parsed.get("repeatedShots"));
        assertEquals(0, parsed.get("missedShots"));

        List<?> sunkBoats = (List<?>) parsed.get("sunkBoats");
        assertEquals(1, sunkBoats.size());

        Map<?, ?> sunk = (Map<?, ?>) sunkBoats.get(0);
        assertEquals("Frigate", sunk.get("type"));
        assertEquals(2, sunk.get("count"));
    }

    private IShip fakeShip(String category) {
        return (IShip) Proxy.newProxyInstance(
                IShip.class.getClassLoader(),
                new Class[]{IShip.class},
                (proxy, method, args) -> {
                    if ("getCategory".equals(method.getName())) {
                        return category;
                    }

                    Class<?> returnType = method.getReturnType();
                    if (returnType.equals(boolean.class)) return false;
                    if (returnType.equals(int.class)) return 0;
                    if (returnType.equals(char.class)) return '\0';
                    return null;
                }
        );
    }

    @Test
    @DisplayName("Deve processar corretamente quando não há tiros exteriores")
    void processEnemyFireWithoutOutsideShots() throws Exception {
        List<IGame.ShotResult> results = List.of(
                new IGame.ShotResult(true, false, null, false),
                new IGame.ShotResult(true, false, null, false),
                new IGame.ShotResult(true, false, null, false)
        );

        Move move = new Move(12, new ArrayList<>(), results);

        String json = move.processEnemyFire(false);

        ObjectMapper mapper = new ObjectMapper();
        Map<?, ?> parsed = mapper.readValue(json, Map.class);

        assertEquals(3, parsed.get("validShots"));
        assertEquals(0, parsed.get("repeatedShots"));
        assertEquals(3, parsed.get("missedShots"));
        assertEquals(0, parsed.get("outsideShots"));
    }


    @Test
    @DisplayName("Deve processar uma combinação de tiro na água, repetido e inválido")
    void processEnemyFireMixedMissRepeatedAndInvalid() throws Exception {
        List<IGame.ShotResult> results = List.of(
                new IGame.ShotResult(true, false, null, false),
                new IGame.ShotResult(true, true, null, false),
                new IGame.ShotResult(false, false, null, false)
        );

        Move move = new Move(13, new ArrayList<>(), results);

        String json = move.processEnemyFire(true);

        ObjectMapper mapper = new ObjectMapper();
        Map<?, ?> parsed = mapper.readValue(json, Map.class);

        assertEquals(1, parsed.get("validShots"));
        assertEquals(1, parsed.get("repeatedShots"));
        assertEquals(1, parsed.get("missedShots"));
        assertEquals(1, parsed.get("outsideShots"));
    }

    @Test
    @DisplayName("Deve processar mistura de barco afundado e barco apenas atingido")
    void processEnemyFireWithSunkAndHitBoats() throws Exception {
        IShip frigate = fakeShip("Frigate");
        IShip ship = fakeShip("Ship");

        List<IGame.ShotResult> results = List.of(
                new IGame.ShotResult(true, false, frigate, true),
                new IGame.ShotResult(true, false, ship, false)
        );

        Move move = new Move(14, new ArrayList<>(), results);

        String json = move.processEnemyFire(true);

        ObjectMapper mapper = new ObjectMapper();
        Map<?, ?> parsed = mapper.readValue(json, Map.class);

        assertEquals(2, parsed.get("validShots"));
        assertEquals(0, parsed.get("repeatedShots"));
        assertEquals(0, parsed.get("missedShots"));
        assertEquals(Game.NUMBER_SHOTS - 2, parsed.get("outsideShots"));

        List<?> sunkBoats = (List<?>) parsed.get("sunkBoats");
        List<?> hitsOnBoats = (List<?>) parsed.get("hitsOnBoats");

        assertEquals(1, sunkBoats.size());
        assertEquals(1, hitsOnBoats.size());

        Map<?, ?> sunk = (Map<?, ?>) sunkBoats.get(0);
        Map<?, ?> hit = (Map<?, ?>) hitsOnBoats.get(0);

        assertEquals("Frigate", sunk.get("type"));
        assertEquals(1, sunk.get("count"));

        assertEquals("Ship", hit.get("type"));
        assertEquals(1, hit.get("hits"));
    }


    @Test
    @DisplayName("Deve contar vários tipos de barcos afundados e acertos em paralelo")
    void processEnemyFireWithMultipleBoatTypes() throws Exception {
        IShip frigate = fakeShip("Frigate");
        IShip caravel = fakeShip("Caravel");

        List<IGame.ShotResult> results = List.of(
                new IGame.ShotResult(true, false, frigate, true),
                new IGame.ShotResult(true, false, caravel, false),
                new IGame.ShotResult(true, false, null, false)
        );

        Move move = new Move(15, new ArrayList<>(), results);

        String json = move.processEnemyFire(true);

        ObjectMapper mapper = new ObjectMapper();
        Map<?, ?> parsed = mapper.readValue(json, Map.class);

        assertEquals(3, parsed.get("validShots"));
        assertEquals(0, parsed.get("repeatedShots"));
        assertEquals(1, parsed.get("missedShots"));
        assertEquals(0, parsed.get("outsideShots"));

        List<?> sunkBoats = (List<?>) parsed.get("sunkBoats");
        List<?> hitsOnBoats = (List<?>) parsed.get("hitsOnBoats");

        assertEquals(1, sunkBoats.size());
        assertEquals(1, hitsOnBoats.size());
    }
}