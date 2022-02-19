package main.net;

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
    private final String[] connectedBlocks;
    private final BlockType type;

    private ParsedBlock(BlockType type, String name, String[] outIns) {
        this.type = type;
        this.name = name;
        this.connectedBlocks = outIns;
    }

    public static List<ParsedBlock> getBlocksThatAreConnectedTo(List<ParsedBlock> blocks, String[] nets) {
        Iterator<ParsedBlock> it = blocks.iterator();
        List<ParsedBlock> results = new LinkedList<>();
        ParsedBlock block;
        while (it.hasNext()) {
            block = it.next();
            if (ParsedBlock.contains(nets, block.getName())) {
                results.add(block);
            }
        }
        return results;
    }

    private static boolean contains(String[] nets, String name) {
        for (int i = 0; i < nets.length; ++i) {
            if (nets[i] != null && nets[i].equals(name)) {
                return true;
            }
        }
        return false;
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
            String[] outIns = new String[SUBBLOCK_LUT_SIZE + 1];

            while (it.hasNext()) {
                blockLine = it.next();
                if (!blockLine.isBlank()) {
                    blockLine = blockLine.trim().split("#")[0];
                    blockName = blockLine.split(" ")[1];
                    pinList = it.next().split(" ");
                    if (blockLine.startsWith(".input") || blockLine.startsWith(".output")) {
                        blockType = BlockType.PAD;
                        // REVIEW Why?
                        outIns = Arrays.copyOfRange(pinList, 1, 2);
                    } else if (blockLine.startsWith(".clb")) {
                        blockType = BlockType.LOGIC_BLOCK;
                        outIns = new String[SUBBLOCK_LUT_SIZE + 1];
                        outIns[0] = pinList[1].equals("open") ? null : pinList[1];
                        outIns[1] = pinList[2].equals("open") ? null : pinList[2];
                        outIns[2] = pinList[3].equals("open") ? null : pinList[3];
                        outIns[3] = pinList[4].equals("open") ? null : pinList[4];
                        outIns[4] = pinList[SUBBLOCK_LUT_SIZE + 1].equals("open") ? null
                                : pinList[SUBBLOCK_LUT_SIZE + 1];
                        // REVIEW Some Logicblocks have more then one Subblock
                        it.next();
                    } else {
                        System.err.format("Error while Parsing \"%s\"\nCould not parse line: \"%s\"", file, blockLine);
                        System.exit(1);
                    }
                    blocks.add(new ParsedBlock(blockType, blockName, outIns));
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

    public String[] getNet() {
        return connectedBlocks;
    }

}