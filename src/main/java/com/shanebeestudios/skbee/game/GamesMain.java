package com.shanebeestudios.skbee.game;

import com.shanebeestudios.skbee.game.checkers.Checkers;
import com.shanebeestudios.skbee.game.pong.Pong;

import javax.swing.*;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Taskbar;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class GamesMain {

    private static final Color BG_COLOR = new Color(8, 8, 20);
    private static final Color CYAN_COLOR = new Color(0, 255, 240);
    private static final Color MAGENTA_COLOR = new Color(255, 0, 200);
    private static final Color YELLOW_COLOR = new Color(255, 230, 0);

    // ── Game registry ─────────────────────────────────────────────────
    private static final String[] GAME_NAMES = {"PONG", "CHECKERS"};
    private int selected = 0;

    public static void main(String[] args) {
        new GamesMain();
    }

    public GamesMain() {
        JFrame frame = new JFrame("SkBee Arcade");
        frame.setSize(640, 480);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);

        Image arcadeIcon = createArcadeIcon(512);
        frame.setIconImage(arcadeIcon);
        try {
            if (Taskbar.isTaskbarSupported()) {
                Taskbar tb = Taskbar.getTaskbar();
                if (tb.isSupported(Taskbar.Feature.ICON_IMAGE)) tb.setIconImage(arcadeIcon);
            }
        } catch (Exception ignored) {
        }

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(
            (int) ((screen.getWidth() - frame.getWidth()) / 2),
            (int) ((screen.getHeight() - frame.getHeight()) / 2)
        );

        // Pre-render the game icons
        Image[] icons = {
            createPongIcon(160),
            Checkers.createCheckersIcon(160)
        };

        JPanel panel = new JPanel(null) {
            @Override
            public void paint(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                int W = getWidth(), H = getHeight();

                // Background
                g2.setColor(BG_COLOR);
                g2.fillRect(0, 0, W, H);

                // Scanlines
                g2.setColor(new Color(0, 0, 0, 45));
                for (int y = 0; y < H; y += 4) g2.drawLine(0, y, W, y);

                // Corner brackets
                g2.setStroke(new BasicStroke(1.2f));
                int bx = 30, by = 24, bw = 50, bh = 35;
                g2.setColor(new Color(0, 255, 240, 130));
                g2.drawLine(bx, by + bh, bx, by);
                g2.drawLine(bx, by, bx + bw, by);
                g2.drawLine(W - bx - bw, by, W - bx, by);
                g2.drawLine(W - bx, by, W - bx, by + bh);
                g2.setColor(new Color(255, 0, 200, 130));
                g2.drawLine(bx, H - by - bh, bx, H - by);
                g2.drawLine(bx, H - by, bx + bw, H - by);
                g2.drawLine(W - bx - bw, H - by, W - bx, H - by);
                g2.drawLine(W - bx, H - by, W - bx, H - by - bh);
                g2.setStroke(new BasicStroke(1));

                // Title — "SkBee Arcade"
                g2.setFont(new Font("Monospaced", Font.BOLD, 38));
                FontMetrics fm = g2.getFontMetrics();
                String title = "SKBEE ARCADE";
                int tx = W / 2 - fm.stringWidth(title) / 2;
                int ty = 80;
                for (int l = 4; l >= 1; l--) {
                    g2.setColor(new Color(CYAN_COLOR.getRed(), CYAN_COLOR.getGreen(), CYAN_COLOR.getBlue(), 14 * l));
                    g2.drawString(title, tx + l, ty + l);
                    g2.drawString(title, tx - l, ty - l);
                }
                g2.setColor(CYAN_COLOR);
                g2.drawString(title, tx, ty);

                // Game cards
                int cardW = 160, cardH = 210;
                int gap = 60;
                int totalW = 2 * cardW + gap;
                int startX = W / 2 - totalW / 2;
                int cardY = 120;

                for (int i = 0; i < GAME_NAMES.length; i++) {
                    int cx = startX + i * (cardW + gap);
                    boolean sel = (i == selected);
                    Color cardColor = sel ? CYAN_COLOR : new Color(80, 80, 100);

                    // Card glow
                    if (sel) {
                        for (int l = 6; l >= 1; l--) {
                            g2.setColor(new Color(cardColor.getRed(), cardColor.getGreen(), cardColor.getBlue(), 10 * l));
                            g2.setStroke(new BasicStroke(l * 1.5f));
                            g2.drawRoundRect(cx - l, cardY - l, cardW + l * 2, cardH + l * 2, 16, 16);
                        }
                        g2.setStroke(new BasicStroke(1));
                    }

                    // Card background
                    g2.setColor(sel ? new Color(0, 255, 240, 18) : new Color(40, 40, 60, 180));
                    g2.fillRoundRect(cx, cardY, cardW, cardH, 16, 16);
                    g2.setColor(cardColor);
                    g2.setStroke(new BasicStroke(sel ? 1.8f : 1f));
                    g2.drawRoundRect(cx, cardY, cardW, cardH, 16, 16);
                    g2.setStroke(new BasicStroke(1));

                    // Game icon
                    int iconSize = 120;
                    int iconX = cx + (cardW - iconSize) / 2;
                    int iconY = cardY + 20;
                    g2.drawImage(icons[i], iconX, iconY, iconSize, iconSize, null);

                    // Game name
                    g2.setFont(new Font("Monospaced", Font.BOLD, 15));
                    fm = g2.getFontMetrics();
                    String name = GAME_NAMES[i];
                    int nameX = cx + cardW / 2 - fm.stringWidth(name) / 2;
                    int nameY = iconY + iconSize + 22;
                    if (sel) {
                        for (int l = 3; l >= 1; l--) {
                            g2.setColor(new Color(cardColor.getRed(), cardColor.getGreen(), cardColor.getBlue(), 18 * l));
                            g2.drawString(name, nameX + l, nameY + l);
                            g2.drawString(name, nameX - l, nameY - l);
                        }
                    }
                    g2.setColor(sel ? cardColor : new Color(140, 140, 160));
                    g2.drawString(name, nameX, nameY);
                }

                // Nav hint
                g2.setFont(new Font("Monospaced", Font.PLAIN, 12));
                fm = g2.getFontMetrics();
                String hint = "\u25c4  \u25ba  select     ENTER  launch";
                g2.setColor(new Color(150, 150, 150, 160));
                g2.drawString(hint, W / 2 - fm.stringWidth(hint) / 2, H - 36);

                // Blinking prompt
                if ((System.currentTimeMillis() / 500) % 2 == 0) {
                    g2.setFont(new Font("Monospaced", Font.PLAIN, 11));
                    fm = g2.getFontMetrics();
                    String prompt = "PRESS ENTER TO START";
                    g2.setColor(new Color(200, 200, 200, 160));
                    g2.drawString(prompt, W / 2 - fm.stringWidth(prompt) / 2, H - 18);
                }
            }
        };
        panel.setBackground(BG_COLOR);

        frame.add(panel);
        frame.setVisible(true);

        // Repaint timer for blinking prompt
        Timer repaintTimer = new Timer(250, e -> panel.repaint());
        repaintTimer.start();

        panel.setFocusable(true);
        panel.requestFocus();
        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_RIGHT) {
                    selected = (selected + (key == KeyEvent.VK_LEFT ? -1 : 1) + GAME_NAMES.length) % GAME_NAMES.length;
                    panel.repaint();
                } else if (key == KeyEvent.VK_ENTER || key == KeyEvent.VK_SPACE) {
                    repaintTimer.stop();
                    frame.dispose();
                    switch (selected) {
                        case 0 -> new Thread(() -> new Pong()).start();
                        case 1 -> new Checkers();
                    }
                }
            }
        });
    }

    // ── Arcade selector icon — neon joystick ─────────────────────────
    private static Image createArcadeIcon(int size) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Background
        g.setColor(new Color(8, 8, 20));
        g.fillRoundRect(0, 0, size, size, size / 6, size / 6);
        // Scanlines
        g.setColor(new Color(0, 0, 0, 60));
        for (int y = 0; y < size; y += Math.max(2, size / 32)) g.drawLine(0, y, size, y);

        int cx = size / 2;
        // Joystick base (rounded rectangle, magenta)
        int baseW = size * 5 / 8, baseH = size / 5;
        int baseX = cx - baseW / 2, baseY = size * 3 / 4 - baseH / 2;
        for (int i = 3; i >= 1; i--) {
            g.setColor(new Color(255, 0, 200, 18 * i));
            g.setStroke(new BasicStroke(i * 1.5f));
            g.drawRoundRect(baseX - i, baseY - i, baseW + i * 2, baseH + i * 2, 12, 12);
        }
        g.setStroke(new BasicStroke(1));
        g.setPaint(new GradientPaint(baseX, baseY, new Color(120, 0, 100), baseX, baseY + baseH, new Color(60, 0, 50)));
        g.fillRoundRect(baseX, baseY, baseW, baseH, 12, 12);
        g.setColor(new Color(255, 0, 200, 180));
        g.drawRoundRect(baseX, baseY, baseW, baseH, 12, 12);

        // Joystick shaft (cyan)
        int shaftW = size / 10, shaftH = size * 2 / 5;
        int shaftX = cx - shaftW / 2, shaftY = baseY - shaftH;
        for (int i = 3; i >= 1; i--) {
            g.setColor(new Color(0, 255, 240, 18 * i));
            g.setStroke(new BasicStroke(i * 1.5f));
            g.drawRoundRect(shaftX - i, shaftY - i, shaftW + i * 2, shaftH + i * 2, shaftW, shaftW);
        }
        g.setStroke(new BasicStroke(1));
        g.setPaint(new GradientPaint(shaftX, 0, new Color(0, 180, 170), shaftX + shaftW, 0, new Color(0, 255, 240)));
        g.fillRoundRect(shaftX, shaftY, shaftW, shaftH, shaftW, shaftW);
        g.setColor(new Color(0, 255, 240, 180));
        g.drawRoundRect(shaftX, shaftY, shaftW, shaftH, shaftW, shaftW);

        // Ball top (yellow)
        int ballR = size / 7;
        int ballX = cx - ballR, ballY = shaftY - ballR;
        for (int i = 3; i >= 1; i--) {
            g.setColor(new Color(255, 230, 0, 22 * i));
            g.setStroke(new BasicStroke(i * 1.5f));
            g.drawOval(ballX - i, ballY - i, ballR * 2 + i * 2, ballR * 2 + i * 2);
        }
        g.setStroke(new BasicStroke(1));
        g.setColor(new Color(255, 230, 0));
        g.fillOval(ballX, ballY, ballR * 2, ballR * 2);
        // Shine on ball
        g.setColor(new Color(255, 255, 200, 120));
        g.fillOval(ballX + ballR / 3, ballY + ballR / 4, ballR / 2, ballR / 3);

        // Three buttons on base (left=cyan, mid=yellow, right=magenta)
        int btnR = size / 18;
        int btnY2 = baseY + baseH / 2;
        int[][] btns = {{baseX + baseW / 5, btnY2}, {cx, btnY2}, {baseX + baseW * 4 / 5, btnY2}};
        Color[] btnCols = {new Color(0, 255, 240), new Color(255, 230, 0), new Color(255, 0, 200)};
        for (int b = 0; b < 3; b++) {
            int bx2 = btns[b][0], by2 = btns[b][1];
            for (int i = 2; i >= 1; i--) {
                g.setColor(new Color(btnCols[b].getRed(), btnCols[b].getGreen(), btnCols[b].getBlue(), 25 * i));
                g.fillOval(bx2 - btnR - i, by2 - btnR - i, btnR * 2 + i * 2, btnR * 2 + i * 2);
            }
            g.setColor(btnCols[b]);
            g.fillOval(bx2 - btnR, by2 - btnR, btnR * 2, btnR * 2);
        }
        g.dispose();
        return img;
    }

    private static Image createPongIcon(int size) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(8, 8, 20));
        g.fillRoundRect(0, 0, size, size, size / 6, size / 6);
        g.setColor(new Color(0, 0, 0, 60));
        for (int y = 0; y < size; y += Math.max(2, size / 32)) g.drawLine(0, y, size, y);
        // Centre dashed line
        g.setColor(new Color(255, 255, 255, 50));
        float[] dash = {size / 12f, size / 16f};
        g.setStroke(new BasicStroke(Math.max(1, size / 64f), BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, dash, 0));
        g.drawLine(size / 2, size / 8, size / 2, 7 * size / 8);
        g.setStroke(new BasicStroke(1));
        // Player paddle (cyan)
        int padW = Math.max(3, size / 16), padH = size / 3, padX = size / 10, padY = size / 2 - padH / 2, radius = padW;
        for (int i = 3; i >= 1; i--) {
            g.setColor(new Color(0, 255, 240, 22 * i));
            g.setStroke(new BasicStroke(i * 1.5f));
            g.drawRoundRect(padX - i, padY - i, padW + i * 2, padH + i * 2, radius, radius);
        }
        g.setStroke(new BasicStroke(1));
        g.setPaint(new GradientPaint(padX, 0, new Color(0, 180, 170), padX + padW, 0, new Color(0, 255, 240)));
        g.fillRoundRect(padX, padY, padW, padH, radius, radius);
        // AI paddle (magenta)
        int aiPadX = size - padX - padW;
        for (int i = 3; i >= 1; i--) {
            g.setColor(new Color(255, 0, 200, 22 * i));
            g.setStroke(new BasicStroke(i * 1.5f));
            g.drawRoundRect(aiPadX - i, padY - i, padW + i * 2, padH + i * 2, radius, radius);
        }
        g.setStroke(new BasicStroke(1));
        g.setPaint(new GradientPaint(aiPadX, 0, new Color(255, 0, 200), aiPadX + padW, 0, new Color(180, 0, 140)));
        g.fillRoundRect(aiPadX, padY, padW, padH, radius, radius);
        // Ball (yellow)
        int ballR = Math.max(4, size / 10), ballX = size / 2 - ballR / 2, ballY = size / 2 - ballR / 2;
        for (int i = 3; i >= 1; i--) {
            g.setColor(new Color(255, 230, 0, 22 * i));
            g.setStroke(new BasicStroke(i * 1.5f));
            g.drawOval(ballX - i, ballY - i, ballR + i * 2, ballR + i * 2);
        }
        g.setStroke(new BasicStroke(1));
        g.setColor(new Color(255, 230, 0));
        g.fillOval(ballX, ballY, ballR, ballR);
        g.dispose();
        return img;
    }

    /**
     * @deprecated use {@link Checkers#createCheckersIcon(int)}
     */
    @Deprecated
    private static Image createCheckersIcon(int size) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Dark rounded background
        g.setColor(new Color(8, 8, 20));
        g.fillRoundRect(0, 0, size, size, size / 6, size / 6);
        // Scanlines
        g.setColor(new Color(0, 0, 0, 60));
        for (int y = 0; y < size; y += Math.max(2, size / 32)) g.drawLine(0, y, size, y);
        // 4x4 checkerboard
        int pad = size / 8;
        int boardSize = size - pad * 2;
        int cellSize = boardSize / 4;
        Color dark = new Color(20, 20, 40);
        Color light = new Color(50, 50, 80);
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                int cx = pad + col * cellSize;
                int cy = pad + row * cellSize;
                g.setColor((row + col) % 2 == 0 ? light : dark);
                g.fillRect(cx, cy, cellSize, cellSize);
            }
        }
        // Board border with magenta glow
        for (int i = 3; i >= 1; i--) {
            g.setColor(new Color(255, 0, 200, 18 * i));
            g.setStroke(new BasicStroke(i * 1.5f));
            g.drawRect(pad - i, pad - i, boardSize + i * 2, boardSize + i * 2);
        }
        g.setStroke(new BasicStroke(1));
        g.setColor(new Color(255, 0, 200, 180));
        g.drawRect(pad, pad, boardSize, boardSize);
        // Pieces — two cyan (top) and two magenta (bottom)
        int pieceR = cellSize / 2 - 4;
        int[][] cyanPieces = {{0, 1}, {0, 3}};
        int[][] magentaPieces = {{3, 0}, {3, 2}};
        for (int[] pos : cyanPieces) {
            int px = pad + pos[1] * cellSize + cellSize / 2;
            int py = pad + pos[0] * cellSize + cellSize / 2;
            for (int i = 3; i >= 1; i--) {
                g.setColor(new Color(0, 255, 240, 20 * i));
                g.fillOval(px - pieceR - i, py - pieceR - i, pieceR * 2 + i * 2, pieceR * 2 + i * 2);
            }
            g.setColor(new Color(0, 200, 190));
            g.fillOval(px - pieceR, py - pieceR, pieceR * 2, pieceR * 2);
            g.setColor(new Color(0, 255, 240, 180));
            g.drawOval(px - pieceR, py - pieceR, pieceR * 2, pieceR * 2);
        }
        for (int[] pos : magentaPieces) {
            int px = pad + pos[1] * cellSize + cellSize / 2;
            int py = pad + pos[0] * cellSize + cellSize / 2;
            for (int i = 3; i >= 1; i--) {
                g.setColor(new Color(255, 0, 200, 20 * i));
                g.fillOval(px - pieceR - i, py - pieceR - i, pieceR * 2 + i * 2, pieceR * 2 + i * 2);
            }
            g.setColor(new Color(200, 0, 160));
            g.fillOval(px - pieceR, py - pieceR, pieceR * 2, pieceR * 2);
            g.setColor(new Color(255, 0, 200, 180));
            g.drawOval(px - pieceR, py - pieceR, pieceR * 2, pieceR * 2);
        }
        g.dispose();
        return img;
    }

}
