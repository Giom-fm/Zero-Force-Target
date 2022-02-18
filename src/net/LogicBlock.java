package net;

import java.util.Iterator;
import java.util.List;

public class LogicBlock extends Block {

    private static final int K = 1;

    public LogicBlock(String name) {
        super(name);
    }

    public Coord getZFT() {
        List<Block> blocks = this.getConnectedBlocks();
        Iterator<Block> it = blocks.iterator();
        double x0_numerator = 0, y0_numerator = 0;
        int x0, y0;
        double weightSum = blocks.size() * K;
        Block block;

        while (it.hasNext()) {
            block = it.next();
            Coord position = block.getPosition();
            x0_numerator += K * position.getX();
            y0_numerator += K * position.getY();
        }
        x0 = (int) Math.round(x0_numerator / weightSum);
        y0 = (int) Math.round(y0_numerator / weightSum);
        return new Coord(x0, y0);
    }

    @Override
    public BlockType getBlockType() {
        return BlockType.LOGIC_BLOCK;
    }
}
