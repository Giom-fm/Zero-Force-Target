package test;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import main.net.LogicBlock;
import main.net.Pad;
import main.net.Pos2D;

public class ZFTTest {

    @Test
    public void ZFT_Corner_UpLeft() {
        Pad padA = new Pad("padA", new Pos2D(0, 1), 0);
        Pad padB = new Pad("padB", new Pos2D(1, 0), 0);

        LogicBlock blockA = new LogicBlock("blockA");
        blockA.addConnectedBlock(padA);
        blockA.addConnectedBlock(padB);

        Pos2D expected = new Pos2D(1, 1);
        assertEquals(expected, blockA.getZFT());
    }

    @Test
    public void ZFT_AllCorners() {
        Pad padA = new Pad("padA", new Pos2D(1, 1), 0);
        Pad padB = new Pad("padB", new Pos2D(3, 1), 0);
        Pad padC = new Pad("padC", new Pos2D(1, 3), 0);
        Pad padD = new Pad("padD", new Pos2D(3, 3), 0);

        LogicBlock blockA = new LogicBlock("blockA");
        blockA.addConnectedBlock(padA);
        blockA.addConnectedBlock(padB);
        blockA.addConnectedBlock(padC);
        blockA.addConnectedBlock(padD);

        Pos2D expected = new Pos2D(2, 2);
        assertEquals(expected, blockA.getZFT());
    }

    @Test
    public void ZFT_MiddleLeft() {
        Pad padA = new Pad("padA", new Pos2D(4, 1), 0);
        Pad padB = new Pad("padB", new Pos2D(4, 3), 0);

        LogicBlock blockA = new LogicBlock("blockA");
        blockA.addConnectedBlock(padA);
        blockA.addConnectedBlock(padB);

        Pos2D expected = new Pos2D(4, 2);
        assertEquals(expected, blockA.getZFT());
    }

}
