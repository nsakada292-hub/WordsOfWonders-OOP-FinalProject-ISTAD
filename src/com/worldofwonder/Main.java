package com.worldofwonder;

import com.worldofwonder.view.MainUI;

import javax.swing.SwingUtilities;

/**
 * Main application entrypoint.
 * Launches the World of Wonder desktop GUI application directly following MVC architecture.
 */
public class Main {

    public static void main(String[] args) {
        // Enable high-DPI LCD subpixel font antialiasing on Windows
        System.setProperty("awt.useSystemAAFontSettings", "lcd");
        System.setProperty("swing.aatext", "true");

        SwingUtilities.invokeLater(MainUI::new);
    }
}

