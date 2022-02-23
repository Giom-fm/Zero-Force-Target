package main;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import main.net.Graph;
import main.net.ParsedBlock;
import main.net.ParsedPlacement;

public class Main {

    private static final int MAX_RIPPLE_ITERATIONS = 20;
    private static final int MAX_ITERATIONS = 500;
    private static final int IO_RAT = 2;

    public static void main(String[] args) throws IOException {

        String[] benchmarks = new String[] { "alu4", "apex2", "apex4", "bigkey", "clma", "des", "diffeq", "dsip",
                "elliptic", "ex5p", "ex1010", "frisc", "misex3", "pdc", "s298", "s38417", "s38584.1", "seq", "spla",
                "tseng" };

        for (int idx = 0; idx < benchmarks.length; ++idx) {
            String benchMarkName = benchmarks[idx];
            System.out.println("Starting Benchmark: " + benchMarkName);
            System.out.println("Parsing...");
            List<ParsedBlock> blocks = ParsedBlock.Parse("Benchmarks/net/" + benchMarkName + ".net");
            Map<String, ParsedPlacement> fixedPads = ParsedPlacement
                    .Parse("Benchmarks/pads/" + benchMarkName + ".pad");

            System.out.println("Create Graph...");
            Graph graph = new Graph(blocks, fixedPads);

            FPGA fpga = new FPGA(graph, IO_RAT);
            System.out.println("Init placing...");
            fpga.initPlace();
            // fpga.placeRandom();
            System.out.println("Ripple move...");
            fpga.rippleMove(MAX_ITERATIONS, MAX_RIPPLE_ITERATIONS);
            FPGA.WriteToFile("./out/" + benchMarkName + ".place", fpga);
            System.out.println("Done!\n");
        }
    }

}
// 7491.522