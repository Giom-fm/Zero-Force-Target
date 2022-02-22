package main.net;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public abstract class Block {
    protected final String name;
    protected static final int K = 1;
    public static final Pos2D INIT_POSITION = new Pos2D(-1, -1);
    protected Pos2D position = new Pos2D(Block.INIT_POSITION);
    private List<Block> connectedBlocks = new LinkedList<>();

    public Block(String name) {
        this.name = name;
    }

    public Block(String name, Pos2D pos) {
        this.name = name;
        this.position = pos;
    }

    public double calcForceToBlocksFrom(Pos2D currentPos) {
        double force = 0;
        Iterator<Block> it = this.getConnectedBlocks().iterator();
        while (it.hasNext()) {
            Pos2D nextPos = it.next().getPosition();
            force += Block.calculateForce(currentPos, nextPos);
        }

        return force;
    }

    // REVIEW K?
    private static double calculateForce(Pos2D posA, Pos2D posB) {
        return K * (Math.sqrt(Math.pow(posA.getX() - posB.getX(), 2) + Math.pow(posA.getY() - posB.getY(), 2)));
    }

    public String getName() {
        return name;
    }

    public Pos2D getPosition() {
        return this.position;
    }

    public void setPosition(Pos2D pos) {
        this.position = pos;
    }

    public List<Block> getConnectedBlocks() {
        return connectedBlocks;
    }

    public void setConnectedBlocks(List<Block> connectedBlocks) {
        this.connectedBlocks = connectedBlocks;
    }

    public void addConnectedBlock(Block block) {
        this.connectedBlocks.add(block);
    }

    public abstract BlockType getBlockType();

    @Override
    public String toString() {
        return this.name + "\t\t\t" + this.position.getX() + "\t" + this.position.getY() + "\t0" + "\n";
    }

    @Override
    public boolean equals(Object obj) {
        Block block = (Block) obj;
        return obj != null
                && obj instanceof Block
                && this.name.equals(block.getName())
                && this.position.equals(block.getPosition());
    }

}
