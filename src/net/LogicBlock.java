package net;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class LogicBlock extends Block {

    private List<Net> inputs;
    private Net output;
    private static final int K = 1;
    private String zft;

    public LogicBlock(String name) {
        super(name);
        this.inputs = new LinkedList<>();
    }

    public void addInput(Net net) {
        this.inputs.add(net);
    }

    public void setOutput(Net output) {
        this.output = output;
    }

    public List<Net> getInputs() {
        return inputs;
    }

    public Net getOutput() {
        return output;
    }

    public List<Block> getConnectedBlocks() {
        List<Block> connectedBlocks = this.inputs.stream().flatMap((net) -> {
            return net.getBlocks().stream();
        }).filter((block) -> {
            return !this.equals(block);
        }).collect(Collectors.toCollection(LinkedList::new));
        connectedBlocks.add(this.output.getBlocks().get(0));
        return connectedBlocks;
    }

   
    public void getZFT() {
        List<Block> blocks = this.getConnectedBlocks();
        Iterator<Block> it = blocks.iterator();
        double x0, y0, x0_numerator = 0, y0_numerator = 0;
        double weightSum = blocks.size() * K;

        while (it.hasNext()) {
            Block block = it.next();
            x0_numerator += K * block.getX();
            y0_numerator += K * block.getY();
        }
        x0 = x0_numerator / weightSum;
        y0 = y0_numerator / weightSum;
        this.zft = x0 + ":" + y0;
    }
    
    @Override
    public BlockType getBlockType() {
        return BlockType.LOGIC_BLOCK;
    }
}
