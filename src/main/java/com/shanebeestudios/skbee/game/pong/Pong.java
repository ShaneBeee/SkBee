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
    private int playerScore = 0;
    private int aiScore = 0;
    private boolean paused = false;

    public static void main(String[] args) {
        new Pong();
    }

    public Pong() {
        frame = new JFrame("SkBee Pong!");
        frame.setSize(640, 640);
        frame.setResizable(false);

        // Center on screen
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
        frame.setLocation(x, y);

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

        JComponent scoreDisplay = new JComponent() {
            @Override
            public void paint(Graphics g) {
                g.setColor(Color.WHITE);
                g.setFont(new Font("Monospaced", Font.BOLD, 48));
                FontMetrics fm = g.getFontMetrics();
                String playerStr = String.valueOf(playerScore);
                String aiStr = String.valueOf(aiScore);
                // Player score on left quarter, AI score on right quarter
                g.drawString(playerStr, getWidth() / 4 - fm.stringWidth(playerStr) / 2, fm.getAscent() + 20);
                g.drawString(aiStr, 3 * getWidth() / 4 - fm.stringWidth(aiStr) / 2, fm.getAscent() + 20);
                // Center dividing line
                Graphics2D g2d = (Graphics2D) g;
                g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{10}, 0));
                g2d.drawLine(getWidth() / 2, 0, getWidth() / 2, getHeight());
            }
        };

        JComponent pauseOverlay = new JComponent() {
            @Override
            public void paint(Graphics g) {
                if (!paused) return;
                // Semi-transparent dark overlay
                g.setColor(new Color(255, 0, 0, 50));
                g.fillRect(0, 0, getWidth(), getHeight());
                // "PAUSED" title
                g.setColor(Color.WHITE);
                g.setFont(new Font("Monospaced", Font.BOLD, 64));
                FontMetrics fm = g.getFontMetrics();
                String title = "PAUSED";
                g.drawString(title, getWidth() / 2 - fm.stringWidth(title) / 2, getHeight() / 2 - 20);
                // Hint text
                g.setFont(new Font("Monospaced", Font.PLAIN, 18));
                fm = g.getFontMetrics();
                String hint = "Press ESC to resume";
                g.drawString(hint, getWidth() / 2 - fm.stringWidth(hint) / 2, getHeight() / 2 + 30);
                // Quit hint
                String quit = "Press Q to quit";
                g.drawString(quit, getWidth() / 2 - fm.stringWidth(quit) / 2, getHeight() / 2 + 56);
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
        scoreDisplay.setSize(mid.width * 2, mid.height * 2);
        scoreDisplay.setLocation(0, 0);
        rootPane.add(scoreDisplay);
        pauseOverlay.setSize(mid.width * 2, mid.height * 2);
        pauseOverlay.setLocation(0, 0);
        rootPane.add(pauseOverlay);
        ball.setLocation(mid.width - 10, mid.height - 10);
        playerPaddle.setLocation(20, mid.height - 50);
        aiPaddle.setLocation(mid.width * 2 - 40, mid.height - 50);

        final int[] addPlayerVel = {0};

        rootPane.addKeyListener(new KeyAdapter() {
            private final boolean[] fuckYouJava = new boolean[2];

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    paused = !paused;
                    pauseOverlay.repaint();
                    return;
                }
                if (e.getKeyCode() == KeyEvent.VK_Q && paused) {
                    System.exit(0);
                }
                if (paused) return;
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

            if (!paused) {
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

                if (loc.x < 10) {
                    // Ball passed player's side — AI scores
                    aiScore++;
                    velX = 2.0f;
                    velY = random.nextInt(5) * 2 - 5;
                    ball.setLocation(mid.width - 10, mid.height - 10);
                    playerPaddle.setLocation(20, mid.height - 50);
                    aiPaddle.setLocation(mid.width * 2 - 40, mid.height - 50);
                    scoreDisplay.repaint();
                } else if (loc.x > mid.width * 2 - 10) {
                    // Ball passed AI's side — Player scores
                    playerScore++;
                    velX = -2.0f;
                    velY = random.nextInt(5) * 2 - 5;
                    ball.setLocation(mid.width - 10, mid.height - 10);
                    playerPaddle.setLocation(20, mid.height - 50);
                    aiPaddle.setLocation(mid.width * 2 - 40, mid.height - 50);
                    scoreDisplay.repaint();
                }

                if (aiPaddle.getBounds().intersects(ball.getBounds())) {
                    velX = -(Math.abs(velX) * random.nextFloat(1.0f) + (random.nextInt(100) == 2 ? 10.0f : 1.0f));
                    velY = ((loc.y + 10) - (aiLoc.y + 50)) / 5.0f;
                }
                if (playerPaddle.getBounds().intersects(ball.getBounds())) {
                    velX = (Math.abs(velX) * random.nextFloat(1.0f) + (random.nextInt(100) == 2 ? 10.0f : 1.0f));
                    velY = ((loc.y + 10) - (playerPaddle.getLocation().y + 50)) / 5.0f;
                }
            } // end if (!paused)

            while (System.nanoTime() < time + 16667000L) Thread.onSpinWait();
        }
    }
}
