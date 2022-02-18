package net;

import java.util.LinkedList;
import java.util.List;

public abstract class Block {
    private final String name;
    private Coord position;
    private List<Block> connectedBlocks = new LinkedList<>();

    public Block(String name) {
        this.name = name;
    }

    public Block(String name, int x, int y) {
        this.name = name;
        this.position = new Coord(x, y);
    }

    public String getName() {
        return name;
    }

    public Coord getPosition() {
        return this.position;
    }

    public void setPosition(Coord coord) {
        this.position = coord;
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
        return obj != null && obj instanceof Block && this.name.equals(((Block) obj).getName());
    }

}
