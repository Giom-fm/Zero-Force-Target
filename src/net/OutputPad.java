package net;

public class OutputPad extends Block {

    public OutputPad(String name){
        super(name);
    }

    @Override
    public BlockType getBlockType() {
        return BlockType.OUTPUT;
    }    
}
