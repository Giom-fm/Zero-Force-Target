package main;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import main.net.Graph;
import main.net.ParsedBlock;
import main.net.ParsedPlacement;

public class Main {

    private static final int ROWS = 44;
    private static final int COLS = 44;
    private static final int MAX_RIPPLE_ITERATIONS = 20;
    private static final int MAX_ITERATIONS = 1000;
    private static final int IO_RAT = 2;

    public static void main(String[] args) throws IOException {

        String basePath = "Benchmarks/";
        String netPath = "net/";
        String padPath = "pads/";
        String[] benchmarks = new String[] { "alu4", "apex2", "apex4", "bigkey", "clma", "des", "diffeq", "dsip",
                "elliptic", "ex5p", "ex1010", "frisc", "misex3", "pdc", "s298", "s38417", "s38584.1", "seq", "spla",
                "tseng" };

        List<ParsedBlock> blocks = ParsedBlock.Parse("Benchmarks/net/apex2.net");
        Map<String, ParsedPlacement> fixedPads = ParsedPlacement.Parse("Benchmarks/pads/apex2.pad");
        Graph graph = new Graph(blocks, fixedPads);
        FPGA fpga = new FPGA(graph, ROWS, COLS, IO_RAT);
        fpga.initPlace();
        // fpga.placeRandom();
        fpga.rippleMove(MAX_ITERATIONS, MAX_RIPPLE_ITERATIONS);
        FPGA.WriteToFile("./out/output0.place", fpga);
        System.out.println("end.");
    }

}
// 7491.522