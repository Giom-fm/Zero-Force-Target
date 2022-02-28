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


    @Before
    public void init() {
        String home = System.getProperty("user.home");
        String path = home + "/Dokumente/workspaces/Uni/EDA/src/test/data/";
        List<ParsedBlock> parsedBlocks = ParsedBlock.Parse(path + "0.net");
        Map<String, ParsedPlacement> parsedPads = ParsedPlacement.Parse(path + "0.pad");
        Graph graph = new Graph(parsedBlocks, parsedPads);
        fpga = new FPGA(graph ,3);
    }

}
