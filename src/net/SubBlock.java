package net;

public class SubBlock extends Block {

    public SubBlock(String name) {
        super(name);
    }

    @Override
    public BlockType getBlockType() {
        return BlockType.SUB_BLOCK;
    }

}
