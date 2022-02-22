package main.net;

public class Pad extends Block {

    private final int subblock;

    public Pad(String name, Pos2D pos, int subblock) {
        super(name, pos);
        this.subblock = subblock;
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

    @Override
    public String toString() {
        return this.name + "\t\t\t" + this.position.getX() + "\t" + this.position.getY() + "\t" + this.subblock + "\n";
    }
}
