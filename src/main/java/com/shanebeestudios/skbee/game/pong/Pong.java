package com.shanebeestudios.skbee.game.pong;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.*;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Taskbar;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class Pong {

    private int playerScore = 0;
    private int aiScore = 0;
    private boolean paused = false;

    // 0=Easy, 1=Medium, 2=Hard
    private int difficulty = 1;
    private static final String[] DIFF_NAMES = {"EASY", "MEDIUM", "HARD"};
    // AI speed (px/frame) and positional error (how far the AI aims from ball centre)
    private static final int[] DIFF_SPEED = {2, 4, 6};
    private static final int[] DIFF_ERROR = {55, 18, 0};

    private static final int[] WIN_OPTIONS = {3, 5, 7, 10};
    private int winOptionIndex = 1; // default: first to 5
    private int splashRow = 0; // 0 = difficulty selected, 1 = score to win selected
    private volatile boolean gameOver = false;
    private float rallySpeed = 1.0f;

    static final Color BG_COLOR = new Color(8, 8, 20);
    static final Color PLAYER_COLOR = new Color(0, 255, 240);
    static final Color AI_COLOR = new Color(255, 0, 200);
    static final Color BALL_COLOR = new Color(255, 230, 0);
    static final Color SCANLINE_COLOR = new Color(0, 0, 0, 40);
    static final Color CENTER_COLOR = new Color(255, 255, 255, 60);

    @SuppressWarnings("SameParameterValue")
    private static void drawGlow(Graphics2D g2, Color base, Runnable shape, int layers) {
        for (int i = layers; i >= 1; i--) {
            g2.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(), Math.min(255, 28 * i)));
            g2.setStroke(new BasicStroke(i * 1.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            shape.run();
        }
        g2.setStroke(new BasicStroke(1));
    }

    private static Image createIcon(int size) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(8, 8, 20));
        g.fillRoundRect(0, 0, size, size, size / 6, size / 6);
        g.setColor(new Color(0, 0, 0, 60));
        for (int y = 0; y < size; y += Math.max(2, size / 32)) g.drawLine(0, y, size, y);
        g.setColor(new Color(255, 255, 255, 50));
        float[] dash = {size / 12f, size / 16f};
        g.setStroke(new BasicStroke(Math.max(1, size / 64f), BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, dash, 0));
        g.drawLine(size / 2, size / 8, size / 2, 7 * size / 8);
        g.setStroke(new BasicStroke(1));
        int padW = Math.max(3, size / 16), padH = size / 3, padX = size / 10, padY = size / 2 - padH / 2, radius = padW;
        for (int i = 3; i >= 1; i--) {
            g.setColor(new Color(0, 255, 240, 22 * i));
            g.setStroke(new BasicStroke(i * 1.5f));
            g.drawRoundRect(padX - i, padY - i, padW + i * 2, padH + i * 2, radius, radius);
        }
        g.setStroke(new BasicStroke(1));
        g.setPaint(new GradientPaint(padX, 0, new Color(0, 180, 170), padX + padW, 0, new Color(0, 255, 240)));
        g.fillRoundRect(padX, padY, padW, padH, radius, radius);
        int aiPadX = size - padX - padW;
        for (int i = 3; i >= 1; i--) {
            g.setColor(new Color(255, 0, 200, 22 * i));
            g.setStroke(new BasicStroke(i * 1.5f));
            g.drawRoundRect(aiPadX - i, padY - i, padW + i * 2, padH + i * 2, radius, radius);
        }
        g.setStroke(new BasicStroke(1));
        g.setPaint(new GradientPaint(aiPadX, 0, new Color(255, 0, 200), aiPadX + padW, 0, new Color(180, 0, 140)));
        g.fillRoundRect(aiPadX, padY, padW, padH, radius, radius);
        int ballR = Math.max(4, size / 10), ballX = size / 2 - ballR / 2, ballY2 = size / 2 - ballR / 2;
        for (int i = 3; i >= 1; i--) {
            g.setColor(new Color(255, 230, 0, 22 * i));
            g.setStroke(new BasicStroke(i * 1.5f));
            g.drawOval(ballX - i, ballY2 - i, ballR + i * 2, ballR + i * 2);
        }
        g.setStroke(new BasicStroke(1));
        g.setColor(new Color(255, 230, 0));
        g.fillOval(ballX, ballY2, ballR, ballR);
        g.dispose();
        return img;
    }

    private static void beep(double freqHz, int ms, float volume, double sweep) {
        Thread t = new Thread(() -> {
            try {
                float sr = 44100f;
                int n = (int) (sr * ms / 1000.0);
                byte[] buf = new byte[n * 2];
                for (int i = 0; i < n; i++) {
                    double freq = freqHz + sweep * ((double) i / n);
                    double angle = 2.0 * Math.PI * freq * i / sr;
                    double env = 1.0;
                    int fi = (int) (sr * 0.005), fo = (int) (sr * 0.010);
                    if (i < fi) env = (double) i / fi;
                    if (i > n - fo) env = (double) (n - i) / fo;
                    short val = (short) (Math.sin(angle) * 32767 * volume * env);
                    buf[i * 2] = (byte) (val & 0xFF);
                    buf[i * 2 + 1] = (byte) ((val >> 8) & 0xFF);
                }
                AudioFormat fmt = new AudioFormat(sr, 16, 1, true, false);
                Clip clip = AudioSystem.getClip();
                clip.open(fmt, buf, 0, buf.length);
                clip.start();
                Thread.sleep(ms + 50);
                clip.close();
            } catch (LineUnavailableException | InterruptedException ignored) {
            }
        });
        t.setDaemon(true);
        t.start();
    }

    private static void soundPaddleHit() {
        beep(900, 55, 0.35f, 120);
    }

    private static void soundWallBounce() {
        beep(420, 45, 0.25f, -80);
    }

    private static void soundPlayerScore() {
        beep(440, 80, 0.40f, 220);
        beep(660, 100, 0.40f, 0);
    }

    private static void soundAiScore() {
        beep(330, 80, 0.40f, -150);
        beep(220, 120, 0.35f, 0);
    }

    private static void soundGameStart() {
        beep(330, 60, 0.30f, 0);
        beep(440, 60, 0.30f, 0);
        beep(660, 80, 0.35f, 0);
        beep(880, 120, 0.35f, 80);
    }

    private static void soundWin() {
        beep(440, 80, 0.4f, 0);
        beep(550, 80, 0.4f, 0);
        beep(660, 80, 0.4f, 0);
        beep(880, 200, 0.45f, 100);
    }

    private static void soundLose() {
        beep(440, 100, 0.4f, -100);
        beep(330, 100, 0.4f, -80);
        beep(220, 180, 0.4f, -100);
    }

    // This is only here for testing in IJ
    public static void main(String[] args) {
        new Pong();
    }

    public Pong() {
        // Must be set before AWT initialises — controls the macOS dock app name
        System.setProperty("apple.awt.application.name", "SkBee Pong");
        JFrame frame = new JFrame("SkBee Pong!");
        frame.setSize(640, 640);
        frame.setResizable(false);
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(
            (int) ((screen.getWidth() - frame.getWidth()) / 2),
            (int) ((screen.getHeight() - frame.getHeight()) / 2)
        );

        Image appIcon = createIcon(512);
        frame.setIconImage(appIcon);
        try {
            if (Taskbar.isTaskbarSupported()) {
                Taskbar tb = Taskbar.getTaskbar();
                if (tb.isSupported(Taskbar.Feature.ICON_IMAGE)) tb.setIconImage(appIcon);
            }
        } catch (Exception ignored) {
        }

        JPanel rootPane = new JPanel(null);
        rootPane.setBackground(BG_COLOR);

        // ── Game components ───────────────────────────────────────────
        final int TRAIL_LEN = 10;
        ConcurrentLinkedDeque<Point> trail = new ConcurrentLinkedDeque<>();

        JComponent ball = new JComponent() {
            @Override
            public void paint(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BALL_COLOR);
                g2.fill(new Ellipse2D.Float(3, 3, 14, 14));
                drawGlow(g2, BALL_COLOR, () -> g2.draw(new Ellipse2D.Float(3, 3, 14, 14)), 3);
            }
        };

        JComponent playerPaddle = new JComponent() {
            @Override
            public void paint(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, PLAYER_COLOR.darker(), 20, 0, PLAYER_COLOR));
                g2.fillRoundRect(3, 3, 14, 94, 8, 8);
                drawGlow(g2, PLAYER_COLOR, () -> g2.drawRoundRect(3, 3, 14, 94, 8, 8), 3);
            }
        };

        JComponent aiPaddle = new JComponent() {
            @Override
            public void paint(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, AI_COLOR, 20, 0, AI_COLOR.darker()));
                g2.fillRoundRect(3, 3, 14, 94, 8, 8);
                drawGlow(g2, AI_COLOR, () -> g2.drawRoundRect(3, 3, 14, 94, 8, 8), 3);
            }
        };

        JComponent scoreDisplay = new JComponent() {
            @Override
            public void paint(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(SCANLINE_COLOR);
                for (int y = 0; y < getHeight(); y += 4) g2.drawLine(0, y, getWidth(), y);

                // Trail
                int ti = 0;
                for (Point p : trail) {
                    float frac = (float) ti / TRAIL_LEN;
                    float size = 14 * frac;
                    g2.setColor(new Color(255, 200, 0, (int) (frac * 140)));
                    g2.fill(new Ellipse2D.Float(p.x + 10 - size / 2, p.y + 10 - size / 2, size, size));
                    ti++;
                }

                float[] dash = {12f, 10f};
                g2.setColor(CENTER_COLOR);
                g2.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, dash, 0));
                g2.drawLine(getWidth() / 2, 0, getWidth() / 2, getHeight());
                g2.setStroke(new BasicStroke(1));

                g2.setFont(new Font("Monospaced", Font.BOLD, 11));
                FontMetrics fm = g2.getFontMetrics();
                g2.setColor(new Color(PLAYER_COLOR.getRed(), PLAYER_COLOR.getGreen(), PLAYER_COLOR.getBlue(), 180));
                String pLabel = "PLAYER";
                g2.drawString(pLabel, getWidth() / 4 - fm.stringWidth(pLabel) / 2, 18);
                g2.setColor(new Color(AI_COLOR.getRed(), AI_COLOR.getGreen(), AI_COLOR.getBlue(), 180));
                String aLabel = "CPU";
                g2.drawString(aLabel, 3 * getWidth() / 4 - fm.stringWidth(aLabel) / 2, 18);

                g2.setFont(new Font("Monospaced", Font.BOLD, 52));
                fm = g2.getFontMetrics();
                String ps = String.valueOf(playerScore), as = String.valueOf(aiScore);
                for (int l = 4; l >= 1; l--) {
                    int a = (int) (0.08f * l * 255);
                    g2.setColor(new Color(PLAYER_COLOR.getRed(), PLAYER_COLOR.getGreen(), PLAYER_COLOR.getBlue(), a));
                    g2.drawString(ps, getWidth() / 4 - fm.stringWidth(ps) / 2 + l, fm.getAscent() + 22 + l);
                    g2.drawString(ps, getWidth() / 4 - fm.stringWidth(ps) / 2 - l, fm.getAscent() + 22 - l);
                    g2.setColor(new Color(AI_COLOR.getRed(), AI_COLOR.getGreen(), AI_COLOR.getBlue(), a));
                    g2.drawString(as, 3 * getWidth() / 4 - fm.stringWidth(as) / 2 + l, fm.getAscent() + 22 + l);
                    g2.drawString(as, 3 * getWidth() / 4 - fm.stringWidth(as) / 2 - l, fm.getAscent() + 22 - l);
                }
                g2.setColor(PLAYER_COLOR);
                g2.drawString(ps, getWidth() / 4 - fm.stringWidth(ps) / 2, fm.getAscent() + 22);
                g2.setColor(AI_COLOR);
                g2.drawString(as, 3 * getWidth() / 4 - fm.stringWidth(as) / 2, fm.getAscent() + 22);
            }
        };

        JComponent pauseOverlay = new JComponent() {
            @Override
            public void paint(Graphics g) {
                if (!paused) return;
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(8, 8, 20, 200));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(0, 0, 0, 60));
                for (int y = 0; y < getHeight(); y += 4) g2.drawLine(0, y, getWidth(), y);
                g2.setColor(new Color(0, 255, 240, 40));
                g2.fillRoundRect(getWidth() / 2 - 170, getHeight() / 2 - 95, 340, 195, 16, 16);
                g2.setColor(new Color(0, 255, 240, 120));
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(getWidth() / 2 - 170, getHeight() / 2 - 95, 340, 195, 16, 16);
                g2.setFont(new Font("Monospaced", Font.BOLD, 64));
                FontMetrics fm = g2.getFontMetrics();
                String title = "PAUSED";
                int tx = getWidth() / 2 - fm.stringWidth(title) / 2, ty = getHeight() / 2 - 10;
                for (int l = 5; l >= 1; l--) {
                    g2.setColor(new Color(0, 255, 240, 18 * l));
                    g2.drawString(title, tx + l, ty + l);
                    g2.drawString(title, tx - l, ty - l);
                }
                g2.setColor(PLAYER_COLOR);
                g2.drawString(title, tx, ty);
                g2.setFont(new Font("Monospaced", Font.PLAIN, 16));
                fm = g2.getFontMetrics();
                g2.setColor(new Color(200, 200, 200, 200));
                String h1 = "[ ESC ]  Resume", h2 = "[  Q  ]  Quit";
                g2.drawString(h1, getWidth() / 2 - fm.stringWidth(h1) / 2, getHeight() / 2 + 44);
                g2.drawString(h2, getWidth() / 2 - fm.stringWidth(h2) / 2, getHeight() / 2 + 66);
                Color[] dCols = {new Color(0, 255, 100, 200), new Color(255, 200, 0, 200), new Color(255, 80, 80, 200)};
                g2.setFont(new Font("Monospaced", Font.PLAIN, 12));
                fm = g2.getFontMetrics();
                String dStr = "DIFFICULTY: " + DIFF_NAMES[difficulty] + "  |  FIRST TO: " + WIN_OPTIONS[winOptionIndex];
                g2.setColor(dCols[difficulty]);
                g2.drawString(dStr, getWidth() / 2 - fm.stringWidth(dStr) / 2, getHeight() / 2 + 92);
            }
        };

        // ── Win overlay ───────────────────────────────────────────────
        JComponent winOverlay = new JComponent() {
            @Override
            public void paint(Graphics g) {
                if (!gameOver) return;
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(8, 8, 20, 210));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(0, 0, 0, 60));
                for (int y = 0; y < getHeight(); y += 4) g2.drawLine(0, y, getWidth(), y);
                boolean playerWon = playerScore >= WIN_OPTIONS[winOptionIndex];
                Color tc = playerWon ? PLAYER_COLOR : AI_COLOR;
                String tt = playerWon ? "YOU WIN!" : "YOU LOSE";
                g2.setColor(new Color(tc.getRed(), tc.getGreen(), tc.getBlue(), 35));
                g2.fillRoundRect(getWidth() / 2 - 200, getHeight() / 2 - 110, 400, 220, 16, 16);
                g2.setColor(new Color(tc.getRed(), tc.getGreen(), tc.getBlue(), 140));
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(getWidth() / 2 - 200, getHeight() / 2 - 110, 400, 220, 16, 16);
                g2.setFont(new Font("Monospaced", Font.BOLD, 68));
                FontMetrics fm = g2.getFontMetrics();
                int tx = getWidth() / 2 - fm.stringWidth(tt) / 2, ty = getHeight() / 2 - 10;
                for (int l = 5; l >= 1; l--) {
                    g2.setColor(new Color(tc.getRed(), tc.getGreen(), tc.getBlue(), 16 * l));
                    g2.drawString(tt, tx + l, ty + l);
                    g2.drawString(tt, tx - l, ty - l);
                }
                g2.setColor(tc);
                g2.drawString(tt, tx, ty);
                g2.setFont(new Font("Monospaced", Font.PLAIN, 18));
                fm = g2.getFontMetrics();
                String score = "PLAYER  " + playerScore + "  —  " + aiScore + "  CPU";
                g2.setColor(new Color(200, 200, 200, 200));
                g2.drawString(score, getWidth() / 2 - fm.stringWidth(score) / 2, getHeight() / 2 + 44);
                if ((System.currentTimeMillis() / 500) % 2 == 0) {
                    g2.setFont(new Font("Monospaced", Font.PLAIN, 13));
                    fm = g2.getFontMetrics();
                    String prompt = "PRESS ANY KEY TO CONTINUE";
                    g2.setColor(new Color(180, 180, 180, 180));
                    g2.drawString(prompt, getWidth() / 2 - fm.stringWidth(prompt) / 2, getHeight() / 2 + 80);
                }
            }
        };

        // ── Splash screen ─────────────────────────────────────────────
        final long[] animStart = {System.currentTimeMillis()};
        final boolean[] splashDone = {false};
        final boolean[] splashFadingOut = {false};
        final long[] fadeOutStart = {0};

        JComponent splash = new JComponent() {
            private static final String TITLE1 = "SKBEE";
            private static final String TITLE2 = "PONG";

            private void glowText(Graphics2D g2, Color c, String s, int x, int y) {
                for (int l = 5; l >= 1; l--) {
                    g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 14 * l));
                    g2.drawString(s, x + l, y + l);
                    g2.drawString(s, x - l, y - l);
                }
                g2.setColor(c);
                g2.drawString(s, x, y);
            }

            @Override
            public void paint(Graphics g) {
                int W = getWidth(), H = getHeight();
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                long now = System.currentTimeMillis();
                long elapsed = now - animStart[0];

                final long BLACK_HOLD = 500L;
                final long FADE_IN = 700L;

                g2.setColor(Color.BLACK);
                g2.fillRect(0, 0, W, H);

                float masterAlpha;
                if (splashFadingOut[0]) {
                    float t = (now - fadeOutStart[0]) / 500f;
                    masterAlpha = Math.max(0f, 1f - t);
                    if (masterAlpha <= 0f) {
                        setVisible(false);
                        return;
                    }
                } else if (elapsed < BLACK_HOLD) {
                    return;
                } else {
                    masterAlpha = Math.min(1f, (elapsed - BLACK_HOLD) / (float) FADE_IN);
                }

                long animElapsed = Math.max(0, elapsed - BLACK_HOLD);

                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, masterAlpha));
                g2.setColor(BG_COLOR);
                g2.fillRect(0, 0, W, H);

                float scanAlpha = clamp((animElapsed - 100) / 400f);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, scanAlpha * masterAlpha));
                g2.setColor(new Color(0, 0, 0, 50));
                for (int y = 0; y < H; y += 4) g2.drawLine(0, y, W, y);

                float bracketAlpha = clamp((animElapsed - 200) / 400f);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, bracketAlpha * masterAlpha));
                g2.setStroke(new BasicStroke(1.2f));
                int bx = 38, by = 44, bw = 60, bh = 40;
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

                // Decorative paddles
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, bracketAlpha * masterAlpha));
                int padH = 90, padW = 14, padY = H / 2 - padH / 2;
                g2.setPaint(new Color(0, 255, 240, 180));
                g2.fillRoundRect(50, padY, padW, padH, 6, 6);
                g2.setPaint(new Color(255, 0, 200, 180));
                g2.fillRoundRect(W - 50 - padW, padY, padW, padH, 6, 6);

                // "SKBEE" type-in
                g2.setFont(new Font("Monospaced", Font.BOLD, 96));
                FontMetrics fm = g2.getFontMetrics();
                int charsVisible1 = (int) Math.clamp((animElapsed - 400) / 80, 0, TITLE1.length());
                if (charsVisible1 > 0) {
                    String partial = TITLE1.substring(0, charsVisible1);
                    float flicker = (charsVisible1 < TITLE1.length()) ? (0.6f + 0.4f * (float) Math.sin(animElapsed * 0.03)) : 1f;
                    float a1 = clamp((animElapsed - 400) / 200f) * flicker;
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, a1 * masterAlpha));
                    int x1 = W / 2 - fm.stringWidth(TITLE1) / 2;
                    int y1 = H / 2 - 30;
                    glowText(g2, PLAYER_COLOR, partial, x1, y1);
                    if (charsVisible1 < TITLE1.length() && (animElapsed / 120) % 2 == 0) {
                        g2.setColor(PLAYER_COLOR);
                        g2.fillRect(x1 + fm.stringWidth(partial) + 4, y1 - fm.getAscent() + 4, 8, fm.getAscent());
                    }
                }

                // "PONG" slam up
                long pongStart = 400 + TITLE1.length() * 80L + 80;
                if (animElapsed > pongStart) {
                    float t = clamp((animElapsed - pongStart) / 260f);
                    float ease = bounce(t);
                    int targetY = H / 2 + 85, startY = H + 60;
                    int currentY = (int) (startY + (targetY - startY) * ease);
                    float a2 = clamp((animElapsed - pongStart) / 150f);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, a2 * masterAlpha));
                    int x2 = W / 2 - fm.stringWidth(TITLE2) / 2;
                    glowText(g2, AI_COLOR, TITLE2, x2, currentY);
                }

                // Difficulty selector
                long subtitleStart = pongStart + 400;
                if (animElapsed > subtitleStart) {
                    float selAlpha = clamp((animElapsed - subtitleStart) / 300f) * masterAlpha;
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, selAlpha));
                    g2.setFont(new Font("Monospaced", Font.BOLD, 13));
                    FontMetrics dfm = g2.getFontMetrics();
                    int arrowW = dfm.stringWidth("<"), gap = 14;

                    // difficulty row
                    String diffLabel = "DIFFICULTY:";
                    g2.setColor(splashRow == 0 ? new Color(255, 255, 255, 220) : new Color(200, 200, 200, 120));
                    g2.drawString(diffLabel, W / 2 - dfm.stringWidth(diffLabel) / 2, H - 175);
                    String dName = DIFF_NAMES[difficulty];
                    Color[] dCols = {new Color(0, 255, 100), new Color(255, 200, 0), new Color(255, 60, 60)};
                    Color dCol = splashRow == 0 ? dCols[difficulty] : new Color(dCols[difficulty].getRed(), dCols[difficulty].getGreen(), dCols[difficulty].getBlue(), 120);
                    int dSlotW = dfm.stringWidth("MEDIUM"), dNameW = dfm.stringWidth(dName);
                    int dTotalW = arrowW + gap + dSlotW + gap + arrowW;
                    int dStartX = W / 2 - dTotalW / 2;
                    int dNameX = dStartX + arrowW + gap + (dSlotW - dNameW) / 2;
                    int dRightAX = dStartX + arrowW + gap + dSlotW + gap;
                    g2.setColor(splashRow == 0 && difficulty > 0 ? new Color(200, 200, 200, 200) : new Color(100, 100, 100, 80));
                    g2.drawString("<", dStartX, H - 155);
                    for (int l = 3; l >= 1; l--) {
                        g2.setColor(new Color(dCol.getRed(), dCol.getGreen(), dCol.getBlue(), splashRow == 0 ? 20 * l : 8 * l));
                        g2.drawString(dName, dNameX + l, H - 155 + l);
                        g2.drawString(dName, dNameX - l, H - 155 - l);
                    }
                    g2.setColor(dCol);
                    g2.drawString(dName, dNameX, H - 155);
                    g2.setColor(splashRow == 0 && difficulty < 2 ? new Color(200, 200, 200, 200) : new Color(100, 100, 100, 80));
                    g2.drawString(">", dRightAX, H - 155);

                    // score to win row
                    String winLabel = "SCORE TO WIN:";
                    g2.setColor(splashRow == 1 ? new Color(255, 255, 255, 220) : new Color(200, 200, 200, 120));
                    g2.drawString(winLabel, W / 2 - dfm.stringWidth(winLabel) / 2, H - 120);
                    String wName = String.valueOf(WIN_OPTIONS[winOptionIndex]);
                    int wSlotW = dfm.stringWidth("10"), wNameW = dfm.stringWidth(wName);
                    int wTotalW = arrowW + gap + wSlotW + gap + arrowW;
                    int wStartX = W / 2 - wTotalW / 2;
                    int wNameX = wStartX + arrowW + gap + (wSlotW - wNameW) / 2;
                    int wRightAX = wStartX + arrowW + gap + wSlotW + gap;
                    g2.setColor(splashRow == 1 && winOptionIndex > 0 ? new Color(200, 200, 200, 200) : new Color(100, 100, 100, 80));
                    g2.drawString("<", wStartX, H - 100);
                    g2.setColor(splashRow == 1 ? new Color(180, 180, 255, 220) : new Color(180, 180, 255, 100));
                    g2.drawString(wName, wNameX, H - 100);
                    g2.setColor(splashRow == 1 && winOptionIndex < WIN_OPTIONS.length - 1 ? new Color(200, 200, 200, 200) : new Color(100, 100, 100, 80));
                    g2.drawString(">", wRightAX, H - 100);

                    // small nav hint
                    g2.setFont(new Font("Monospaced", Font.PLAIN, 11));
                    FontMetrics hfm = g2.getFontMetrics();
                    String hint = splashRow == 0 ? "\u25bc  to select SCORE TO WIN" : "\u25b2  to select DIFFICULTY";
                    g2.setColor(new Color(150, 150, 150, 140));
                    g2.drawString(hint, W / 2 - hfm.stringWidth(hint) / 2, H - 70);
                }

                // "PRESS ANY KEY"
                if (animElapsed > subtitleStart) {
                    boolean blink = splashDone[0] && ((animElapsed / 500) % 2 == 0);
                    float subAlpha = splashDone[0] ? (blink ? 0.65f : 0.3f) : clamp((animElapsed - subtitleStart) / 300f) * 0.55f;
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, subAlpha * masterAlpha));
                    g2.setFont(new Font("Monospaced", Font.PLAIN, 13));
                    FontMetrics sfm = g2.getFontMetrics();
                    String sub = "PRESS ANY KEY TO START";
                    g2.setColor(Color.WHITE);
                    g2.drawString(sub, W / 2 - sfm.stringWidth(sub) / 2, H - 55);
                    if (!splashDone[0] && animElapsed > subtitleStart + 400) splashDone[0] = true;
                }

                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            }

            private float clamp(float v) {
                return Math.clamp(v, 0f, 1f);
            }

            private float bounce(float t) {
                if (t >= 1f) return 1f;
                double s = 1.70158;
                t = t - 1;
                return (float) (t * t * ((s + 1) * t + s) + 1);
            }
        };

        // ── Layout ────────────────────────────────────────────────────
        ball.setSize(20, 20);
        playerPaddle.setSize(20, 100);
        aiPaddle.setSize(20, 100);

        frame.add(rootPane);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setVisible(true);

        var mid = new Dimension(rootPane.getSize().width / 2, rootPane.getSize().height / 2);

        splash.setSize(mid.width * 2, mid.height * 2);
        splash.setLocation(0, 0);
        rootPane.add(splash);

        ball.setLocation(mid.width - 10, mid.height - 10);
        playerPaddle.setLocation(20, mid.height - 50);
        aiPaddle.setLocation(mid.width * 2 - 40, mid.height - 50);

        final AtomicBoolean gameStarted = new AtomicBoolean(false);
        Timer animTimer = new Timer(16, e -> {
            if (splash.isVisible()) splash.repaint();
        });
        animTimer.start();

        final int[] addPlayerVel = {0};

        rootPane.addKeyListener(new KeyAdapter() {
            private final boolean[] fuckYouJava = new boolean[2];

            @Override
            public void keyPressed(KeyEvent e) {
                if (splash.isVisible() && splashDone[0] && !splashFadingOut[0]) {
                    if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT) {
                        if (splashRow == 0) {
                            if (e.getKeyCode() == KeyEvent.VK_LEFT) difficulty = Math.max(0, difficulty - 1);
                            else difficulty = Math.min(2, difficulty + 1);
                        } else {
                            if (e.getKeyCode() == KeyEvent.VK_LEFT) winOptionIndex = Math.max(0, winOptionIndex - 1);
                            else winOptionIndex = Math.min(WIN_OPTIONS.length - 1, winOptionIndex + 1);
                        }
                        splash.repaint();
                        return;
                    }
                    if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN) {
                        if (e.getKeyCode() == KeyEvent.VK_DOWN) splashRow = Math.min(1, splashRow + 1);
                        else splashRow = Math.max(0, splashRow - 1);
                        splash.repaint();
                        return;
                    }
                    splashFadingOut[0] = true;
                    fadeOutStart[0] = System.currentTimeMillis();
                    new Timer(520, ev -> {
                        splash.setVisible(false);
                        rootPane.remove(splash);
                        scoreDisplay.setSize(mid.width * 2, mid.height * 2);
                        scoreDisplay.setLocation(0, 0);
                        rootPane.add(aiPaddle);
                        rootPane.add(playerPaddle);
                        rootPane.add(ball);
                        rootPane.add(scoreDisplay);
                        pauseOverlay.setSize(mid.width * 2, mid.height * 2);
                        pauseOverlay.setLocation(0, 0);
                        rootPane.add(pauseOverlay);
                        winOverlay.setSize(mid.width * 2, mid.height * 2);
                        winOverlay.setLocation(0, 0);
                        rootPane.add(winOverlay);
                        rootPane.revalidate();
                        rootPane.repaint();
                        rootPane.requestFocus();
                        soundGameStart();
                        gameStarted.set(true);
                        animTimer.stop();
                        ((Timer) ev.getSource()).stop();
                    }) {{
                        setRepeats(false);
                    }}.start();
                    return;
                }
                if (splash.isVisible()) return;

                if (gameOver) {
                    gameOver = false;
                    playerScore = 0;
                    aiScore = 0;
                    rootPane.remove(winOverlay);
                    rootPane.remove(pauseOverlay);
                    rootPane.remove(scoreDisplay);
                    rootPane.remove(ball);
                    rootPane.remove(playerPaddle);
                    rootPane.remove(aiPaddle);
                    trail.clear();
                    ball.setLocation(mid.width - 10, mid.height - 10);
                    playerPaddle.setLocation(20, mid.height - 50);
                    aiPaddle.setLocation(mid.width * 2 - 40, mid.height - 50);
                    gameStarted.set(false);
                    animStart[0] = System.currentTimeMillis();
                    splashDone[0] = false;
                    splashFadingOut[0] = false;
                    splashRow = 0;
                    splash.setVisible(true);
                    rootPane.add(splash);
                    rootPane.revalidate();
                    rootPane.repaint();
                    animTimer.start();
                    return;
                }

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

            if (gameOver) {
                winOverlay.repaint();
            }
            if (gameStarted.get() && !paused && !gameOver) {
                var loc = ball.getLocation();
                trail.addLast(new Point(loc.x, loc.y));
                if (trail.size() > TRAIL_LEN) trail.pollFirst();

                ball.setLocation((int) (loc.x + Math.ceil(velX * rallySpeed)), (int) (loc.y + Math.ceil(velY * rallySpeed)));
                ball.repaint();
                scoreDisplay.repaint();

                if (addPlayerVel[0] != 0) {
                    var pl = playerPaddle.getLocation();
                    playerPaddle.setLocation(pl.x, Math.clamp(pl.y + addPlayerVel[0], 0, mid.height * 2 - 100));
                }
                playerPaddle.repaint();

                var aiLoc = aiPaddle.getLocation();
                int aiSpeed = DIFF_SPEED[difficulty], aiError = DIFF_ERROR[difficulty];
                int aiTarget = loc.y - 50 + aiError;
                int aiDelta = Integer.compare(aiTarget, aiLoc.y) * aiSpeed;
                aiPaddle.setLocation(aiLoc.x, Math.clamp(aiLoc.y + aiDelta, 0, mid.height * 2 - 100));
                aiPaddle.repaint();

                if (loc.y < 10) {
                    velY = Math.abs(velY);
                    soundWallBounce();
                }
                if (loc.y > mid.height * 2 - 10) {
                    velY = -Math.abs(velY);
                    soundWallBounce();
                }

                int winScore = WIN_OPTIONS[winOptionIndex];
                if (loc.x < 10) {
                    soundAiScore();
                    aiScore++;
                    if (aiScore >= winScore) {
                        gameOver = true;
                        soundLose();
                    }
                    velX = 2.0f;
                    velY = random.nextInt(5) * 2 - 5;
                    rallySpeed = 1.0f;
                    trail.clear();
                    ball.setLocation(mid.width - 10, mid.height - 10);
                    playerPaddle.setLocation(20, mid.height - 50);
                    aiPaddle.setLocation(mid.width * 2 - 40, mid.height - 50);
                    scoreDisplay.repaint();
                } else if (loc.x > mid.width * 2 - 10) {
                    soundPlayerScore();
                    playerScore++;
                    if (playerScore >= winScore) {
                        gameOver = true;
                        soundWin();
                    }
                    velX = -2.0f;
                    velY = random.nextInt(5) * 2 - 5;
                    rallySpeed = 1.0f;
                    trail.clear();
                    ball.setLocation(mid.width - 10, mid.height - 10);
                    playerPaddle.setLocation(20, mid.height - 50);
                    aiPaddle.setLocation(mid.width * 2 - 40, mid.height - 50);
                    scoreDisplay.repaint();
                }

                if (aiPaddle.getBounds().intersects(ball.getBounds())) {
                    soundPaddleHit();
                    rallySpeed = Math.min(rallySpeed + 0.08f, 2.2f);
                    float hitPos = Math.clamp(((loc.y + 10) - (aiLoc.y + 50)) / 50.0f, -1f, 1f);
                    float speedX = (Math.abs(velX) + 0.5f) * rallySpeed;
                    velX = -(speedX + (random.nextInt(100) == 2 ? 10f : 0f));
                    velY = hitPos * 6.0f;
                }
                if (playerPaddle.getBounds().intersects(ball.getBounds())) {
                    soundPaddleHit();
                    rallySpeed = Math.min(rallySpeed + 0.08f, 2.2f);
                    float hitPos = Math.clamp(((loc.y + 10) - (playerPaddle.getLocation().y + 50)) / 50.0f, -1f, 1f);
                    float speedX = (Math.abs(velX) + 0.5f) * rallySpeed;
                    velX = speedX + (random.nextInt(100) == 2 ? 10f : 0f);
                    velY = hitPos * 6.0f;
                }
            }

            while (System.nanoTime() < time + 16667000L) Thread.onSpinWait();
        }
    }

}
