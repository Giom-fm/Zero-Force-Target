package net;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Graph {

    private Map<String, Block> blocks;
    private Map<String, Net> nets;

    public Graph(List<ParsedBlock> parsedBlocks, Map<String, ParsedPlacement> parsedPads) {
        this.createBlocksAndNets(parsedBlocks, parsedPads);
    }

    private void createBlocks(List<ParsedBlock> parsedBlocks, Map<String, ParsedPlacement> parsedPads) {
        this.blocks = new HashMap<>();

        Iterator<ParsedBlock> it = parsedBlocks.iterator();
        ParsedBlock parsedBlock;
        BlockType blockType;
        String blockName;

        while (it.hasNext()) {
            parsedBlock = it.next();
            blockType = parsedBlock.getType();
            blockName = parsedBlock.getName();

            if (blockType == BlockType.INPUT) {
                ParsedPlacement pad = parsedPads.get(blockName);
                InputPad ipad = new InputPad(blockName, pad.getX(), pad.getY());
                String parsedBlockInput = parsedBlock.getInputs()[0];
                List<ParsedBlock> inputs = ParsedBlock.getBlockThatIsConnectedTo(parsedBlocks, parsedBlockInput);

            } else if (blockType == BlockType.OUTPUT) {

            } else if (blockType == BlockType.LOGIC_BLOCK) {

            } else {

            }
        }
    }

    private void createBlocksAndNets(List<ParsedBlock> parsedBlocks, Map<String, ParsedPlacement> parsedPads) {
        this.blocks = new HashMap<>();
        this.nets = new HashMap<>();

        Iterator<ParsedBlock> it = parsedBlocks.iterator();
        while (it.hasNext()) {
            ParsedBlock parsedBlock = it.next();
            BlockType blockType = parsedBlock.getType();
            String blockName = parsedBlock.getName();

            if (blockType == BlockType.INPUT) {
                ParsedPlacement pad = parsedPads.get(blockName);
                InputPad ipad = new InputPad(blockName, pad.getX(), pad.getY());
                String parsedBlockInput = parsedBlock.getInputs()[0];
                Net net = this.nets.get(parsedBlockInput);
                if (net == null) {
                    net = new Net(parsedBlockInput);
                }
                net.addBlock(ipad);
                ipad.setInput(net);
                this.blocks.put(blockName, ipad);
                this.nets.put(parsedBlockInput, net);
            } else if (blockType == BlockType.OUTPUT) {
                ParsedPlacement pad = parsedPads.get(blockName);
                OutputPad opad = new OutputPad(blockName, pad.getX(), pad.getY());
                String parsedBlockOutput = parsedBlock.getOutput();
                Net net = this.nets.get(parsedBlockOutput);
                if (net == null) {
                    net = new Net(parsedBlockOutput);
                }
                net.addBlock(opad);
                opad.setOutput(net);
                this.blocks.put(blockName, opad);
                this.nets.put(parsedBlockOutput, net);
            } else if (blockType == BlockType.LOGIC_BLOCK) {
                LogicBlock logicBlock = new LogicBlock(blockName);
                String parsedBlockOutput = parsedBlock.getOutput();
                Net net = this.nets.get(parsedBlockOutput);
                if (net == null) {
                    net = new Net(parsedBlockOutput);
                }
                net.addBlock(logicBlock);
                logicBlock.setOutput(net);
                this.nets.put(parsedBlockOutput, net);

                String[] parsedBlockInputs = parsedBlock.getInputs();
                for (int i = 0; i < parsedBlockInputs.length; ++i) {
                    String parsedBlockInput = parsedBlockInputs[i];
                    if (parsedBlockInput != null) {
                        net = this.nets.get(parsedBlockInput);
                        if (net == null) {
                            net = new Net(parsedBlockInput);
                        }
                        net.addBlock(logicBlock);
                        logicBlock.addInput(net);
                        this.nets.put(parsedBlockInput, net);
                    }
                }
                this.blocks.put(blockName, logicBlock);
            }
        }
    }

    public Map<String, Block> getBlocks() {
        return blocks;
    }

    public Map<String, Net> getNets() {
        return nets;
    }

    public List<LogicBlock> getLogicBlocks() {
        return this.blocks.values().stream().filter((block) -> {
            return block.getBlockType() == BlockType.LOGIC_BLOCK;
        }).map((block) -> {
            return (LogicBlock) block;
        }).collect(Collectors.toList());
    }

    public List<Block> getPads() {
        return this.blocks.values().stream().filter((block) -> {
            return block.getBlockType() == BlockType.INPUT || block.getBlockType() == BlockType.OUTPUT;
        }).collect(Collectors.toList());
    }

    public Block getBlock(String blockName) {
        return this.blocks.get(blockName);
    }

    public Net getNet(String netName) {
        return this.nets.get(netName);
    }

}
