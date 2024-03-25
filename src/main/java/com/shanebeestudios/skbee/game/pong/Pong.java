package com.shanebeestudios.skbee.game.pong;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.concurrent.ThreadLocalRandom;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class Pong {
    private final JFrame frame;
    private final JPanel rootPane;

    private float velX, velY;

    public Pong() {
        frame = new JFrame("SkBee Pong!");
        frame.setSize(640, 640);
        frame.setResizable(false);
        rootPane = new JPanel();
        rootPane.setLayout(null);

        rootPane.setBackground(Color.BLACK);

        Component ball = new JComponent() {
            @Override
            public void paint(Graphics g) {
                g.setColor(Color.WHITE);
                g.fillOval(0, 0, 20, 20);
            }
        };

        Component playerPaddle = new JComponent() {
            @Override
            public void paint(Graphics g) {
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, 20, 100);
            }
        };

        Component aiPaddle = new JComponent() {
            @Override
            public void paint(Graphics g) {
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, 20, 100);
            }
        };

        ball.setSize(20, 20);
        playerPaddle.setSize(20, 100);
        aiPaddle.setSize(20, 100);
        rootPane.add(aiPaddle);
        rootPane.add(playerPaddle);
        rootPane.add(ball);
        playerPaddle.repaint();
        ball.repaint();

        frame.add(rootPane);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setVisible(true);

        var mid = new Dimension(rootPane.getSize().width / 2, rootPane.getSize().height / 2);
        ball.setLocation(mid.width - 10, mid.height - 10);
        playerPaddle.setLocation(20, mid.height - 50);
        aiPaddle.setLocation(mid.width * 2 - 40, mid.height - 50);

        final int[] addPlayerVel = {0};

        rootPane.addKeyListener(new KeyAdapter() {
            private final boolean[] fuckYouJava = new boolean[2];

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    addPlayerVel[0] = -4;
                    fuckYouJava[0] = true;
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    addPlayerVel[0] = 4;
                    fuckYouJava[1] = true;
                }

            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_UP) fuckYouJava[0] = false;
                if (e.getKeyCode() == KeyEvent.VK_DOWN) fuckYouJava[1] = false;
                if (!(fuckYouJava[0] || fuckYouJava[1]) && (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN)) addPlayerVel[0] = 0;
            }
        });
        rootPane.requestFocus();

        var random = ThreadLocalRandom.current();
        velX = 2.0f;
        velY = random.nextInt(5) * 2 - 5;

        //noinspection InfiniteLoopStatement
        while (true) {
            var time = System.nanoTime();
            var loc = ball.getLocation();

            ball.setLocation((int) (loc.x + Math.ceil(velX)), (int) (loc.y + Math.ceil(velY)));
            ball.repaint();

            if (addPlayerVel[0] != 0) {
                var playerLoc = playerPaddle.getLocation();
                playerPaddle.setLocation(playerLoc.x, Math.min(mid.height * 2 - 100, Math.max(0, playerLoc.y + addPlayerVel[0])));
            }
            playerPaddle.repaint();

            var aiLoc = aiPaddle.getLocation();
            aiPaddle.setLocation(aiLoc.x, Math.min(mid.height * 2 - 100, Math.max(0, aiLoc.y + Integer.compare(loc.y - 50, aiLoc.y) * 4)));
            aiPaddle.repaint();

            if (loc.y < 10) velY = Math.abs(velY);
            if (loc.y > mid.height * 2 - 10) velY = -(Math.abs(velY));

            if (loc.x < 10 || loc.x > mid.height * 2 - 10) {
                velX = 2.0f;
                velY = random.nextInt(5) * 2 - 5;
                ball.setLocation(mid.width - 10, mid.height - 10);
                playerPaddle.setLocation(20, mid.height - 50);
                aiPaddle.setLocation(mid.width * 2 - 40, mid.height - 50);
            }

            if (aiPaddle.getBounds().intersects(ball.getBounds())) {
                velX = -(Math.abs(velX) * random.nextFloat(1.0f) + (random.nextInt(100) == 2 ? 10.0f : 1.0f));
                velY = ((loc.y + 10) - (aiLoc.y + 50)) / 5.0f;
            }
            if (playerPaddle.getBounds().intersects(ball.getBounds())) {
                velX = (Math.abs(velX) * random.nextFloat(1.0f) + (random.nextInt(100) == 2 ? 10.0f : 1.0f));
                velY = ((loc.y + 10) - (playerPaddle.getLocation().y + 50)) / 5.0f;
            }

            while (System.nanoTime() < time + 16667000L) Thread.onSpinWait();
        }
    }
}
