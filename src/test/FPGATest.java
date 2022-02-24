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

public class FPGATest {

    private FPGA fpga;
    private int IO_RAT = 1;

    @Before
    public void init() {
        String home = System.getProperty("user.home");
        String path = home + "/Dokumente/workspaces/Uni/EDA/src/test/data/";
        List<ParsedBlock> parsedBlocks = ParsedBlock.Parse(path + "0.net");
        Map<String, ParsedPlacement> parsedPads = ParsedPlacement.Parse(path + "0.pad");
        Graph graph = new Graph(parsedBlocks, parsedPads);
        fpga = new FPGA(graph, IO_RAT, 3);
    }

    @Test
    public void SetCell_Manipulate_GetCell() {
        Pos2D pos = new Pos2D(1, 1);
        Block block = new LogicBlock("test", pos);
        fpga.setCell(block, pos);
        block.setPosition(new Pos2D(2, 2));
        assertEquals(block, this.fpga.getCellByPos(pos));
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
        Block getBlockA = this.fpga.getCellByPos(posB);
        Block getBlockB = this.fpga.getCellByPos(posA);

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
        assertEquals(null, this.fpga.getCellByPos(posA));
        assertEquals(blockA, this.fpga.getCellByPos(posB));
    }

    @Test
    public void removeCell() {
        Pos2D posA = new Pos2D(1, 1);

        LogicBlock blockA = new LogicBlock("a", posA);
        this.fpga.setCell(blockA, posA);

        this.fpga.removeCell(posA);
        assertEquals(null, this.fpga.getCellByPos(posA));
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
        assertTrue(this.fpga.validatePos(new Pos2D(1, this.fpga.getSIZE())));
        assertTrue(this.fpga.validatePos(new Pos2D(this.fpga.getSIZE(), 1)));
        assertFalse(this.fpga.validatePos(new Pos2D(this.fpga.getSIZE() + 1, 1)));
        assertFalse(this.fpga.validatePos(new Pos2D(1, this.fpga.getSIZE() + 1)));
    }




}
