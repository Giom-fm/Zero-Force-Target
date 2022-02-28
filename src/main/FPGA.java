package main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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

    private Graph graph;
    private final int SIZE;
    private Block[][] cells;

    public FPGA(Graph graph, int size) {
        this.SIZE = size;
        this.cells = new Block[this.SIZE + 2][this.SIZE + 2];
        this.graph = graph;
        Iterator<Pad> it = this.graph.getSortedPads().iterator();
        while (it.hasNext()) {
            Block pad = it.next();
            this.setCell(pad, pad.getPosition());
        }
    }

    public static void WriteToFile(String path, FPGA fpga) {
        try {
            Files.writeString(Paths.get(path), fpga.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void rippleMove(int maxIterations, int maxRippleIterations) {
        List<LogicBlock> logicBlocks = this.graph.getSortedLogicBlocks();
        Iterator<LogicBlock> it = logicBlocks.iterator();
        Set<Pos2D> lockedPositions = new HashSet<>();
        int rippleIterations = 0;

        // REVIEW Parsing from block is wrong. Not all inputs are shown
        it = logicBlocks.iterator();
        while (maxIterations > 0) {
            if (!it.hasNext()) {
                it = logicBlocks.iterator();
            }
            boolean rippleDone = false;
            LogicBlock currentCell = it.next();
            Pos2D targetPosition = currentCell.getZFT();
            this.removeCell(currentCell.getPosition());
            while (rippleDone == false) {

                if (!validatePos(targetPosition)) {
                    targetPosition = this.getBestAvailPosNearBy(currentCell, targetPosition, lockedPositions);
                }
                Block targetCell = this.getCellByPos(targetPosition);
                if (lockedPositions.contains(targetPosition)) {
                    rippleIterations++;
                    if (rippleIterations >= maxRippleIterations) {
                        lockedPositions.clear();
                        maxIterations--;
                        targetPosition = this.getBestFreePosNearBy(currentCell, targetPosition, lockedPositions);
                        this.setCell(currentCell, targetPosition);
                        rippleDone = true;
                    } else {
                        targetPosition = this.getBestAvailPosNearBy(currentCell, targetPosition, lockedPositions);
                    }
                } else if (currentCell.getPosition().equals(targetPosition)) {
                    this.setCell(currentCell, targetPosition);
                    lockedPositions.add(targetPosition);
                    rippleIterations = 0;
                    rippleDone = true;
                } else if (targetCell == null) {
                    this.setCell(currentCell, targetPosition);
                    lockedPositions.add(targetPosition);
                    rippleDone = true;
                    rippleIterations = 0;
                } else if (targetCell.getBlockType() == BlockType.LOGIC_BLOCK) {
                    this.setCell(currentCell, targetPosition);
                    currentCell = (LogicBlock) targetCell;
                    currentCell.setPosition(Block.INIT_POSITION);
                    lockedPositions.add(targetPosition);
                    targetPosition = currentCell.getZFT();
                    rippleIterations = 0;
                }

            }
        }

    }

    public boolean validatePos(Pos2D pos) {
        return pos.getX() > 0
                && pos.getX() <= this.SIZE
                && pos.getY() > 0
                && pos.getY() <= this.SIZE;
    }

    public Pos2D getBestFreePosNearBy(Block currentBlock, Pos2D targetPosition, Set<Pos2D> lockedCells) {
        return this.getBestPosNearBy(currentBlock, targetPosition, lockedCells, false);
    }

    public Pos2D getBestAvailPosNearBy(Block currentBlock, Pos2D targetPosition, Set<Pos2D> lockedCells) {
        return this.getBestPosNearBy(currentBlock, targetPosition, lockedCells, true);
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
        while (coords.size() == 0) {
            ++depth;
            for (int x = Math.max(1, i - depth); x <= Math.min(i + depth, this.SIZE); ++x) {
                for (int y = Math.max(1, j - depth); y <= Math.min(j + depth, this.SIZE); ++y) {
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
                x = rand.nextInt(this.SIZE) + 1;
                y = rand.nextInt(this.SIZE) + 1;

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

    public void setCell(Block block, Pos2D pos) {
        block.setPosition(pos);
        this.cells[pos.getX()][pos.getY()] = block;
    }

    public void removeCell(Pos2D pos) {
        this.cells[pos.getX()][pos.getY()] = null;
    }

    public boolean isCellEmpty(Pos2D pos) {
        return this.cells[pos.getX()][pos.getY()] == null;
    }

    public void moveCell(Pos2D posFrom, Pos2D posTo) {
        Block blockA = this.getCellByPos(posFrom);
        this.removeCell(posFrom);
        this.setCell(blockA, posTo);
    }

    public int getSIZE() {
        return SIZE;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Netlist file: dummy\tArchitecture file: dummy\n");
        builder.append("Array size: " + this.SIZE + " x " + this.SIZE + " logic blocks\n\n");
        builder.append("#block name	x	y	subblk	block number\n");
        builder.append("#----------	--	--	------	------------\n");
        builder.append(this.graph.toString());

        return builder.toString();
    }

}
