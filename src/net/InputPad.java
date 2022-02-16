package net;

public class InputPad extends Block {

    private Net input;

    public InputPad(String name, int x, int y) {
        super(name, x, y);
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

    @Override
    public String toString() {

        String.format("%s", super.toString());

        return super.toString();
    }
}
