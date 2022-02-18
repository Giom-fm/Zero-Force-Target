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
            this.setCell(pad, pad.getPosition());
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

                // REVIEW IO_RAT
                while (done == false) {
                    if (!validateCoord(targetPosition)) {
                        targetPosition = this.getNearByCoord(
                                targetPosition,
                                lockedCells,
                                true);
                        targetCell = this.getCell(targetPosition);
                    }

                    if (targetCell == null) {
                        // Target cell is empty -> set current block to it
                        this.moveCell(currentCell, targetPosition);
                        done = true;
                    } else if (subIteration <= 0
                            || targetCell.getBlockType() == BlockType.PAD
                            || lockedCells.contains(targetPosition)) {

                        targetPosition = this.getNearByCoord(
                                targetPosition,
                                lockedCells,
                                subIteration > 0);
                        targetCell = this.getCell(targetPosition);
                    } else if (targetCell.getBlockType() == BlockType.LOGIC_BLOCK) {
                        // Target cell is occupied by logic block -> replace it and search new position
                        // for next block
                        // if zft-position is locked search an new position

                        this.swapCells(currentCell, targetCell);
                        lockedCells.add(targetPosition);
                        currentCell = (LogicBlock) targetCell;
                        targetPosition = currentCell.getZFT();
                        if (!this.validateCoord(targetPosition)) {
                            targetPosition = this.getNearByCoord(
                                    targetPosition,
                                    lockedCells,
                                    true);
                        }
                        targetCell = this.getCell(targetPosition);

                    }
                    --subIteration;
                }
            }
        }
    }

    private boolean validateCoord(Coord coord) {
        return coord.getX() >= this.iorat / 2
                && coord.getX() <= this.ROWS
                && coord.getY() >= this.iorat / 2
                && coord.getY() <= this.COLS;
    }

    

    private Coord getNearByCoord(Coord coord, List<Coord> lockedCells, boolean getLogicBlock) {
        int i = coord.getX(), j = coord.getY();
        int depth = 0;
        // FIXME HOLY SHIT
        while (true) {
            ++depth;
            for (int x = Math.max(this.iorat - 1, i - depth); x <= Math.min(i + depth,
                    this.ROWS + this.iorat / 2 - 1); ++x) {
                for (int y = Math.max(this.iorat - 1, j - depth); y <= Math.min(j + depth,
                        this.COLS + this.iorat / 2 - 1); ++y) {
                    if (x != i || y != j) {
                        Block cell = this.getCell(x, y);
                        if ((cell == null)
                                || getLogicBlock
                                        && cell != null
                                        && cell.getBlockType() == BlockType.LOGIC_BLOCK
                                        && !lockedCells.contains(new Coord(x, y))) {
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
                    logicBlock.setPosition(new Coord(x, y));
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

    public void removeCell(Coord coord) {
        this.cells[coord.getX()][coord.getY()] = null;
    }

    public boolean isCellEmpty(int x, int y) {
        return this.cells[x][y] == null;
    }

    public void swapCells(Block a, Block b) {
        Coord posA = a.getPosition();
        Coord posB = b.getPosition();
        this.setCell(a, posB);
        this.setCell(b, posA);
        a.setPosition(posB);
        b.setPosition(posA);
    }

    public void moveCell(Block block, Coord position) {
        this.removeCell(block.getPosition());
        this.setCell(block, position);
        block.setPosition(position);
    }

}
