package com.sudoku.elite;

import java.awt.Color;

/**
 * Defines the color palettes for the application themes.
 */
public enum SudokuTheme {
    DARK(
        new Color(33, 33, 33),   // Background
        new Color(25, 25, 25),   // Header
        new Color(45, 45, 45),   // Side/Cell Bg
        Color.WHITE,             // Text
        new Color(100, 180, 255) // Accent/Highlight
    ),
    LIGHT(
        new Color(245, 245, 245),
        new Color(220, 220, 220),
        Color.WHITE,
        new Color(33, 33, 33),
        new Color(0, 100, 200)
    ),
    CLASSIC_BLUE(
        new Color(230, 240, 255),
        new Color(0, 51, 102),
        new Color(255, 255, 255),
        new Color(0, 51, 102),
        new Color(0, 153, 255)
    );

    public final Color background;
    public final Color header;
    public final Color sidePanel;
    public final Color text;
    public final Color accent;

    SudokuTheme(Color background, Color header, Color sidePanel, Color text, Color accent) {
        this.background = background;
        this.header = header;
        this.sidePanel = sidePanel;
        this.text = text;
        this.accent = accent;
    }

    @Override
    public String toString() {
        return LanguageManager.getInstance().getString("theme." + this.name().toLowerCase());
    }
}
