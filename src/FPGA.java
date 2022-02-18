import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import net.Block;
import net.BlockType;
import net.Graph;
import net.LogicBlock;
import net.Pad;
import net.Pos2D;

public class FPGA {

    private final int iorat = 2;
    private Graph graph;
    private final int ROWS;
    private final int COLS;
    private Block[][] cells;

    private Pos2D debug = new Pos2D(7, 41);

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
        List<Pos2D> iterationLockedCells = new LinkedList<>();
        List<Pos2D> gloablLockedCells = new LinkedList<>();

        while (maxIterations-- > 0) {
            gloablLockedCells.clear();
            while (it.hasNext()) {
                LogicBlock currentCell = it.next();
                int subIteration = maxSubIterations;
                boolean done = false;
                iterationLockedCells.clear();

                // REVIEW IO_RAT

                while (done == false) {
                    Pos2D targetPosition = currentCell.getZFT();
                    Block targetCell = this.getCell(targetPosition);
               

                    if (currentCell.getPosition().equals(targetPosition)) {
                        // Already best position -> lock cell
                        gloablLockedCells.add(targetPosition);
                    } else {
                        if (!validateCoord(targetPosition)) {
                            List<Pos2D> allLockedCells = new LinkedList<>();
                            allLockedCells.addAll(iterationLockedCells);
                            allLockedCells.addAll(gloablLockedCells);
                            targetPosition = this.getBestCellNearBy(currentCell, targetPosition, allLockedCells,
                                    true);
                            targetCell = this.getCell(targetPosition);
                        }
                        if (targetCell == null) {
                            // Target cell is empty -> set current block to it
                            if (subIteration == maxSubIterations) {
                                gloablLockedCells.add(targetPosition);
                            }
                            this.moveCell(currentCell, targetPosition);
                            done = true;
                        } else if (subIteration <= 0
                                || targetCell.getBlockType() == BlockType.PAD
                                || iterationLockedCells.contains(targetPosition)) {
                            List<Pos2D> allLockedCells = new LinkedList<>();
                            allLockedCells.addAll(iterationLockedCells);
                            allLockedCells.addAll(gloablLockedCells);
                            targetPosition = this.getBestCellNearBy(currentCell, targetPosition, allLockedCells,
                                    subIteration > 0);
                            targetCell = this.getCell(targetPosition);
                        } else if (targetCell.getBlockType() == BlockType.LOGIC_BLOCK) {
                            // Target cell is occupied by logic block -> replace it and search new position
                            // for next block
                            // if zft-position is locked search an new position

                            if (subIteration == maxSubIterations) {
                                gloablLockedCells.add(targetPosition);
                            }
                            this.swapCells(currentCell, targetCell);
                            iterationLockedCells.add(targetPosition);
                            currentCell = (LogicBlock) targetCell;
                            targetPosition = currentCell.getZFT();
                            if (!this.validateCoord(targetPosition)) {
                                List<Pos2D> allLockedCells = new LinkedList<>();
                                allLockedCells.addAll(iterationLockedCells);
                                allLockedCells.addAll(gloablLockedCells);
                                targetPosition = this.getBestCellNearBy(currentCell, targetPosition, allLockedCells,
                                        true);
                            }
                            targetCell = this.getCell(targetPosition);
                        }
                    }
                    --subIteration;
                }
            }
        }
    }

    private boolean validateCoord(Pos2D coord) {
        return coord.getX() >= this.iorat / 2
                && coord.getX() <= this.ROWS
                && coord.getY() >= this.iorat / 2
                && coord.getY() <= this.COLS;
    }

    private Pos2D getBestCellNearBy(Block currentBlock, Pos2D targetPosition, List<Pos2D> lockedCells,
            boolean getLogicBlock) {

        if (targetPosition.equals(debug) && currentBlock.getName().equals("[377]")) {
            System.out.println("");
        }

        List<Pos2D> nearByCoords = getNearByCoords(targetPosition, lockedCells, getLogicBlock);
        double bestForce = Double.MAX_VALUE;
        Pos2D bestPos = targetPosition;
        Iterator<Pos2D> it = nearByCoords.iterator();
        while (it.hasNext()) {
            Pos2D nearByPos = it.next();
            double currentForce = currentBlock.calcForceToBlocksFrom(nearByPos);
            if (currentForce < bestForce) {
                bestPos = nearByPos;
                bestForce = currentForce;
            }
        }

        return bestPos;
    }

    private List<Pos2D> getNearByCoords(Pos2D coord, List<Pos2D> lockedCells, boolean getLogicBlock) {
        int i = coord.getX(), j = coord.getY();
        int depth = 0;
        List<Pos2D> coords = new LinkedList<>();
        // REVIEW get only new Blocks
        while (coords.size() == 0) {
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
                                        && lockedCells.contains(new Pos2D(x, y))) {
                            coords.add(new Pos2D(x, y));
                        }
                    }
                }
            }
        }

        return coords;
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
                    Pos2D position = new Pos2D(x, y);
                    logicBlock.setPosition(position);
                    this.setCell(logicBlock, position);
                    found = true;
                }
            }

        }
    }

    public Block getCell(int x, int y) {
        return this.cells[x][y];
    }

    public Block getCell(Pos2D coord) {
        return this.cells[coord.getX()][coord.getY()];
    }

    public void setCell(Block block, Pos2D coord) {
        this.cells[coord.getX()][coord.getY()] = block;
    }

    public void removeCell(Pos2D coord) {
        this.cells[coord.getX()][coord.getY()] = null;
    }

    public boolean isCellEmpty(int x, int y) {
        return this.cells[x][y] == null;
    }

    public void swapCells(Block a, Block b) {
        Pos2D posA = a.getPosition();
        Pos2D posB = b.getPosition();
        this.setCell(a, posB);
        this.setCell(b, posA);
        a.setPosition(posB);
        b.setPosition(posA);
    }

    public void moveCell(Block block, Pos2D position) {
        this.removeCell(block.getPosition());
        this.setCell(block, position);
        block.setPosition(position);
    }

}
