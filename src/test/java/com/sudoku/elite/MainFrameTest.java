package com.sudoku.elite;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/** Basic tests for MainFrame. */
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
