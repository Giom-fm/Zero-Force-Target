package net;

enum BlockType {
    INPUT,
    OUTPUT,
    LOGIC_BLOCK,
    SUB_BLOCK
};

public abstract class Block {
    private final String name;
    private int x, y;

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

    public void setY(int y) {
        this.y = y;
    }

    public abstract BlockType getBlockType();

    @Override
    public String toString() {
        return "[" + this.name + "@" + this.x + ":" + this.y + "]";
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof Block && this.name.equals(((Block) obj).getName());
    }

}
