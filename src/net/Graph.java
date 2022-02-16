package net;

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

    public void createBlocks(List<ParsedBlock> parsedBlocks, Map<String, ParsedPlacement> parsedPads) {

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
                newBlock = new Pad(blockName, parsedPad.getX(), parsedPad.getY());
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
            List<ParsedBlock> net = ParsedBlock.getBlocksThatAreConnectedTo(parsedBlocks, parsedBlock.getNet());
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

    public Map<String, Block> getBlocks() {
        return blocks;
    }

    public List<LogicBlock> getLogicBlocks() {
        return this.blocks.values().stream().filter((block) -> {
            return block.getBlockType() == BlockType.LOGIC_BLOCK;
        }).map((block) -> {
            return (LogicBlock) block;
        }).collect(Collectors.toCollection(LinkedList::new));
    }

    public List<Pad> getPads() {
        return this.blocks.values().stream().filter((block) -> {
            return block.getBlockType() == BlockType.PAD;
        }).map((block) -> {
            return (Pad) block;
        }).collect(Collectors.toCollection(LinkedList::new));
    }

    public Block getBlock(String blockName) {
        return this.blocks.get(blockName);
    }

}
