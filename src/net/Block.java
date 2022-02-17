package net;

import java.util.LinkedList;
import java.util.List;

public abstract class Block {
    private final String name;
    private int x = 0, y = 0;
    private List<Block> connectedBlocks = new LinkedList<>();

    public Block(String name) {
        this.name = name;
    }

    public Block(String name, int x, int y) {
        this.name = name;
        this.x = x;
        this.y = y;
    }

    public String getName() {
        return name;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public Coord getPosition() {
        return new Coord(this.x, this.y);
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setY(int y) {
        this.y = y;
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
        return this.name + "\t\t\t" + this.x + "\t" + this.y + "\n";
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof Block && this.name.equals(((Block) obj).getName());
    }

}
