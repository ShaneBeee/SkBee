package com.shanebeestudios.skbee.game.pong;

import javax.swing.*;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Ellipse2D;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ThreadLocalRandom;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class Pong {

    private int playerScore = 0;
    private int aiScore = 0;
    private boolean paused = false;

    // Neon palette
    static final Color BG_COLOR = new Color(8, 8, 20);
    static final Color PLAYER_COLOR = new Color(0, 255, 240);   // cyan
    static final Color AI_COLOR = new Color(255, 0, 200);   // magenta
    static final Color BALL_COLOR = new Color(255, 230, 0);   // yellow
    static final Color SCANLINE_COLOR = new Color(0, 0, 0, 40);
    static final Color CENTER_COLOR = new Color(255, 255, 255, 60);

    /**
     * Draw a subtle neon glow: thin strokes close to the shape, low opacity
     */
    private static void drawGlow(Graphics2D g2, Color base, Runnable drawShape, int layers) {
        for (int i = layers; i >= 1; i--) {
            int alpha = Math.min(255, 28 * i);
            g2.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(), alpha));
            g2.setStroke(new BasicStroke(i * 1.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            drawShape.run();
        }
        g2.setStroke(new BasicStroke(1));
    }

    public static void main(String[] args) {
        new Pong();
    }

    public Pong() {
        JFrame frame = new JFrame("SkBee Pong!");
        frame.setSize(640, 640);
        frame.setResizable(false);

        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
        frame.setLocation(x, y);

        JPanel rootPane = new JPanel();
        rootPane.setLayout(null);
        rootPane.setBackground(BG_COLOR);

        // ── Ball trail ────────────────────────────────────────────────
        final int TRAIL_LEN = 10;
        Deque<Point> trail = new ArrayDeque<>(TRAIL_LEN + 1);

        // ── Ball ──────────────────────────────────────────────────────
        JComponent ball = new JComponent() {
            @Override
            public void paint(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Core fill first
                g2.setColor(BALL_COLOR);
                g2.fill(new Ellipse2D.Float(3, 3, 14, 14));
                // Subtle glow on top
                drawGlow(g2, BALL_COLOR, () -> g2.draw(new Ellipse2D.Float(3, 3, 14, 14)), 3);
            }
        };

        // ── Player paddle (cyan) ──────────────────────────────────────
        JComponent playerPaddle = new JComponent() {
            @Override
            public void paint(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Core fill
                GradientPaint gp = new GradientPaint(0, 0, PLAYER_COLOR.darker(), 20, 0, PLAYER_COLOR);
                g2.setPaint(gp);
                g2.fillRoundRect(3, 3, 14, 94, 8, 8);
                // Subtle glow on edges
                drawGlow(g2, PLAYER_COLOR, () -> g2.drawRoundRect(3, 3, 14, 94, 8, 8), 3);
            }
        };

        // ── AI paddle (magenta) ───────────────────────────────────────
        JComponent aiPaddle = new JComponent() {
            @Override
            public void paint(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Core fill
                GradientPaint gp = new GradientPaint(0, 0, AI_COLOR, 20, 0, AI_COLOR.darker());
                g2.setPaint(gp);
                g2.fillRoundRect(3, 3, 14, 94, 8, 8);
                // Subtle glow on edges
                drawGlow(g2, AI_COLOR, () -> g2.drawRoundRect(3, 3, 14, 94, 8, 8), 3);
            }
        };

        // ── Score + center line + scanlines ──────────────────────────
        JComponent scoreDisplay = new JComponent() {
            @Override
            public void paint(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Scanlines
                g2.setColor(SCANLINE_COLOR);
                for (int y = 0; y < getHeight(); y += 4) {
                    g2.drawLine(0, y, getWidth(), y);
                }

                // Ball trail (absolute coords — scoreDisplay is full-panel size)
                int ti = 0;
                for (Point p : trail) {
                    float frac = (float) ti / TRAIL_LEN;
                    int alpha = (int) (frac * 140);
                    float size = 14 * frac;
                    g2.setColor(new Color(255, 200, 0, alpha));
                    g2.fill(new Ellipse2D.Float(p.x + 10 - size / 2, p.y + 10 - size / 2, size, size));
                    ti++;
                }

                // Dashed center line
                g2.setColor(CENTER_COLOR);
                float[] dash = {12f, 10f};
                g2.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, dash, 0));
                g2.drawLine(getWidth() / 2, 0, getWidth() / 2, getHeight());
                g2.setStroke(new BasicStroke(1));

                // Labels
                g2.setFont(new Font("Monospaced", Font.BOLD, 11));
                g2.setColor(new Color(PLAYER_COLOR.getRed(), PLAYER_COLOR.getGreen(), PLAYER_COLOR.getBlue(), 180));
                String pLabel = "PLAYER";
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(pLabel, getWidth() / 4 - fm.stringWidth(pLabel) / 2, 18);

                g2.setColor(new Color(AI_COLOR.getRed(), AI_COLOR.getGreen(), AI_COLOR.getBlue(), 180));
                String aLabel = "CPU";
                g2.drawString(aLabel, 3 * getWidth() / 4 - fm.stringWidth(aLabel) / 2, 18);

                // Scores — player cyan, AI magenta
                g2.setFont(new Font("Monospaced", Font.BOLD, 52));
                fm = g2.getFontMetrics();
                String playerStr = String.valueOf(playerScore);
                String aiStr = String.valueOf(aiScore);

                // Glow pass for scores
                for (int layer = 4; layer >= 1; layer--) {
                    float a = 0.08f * layer;
                    g2.setColor(new Color(PLAYER_COLOR.getRed(), PLAYER_COLOR.getGreen(), PLAYER_COLOR.getBlue(), (int) (a * 255)));
                    g2.drawString(playerStr, getWidth() / 4 - fm.stringWidth(playerStr) / 2 + layer, fm.getAscent() + 22 + layer);
                    g2.drawString(playerStr, getWidth() / 4 - fm.stringWidth(playerStr) / 2 - layer, fm.getAscent() + 22 - layer);
                }
                g2.setColor(PLAYER_COLOR);
                g2.drawString(playerStr, getWidth() / 4 - fm.stringWidth(playerStr) / 2, fm.getAscent() + 22);

                for (int layer = 4; layer >= 1; layer--) {
                    float a = 0.08f * layer;
                    g2.setColor(new Color(AI_COLOR.getRed(), AI_COLOR.getGreen(), AI_COLOR.getBlue(), (int) (a * 255)));
                    g2.drawString(aiStr, 3 * getWidth() / 4 - fm.stringWidth(aiStr) / 2 + layer, fm.getAscent() + 22 + layer);
                    g2.drawString(aiStr, 3 * getWidth() / 4 - fm.stringWidth(aiStr) / 2 - layer, fm.getAscent() + 22 - layer);
                }
                g2.setColor(AI_COLOR);
                g2.drawString(aiStr, 3 * getWidth() / 4 - fm.stringWidth(aiStr) / 2, fm.getAscent() + 22);
            }
        };

        // ── Pause overlay ─────────────────────────────────────────────
        JComponent pauseOverlay = new JComponent() {
            @Override
            public void paint(Graphics g) {
                if (!paused) return;
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Dark tinted veil
                g2.setColor(new Color(8, 8, 20, 200));
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Scanlines on overlay too
                g2.setColor(new Color(0, 0, 0, 60));
                for (int y = 0; y < getHeight(); y += 4) g2.drawLine(0, y, getWidth(), y);

                // Neon box
                g2.setColor(new Color(0, 255, 240, 40));
                g2.fillRoundRect(getWidth() / 2 - 170, getHeight() / 2 - 80, 340, 160, 16, 16);
                g2.setColor(new Color(0, 255, 240, 120));
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(getWidth() / 2 - 170, getHeight() / 2 - 80, 340, 160, 16, 16);

                // "PAUSED" with cyan glow
                g2.setFont(new Font("Monospaced", Font.BOLD, 64));
                FontMetrics fm = g2.getFontMetrics();
                String title = "PAUSED";
                int tx = getWidth() / 2 - fm.stringWidth(title) / 2;
                int ty = getHeight() / 2 - 10;
                for (int layer = 5; layer >= 1; layer--) {
                    g2.setColor(new Color(0, 255, 240, 18 * layer));
                    g2.drawString(title, tx + layer, ty + layer);
                    g2.drawString(title, tx - layer, ty - layer);
                }
                g2.setColor(PLAYER_COLOR);
                g2.drawString(title, tx, ty);

                // Hints
                g2.setFont(new Font("Monospaced", Font.PLAIN, 16));
                fm = g2.getFontMetrics();
                g2.setColor(new Color(200, 200, 200, 200));
                String hint = "[ ESC ]  Resume";
                g2.drawString(hint, getWidth() / 2 - fm.stringWidth(hint) / 2, getHeight() / 2 + 44);
                String quit = "[  Q  ]  Quit";
                g2.drawString(quit, getWidth() / 2 - fm.stringWidth(quit) / 2, getHeight() / 2 + 66);
            }
        };

        // ── Layout ────────────────────────────────────────────────────
        ball.setSize(20, 20);
        playerPaddle.setSize(20, 100);
        aiPaddle.setSize(20, 100);
        rootPane.add(aiPaddle);
        rootPane.add(playerPaddle);
        rootPane.add(ball);

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
                if (e.getKeyCode() == KeyEvent.VK_Q && paused) System.exit(0);
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
                if (!(fuckYouJava[0] || fuckYouJava[1]) &&
                    (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN))
                    addPlayerVel[0] = 0;
            }
        });
        rootPane.requestFocus();

        var random = ThreadLocalRandom.current();
        float velX = 2.0f;
        float velY = random.nextInt(5) * 2 - 5;

        //noinspection InfiniteLoopStatement
        while (true) {
            var time = System.nanoTime();

            if (!paused) {
                var loc = ball.getLocation();

                // Update trail
                trail.addLast(new Point(loc.x, loc.y));
                if (trail.size() > TRAIL_LEN) trail.pollFirst();

                ball.setLocation((int) (loc.x + Math.ceil(velX)), (int) (loc.y + Math.ceil(velY)));
                ball.repaint();
                scoreDisplay.repaint();

                if (addPlayerVel[0] != 0) {
                    var playerLoc = playerPaddle.getLocation();
                    playerPaddle.setLocation(playerLoc.x,
                        Math.min(mid.height * 2 - 100, Math.max(0, playerLoc.y + addPlayerVel[0])));
                }
                playerPaddle.repaint();

                var aiLoc = aiPaddle.getLocation();
                aiPaddle.setLocation(aiLoc.x,
                    Math.min(mid.height * 2 - 100,
                        Math.max(0, aiLoc.y + Integer.compare(loc.y - 50, aiLoc.y) * 4)));
                aiPaddle.repaint();

                if (loc.y < 10) velY = Math.abs(velY);
                if (loc.y > mid.height * 2 - 10) velY = -(Math.abs(velY));

                if (loc.x < 10) {
                    aiScore++;
                    velX = 2.0f;
                    velY = random.nextInt(5) * 2 - 5;
                    trail.clear();
                    ball.setLocation(mid.width - 10, mid.height - 10);
                    playerPaddle.setLocation(20, mid.height - 50);
                    aiPaddle.setLocation(mid.width * 2 - 40, mid.height - 50);
                    scoreDisplay.repaint();
                } else if (loc.x > mid.width * 2 - 10) {
                    playerScore++;
                    velX = -2.0f;
                    velY = random.nextInt(5) * 2 - 5;
                    trail.clear();
                    ball.setLocation(mid.width - 10, mid.height - 10);
                    playerPaddle.setLocation(20, mid.height - 50);
                    aiPaddle.setLocation(mid.width * 2 - 40, mid.height - 50);
                    scoreDisplay.repaint();
                }

                if (aiPaddle.getBounds().intersects(ball.getBounds())) {
                    velX = -(Math.abs(velX) * random.nextFloat(1.0f) +
                        (random.nextInt(100) == 2 ? 10.0f : 1.0f));
                    velY = ((loc.y + 10) - (aiLoc.y + 50)) / 5.0f;
                }
                if (playerPaddle.getBounds().intersects(ball.getBounds())) {
                    velX = (Math.abs(velX) * random.nextFloat(1.0f) +
                        (random.nextInt(100) == 2 ? 10.0f : 1.0f));
                    velY = ((loc.y + 10) - (playerPaddle.getLocation().y + 50)) / 5.0f;
                }
            }

            while (System.nanoTime() < time + 16667000L) Thread.onSpinWait();
        }
    }

}
