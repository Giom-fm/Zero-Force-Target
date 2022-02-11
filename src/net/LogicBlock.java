package net;

public class LogicBlock extends Block {

    public LogicBlock(String name) {
        super(name);
    }

    @Override
    public BlockType getBlockType() {
        return BlockType.LOGIC_BLOCK;
    }
}
