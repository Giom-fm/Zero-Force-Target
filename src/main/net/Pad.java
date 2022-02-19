package main.net;

public class Pad extends Block {

    public Pad(String name, Pos2D pos) {
        super(name, pos);
    }

    @Override
    public BlockType getBlockType() {
        return BlockType.PAD;
    }

    @Override
    public boolean equals(Object obj) {
        Block block = (Block) obj;
        return obj != null
                && obj instanceof Pad
                && this.getName().equals(block.getName())
                && this.getPosition().equals(block.getPosition());
    }
}
