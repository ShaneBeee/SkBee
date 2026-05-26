package com.shanebeestudios.skbee.game.pong;

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

    // 0=Easy, 1=Medium, 2=Hard
    private int difficulty = 1;
    private static final String[] DIFF_NAMES = {"EASY", "MEDIUM", "HARD"};
    // AI speed (px/frame) and positional error (how far the AI aims from ball centre)
    private static final int[] DIFF_SPEED = {2, 4, 6};
    private static final int[] DIFF_ERROR = {55, 18, 0};

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

    public static void main(String[] args) {
        new Pong();
    }

    public Pong() {
        JFrame frame = new JFrame("SkBee Pong!");
        frame.setSize(640, 640);
        frame.setResizable(false);
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(
            (int) ((screen.getWidth() - frame.getWidth()) / 2),
            (int) ((screen.getHeight() - frame.getHeight()) / 2)
        );

        JPanel rootPane = new JPanel(null);
        rootPane.setBackground(BG_COLOR);

        // ── Game components ───────────────────────────────────────────
        final int TRAIL_LEN = 10;
        Deque<Point> trail = new ArrayDeque<>(TRAIL_LEN + 1);

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
                // Difficulty indicator
                Color[] dCols = {new Color(0, 255, 100, 200), new Color(255, 200, 0, 200), new Color(255, 80, 80, 200)};
                g2.setFont(new Font("Monospaced", Font.PLAIN, 12));
                fm = g2.getFontMetrics();
                String dStr = "DIFFICULTY: " + DIFF_NAMES[difficulty];
                g2.setColor(dCols[difficulty]);
                g2.drawString(dStr, getWidth() / 2 - fm.stringWidth(dStr) / 2, getHeight() / 2 + 92);
            }
        };

        // ── Splash screen ─────────────────────────────────────────────
        // Animation state (all driven off animTick, updated by a javax.swing.Timer)
        final long[] animStart = {System.currentTimeMillis()};
        final boolean[] splashDone = {false};   // animation finished, waiting for key
        final boolean[] splashFadingOut = {false};   // key pressed, fading out
        final long[] fadeOutStart = {0};

        JComponent splash = new JComponent() {
            // "SKBEE" type-in cursor
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

                // Phase 1: hold pure black for 500ms
                // Phase 2: fade from black into splash over 700ms
                // Phase 3: full splash visible, animations run (elapsed offset by 1200ms)
                final long BLACK_HOLD = 500L;
                final long FADE_IN = 700L;

                // Always paint black base first
                g2.setColor(Color.BLACK);
                g2.fillRect(0, 0, W, H);

                // Master alpha: 0 during hold, ramps up during fade-in, 1 after
                float masterAlpha;
                if (splashFadingOut[0]) {
                    float t = (now - fadeOutStart[0]) / 500f;
                    masterAlpha = Math.max(0f, 1f - t);
                    if (masterAlpha <= 0f) {
                        setVisible(false);
                        return;
                    }
                } else if (elapsed < BLACK_HOLD) {
                    return; // pure black, nothing to draw yet
                } else {
                    masterAlpha = Math.min(1f, (elapsed - BLACK_HOLD) / (float) FADE_IN);
                }

                // Shift animation clock so t=0 is when the fade starts becoming visible
                long animElapsed = Math.max(0, elapsed - BLACK_HOLD);

                // Background (dark navy fades in over masterAlpha)
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, masterAlpha));
                g2.setColor(BG_COLOR);
                g2.fillRect(0, 0, W, H);

                // Scanlines
                float scanAlpha = clamp((animElapsed - 100) / 400f);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, scanAlpha * masterAlpha));
                g2.setColor(new Color(0, 0, 0, 50));
                for (int y = 0; y < H; y += 4) g2.drawLine(0, y, W, y);

                // Corner brackets — fade in at 200ms of animElapsed
                float bracketAlpha = clamp((animElapsed - 200) / 400f);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, bracketAlpha * masterAlpha));
                g2.setStroke(new BasicStroke(1.2f));
                int bx = 38, by = 44, bw = 60, bh = 40;
                // top-left
                g2.setColor(new Color(0, 255, 240, 130));
                g2.drawLine(bx, by + bh, bx, by);
                g2.drawLine(bx, by, bx + bw, by);
                // top-right
                g2.drawLine(W - bx - bw, by, W - bx, by);
                g2.drawLine(W - bx, by, W - bx, by + bh);
                // bottom-left
                g2.setColor(new Color(255, 0, 200, 130));
                g2.drawLine(bx, H - by - bh, bx, H - by);
                g2.drawLine(bx, H - by, bx + bw, H - by);
                // bottom-right
                g2.drawLine(W - bx - bw, H - by, W - bx, H - by);
                g2.drawLine(W - bx, H - by, W - bx, H - by - bh);


                // Decorative paddles — flank the title area
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, bracketAlpha * masterAlpha));
                int padH = 90, padW = 14, padY = H / 2 - padH / 2;
                g2.setPaint(new Color(0, 255, 240, 180));
                g2.fillRoundRect(50, padY, padW, padH, 6, 6);
                g2.setPaint(new Color(255, 0, 200, 180));
                g2.fillRoundRect(W - 50 - padW, padY, padW, padH, 6, 6);

                // ── "SKBEE" — type-in: one char every 80ms, starting at 400ms ────
                g2.setFont(new Font("Monospaced", Font.BOLD, 96));
                FontMetrics fm = g2.getFontMetrics();
                int charsVisible1 = (int) Math.clamp((animElapsed - 400) / 80, 0, TITLE1.length());
                if (charsVisible1 > 0) {
                    String partial = TITLE1.substring(0, charsVisible1);
                    // flicker on the last char while typing
                    float flicker = (charsVisible1 < TITLE1.length()) ? (0.6f + 0.4f * (float) Math.sin(animElapsed * 0.03)) : 1f;
                    float a1 = clamp((animElapsed - 400) / 200f) * flicker;
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, a1 * masterAlpha));
                    int x1 = W / 2 - fm.stringWidth(TITLE1) / 2;
                    int y1 = H / 2 - 30;
                    glowText(g2, PLAYER_COLOR, partial, x1, y1);
                    // blinking cursor while typing
                    if (charsVisible1 < TITLE1.length() && (animElapsed / 120) % 2 == 0) {
                        g2.setColor(PLAYER_COLOR);
                        g2.fillRect(x1 + fm.stringWidth(partial) + 4, y1 - fm.getAscent() + 4, 8, fm.getAscent());
                    }
                }

                // ── "PONG" — slam up from below, starting when SKBEE finishes ────
                long pongStart = 400 + TITLE1.length() * 80L + 80;
                if (animElapsed > pongStart) {
                    float t = clamp((animElapsed - pongStart) / 260f);
                    // bounce easing: overshoot then settle
                    float ease = bounce(t);
                    int targetY = H / 2 + 85;
                    int startY = H + 60; // slides up from below the screen
                    int currentY = (int) (startY + (targetY - startY) * ease);
                    // alpha fade in quickly
                    float a2 = clamp((animElapsed - pongStart) / 150f);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, a2 * masterAlpha));
                    int x2 = W / 2 - fm.stringWidth(TITLE2) / 2;
                    glowText(g2, AI_COLOR, TITLE2, x2, currentY);
                }

                // ── Difficulty selector — appears with subtitle ────────────────
                long subtitleStart = pongStart + 400;
                if (animElapsed > subtitleStart) {
                    float selAlpha = clamp((animElapsed - subtitleStart) / 300f) * masterAlpha;
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, selAlpha));

                    g2.setFont(new Font("Monospaced", Font.BOLD, 13));
                    FontMetrics dfm = g2.getFontMetrics();
                    String label = "DIFFICULTY:";
                    int labelW = dfm.stringWidth(label);
                    String dName = DIFF_NAMES[difficulty];
                    // pick colour per difficulty
                    Color[] dCols = {new Color(0, 255, 100), new Color(255, 200, 0), new Color(255, 60, 60)};
                    Color dCol = dCols[difficulty];

                    int rowY = H - 145;
                    // label
                    g2.setColor(new Color(200, 200, 200, 200));
                    g2.drawString(label, W / 2 - labelW / 2, rowY);

                    // Use the widest name as the fixed slot so arrows don't jump around
                    int slotW = dfm.stringWidth("MEDIUM");
                    int nameW = dfm.stringWidth(dName);
                    int arrowW = dfm.stringWidth("<");
                    int gap = 14; // px between arrow and name
                    int totalW = arrowW + gap + slotW + gap + arrowW;
                    int startX = W / 2 - totalW / 2;
                    int nameX = startX + arrowW + gap + (slotW - nameW) / 2; // centred in slot
                    int rightAX = startX + arrowW + gap + slotW + gap;

                    // left arrow
                    g2.setColor(difficulty > 0 ? new Color(200, 200, 200, 200) : new Color(100, 100, 100, 120));
                    g2.drawString("<", startX, rowY + 22);

                    // difficulty name with glow, centred in fixed slot
                    for (int l = 3; l >= 1; l--) {
                        g2.setColor(new Color(dCol.getRed(), dCol.getGreen(), dCol.getBlue(), 20 * l));
                        g2.drawString(dName, nameX + l, rowY + 22 + l);
                        g2.drawString(dName, nameX - l, rowY + 22 - l);
                    }
                    g2.setColor(dCol);
                    g2.drawString(dName, nameX, rowY + 22);

                    // right arrow
                    g2.setColor(difficulty < 2 ? new Color(200, 200, 200, 200) : new Color(100, 100, 100, 120));
                    g2.drawString(">", rightAX, rowY + 22);
                }

                // ── Subtitle "PRESS ANY KEY" — blinking, appears after everything ─
                if (animElapsed > subtitleStart) {
                    boolean blink = splashDone[0] && ((animElapsed / 500) % 2 == 0);
                    float subAlpha = splashDone[0] ? (blink ? 0.65f : 0.3f) : clamp((animElapsed - subtitleStart) / 300f) * 0.55f;
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, subAlpha * masterAlpha));
                    g2.setFont(new Font("Monospaced", Font.PLAIN, 13));
                    FontMetrics sfm = g2.getFontMetrics();
                    String sub = "PRESS ANY KEY TO START";
                    g2.setColor(Color.WHITE);
                    g2.drawString(sub, W / 2 - sfm.stringWidth(sub) / 2, H - 55);
                    // mark animation done so blink can begin
                    if (!splashDone[0] && animElapsed > subtitleStart + 400) splashDone[0] = true;
                }

                // Reset composite
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            }

            private float clamp(float v) {
                return Math.clamp(v, 0f, 1f);
            }

            /** Bounce easing: goes past 1 then settles back */
            private float bounce(float t) {
                if (t >= 1f) return 1f;
                // Simple overshoot spring
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

        // Splash sits alone on top — game components added only after splash exits
        splash.setSize(mid.width * 2, mid.height * 2);
        splash.setLocation(0, 0);
        rootPane.add(splash);

        ball.setLocation(mid.width - 10, mid.height - 10);
        playerPaddle.setLocation(20, mid.height - 50);
        aiPaddle.setLocation(mid.width * 2 - 40, mid.height - 50);

        // Animation timer: repaint splash at ~60fps
        final java.util.concurrent.atomic.AtomicBoolean gameStarted = new java.util.concurrent.atomic.AtomicBoolean(false);
        javax.swing.Timer animTimer = new javax.swing.Timer(16, e -> {
            if (splash.isVisible()) splash.repaint();
        });
        animTimer.start();

        final int[] addPlayerVel = {0};

        rootPane.addKeyListener(new KeyAdapter() {
            private final boolean[] fuckYouJava = new boolean[2];

            @Override
            public void keyPressed(KeyEvent e) {
                // While splash is visible and animation is done
                if (splash.isVisible() && splashDone[0] && !splashFadingOut[0]) {
                    // Left/right cycle difficulty — do NOT start the game
                    if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT) {
                        if (e.getKeyCode() == KeyEvent.VK_LEFT)
                            difficulty = Math.max(0, difficulty - 1);
                        else
                            difficulty = Math.min(2, difficulty + 1);
                        splash.repaint();
                        return;
                    }
                    // Any other key starts the game
                    splashFadingOut[0] = true;
                    fadeOutStart[0] = System.currentTimeMillis();
                    new javax.swing.Timer(520, ev -> {
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
                        rootPane.revalidate();
                        rootPane.repaint();
                        rootPane.requestFocus();
                        gameStarted.set(true);
                        animTimer.stop();
                        ((javax.swing.Timer) ev.getSource()).stop();
                    }) {{
                        setRepeats(false);
                    }}.start();
                    return;
                }
                if (splash.isVisible()) return; // animation still running, eat the key

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

            if (gameStarted.get() && !paused) {
                var loc = ball.getLocation();
                trail.addLast(new Point(loc.x, loc.y));
                if (trail.size() > TRAIL_LEN) trail.pollFirst();

                ball.setLocation((int) (loc.x + Math.ceil(velX)), (int) (loc.y + Math.ceil(velY)));
                ball.repaint();
                scoreDisplay.repaint();

                if (addPlayerVel[0] != 0) {
                    var pl = playerPaddle.getLocation();
                    playerPaddle.setLocation(pl.x, Math.clamp(pl.y + addPlayerVel[0], 0, mid.height * 2 - 100));
                }
                playerPaddle.repaint();

                var aiLoc = aiPaddle.getLocation();
                // Difficulty: speed cap + positional error so easy AI aims slightly off-centre
                int aiSpeed = DIFF_SPEED[difficulty];
                int aiError = DIFF_ERROR[difficulty];
                // On easy/medium the AI tracks a point offset from the true ball centre
                int aiTarget = loc.y - 50 + aiError;
                int aiDelta = Integer.compare(aiTarget, aiLoc.y) * aiSpeed;
                aiPaddle.setLocation(aiLoc.x, Math.clamp(aiLoc.y + aiDelta, 0, mid.height * 2 - 100));
                aiPaddle.repaint();

                if (loc.y < 10) velY = Math.abs(velY);
                if (loc.y > mid.height * 2 - 10) velY = -Math.abs(velY);

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
                    velX = -(Math.abs(velX) * random.nextFloat(1.0f) + (random.nextInt(100) == 2 ? 10f : 1f));
                    velY = ((loc.y + 10) - (aiLoc.y + 50)) / 5.0f;
                }
                if (playerPaddle.getBounds().intersects(ball.getBounds())) {
                    velX = (Math.abs(velX) * random.nextFloat(1.0f) + (random.nextInt(100) == 2 ? 10f : 1f));
                    velY = ((loc.y + 10) - (playerPaddle.getLocation().y + 50)) / 5.0f;
                }
            }

            while (System.nanoTime() < time + 16667000L) Thread.onSpinWait();
        }
    }

}
