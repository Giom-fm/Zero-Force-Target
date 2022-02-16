package net;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Net {
    private Map<String, Block> blocks = new HashMap<>();
    private final String name;

    public Net(String name) {
        this.name = name;
    }

    public void addBlock(Block block){
        this.blocks.put(block.getName(), block);
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
       Iterator<Block> blocks =  this.blocks.values().iterator();
       while(blocks.hasNext()){
           Block block = blocks.next();
       }


        return null;
    }
}
