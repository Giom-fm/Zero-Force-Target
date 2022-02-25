package main.net;

import java.util.Iterator;
import java.util.List;

public class LogicBlock extends Block {

    public LogicBlock(String name) {
        super(name);
    }

    public LogicBlock(String name, Pos2D pos) {
        super(name, pos);
    }

    public Pos2D getZFT() {
        // REVIEW Block weight
        List<Block> blocks = this.getConnectedBlocks();
        Iterator<Block> it = blocks.iterator();
        double x0_numerator = 0, y0_numerator = 0;
        double weightSum = 0;

        while (it.hasNext()) {
            Block block = it.next();
            if (!block.getPosition().equals(Block.INIT_POSITION)) {
                int weight = block.getBlockType() == BlockType.PAD ? 5 : Block.K;
                //int weight = 1;
                Pos2D position = block.getPosition();
                x0_numerator += weight * position.getX();
                y0_numerator += weight * position.getY();
                weightSum += weight;
            }

        }
        if (x0_numerator > 0 || y0_numerator > 0) {
            int x0 = (int) Math.round(x0_numerator / weightSum);
            int y0 = (int) Math.round(y0_numerator / weightSum);
            return new Pos2D(x0, y0);
        } else {
            return Block.INIT_POSITION;
        }

    }

    @Override
    public BlockType getBlockType() {
        return BlockType.LOGIC_BLOCK;
    }

    @Override
    public boolean equals(Object obj) {
        Block block = (Block) obj;
        return obj != null
                && obj instanceof LogicBlock
                && this.getName().equals(block.getName())
                && this.getPosition().equals(block.getPosition());
    }
}
