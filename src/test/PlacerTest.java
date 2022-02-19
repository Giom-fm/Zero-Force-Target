package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import main.FPGA;
import main.net.Block;
import main.net.Graph;
import main.net.LogicBlock;
import main.net.ParsedBlock;
import main.net.ParsedPlacement;
import main.net.Pos2D;

public class PlacerTest {

    private FPGA fpga;
    private int ROWS = 3;
    private int COLS = 3;
    private int IO_RAT = 1;

    @Before
    public void init() {
        String home = System.getProperty("user.home");
        String path = home + "/Dokumente/workspaces/Uni/EDA/src/test/data/";
        List<ParsedBlock> parsedBlocks = ParsedBlock.Parse(path + "0.net");
        Map<String, ParsedPlacement> parsedPads = ParsedPlacement.Parse(path + "0.pad");
        Graph graph = new Graph(parsedBlocks, parsedPads);
        fpga = new FPGA(graph, ROWS, COLS, IO_RAT);
    }

    @Test
    public void SetCell_Manipulate_GetCell() {
        Pos2D pos = new Pos2D(1, 1);
        Block block = new LogicBlock("test", pos);
        fpga.setCell(block, pos);
        block.setPosition(new Pos2D(2, 2));
        assertEquals(block, this.fpga.getCell(pos));
    }

    @Test
    public void SwapCells() {
        Pos2D posA = new Pos2D(1, 1);
        Pos2D posB = new Pos2D(2, 2);

        LogicBlock blockA = new LogicBlock("a", posA);
        LogicBlock blockB = new LogicBlock("a", posB);

        this.fpga.setCell(blockA, posA);
        this.fpga.setCell(blockB, posB);

        this.fpga.swapCells(blockA, blockB);
        Block getBlockA = this.fpga.getCell(posB);
        Block getBlockB = this.fpga.getCell(posA);

        assertEquals(blockA, getBlockA);
        assertEquals(blockB, getBlockB);
    }

    @Test
    public void MoveCell() {
        Pos2D posA = new Pos2D(1, 1);
        Pos2D posB = new Pos2D(2, 2);

        LogicBlock blockA = new LogicBlock("a", posA);
        this.fpga.setCell(blockA, posA);

        this.fpga.moveCell(posA, posB);
        assertEquals(null, this.fpga.getCell(posA));
        assertEquals(blockA, this.fpga.getCell(posB));
    }

    @Test
    public void removeCell() {
        Pos2D posA = new Pos2D(1, 1);

        LogicBlock blockA = new LogicBlock("a", posA);
        this.fpga.setCell(blockA, posA);

        this.fpga.removeCell(posA);
        assertEquals(null, this.fpga.getCell(posA));
    }

    @Test
    public void IsCellEmpty() {
        Pos2D posA = new Pos2D(1, 1);

        LogicBlock blockA = new LogicBlock("a", posA);
        this.fpga.setCell(blockA, posA);

        this.fpga.removeCell(posA);
        assertTrue(this.fpga.isCellEmpty(posA));
    }

    @Test
    public void ValidatePos() {
        assertTrue(this.fpga.validatePos(new Pos2D(1, 1)));
        assertFalse(this.fpga.validatePos(new Pos2D(0, 1)));
        assertFalse(this.fpga.validatePos(new Pos2D(0, 1)));
        assertTrue(this.fpga.validatePos(new Pos2D(1, this.COLS)));
        assertTrue(this.fpga.validatePos(new Pos2D(this.ROWS, 1)));
        assertFalse(this.fpga.validatePos(new Pos2D(this.ROWS + 1, 1)));
        assertFalse(this.fpga.validatePos(new Pos2D(1, this.COLS + 1)));
    }

    @Test
    public void GetNearByCoords_Middle() {

        Pos2D pos = new Pos2D(2, 2);
        List<Pos2D> nearByPos = this.fpga.getNearByPositions(pos, new LinkedList<>(), true);
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
        List<Pos2D> nearByPos = this.fpga.getNearByPositions(pos, new LinkedList<>(), true);
        List<Pos2D> expected = new LinkedList<>();

        expected.add(new Pos2D(1, 2));
        expected.add(new Pos2D(2, 1));
        expected.add(new Pos2D(2, 2));

        assertEquals(expected, nearByPos);

    }

    @Test
    public void GetNearByCoords_CornerUpRight() {

        Pos2D pos = new Pos2D(3, 1);
        List<Pos2D> nearByPos = this.fpga.getNearByPositions(pos, new LinkedList<>(), true);
        List<Pos2D> expected = new LinkedList<>();

        expected.add(new Pos2D(2, 1));
        expected.add(new Pos2D(2, 2));
        expected.add(new Pos2D(3, 2));

        assertEquals(expected, nearByPos);

    }

    @Test
    public void GetNearByCoords_CornerDownLeft() {

        Pos2D pos = new Pos2D(1, 3);
        List<Pos2D> nearByPos = this.fpga.getNearByPositions(pos, new LinkedList<>(), true);
        List<Pos2D> expected = new LinkedList<>();

        expected.add(new Pos2D(1, 2));
        expected.add(new Pos2D(2, 2));
        expected.add(new Pos2D(2, 3));

        assertEquals(expected, nearByPos);
    }

    @Test
    public void GetNearByCoords_CornerDownRight() {

        Pos2D pos = new Pos2D(3, 3);
        List<Pos2D> nearByPos = this.fpga.getNearByPositions(pos, new LinkedList<>(), true);
        List<Pos2D> expected = new LinkedList<>();

        expected.add(new Pos2D(2, 2));
        expected.add(new Pos2D(2, 3));
        expected.add(new Pos2D(3, 2));

        assertEquals(expected, nearByPos);
    }

    @Test
    public void GetNearByCoords_MiddleUp() {

        Pos2D pos = new Pos2D(2, 1);
        List<Pos2D> nearByPos = this.fpga.getNearByPositions(pos, new LinkedList<>(), true);
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
        List<Pos2D> nearByPos = this.fpga.getNearByPositions(pos, new LinkedList<>(), true);
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
        List<Pos2D> nearByPos = this.fpga.getNearByPositions(pos, new LinkedList<>(), true);
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
        List<Pos2D> nearByPos = this.fpga.getNearByPositions(pos, new LinkedList<>(), true);
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
        List<Pos2D> nearByPos = this.fpga.getNearByPositions(pos, new LinkedList<>(), true);
        List<Pos2D> expected = new LinkedList<>();
        expected.add(new Pos2D(1, 1));
        assertEquals(expected, nearByPos);

        pos = new Pos2D(0, 4);
        nearByPos = this.fpga.getNearByPositions(pos, new LinkedList<>(), true);
        expected = new LinkedList<>();
        expected.add(new Pos2D(1, 3));
        assertEquals(expected, nearByPos);

        pos = new Pos2D(4, 4);
        nearByPos = this.fpga.getNearByPositions(pos, new LinkedList<>(), true);
        expected = new LinkedList<>();
        expected.add(new Pos2D(3, 3));
        assertEquals(expected, nearByPos);

        pos = new Pos2D(4, 0);
        nearByPos = this.fpga.getNearByPositions(pos, new LinkedList<>(), true);
        expected = new LinkedList<>();
        expected.add(new Pos2D(3, 1));
        assertEquals(expected, nearByPos);
    }

    @Test
    public void GetNearByCoords_Forbidden_MiddleLeft() {

        Pos2D pos = new Pos2D(0, 2);
        List<Pos2D> nearByPos = this.fpga.getNearByPositions(pos, new LinkedList<>(), true);
        List<Pos2D> expected = new LinkedList<>();

        expected.add(new Pos2D(1, 1));
        expected.add(new Pos2D(1, 2));
        expected.add(new Pos2D(1, 3));

        assertEquals(expected, nearByPos);
    }


    @Test
    public void GetNearByCoords_Forbidden_MiddleRight() {

        Pos2D pos = new Pos2D(4, 2);
        List<Pos2D> nearByPos = this.fpga.getNearByPositions(pos, new LinkedList<>(), true);
        List<Pos2D> expected = new LinkedList<>();

        expected.add(new Pos2D(3, 1));
        expected.add(new Pos2D(3, 2));
        expected.add(new Pos2D(3, 3));
        
        assertEquals(expected, nearByPos);
    }

    @Test
    public void GetNearByCoords_Forbidden_MiddleUp() {

        Pos2D pos = new Pos2D(2, 0);
        List<Pos2D> nearByPos = this.fpga.getNearByPositions(pos, new LinkedList<>(), true);
        List<Pos2D> expected = new LinkedList<>();

        expected.add(new Pos2D(1, 1));
        expected.add(new Pos2D(2, 1));
        expected.add(new Pos2D(3, 1));
        
        assertEquals(expected, nearByPos);
    }


    @Test
    public void GetNearByCoords_Forbidden_MiddleDown() {

        Pos2D pos = new Pos2D(2, 4);
        List<Pos2D> nearByPos = this.fpga.getNearByPositions(pos, new LinkedList<>(), true);
        List<Pos2D> expected = new LinkedList<>();

        expected.add(new Pos2D(1, 3));
        expected.add(new Pos2D(2, 3));
        expected.add(new Pos2D(3, 3));
        
        assertEquals(expected, nearByPos);
    }

}
