package edu.college.wms.ui;

import java.awt.Color;
import java.awt.Font;

public final class AppTheme {
    public static final Color NAVY = new Color(25, 45, 72);
    public static final Color BLUE = new Color(41, 104, 173);
    public static final Color BACKGROUND = new Color(244, 247, 251);
    public static final Color MUTED = new Color(104, 118, 137);
    public static final Color BORDER = new Color(220, 227, 235);
    public static final Color WHITE = Color.WHITE;

    private AppTheme() {
    }

    public static Font font(int style, int size) {
        return new Font("SansSerif", style, size);
    }
}
