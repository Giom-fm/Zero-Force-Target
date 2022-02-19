package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import main.net.Pos2D;

public class Pos2DTest {

    @Test
    public void Creation() {
        int x = 0, y = 1;
        Pos2D pos = new Pos2D(x, y);
        assertEquals(x, pos.getX());
        assertEquals(y, pos.getY());
    }

    @Test
    public void Equality() {
        Pos2D posA = new Pos2D(10, 10);
        Pos2D posB = new Pos2D(10, 10);
        assertTrue(posA.equals(posB));
    }
}