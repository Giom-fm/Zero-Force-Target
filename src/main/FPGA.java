package main;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

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

    // REVIEW IO_RAT
    public FPGA(Graph graph, int rows, int cols, int io_rat) {
        this.ROWS = rows;
        this.COLS = cols;
        this.iorat = io_rat;
        this.cells = new Block[this.COLS + 2][this.ROWS + 2];
        this.graph = graph;
        Iterator<Pad> it = this.graph.getSortedPads().iterator();
        while (it.hasNext()) {
            Block pad = it.next();
            this.setCell(pad, pad.getPosition());
        }
    }

    public void rippleMove(int maxIterations, int maxRippleIterations) {
        List<LogicBlock> logicBlocks = this.graph.getSortedLogicBlocks();
        Iterator<LogicBlock> it = logicBlocks.iterator();
        Set<Pos2D> lockedPositions = new HashSet<>();
        int rippleIterations = 0;

        // REVIEW Add pad positions to global locked cells
        it = logicBlocks.iterator();
        while (maxIterations > 0) {
            if (!it.hasNext()) {
                it = logicBlocks.iterator();
            }
            LogicBlock currentCell = it.next();

            Pos2D targetPosition = currentCell.getZFT();
            boolean rippleDone = false;
            while (rippleDone == false) {
                Block targetCell = this.getCellByPos(targetPosition);
                if (currentCell.getPosition().equals(targetPosition)) {
                    rippleIterations = 0;
                    rippleDone = true;
                } else if (lockedPositions.contains(targetPosition)) {
                    rippleIterations++;
                    if (rippleIterations >= maxRippleIterations) {
                        lockedPositions.clear();
                        maxIterations--;
                        rippleDone = true;
                    } else {
                        targetPosition = this.getBestPosNearBy(currentCell, targetPosition, lockedPositions, true);
                        lockedPositions.add(targetPosition);
                    }
                } else if (!validatePos(targetPosition)) {
                    targetPosition = this.getBestPosNearBy(currentCell, targetPosition, lockedPositions, true);
                } else if (targetCell == null) {
                    this.moveCell(currentCell.getPosition(), targetPosition);
                    lockedPositions.add(targetPosition);
                    rippleDone = true;
                    rippleIterations = 0;
                } else if (targetCell.getBlockType() == BlockType.LOGIC_BLOCK) {
                    this.swapCells(currentCell, targetCell);
                    lockedPositions.add(targetPosition);
                    currentCell = (LogicBlock) targetCell;
                    targetPosition = currentCell.getZFT();
                    rippleIterations = 0;
                }

            }
        }

    }

    public boolean validatePos(Pos2D pos) {
        return pos.getX() > 0
                && pos.getX() <= this.ROWS
                && pos.getY() > 0
                && pos.getY() <= this.COLS;
    }

    public Pos2D getBestPosNearBy(Block currentBlock, Pos2D targetPosition, Set<Pos2D> lockedCells,
            boolean getLogicBlock) {

        List<Pos2D> nearByCoords = getPosNearBy(targetPosition, lockedCells, getLogicBlock);
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

    public List<Pos2D> getPosNearBy(Pos2D coord, Set<Pos2D> lockedCells, boolean getLogicBlock) {
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
                        Block cell = this.getCellByPos(pos);
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
        Iterator<LogicBlock> it = this.graph.getSortedLogicBlocks().iterator();
        int x, y;
        boolean found;
        LogicBlock logicBlock;
        // int XBound = this.COLS - 1 + 1
        while (it.hasNext()) {
            logicBlock = it.next();
            found = false;
            while (found == false) {
                x = rand.nextInt(this.COLS) + 1;
                y = rand.nextInt(this.ROWS) + 1;

                Pos2D pos = new Pos2D(x, y);
                if (this.isCellEmpty(pos)) {
                    this.setCell(logicBlock, pos);
                    found = true;
                }
            }

        }
    }

    public void initPlace() {
        List<LogicBlock> blocks = this.graph.getSortedLogicBlocks();
        Iterator<LogicBlock> it = blocks.iterator();
        int unset_counter = blocks.size();
        Set<Pos2D> lockedCells = new HashSet<>();

        while (unset_counter > 0) {
            if (!it.hasNext()) {
                it = blocks.iterator();
            }
            LogicBlock currentCell = it.next();
            if (currentCell.getPosition().equals(Block.INIT_POSITION)) {
                Pos2D targetPosition = currentCell.getZFT();
                if (!targetPosition.equals(Block.INIT_POSITION)) {
                    Block targetCell = this.getCellByPos(targetPosition);
                    if (targetCell != null || !this.validatePos(targetPosition)) {
                        targetPosition = this.getBestPosNearBy(currentCell, targetPosition, lockedCells, false);
                    }
                    this.setCell(currentCell, targetPosition);
                    unset_counter--;
                }
            }
        }
    }

    public Block getCellByPos(Pos2D pos) {
        return this.cells[pos.getX()][pos.getY()];
    }

    public Block getCellByName(String name) {
        return this.graph.getBlock(name);
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
        Block blockA = this.getCellByPos(posFrom);
        this.removeCell(posFrom);
        this.setCell(blockA, posTo);
    }

}
