package net;

public class InputPad extends Block {

    public InputPad(String name){
        super(name);
    }

    @Override
    public BlockType getBlockType() {
        return BlockType.INPUT;
    }    
}
