package main;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import main.net.Block;
import main.net.BlockType;
import main.net.Graph;
import main.net.LogicBlock;
import main.net.Pad;
import main.net.Pos2D;

public class FPGA {

    private final int iorat;
    private Graph graph;
    private final int ROWS;
    private final int COLS;
    private Block[][] cells;

    private Pos2D debug = new Pos2D(7, 41);

    // TODO IO_RAT not the size of Matrix
    public FPGA(Graph graph, int rows, int cols, int io_rat) {
        this.ROWS = rows;
        this.COLS = cols;
        this.iorat = io_rat;
        this.cells = new Block[this.ROWS + 2][this.COLS + 2];
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
        List<Pos2D> globalLockedCells = new LinkedList<>();

        while (maxIterations-- > 0) {
            // REVIEW Add pad positions to global locked cells
            globalLockedCells.clear();
            while (it.hasNext()) {
                LogicBlock currentCell = it.next();
                Pos2D targetPosition = currentCell.getZFT();
                int subIteration = maxSubIterations;
                boolean done = false;
                iterationLockedCells.clear();

                // REVIEW IO_RAT

                while (done == false) {

                    Block targetCell = this.getCell(targetPosition);

                    if (currentCell.getPosition().equals(targetPosition)) {
                        // Already best position -> lock cell
                        globalLockedCells.add(targetPosition);
                        done = true;
                    } else {
                        if (subIteration <= 0 || iterationLockedCells.contains(targetPosition)
                                || !validatePos(targetPosition)) {

                            List<Pos2D> lockedCells = new LinkedList<>(globalLockedCells);
                            lockedCells.addAll(iterationLockedCells);
                            targetPosition = this.getBestCellNearBy(currentCell, targetPosition, lockedCells,
                                    subIteration > 0);
                            targetCell = this.getCell(targetPosition);
                        }
                        if (targetCell == null) {
                            // Target cell is empty -> set current block to it
                            this.moveCell(currentCell.getPosition(), targetPosition);
                            done = true;
                        } else if (targetCell.getBlockType() == BlockType.LOGIC_BLOCK) {
                            // Target cell is occupied by logic block -> replace it and search new position
                            // for next block
                            // if zft-position is locked search an new position
                            this.swapCells(currentCell, targetCell);
                            iterationLockedCells.add(targetPosition);
                            currentCell = (LogicBlock) targetCell;
                            targetPosition = currentCell.getZFT();
                        }
                    }
                    --subIteration;
                }
            }
        }
    }

    public boolean validatePos(Pos2D coord) {
        return coord.getX() > 0
                && coord.getX() <= this.ROWS
                && coord.getY() > 0
                && coord.getY() <= this.COLS;
    }

    public Pos2D getBestCellNearBy(Block currentBlock, Pos2D targetPosition, List<Pos2D> lockedCells,
            boolean getLogicBlock) {

        List<Pos2D> nearByCoords = getNearByPositions(targetPosition, lockedCells, getLogicBlock);
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

    public List<Pos2D> getNearByPositions(Pos2D coord, List<Pos2D> lockedCells, boolean getLogicBlock) {
        int i = coord.getX(), j = coord.getY();
        int depth = 0;
        List<Pos2D> coords = new LinkedList<>();
        // REVIEW get only new Blocks
        while (coords.size() == 0) {
            ++depth;
            for (int x = Math.max(1, i - depth); x <= Math.min(i + depth, this.ROWS); ++x) {
                for (int y = Math.max(1, j - depth); y <= Math.min(j + depth, this.COLS); ++y) {
                    if (x != i || y != j) {
                        Pos2D pos = new Pos2D(x, y);
                        Block cell = this.getCell(pos);
                        if ((cell == null) || getLogicBlock && cell != null && !lockedCells.contains(pos)) {
                            coords.add(pos);
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
                Pos2D pos = new Pos2D(x, y);
                if (this.isCellEmpty(pos)) {
                    this.setCell(logicBlock, pos);
                    found = true;
                }
            }

        }
    }

    public Block getCell(Pos2D pos) {
        return this.cells[pos.getX()][pos.getY()];
    }

    public void setCell(Block block, Pos2D pos) {
        block.setPosition(pos);
        this.cells[pos.getX()][pos.getY()] = block;
    }

    public void removeCell(Pos2D pos) {
        // REVIEW Remove position?
        this.cells[pos.getX()][pos.getY()] = null;
    }

    public boolean isCellEmpty(Pos2D pos) {
        return this.cells[pos.getX()][pos.getY()] == null;
    }

    public void swapCells(Block blockA, Block blockB) {
        Pos2D posA = blockA.getPosition();
        Pos2D posB = blockB.getPosition();
        this.setCell(blockA, posB);
        this.setCell(blockB, posA);
    }

    public void moveCell(Pos2D posFrom, Pos2D posTo) {
        Block blockA = this.getCell(posFrom);
        this.removeCell(posFrom);
        this.setCell(blockA, posTo);
    }

}
