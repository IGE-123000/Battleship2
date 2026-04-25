package battleship;

import org.junit.jupiter.api.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PositionTest {

	private Position position;

	@BeforeEach
	void setUp() {
		position = new Position(2, 3); // C4
	}

	@AfterEach
	void tearDown() {
		position = null;
	}

	@Test
	void constructorIntInt() {
		Position pos = new Position(1, 1);
		assertNotNull(pos);
		assertEquals(1, pos.getRow());
		assertEquals(1, pos.getColumn());
		assertFalse(pos.isOccupied());
		assertFalse(pos.isHit());
	}

	@Test
	void constructorCharInt() {
		Position pos = new Position('C', 4);
		assertNotNull(pos);
		assertEquals(2, pos.getRow());
		assertEquals(3, pos.getColumn());
		assertEquals('C', pos.getClassicRow());
		assertEquals(4, pos.getClassicColumn());
		assertEquals("C4", pos.toString());
	}

	@Test
	void getRow() {
		assertEquals(2, position.getRow());
	}

	@Test
	void getColumn() {
		assertEquals(3, position.getColumn());
	}

	@Test
	void getClassicRow() {
		assertEquals('C', position.getClassicRow());
	}

	@Test
	void getClassicColumn() {
		assertEquals(4, position.getClassicColumn());
	}

	@Test
	void isInsideValidPosition() {
		position = new Position(0, 0);
		assertTrue(position.isInside());
	}

	@Test
	void isInsideInvalidNegativeRow() {
		position = new Position(-1, 5);
		assertFalse(position.isInside());
	}

	@Test
	void isInsideInvalidNegativeColumn() {
		position = new Position(5, -1);
		assertFalse(position.isInside());
	}

	@Test
	void isInsideInvalidRowTooLarge() {
		position = new Position(Game.BOARD_SIZE, 5);
		assertFalse(position.isInside());
	}

	@Test
	void isInsideInvalidColumnTooLarge() {
		position = new Position(5, Game.BOARD_SIZE);
		assertFalse(position.isInside());
	}

	@Test
	void isAdjacentToHorizontal() {
		Position other = new Position(2, 4);
		assertTrue(position.isAdjacentTo(other));
	}

	@Test
	void isAdjacentToVertical() {
		Position other = new Position(3, 3);
		assertTrue(position.isAdjacentTo(other));
	}

	@Test
	void isAdjacentToDiagonal() {
		Position other = new Position(3, 4);
		assertTrue(position.isAdjacentTo(other));
	}

	@Test
	void isAdjacentToSamePosition() {
		Position other = new Position(2, 3);
		assertTrue(position.isAdjacentTo(other));
	}

	@Test
	void isAdjacentToNonAdjacent() {
		Position other = new Position(4, 5);
		assertFalse(position.isAdjacentTo(other));
	}

	@Test
	void isAdjacentToWithNull() {
		assertThrows(NullPointerException.class, () -> position.isAdjacentTo(null));
	}

	@Test
	void adjacentPositionsCenterHasEight() {
		Position center = new Position(5, 5);
		List<IPosition> adjacents = center.adjacentPositions();

		assertEquals(8, adjacents.size());
		assertTrue(adjacents.contains(new Position(4, 5))); // norte
		assertTrue(adjacents.contains(new Position(5, 6))); // este
		assertTrue(adjacents.contains(new Position(6, 5))); // sul
		assertTrue(adjacents.contains(new Position(5, 4))); // oeste
		assertTrue(adjacents.contains(new Position(6, 6))); // diagonal
	}

	@Test
	void adjacentPositionsCornerHasThree() {
		Position corner = new Position(0, 0);
		List<IPosition> adjacents = corner.adjacentPositions();

		assertEquals(3, adjacents.size());
		assertTrue(adjacents.contains(new Position(0, 1)));
		assertTrue(adjacents.contains(new Position(1, 0)));
		assertTrue(adjacents.contains(new Position(1, 1)));
	}

	@Test
	void isOccupied() {
		assertFalse(position.isOccupied());
		position.occupy();
		assertTrue(position.isOccupied());
	}

	@Test
	void isHit() {
		assertFalse(position.isHit());
		position.shoot();
		assertTrue(position.isHit());
	}

	@Test
	void equalsSameCoordinates() {
		Position same = new Position(2, 3);
		assertEquals(position, same);
	}

	@Test
	void equalsNull() {
		assertNotEquals(position, null);
	}

	@Test
	void equalsDifferentType() {
		Object other = new Object();
		assertNotEquals(position, other);
	}

	@Test
	void equalsDifferentCoordinates() {
		Position other = new Position(2, 4);
		assertNotEquals(position, other);
	}

	@Test
	void equalsItself() {
		assertEquals(position, position);
	}

	@Test
	void hashCodeConsistencyForEqualObjects() {
		Position same = new Position(2, 3);
		assertEquals(position.hashCode(), same.hashCode());
	}

	@Test
	void toStringFormat() {
		assertEquals("C4", position.toString());
	}

	@Test
	void randomPositionIsInsideBoard() {
		Position random = Position.randomPosition();
		assertNotNull(random);
		assertTrue(random.isInside());
	}
	@Test
	void isAdjacentToFalseWhenSameRowButColumnTooFar() {
		Position other = new Position(2, 5); // mesma linha, coluna demasiado longe
		assertFalse(position.isAdjacentTo(other));
	}
}