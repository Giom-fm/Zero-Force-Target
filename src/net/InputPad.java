package net;


public class InputPad extends Block {

    private Net input;

    public InputPad(String name) {
        super(name);
    }

    public Net getInputs() {
        return input;
    }

    public void setInput(Net input) {
        this.input = input;
    }

    @Override
    public BlockType getBlockType() {
        return BlockType.INPUT;
    }
}
