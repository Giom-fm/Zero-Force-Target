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
    
    

    public static Block Builder(BlockType blockType, String blockName) {
        Block block = null;
        if (blockType == BlockType.INPUT) {
            block = new InputPad(blockName);
        } else if (blockType == BlockType.OUTPUT) {
            block = new OutputPad(blockName);
        } else if (blockType == BlockType.LOGIC_BLOCK) {
            block = new LogicBlock(blockName);
        } else if (blockType == BlockType.SUB_BLOCK) {
            block = new SubBlock(blockName);
        }
        return block;
    }

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

}
