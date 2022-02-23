package test;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import main.FPGA;
import main.net.Graph;
import main.net.ParsedBlock;
import main.net.ParsedPlacement;
import main.net.Pos2D;

public class GetNearByPositionsTest {

    private FPGA fpga;
    private int IO_RAT = 1;

    @Before
    public void init() {
        String home = System.getProperty("user.home");
        String path = home + "/Dokumente/workspaces/Uni/EDA/src/test/data/";
        List<ParsedBlock> parsedBlocks = ParsedBlock.Parse(path + "0.net");
        Map<String, ParsedPlacement> parsedPads = ParsedPlacement.Parse(path + "0.pad");
        Graph graph = new Graph(parsedBlocks, parsedPads);
        fpga = new FPGA(graph, IO_RAT);
    }

    @Test
    public void GetNearByCoords_Middle() {

        Pos2D pos = new Pos2D(2, 2);
        List<Pos2D> nearByPos = this.fpga.getPosNearBy(pos, new HashSet<>(), true);
        List<Pos2D> expected = new LinkedList<>();

        expected.add(new Pos2D(1, 1));
        expected.add(new Pos2D(1, 2));
        expected.add(new Pos2D(1, 3));

        expected.add(new Pos2D(2, 1));
        expected.add(new Pos2D(2, 3));

        expected.add(new Pos2D(3, 1));
        expected.add(new Pos2D(3, 2));
        expected.add(new Pos2D(3, 3));

        assertEquals(expected, nearByPos);
    }

    @Test
    public void GetNearByCoords_CornerUpLeft() {

        Pos2D pos = new Pos2D(1, 1);
        List<Pos2D> nearByPos = this.fpga.getPosNearBy(pos, new HashSet<>(), true);
        List<Pos2D> expected = new LinkedList<>();

        expected.add(new Pos2D(1, 2));
        expected.add(new Pos2D(2, 1));
        expected.add(new Pos2D(2, 2));

        assertEquals(expected, nearByPos);

    }

    @Test
    public void GetNearByCoords_CornerUpRight() {

        Pos2D pos = new Pos2D(3, 1);
        List<Pos2D> nearByPos = this.fpga.getPosNearBy(pos, new HashSet<>(), true);
        List<Pos2D> expected = new LinkedList<>();

        expected.add(new Pos2D(2, 1));
        expected.add(new Pos2D(2, 2));
        expected.add(new Pos2D(3, 2));

        assertEquals(expected, nearByPos);

    }

    @Test
    public void GetNearByCoords_CornerDownLeft() {

        Pos2D pos = new Pos2D(1, 3);
        List<Pos2D> nearByPos = this.fpga.getPosNearBy(pos, new HashSet<>(), true);
        List<Pos2D> expected = new LinkedList<>();

        expected.add(new Pos2D(1, 2));
        expected.add(new Pos2D(2, 2));
        expected.add(new Pos2D(2, 3));

        assertEquals(expected, nearByPos);
    }

    @Test
    public void GetNearByCoords_CornerDownRight() {

        Pos2D pos = new Pos2D(3, 3);
        List<Pos2D> nearByPos = this.fpga.getPosNearBy(pos, new HashSet<>(), true);
        List<Pos2D> expected = new LinkedList<>();

        expected.add(new Pos2D(2, 2));
        expected.add(new Pos2D(2, 3));
        expected.add(new Pos2D(3, 2));

        assertEquals(expected, nearByPos);
    }

    @Test
    public void GetNearByCoords_MiddleUp() {

        Pos2D pos = new Pos2D(2, 1);
        List<Pos2D> nearByPos = this.fpga.getPosNearBy(pos, new HashSet<>(), true);
        List<Pos2D> expected = new LinkedList<>();

        expected.add(new Pos2D(1, 1));
        expected.add(new Pos2D(1, 2));
        expected.add(new Pos2D(2, 2));
        expected.add(new Pos2D(3, 1));
        expected.add(new Pos2D(3, 2));

        assertEquals(expected, nearByPos);
    }

    @Test
    public void GetNearByCoords_MiddleDown() {

        Pos2D pos = new Pos2D(2, 3);
        List<Pos2D> nearByPos = this.fpga.getPosNearBy(pos, new HashSet<>(), true);
        List<Pos2D> expected = new LinkedList<>();

        expected.add(new Pos2D(1, 2));
        expected.add(new Pos2D(1, 3));
        expected.add(new Pos2D(2, 2));
        expected.add(new Pos2D(3, 2));
        expected.add(new Pos2D(3, 3));

        assertEquals(expected, nearByPos);
    }

    @Test
    public void GetNearByCoords_MiddleLeft() {

        Pos2D pos = new Pos2D(1, 2);
        List<Pos2D> nearByPos = this.fpga.getPosNearBy(pos, new HashSet<>(), true);
        List<Pos2D> expected = new LinkedList<>();

        expected.add(new Pos2D(1, 1));
        expected.add(new Pos2D(1, 3));
        expected.add(new Pos2D(2, 1));
        expected.add(new Pos2D(2, 2));
        expected.add(new Pos2D(2, 3));

        assertEquals(expected, nearByPos);
    }

    @Test
    public void GetNearByCoords_MiddleRight() {

        Pos2D pos = new Pos2D(3, 2);
        List<Pos2D> nearByPos = this.fpga.getPosNearBy(pos, new HashSet<>(), true);
        List<Pos2D> expected = new LinkedList<>();

        expected.add(new Pos2D(2, 1));
        expected.add(new Pos2D(2, 2));
        expected.add(new Pos2D(2, 3));
        expected.add(new Pos2D(3, 1));
        expected.add(new Pos2D(3, 3));

        assertEquals(expected, nearByPos);
    }

    @Test
    public void GetNearByCoords_Forbidden_Corners() {

        Pos2D pos = new Pos2D(0, 0);
        List<Pos2D> nearByPos = this.fpga.getPosNearBy(pos, new HashSet<>(), true);
        List<Pos2D> expected = new LinkedList<>();
        expected.add(new Pos2D(1, 1));
        assertEquals(expected, nearByPos);

        pos = new Pos2D(0, 4);
        nearByPos = this.fpga.getPosNearBy(pos, new HashSet<>(), true);
        expected = new LinkedList<>();
        expected.add(new Pos2D(1, 3));
        assertEquals(expected, nearByPos);

        pos = new Pos2D(4, 4);
        nearByPos = this.fpga.getPosNearBy(pos, new HashSet<>(), true);
        expected = new LinkedList<>();
        expected.add(new Pos2D(3, 3));
        assertEquals(expected, nearByPos);

        pos = new Pos2D(4, 0);
        nearByPos = this.fpga.getPosNearBy(pos, new HashSet<>(), true);
        expected = new LinkedList<>();
        expected.add(new Pos2D(3, 1));
        assertEquals(expected, nearByPos);
    }

    @Test
    public void GetNearByCoords_Forbidden_MiddleLeft() {

        Pos2D pos = new Pos2D(0, 2);
        List<Pos2D> nearByPos = this.fpga.getPosNearBy(pos, new HashSet<>(), true);
        List<Pos2D> expected = new LinkedList<>();

        expected.add(new Pos2D(1, 1));
        expected.add(new Pos2D(1, 2));
        expected.add(new Pos2D(1, 3));

        assertEquals(expected, nearByPos);
    }

    @Test
    public void GetNearByCoords_Forbidden_MiddleRight() {

        Pos2D pos = new Pos2D(4, 2);
        List<Pos2D> nearByPos = this.fpga.getPosNearBy(pos, new HashSet<>(), true);
        List<Pos2D> expected = new LinkedList<>();

        expected.add(new Pos2D(3, 1));
        expected.add(new Pos2D(3, 2));
        expected.add(new Pos2D(3, 3));

        assertEquals(expected, nearByPos);
    }

    @Test
    public void GetNearByCoords_Forbidden_MiddleUp() {

        Pos2D pos = new Pos2D(2, 0);
        List<Pos2D> nearByPos = this.fpga.getPosNearBy(pos, new HashSet<>(), true);
        List<Pos2D> expected = new LinkedList<>();

        expected.add(new Pos2D(1, 1));
        expected.add(new Pos2D(2, 1));
        expected.add(new Pos2D(3, 1));

        assertEquals(expected, nearByPos);
    }

    @Test
    public void GetNearByCoords_Forbidden_MiddleDown() {

        Pos2D pos = new Pos2D(2, 4);
        List<Pos2D> nearByPos = this.fpga.getPosNearBy(pos, new HashSet<>(), true);
        List<Pos2D> expected = new LinkedList<>();

        expected.add(new Pos2D(1, 3));
        expected.add(new Pos2D(2, 3));
        expected.add(new Pos2D(3, 3));

        assertEquals(expected, nearByPos);
    }

}
