package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import main.net.Block;
import main.net.Graph;
import main.net.LogicBlock;
import main.net.Pad;
import main.net.ParsedBlock;
import main.net.ParsedPlacement;
import main.net.Pos2D;

public class GraphTest {

    private Graph graph;

    @Before
    public void init() {
        String home = System.getProperty("user.home");
        String path = home + "/Dokumente/workspaces/Uni/EDA/src/test/data/";
        List<ParsedBlock> parsedBlocks = ParsedBlock.Parse(path + "0.net");
        Map<String, ParsedPlacement>  parsedPads = ParsedPlacement.Parse(path + "0.pad");
        this.graph = new Graph(parsedBlocks, parsedPads);
    }

    @Test
    public void Creation() {
        Map<String, Block> pads = new HashMap<>();
        pads.put("i_0", new Pad("i_0", new Pos2D(0, 1)));
        pads.put("i_1", new Pad("i_1", new Pos2D(0, 2)));
        pads.put("i_2", new Pad("i_2", new Pos2D(0, 3)));
        pads.put("o_0", new Pad("o_0", new Pos2D(4, 1)));
        pads.put("o_1", new Pad("o_1", new Pos2D(4, 2)));
        pads.put("o_2", new Pad("o_2", new Pos2D(4, 3)));

        Map<String, LogicBlock> blocks = new HashMap<>();
        blocks.put("clb0", new LogicBlock("clb0"));
        blocks.put("clb1", new LogicBlock("clb1"));
        blocks.put("clb2", new LogicBlock("clb2"));

        assertEquals(pads.values().stream().collect(Collectors.toCollection(LinkedList::new)), this.graph.getPads());
        assertEquals(blocks.values().stream().collect(Collectors.toCollection(LinkedList::new)),
                this.graph.getLogicBlocks());
    }
}
