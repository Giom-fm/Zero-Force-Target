package test;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import main.FPGA;
import main.net.Block;
import main.net.Graph;
import main.net.LogicBlock;
import main.net.ParsedBlock;
import main.net.ParsedPlacement;
import main.net.Pos2D;

public class GetBestCellTest {
    private FPGA fpga;

    private int IO_RAT = 1;

    @Before
    public void init() {
        String home = System.getProperty("user.home");
        String path = home + "/Dokumente/workspaces/Uni/EDA/src/test/data/";
        List<ParsedBlock> parsedBlocks = ParsedBlock.Parse(path + "0.net");
        Map<String, ParsedPlacement> parsedPads = ParsedPlacement.Parse(path + "0.pad");
        Graph graph = new Graph(parsedBlocks, parsedPads);
        fpga = new FPGA(graph, IO_RAT ,3);
    }

    @Test
    public void GetBestPosNearBy() {

        Block pad_01 = this.fpga.getCellByName("i_01");
        Block pad_10 = this.fpga.getCellByName("i_10");
        Block currentBlock = new LogicBlock("blockA");
        currentBlock.addConnectedBlock(pad_01);
        currentBlock.addConnectedBlock(pad_10);
        Pos2D targetPosition = new Pos2D(2, 2);
        Set<Pos2D> lockedCells = new HashSet<>();

        Pos2D bestPos = this.fpga.getBestPosNearBy(currentBlock, targetPosition, lockedCells, true);
        Pos2D expected = new Pos2D(1, 1);

        assertEquals(expected, bestPos);
    }
}
