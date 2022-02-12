package net;

public class OutputPad extends Block {

    private Net output;

    public OutputPad(String name) {
        super(name);
    }

    public Net getOutput() {
        return output;
    }

    public void setOutput(Net output) {
        this.output = output;
    }

    @Override
    public BlockType getBlockType() {
        return BlockType.OUTPUT;
    }
}
