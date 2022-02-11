package net;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.stream.Stream;


public class ParsedBlock {

    static final private int SUBBLOCK_LUT_SIZE = 4;
    static final private int SUBBLOCKS_PER_CLB = 1;

    private String name;
    private String output;
    private String[] inputs;
    private BlockType type;
    private ParsedBlock subBlock;

    private ParsedBlock(BlockType type, String name, String[] input, String output, ParsedBlock subBlock) {
        this.type = type;
        this.name = name;
        this.inputs = input;
        this.output = output;
        this.subBlock = subBlock;
    }

    private ParsedBlock(BlockType type, String name, String[] input, String output) {
        this.type = type;
        this.name = name;
        this.inputs = input;
        this.output = output;
    }

    public static LinkedList<ParsedBlock> Parse(String file) {

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

            ParsedBlock subBlock = null;
            String subBlockLine;
            String[] subBlockElements;

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
                        subBlock = null;
                    } else if (blockLine.startsWith(".output")) {
                        blockType = BlockType.OUTPUT;
                        inputs = null;
                        output = pinList[1];
                        subBlock = null;
                    } else if (blockLine.startsWith(".clb")) {
                        blockType = BlockType.LOGIC_BLOCK;
                        inputs = new String[SUBBLOCK_LUT_SIZE];
                        inputs[0] = pinList[1].equals("open") ? null : pinList[1];
                        inputs[1] = pinList[2].equals("open") ? null : pinList[2];
                        inputs[2] = pinList[3].equals("open") ? null : pinList[3];
                        inputs[3] = pinList[4].equals("open") ? null : pinList[4];
                        output = pinList[SUBBLOCK_LUT_SIZE + 1].equals("open") ? null : pinList[SUBBLOCK_LUT_SIZE + 1];
                        // TODO Remove "open" in Subblock 
                        subBlockLine = it.next();
                        subBlockElements = subBlockLine.split(" ");
                        subBlock = new ParsedBlock(BlockType.SUB_BLOCK, subBlockElements[1],
                                Arrays.copyOfRange(subBlockElements, 2, SUBBLOCK_LUT_SIZE + 2),
                                subBlockElements[SUBBLOCK_LUT_SIZE + 2]);

                    } else {
                        System.err.format("Error while Parsing \"%s\"\nCould not parse line: \"%s\"", file, blockLine);
                        System.exit(1);
                    }
                    blocks.add(new ParsedBlock(blockType, blockName, inputs, output, subBlock));
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

    public ParsedBlock getSubBlock() {
        return subBlock;
    }

}