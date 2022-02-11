package net;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Net {

    private Map<String, Block> blocks;

    public Net(List<ParsedBlock> parsedBlocks) {

        this.blocks = Net.createBlocks(parsedBlocks);
        this.blocks = Net.createGraphFromBlocks(blocks, parsedBlocks);

    }

    private static Map<String, Block> createBlocks(List<ParsedBlock> parsedBlocks) {
        Map<String, Block> blocks = new HashMap<>();
        Iterator<ParsedBlock> it = parsedBlocks.iterator();
        while (it.hasNext()) {
            ParsedBlock parsedBlock = it.next();
            BlockType blockType = parsedBlock.getType();
            String blockName = parsedBlock.getName();
            Block block = Block.Builder(blockType, blockName);
            blocks.put(blockName, block);
        }
        return blocks;
    }

    private static Map<String, Block> createGraphFromBlocks(Map<String, Block> blocks, List<ParsedBlock> parsedBlocks) {

        Iterator<ParsedBlock> it = parsedBlocks.iterator();
        ParsedBlock parsedBlock;
        String blockName, parsedBlockOutput;
        BlockType blockType;
        String[] parsedBlockInputs;
        Block block, blockOutput;
        List<Block> blockInputs;
        while (it.hasNext()) {
            parsedBlock = it.next();
            blockName = parsedBlock.getName();
            blockType = parsedBlock.getType();
            parsedBlockInputs = parsedBlock.getInputs();
            parsedBlockOutput = parsedBlock.getOutput();
            blockInputs = new LinkedList<>();

            blockOutput = parsedBlockOutput == null ? null : blocks.get(parsedBlockOutput);
            for (int i = 0; i < parsedBlockInputs.length; ++i) {
                blockInputs.add(parsedBlockInputs[i] == null ? null : blocks.get(parsedBlockInputs[i]));
            }

            block = blocks.get(blockName);
            if (blockType == BlockType.INPUT || blockType == BlockType.OUTPUT) {
                block.setInputs(blockInputs);
                block.setOutput(blockOutput);
            } else if (blockType == BlockType.LOGIC_BLOCK) {

            }

            block.setInputs(blockInputs);
            block.setOutput(blockOutput);

        }

        return blocks;
    }

}
