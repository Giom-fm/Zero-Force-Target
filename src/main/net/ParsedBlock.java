package main.net;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class ParsedBlock {

    static private final int SUBBLOCK_LUT_SIZE = 4;


    private final String name;
    private final List<String> in;
    private final List<String> out;
    private final BlockType type;

    private ParsedBlock(BlockType type, String name, List<String> in, List<String> out) {
        this.type = type;
        this.name = name;
        this.in = in;
        this.out = out;
    }

    public static List<ParsedBlock> getBlocksThatAreConnectedTo(List<ParsedBlock> allBlocks, List<String> inputs,
            List<String> outputs) {

        Iterator<ParsedBlock> it = allBlocks.iterator();
        List<ParsedBlock> results = new LinkedList<>();
        while (it.hasNext()) {
            ParsedBlock block = it.next();
            List<String> blockInputs = block.getInputs();
            List<String> blockOutputs = block.getOutputs();

            if (!Collections.disjoint(blockInputs, outputs) || !Collections.disjoint(blockOutputs, inputs)) {
                results.add(block);
            }

        }
        return results;
    }

    public static List<ParsedBlock> Parse(String file) {

        Path path = Path.of(file);
        LinkedList<ParsedBlock> blocks = new LinkedList<>();

        try (Stream<String> lines = Files.lines(path)) {
            Iterator<String> it = lines.iterator();

            String blockLine;
            BlockType blockType = null;
            String blockName;
            String[] pinList;
            while (it.hasNext()) {
                blockLine = it.next();
                List<String> in = new LinkedList<>();
                List<String> out = new LinkedList<>();
                if (!blockLine.isBlank()) {
                    blockLine = blockLine.trim().split("#")[0];
                    blockName = blockLine.split(" ")[1];
                    pinList = it.next().split(" ");
                    if (blockLine.startsWith(".global")) {
                        blockType = BlockType.PAD;
                    } else if (blockLine.startsWith(".input")) {
                        blockType = BlockType.PAD;
                        out.add(pinList[1]);
                    } else if (blockLine.startsWith(".output")) {
                        blockType = BlockType.PAD;
                        in.add(pinList[1]);
                    } else if (blockLine.startsWith(".clb")) {
                        blockType = BlockType.LOGIC_BLOCK;
                        // Add Inputs
                        for (int i = 1; i < SUBBLOCK_LUT_SIZE; ++i) {
                            in.add(pinList[i].equals("open") ? null : pinList[i]);
                        }
                        // Add Output
                        out.add(pinList[SUBBLOCK_LUT_SIZE + 1].equals("open") ? null : pinList[SUBBLOCK_LUT_SIZE + 1]);
                        // Add clock as input
                        in.add(pinList[SUBBLOCK_LUT_SIZE + 2].equals("open") ? null : pinList[SUBBLOCK_LUT_SIZE + 2]);
                        // REVIEW Some Logicblocks have more then one Subblock
                        it.next();
                    } else {
                        System.err.format("Error while Parsing \"%s\"\nCould not parse line: \"%s\"", file, blockLine);
                        System.exit(1);
                    }
                    blocks.add(new ParsedBlock(blockType, blockName, in, out));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return blocks;

    }

    public BlockType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public List<String> getInputs() {
        return in;
    }

    public List<String> getOutputs() {
        return out;
    }

}