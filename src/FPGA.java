import java.util.Iterator;
import java.util.Random;

import net.Block;
import net.Graph;
import net.LogicBlock;

public class FPGA {

    private final int iorat = 2;
    private Graph graph;
    private final int ROWS;
    private final int COLS;
    private Block[][] cells;

    public FPGA(Graph graph, int rows, int cols) {
        this.ROWS = rows;
        this.COLS = cols;
        this.cells = new Block[this.ROWS + this.iorat][this.COLS+ this.iorat];
        this.graph = graph;

        Iterator<Block> it = this.graph.getPads().iterator();
        Block pad;
        while (it.hasNext()) {
            pad = it.next();
            this.setCell(pad, pad.getX(), pad.getY());
        }
    }

    public void placeRandom() {
        Random rand = new Random();
        Iterator<LogicBlock> it = this.graph.getLogicBlocks().iterator();
        int x, y;
        boolean found;
        LogicBlock logicBlock;

        while (it.hasNext()) {
            logicBlock = it.next();
            found = false;

            while (found == false) {
                x = rand.nextInt(this.ROWS);
                y = rand.nextInt(this.COLS);
                if (this.isCellEmpty(x, y)) {
                    logicBlock.setX(x);
                    logicBlock.setY(y);
                    found = true;
                    this.setCell(logicBlock, x, y);
                }
            }

        }
    }


    public Block getCell(int x, int y) {
        return this.cells[x][y];
    }

    public void setCell(Block block, int x, int y) {
        this.cells[x][y] = block;
    }

    public void removeCell(int x, int y) {
        this.cells[x][y] = null;
    }

    public boolean isCellEmpty(int x, int y) {
        return this.cells[x][y] == null;
    }

}
