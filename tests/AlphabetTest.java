import org.junit.Test;
import processing.core.PApplet;

import static org.junit.Assert.assertEquals;

public class AlphabetTest extends PApplet {

    @Test
    public void testIsHere() throws Exception {

        //Board sb = new Board(this, new String[] {"098/101","MAKING THINGS", "MUCH BETTER", " "});

        assertEquals(true, Board.Alphabet.isHere(' '));
        assertEquals(true, Board.Alphabet.isHere('A'));
        assertEquals(true, Board.Alphabet.isHere('X'));
        assertEquals(false, Board.Alphabet.isHere(')'));
    }

    @Test
    public void testRank() throws Exception {
    }

    @Test
    public void testGetDistance() throws Exception {
        assertEquals(26, Board.Alphabet.getDistance('A', ' '));
        assertEquals(25, Board.Alphabet.getDistance('A', 'Z'));
        assertEquals(17, Board.Alphabet.getDistance(' ', 'A'));

        assertEquals(1, Board.Alphabet.getDistance(' ', '!'));
        assertEquals(1, Board.Alphabet.getDistance('!', '+'));

    }

    @Test
    public void testFindSymbol() throws Exception {
        assertEquals(0, Board.Alphabet.findSymbol(' '));
        assertEquals(2, Board.Alphabet.findSymbol('+'));
        assertEquals(17, Board.Alphabet.findSymbol('A'));
        assertEquals(42, Board.Alphabet.findSymbol('Z'));
        assertEquals(-1, Board.Alphabet.findSymbol('Ф'));

    }

    @Test
    public void testNextSymbol() throws Exception {
        assertEquals('B', Board.Alphabet.nextSymbol('A'));
        assertEquals('!', Board.Alphabet.nextSymbol(' '));
        assertEquals('+', Board.Alphabet.nextSymbol('!'));

        char symbol = ' ';
        for (int i = 0; i < 42; i++ ) {
            symbol = Board.Alphabet.nextSymbol(symbol);
        }
        assertEquals('Z', symbol);
    }
}