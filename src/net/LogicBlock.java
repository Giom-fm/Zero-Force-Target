package net;

import java.util.Iterator;
import java.util.List;

public class LogicBlock extends Block {

    private static final int K = 1;
    private String zft;

    public LogicBlock(String name) {
        super(name);
    }

    public void getZFT() {
        List<Block> blocks = this.getConnectedBlocks();
        Iterator<Block> it = blocks.iterator();
        double x0, y0, x0_numerator = 0, y0_numerator = 0;
        double weightSum = blocks.size() * K;

        while (it.hasNext()) {
            Block block = it.next();
            x0_numerator += K * block.getX();
            y0_numerator += K * block.getY();
        }
        x0 = x0_numerator / weightSum;
        y0 = y0_numerator / weightSum;
        this.zft = x0 + ":" + y0;
    }
    
    @Override
    public BlockType getBlockType() {
        return BlockType.LOGIC_BLOCK;
    }
}
