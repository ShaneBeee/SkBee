package com.shanebeestudios.skbee.checkers;

import javax.swing.*;
import java.awt.*;

public class Checkers {
    public Checkers() {
        var frame = new JFrame("Checkers \uD83D\uDE0E");

        frame.getContentPane().setPreferredSize(new Dimension(512, 512));
        frame.pack();

        var panel = new Game(frame.getHeight());
        frame.add(panel);
        frame.addMouseListener(panel);

        frame.setResizable(false);
        frame.setVisible(true);
        frame.setLayout(null);
    }
}
