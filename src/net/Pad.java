package net;

public class Pad extends Block {

    public Pad(String name, int x, int y) {
        super(name, x, y);
    }

    @Override
    public BlockType getBlockType() {
        return BlockType.PAD;
    }

}
