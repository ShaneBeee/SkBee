package com.shanebeestudios.skbee.game.checkers;

import javax.swing.*;
import java.awt.*;

public class Checkers {
    public Checkers() {
        var frame = new JFrame("Checkers \uD83D\uDE0E");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        frame.getContentPane().setPreferredSize(new Dimension(512, 512));
        frame.pack();

        // Center on screen
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
        frame.setLocation(x, y);

        var panel = new Game(frame.getHeight());
        frame.add(panel);
        frame.addMouseListener(panel);

        frame.setResizable(false);
        frame.setVisible(true);
        frame.setLayout(null);
    }
}
