import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import net.Block;
import net.BlockType;
import net.Graph;
import net.LogicBlock;
import net.Pad;
import net.Coord;

public class FPGA {

    private final int iorat = 2;
    private Graph graph;
    private final int ROWS;
    private final int COLS;
    private Block[][] cells;

    public FPGA(Graph graph, int rows, int cols) {
        this.ROWS = rows;
        this.COLS = cols;
        this.cells = new Block[this.ROWS + this.iorat][this.COLS + this.iorat];
        this.graph = graph;
        Iterator<Pad> it = this.graph.getPads().iterator();
        while (it.hasNext()) {
            Block pad = it.next();
            this.setCell(pad, pad.getX(), pad.getY());
        }
    }

    public void rippleMove(int maxIterations, int maxSubIterations) {
        List<LogicBlock> logicBlocks = this.graph.getLogicBlocks();
        Iterator<LogicBlock> it = logicBlocks.iterator();
        List<Coord> lockedCells = new LinkedList<>();

        while (maxIterations-- > 0) {
            while (it.hasNext()) {
                LogicBlock currentCell = it.next();
                Coord targetPosition = currentCell.getZFT();
                Block targetCell = this.getCell(targetPosition);
                int subIteration = maxSubIterations;
                boolean done = false;
                lockedCells.clear();

                // REVIEW Use Coords?
                while (done == false) {
                    if (targetCell == null) {
                        // Target cell is empty -> set current block to it
                        this.removeCell(currentCell.getX(), currentCell.getY());
                        currentCell.setPosition(targetPosition.getX(), targetPosition.getY());
                        this.setCell(currentCell, targetPosition.getX(), targetPosition.getY());
                        done = true;
                    } else if (subIteration <= 0 || targetCell.getBlockType() == BlockType.PAD) {
                        targetPosition = this.getSurroundingCell(targetCell.getX(), targetCell.getY(), true,
                                subIteration > 0);
                        targetCell = this.getCell(targetPosition);
                    } else if (targetCell.getBlockType() == BlockType.LOGIC_BLOCK) {
                        // Target cell is occupied by logic block -> replace it and search new position
                        // for next block
                        // if zft-position is locked search an new position
                        if (lockedCells.contains(targetPosition)) {
                            targetPosition = this.getSurroundingCell(targetCell.getX(), targetCell.getY(), true, true);
                            targetCell = this.getCell(targetPosition);
                        } else {
                            this.setCell(currentCell, targetPosition);
                            this.setCell(targetCell, currentCell.getPosition());
                            targetCell.setPosition(currentCell.getX(), currentCell.getY());
                            currentCell.setPosition(targetPosition.getX(), targetPosition.getY());

                            lockedCells.add(targetPosition);
                            currentCell = (LogicBlock) targetCell;
                            targetPosition = currentCell.getZFT();
                            targetCell = this.getCell(targetPosition);
                        }
                    }
                    --subIteration;
                }
            }
        }
    }

    private Coord getSurroundingCell(int i, int j, boolean getEmpty, boolean getLogicBlock) {

        int depth = 0;
        // FIXME HOLY SHIT
        // REVIEW depth to get only new Coords
        while (true) {
            ++depth;
            for (int x = Math.max(0, i - depth); x <= Math.min(i + depth, this.ROWS + this.iorat - 1); ++x) {
                for (int y = Math.max(0, j - depth); y <= Math.min(j + depth, this.COLS + this.iorat - 1); ++y) {
                    if (x != i || y != j) {
                        Block cell = this.getCell(x, y);
                        if ((getEmpty && cell == null)
                                || (getLogicBlock && cell != null && cell.getBlockType() == BlockType.LOGIC_BLOCK)) {
                            return new Coord(x, y);
                        }
                    }
                }
            }
        }
    }

    public void placeRandom() {
        Random rand = new Random();
        rand.setSeed(123456789);
        Iterator<LogicBlock> it = this.graph.getLogicBlocks().iterator();
        int x, y;
        boolean found;
        LogicBlock logicBlock;

        while (it.hasNext()) {
            logicBlock = it.next();
            found = false;

            while (found == false) {
                // REVIEW nextInt could return some Pad positions that are locked for logic
                // blocks
                x = rand.nextInt(this.ROWS);
                y = rand.nextInt(this.COLS);
                if (this.isCellEmpty(x, y)) {
                    logicBlock.setPosition(x, y);
                    this.setCell(logicBlock, x, y);
                    found = true;
                }
            }

        }
    }

    public Block getCell(int x, int y) {
        return this.cells[x][y];
    }

    public Block getCell(Coord coord) {
        return this.cells[coord.getX()][coord.getY()];
    }

    public void setCell(Block block, Coord coord) {
        this.setCell(block, coord.getX(), coord.getY());
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

    public void moveCell(Block block, Coord coord) {
        this.moveCell(block, coord.getX(), coord.getY());
    }

    public void moveCell(Block block, int x, int y) {
        this.removeCell(block.getX(), block.getY());
        block.setPosition(x, y);
        this.setCell(block, x, y);
    }
}
