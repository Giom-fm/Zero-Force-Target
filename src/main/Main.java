package main;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import main.net.Graph;
import main.net.ParsedBlock;
import main.net.ParsedPlacement;

public class Main {

    private static final int MAX_RIPPLE_ITERATIONS = 20;
    private static final int MAX_ITERATIONS = 1000;
    private static final int IO_RAT = 2;

    public static void main(String[] args) throws IOException {

        String[] benchmarks = new String[] { "alu4", "apex2", "apex4", "bigkey", "clma", "des", "diffeq", "dsip",
                "elliptic", "ex5p", "ex1010", "frisc", "misex3", "pdc", "s298", "s38417", "s38584.1", "seq", "spla",
                "tseng" };
        int[] size = new int[] { 40, 44, 36, 54, 92, 63, 39, 54, 61, 33, 68, 60, 38, 68, 44, 81, 81, 42, 61, 33 };

        // some time passes

        for (int idx = 0; idx < benchmarks.length; ++idx) {
            String benchMarkName = benchmarks[idx];
            System.out.println("Starting Benchmark: " + benchMarkName);
            System.out.println("Parsing...");
            long start = System.currentTimeMillis();
            List<ParsedBlock> blocks = ParsedBlock.Parse("Benchmarks/net/" + benchMarkName + ".net");
            Map<String, ParsedPlacement> fixedPads = ParsedPlacement
                    .Parse("Benchmarks/pads/" + benchMarkName + ".pad");

            System.out.println("Create Graph...");
            Graph graph = new Graph(blocks, fixedPads);

            FPGA fpga = new FPGA(graph, IO_RAT, size[idx]);
            System.out.println("Init placing...");
            fpga.initPlace();
            // fpga.placeRandom();
            System.out.println("Ripple move...");
            fpga.rippleMove(MAX_ITERATIONS, MAX_RIPPLE_ITERATIONS);
            long end = System.currentTimeMillis();
            FPGA.WriteToFile("./out/place/" + benchMarkName + ".place", fpga);
            long elapsedTime = end - start;
            System.out.println("Done! Elapsed time: " + elapsedTime + " ms\n");
        }
    }

}
// 7491.522