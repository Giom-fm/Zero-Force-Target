package main.net;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Graph {

    private Map<String, Block> blocks;

    public Graph(List<ParsedBlock> parsedBlocks, Map<String, ParsedPlacement> parsedPads) {
        this.createBlocks(parsedBlocks, parsedPads);
        this.connectBlocks(parsedBlocks);
    }

    private void createBlocks(List<ParsedBlock> parsedBlocks, Map<String, ParsedPlacement> parsedPads) {

        this.blocks = new HashMap<>();

        Iterator<ParsedBlock> it = parsedBlocks.iterator();
        ParsedBlock parsedBlock;
        BlockType blockType;
        String blockName;
        Block newBlock = null;

        while (it.hasNext()) {
            parsedBlock = it.next();
            blockType = parsedBlock.getType();
            blockName = parsedBlock.getName();

            if (blockType == BlockType.PAD) {
                ParsedPlacement parsedPad = parsedPads.get(blockName);
                newBlock = new Pad(blockName, new Pos2D(parsedPad.getX(), parsedPad.getY()), parsedPad.getSubBlock());
            } else if (blockType == BlockType.LOGIC_BLOCK) {
                newBlock = new LogicBlock(blockName);
            } else {
                System.err.println("Wrong blocktype while creating blocks");
                System.exit(1);
            }

            this.blocks.put(blockName, newBlock);
        }
    }

    private void connectBlocks(List<ParsedBlock> parsedBlocks) {

        Iterator<ParsedBlock> it = parsedBlocks.iterator();
        ParsedBlock parsedBlock;
        String blockName;

        while (it.hasNext()) {
            parsedBlock = it.next();
            blockName = parsedBlock.getName();
            Block block = this.blocks.get(blockName);
            List<ParsedBlock> net = ParsedBlock.getBlocksThatAreConnectedTo(parsedBlocks, parsedBlock.getInputs(),
                    parsedBlock.getOutputs());
            Iterator<ParsedBlock> jt = net.iterator();

            while (jt.hasNext()) {
                ParsedBlock parsedConnectedBlock = jt.next();
                if (parsedConnectedBlock != null) {
                    Block connectedBlock = this.blocks.get(parsedConnectedBlock.getName());
                    block.addConnectedBlock(connectedBlock);
                }
            }
        }
    }

    public List<LogicBlock> getSortedLogicBlocks() {
        return this.blocks.values().stream().filter((block) -> {
            return block.getBlockType() == BlockType.LOGIC_BLOCK;
        }).sorted((a, b) -> {
            int sizeA = a.getConnectedBlocks().size();
            int sizeB = b.getConnectedBlocks().size();
            return sizeB - sizeA;
        }).map((block) -> {
            return (LogicBlock) block;
        }).collect(Collectors.toCollection(LinkedList::new));
    }

    public List<LogicBlock> getSortedPadsByLogicBlocks() {
        return this.blocks.values().stream().filter((block) -> {
            return block.getBlockType() == BlockType.LOGIC_BLOCK;
        }).filter((block) -> {
            Iterator<Block> it = block.getConnectedBlocks().iterator();
            int counter = 0;
            boolean filter = false;
            while (it.hasNext() && !filter) {
                if (it.next().getBlockType() == BlockType.PAD) {
                    counter++;
                }
                filter = counter < 2;
            }
            return filter;
        }).sorted((a, b) -> {
            int sizeA = a.getConnectedBlocks().size();
            int sizeB = b.getConnectedBlocks().size();
            return sizeB - sizeA;
        }).map((block) -> {
            return (LogicBlock) block;
        }).collect(Collectors.toCollection(LinkedList::new));
    }

    public List<Pad> getSortedPads() {
        return this.blocks.values().stream().filter((block) -> {
            return block.getBlockType() == BlockType.PAD;
        }).sorted((a, b) -> {
            int sizeA = a.getConnectedBlocks().size();
            int sizeB = b.getConnectedBlocks().size();
            return sizeB - sizeA;
        }).map((block) -> {
            return (Pad) block;
        }).collect(Collectors.toCollection(LinkedList::new));
    }

    public Block getBlock(String blockName) {
        return this.blocks.get(blockName);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        this.blocks.values().forEach((block) -> {
            builder.append(block.toString());
        });

        return builder.toString();
    }

}
