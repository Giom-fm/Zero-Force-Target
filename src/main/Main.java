package main;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import main.net.Graph;
import main.net.ParsedBlock;
import main.net.ParsedPlacement;

public class Main {

    private static final int ROWS = 40;
    private static final int COLS = 40;
    private static final int MAX_SUB_ITERATIONS = 10;
    private static final int MAX_ITERATIONS = 1000000;
    private static final int IO_RAT = 2;

    public static void main(String[] args) throws IOException {
        List<ParsedBlock> blocks = ParsedBlock.Parse("Benchmarks/net/alu4.net");
        Map<String, ParsedPlacement> fixedPads = ParsedPlacement.Parse("Benchmarks/pads/alu4.pad");
        Graph graph = new Graph(blocks, fixedPads);
        FPGA fpga = new FPGA(graph, ROWS, COLS, IO_RAT);
        fpga.placeRandom();
        fpga.rippleMove(MAX_ITERATIONS, MAX_SUB_ITERATIONS);
        Graph.WriteToFile("./out/output0.place", graph);
        System.out.println("end.");
    }

}
