package net;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class ParsedBlock {

    static private final int SUBBLOCK_LUT_SIZE = 4;
    static private final int SUBBLOCKS_PER_CLB = 1;

    private final String name;
    private final String output;
    private final String[] inputs;
    private final BlockType type;

    private ParsedBlock(BlockType type, String name, String[] input, String output) {
        this.type = type;
        this.name = name;
        this.inputs = input;
        this.output = output;
    }

    public static List<ParsedBlock> getBlockThatIsConnectedTo(List<ParsedBlock> blocks, String net) {
        Iterator<ParsedBlock> it = blocks.iterator();
        List<ParsedBlock> results = new LinkedList<>();
        ParsedBlock block;
        while (it.hasNext()) {
            block = it.next();
            if (block.getName().equals(net)) {
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
            String output = null;
            String[] inputs = null;

            while (it.hasNext()) {
                blockLine = it.next();
                if (!blockLine.isBlank()) {
                    blockLine = blockLine.trim().split("#")[0];
                    blockName = blockLine.split(" ")[1];
                    pinList = it.next().split(" ");
                    if (blockLine.startsWith(".input")) {
                        blockType = BlockType.INPUT;
                        inputs = Arrays.copyOfRange(pinList, 1, 2);
                        output = null;

                    } else if (blockLine.startsWith(".output")) {
                        blockType = BlockType.OUTPUT;
                        inputs = null;
                        output = pinList[1];

                    } else if (blockLine.startsWith(".clb")) {
                        blockType = BlockType.LOGIC_BLOCK;
                        inputs = new String[SUBBLOCK_LUT_SIZE];
                        inputs[0] = pinList[1].equals("open") ? null : pinList[1];
                        inputs[1] = pinList[2].equals("open") ? null : pinList[2];
                        inputs[2] = pinList[3].equals("open") ? null : pinList[3];
                        inputs[3] = pinList[4].equals("open") ? null : pinList[4];
                        output = pinList[SUBBLOCK_LUT_SIZE + 1].equals("open") ? null : pinList[SUBBLOCK_LUT_SIZE + 1];
                        it.next();
                    } else {
                        System.err.format("Error while Parsing \"%s\"\nCould not parse line: \"%s\"", file, blockLine);
                        System.exit(1);
                    }
                    blocks.add(new ParsedBlock(blockType, blockName, inputs, output));
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

    public String[] getInputs() {
        return inputs;
    }

    public String getOutput() {
        return output;
    }

}