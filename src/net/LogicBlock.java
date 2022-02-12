package net;

import java.util.LinkedList;
import java.util.List;

public class LogicBlock extends Block {

    private List<Net> inputs;
    private Net output;

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

    @Override
    public BlockType getBlockType() {
        return BlockType.LOGIC_BLOCK;
    }
}
