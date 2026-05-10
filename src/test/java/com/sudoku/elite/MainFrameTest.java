package com.sudoku.elite;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Basic tests for MainFrame.
 */
public class MainFrameTest {

    @Test
    public void testInitialization() {
        try {
            MainFrame frame = new MainFrame();
            assertNotNull(frame);
        } catch (java.awt.HeadlessException e) {
            System.out.println("Headless environment, skipping UI test.");
        }
    }
}
