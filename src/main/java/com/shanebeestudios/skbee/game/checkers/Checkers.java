package com.shanebeestudios.skbee.game.checkers;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

public class Checkers {

    private int difficulty = 1;

    public Checkers() {
        var frame = new JFrame("Checkers \uD83D\uDE0E");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().setPreferredSize(new Dimension(640, 640));
        frame.pack();

        Image icon = createCheckersIcon(512);
        frame.setIconImage(icon);
        try { if(Taskbar.isTaskbarSupported()){Taskbar tb=Taskbar.getTaskbar();if(tb.isSupported(Taskbar.Feature.ICON_IMAGE))tb.setIconImage(icon);} } catch(Exception ignored){}

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation((int)((screen.getWidth()-frame.getWidth())/2),(int)((screen.getHeight()-frame.getHeight())/2));
        frame.setResizable(false);

        // ── Splash ───────────────────────────────────────────────────
        final long[] animStart = {System.currentTimeMillis()};
        final boolean[] splashDone = {false};
        final boolean[] fadingOut = {false};
        final long[] fadeOutStart = {0};

        Color BG       = new Color(8, 8, 20);
        Color CYAN     = new Color(0, 255, 240);
        Color MAGENTA  = new Color(255, 0, 200);
        Color[] dCols  = {new Color(0,255,100), new Color(255,200,0), new Color(255,60,60)};
        String[] dNames = Game.DIFF_NAMES;

        JPanel splash = new JPanel(null) {
            @Override public void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                int W = getWidth(), H = getHeight();
                long now = System.currentTimeMillis();
                long elapsed = now - animStart[0];
                final long BLACK_HOLD = 500L, FADE_IN = 700L;

                g2.setColor(Color.BLACK); g2.fillRect(0,0,W,H);

                float master;
                if (fadingOut[0]) {
                    master = Math.max(0f, 1f - (now - fadeOutStart[0]) / 500f);
                    if (master <= 0f) { setVisible(false); return; }
                } else if (elapsed < BLACK_HOLD) { return;
                } else { master = Math.min(1f, (elapsed - BLACK_HOLD) / (float) FADE_IN); }

                long ae = Math.max(0, elapsed - BLACK_HOLD);

                // BG
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, master));
                g2.setColor(BG); g2.fillRect(0,0,W,H);

                // Scanlines
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, clamp((ae-100)/400f)*master));
                g2.setColor(new Color(0,0,0,50));
                for (int y=0;y<H;y+=4) g2.drawLine(0,y,W,y);

                // Corner brackets
                float ba = clamp((ae-200)/400f);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, ba*master));
                g2.setStroke(new BasicStroke(1.2f));
                int bx=38,by=44,bw=60,bh=40;
                g2.setColor(new Color(0,255,240,130));
                g2.drawLine(bx,by+bh,bx,by); g2.drawLine(bx,by,bx+bw,by);
                g2.drawLine(W-bx-bw,by,W-bx,by); g2.drawLine(W-bx,by,W-bx,by+bh);
                g2.setColor(new Color(255,0,200,130));
                g2.drawLine(bx,H-by-bh,bx,H-by); g2.drawLine(bx,H-by,bx+bw,H-by);
                g2.drawLine(W-bx-bw,H-by,W-bx,H-by); g2.drawLine(W-bx,H-by,W-bx,H-by-bh);
                g2.setStroke(new BasicStroke(1));

                // Decorative mini board
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, ba*master));
                int bs=80, bsx=W/2-bs/2, bsy=H/2-145;
                for (int r=0;r<4;r++) for (int c=0;c<4;c++) {
                    g2.setColor((r+c)%2==0?new Color(28,30,55):new Color(8,8,20));
                    g2.fillRect(bsx+c*(bs/4), bsy+r*(bs/4), bs/4, bs/4);
                }
                g2.setColor(new Color(255,0,200,120)); g2.setStroke(new BasicStroke(1.2f));
                g2.drawRect(bsx,bsy,bs,bs); g2.setStroke(new BasicStroke(1));

                // Title "CHECKERS" type-in
                long typeStart = 400;
                g2.setFont(new Font("Monospaced", Font.BOLD, 58));
                FontMetrics fm = g2.getFontMetrics();
                String TITLE = "CHECKERS";
                int charsV = (int)Math.clamp((ae-typeStart)/80, 0, TITLE.length());
                if (charsV > 0) {
                    String partial = TITLE.substring(0, charsV);
                    float flicker = charsV < TITLE.length() ? (0.6f+0.4f*(float)Math.sin(ae*0.03)) : 1f;
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, clamp((ae-typeStart)/200f)*flicker*master));
                    int tx = W/2-fm.stringWidth(TITLE)/2, ty = H/2-20;
                    // glow
                    for (int l=5;l>=1;l--) {
                        g2.setColor(new Color(MAGENTA.getRed(),MAGENTA.getGreen(),MAGENTA.getBlue(),14*l));
                        g2.drawString(partial,tx+l,ty+l); g2.drawString(partial,tx-l,ty-l);
                    }
                    g2.setColor(MAGENTA); g2.drawString(partial,tx,ty);
                    if (charsV<TITLE.length()&&(ae/120)%2==0) {
                        g2.setColor(MAGENTA);
                        g2.fillRect(tx+fm.stringWidth(partial)+4,ty-fm.getAscent()+4,6,fm.getAscent());
                    }
                }

                // Difficulty selector
                long subStart = typeStart + TITLE.length()*80L + 300;
                if (ae > subStart) {
                    float sa = clamp((ae-subStart)/300f)*master;
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, sa));
                    g2.setFont(new Font("Monospaced",Font.BOLD,13));
                    FontMetrics dfm = g2.getFontMetrics();
                    int arrowW=dfm.stringWidth("<"), gap=14;
                    String dLabel="DIFFICULTY:";
                    g2.setColor(new Color(200,200,200,200));
                    g2.drawString(dLabel, W/2-dfm.stringWidth(dLabel)/2, H-160);
                    String dName = dNames[difficulty];
                    Color dCol = dCols[difficulty];
                    int slotW=dfm.stringWidth("MEDIUM"), nameW=dfm.stringWidth(dName);
                    int startX=W/2-(arrowW+gap+slotW+gap+arrowW)/2;
                    int nameX=startX+arrowW+gap+(slotW-nameW)/2;
                    int rightAX=startX+arrowW+gap+slotW+gap;
                    g2.setColor(difficulty>0?new Color(200,200,200,200):new Color(100,100,100,80));
                    g2.drawString("<",startX,H-140);
                    for (int l=3;l>=1;l--) {
                        g2.setColor(new Color(dCol.getRed(),dCol.getGreen(),dCol.getBlue(),20*l));
                        g2.drawString(dName,nameX+l,H-140+l); g2.drawString(dName,nameX-l,H-140-l);
                    }
                    g2.setColor(dCol); g2.drawString(dName,nameX,H-140);
                    g2.setColor(difficulty<2?new Color(200,200,200,200):new Color(100,100,100,80));
                    g2.drawString(">",rightAX,H-140);

                    // Nav hint
                    g2.setFont(new Font("Monospaced",Font.PLAIN,11));
                    FontMetrics hfm=g2.getFontMetrics();
                    String hint="\u25c4  \u25ba  change difficulty";
                    g2.setColor(new Color(150,150,150,140));
                    g2.drawString(hint,W/2-hfm.stringWidth(hint)/2,H-112);

                    // Press any key
                    boolean blink=splashDone[0]&&((ae/500)%2==0);
                    float pa=splashDone[0]?(blink?0.65f:0.3f):clamp((ae-subStart)/300f)*0.55f;
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, pa*master));
                    g2.setFont(new Font("Monospaced",Font.PLAIN,13));
                    FontMetrics sfm=g2.getFontMetrics();
                    String sub="PRESS ANY KEY TO START";
                    g2.setColor(Color.WHITE);
                    g2.drawString(sub,W/2-sfm.stringWidth(sub)/2,H-78);
                    if (!splashDone[0]&&ae>subStart+400) splashDone[0]=true;
                }
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1f));
            }
            private float clamp(float v){return Math.clamp(v,0f,1f);}
        };
        splash.setBackground(BG);
        splash.setFocusable(true);

        frame.add(splash);
        frame.setVisible(true);
        frame.setLayout(null);

        Timer animTimer = new Timer(16, e -> { if (splash.isVisible()) splash.repaint(); });
        animTimer.start();

        splash.requestFocus();
        splash.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (!splashDone[0] || fadingOut[0]) return;
                if (e.getKeyCode()==KeyEvent.VK_ESCAPE) { animTimer.stop(); frame.dispose(); new com.shanebeestudios.skbee.game.GamesMain(); return; }
                if (e.getKeyCode()==KeyEvent.VK_LEFT)  { difficulty=Math.max(0,difficulty-1); splash.repaint(); return; }
                if (e.getKeyCode()==KeyEvent.VK_RIGHT) { difficulty=Math.min(2,difficulty+1); splash.repaint(); return; }
                // Any other key — fade out and start game
                fadingOut[0]=true; fadeOutStart[0]=System.currentTimeMillis();
                new Timer(520, ev -> {
                    animTimer.stop();
                    splash.setVisible(false);
                    frame.remove(splash);
                    var panel = new Game(640);
                    panel.difficulty = difficulty;
                    panel.setSize(640, 640);
                    panel.onReturnToMenu = () -> {
                        frame.remove(panel);
                        animStart[0] = System.currentTimeMillis();
                        splashDone[0] = false;
                        fadingOut[0] = false;
                        splash.setVisible(true);
                        frame.add(splash);
                        frame.revalidate(); frame.repaint();
                        animTimer.start();
                        SwingUtilities.invokeLater(splash::requestFocus);
                    };
                    frame.add(panel);
                    panel.addMouseListener(panel);
                    frame.revalidate(); frame.repaint();
                    SwingUtilities.invokeLater(panel::requestFocus);
                    ((Timer)ev.getSource()).stop();
                }){{setRepeats(false);}}.start();
            }
        });
    }

    public static Image createCheckersIcon(int size) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(8, 8, 20));
        g.fillRoundRect(0, 0, size, size, size / 6, size / 6);
        g.setColor(new Color(0, 0, 0, 60));
        for (int y = 0; y < size; y += Math.max(2, size / 32)) g.drawLine(0, y, size, y);
        int pad = size / 8, boardSize = size - pad * 2, cellSize = boardSize / 4;
        Color dark = new Color(20, 20, 40), light = new Color(50, 50, 80);
        for (int row = 0; row < 4; row++)
            for (int col = 0; col < 4; col++) {
                g.setColor((row + col) % 2 == 0 ? light : dark);
                g.fillRect(pad + col * cellSize, pad + row * cellSize, cellSize, cellSize);
            }
        for (int i = 3; i >= 1; i--) { g.setColor(new Color(255,0,200,18*i)); g.setStroke(new BasicStroke(i*1.5f)); g.drawRect(pad-i,pad-i,boardSize+i*2,boardSize+i*2); }
        g.setStroke(new BasicStroke(1)); g.setColor(new Color(255,0,200,180)); g.drawRect(pad,pad,boardSize,boardSize);
        int pieceR = cellSize / 2 - 4;
        int[][] cyanPieces = {{0,1},{0,3}}, magentaPieces = {{3,0},{3,2}};
        for (int[] pos : cyanPieces) {
            int px = pad+pos[1]*cellSize+cellSize/2, py = pad+pos[0]*cellSize+cellSize/2;
            for (int i=2;i>=1;i--){g.setColor(new Color(0,255,240,20*i));g.fillOval(px-pieceR-i,py-pieceR-i,pieceR*2+i*2,pieceR*2+i*2);}
            g.setColor(new Color(0,200,190)); g.fillOval(px-pieceR,py-pieceR,pieceR*2,pieceR*2);
            g.setColor(new Color(0,255,240,180)); g.drawOval(px-pieceR,py-pieceR,pieceR*2,pieceR*2);
        }
        for (int[] pos : magentaPieces) {
            int px = pad+pos[1]*cellSize+cellSize/2, py = pad+pos[0]*cellSize+cellSize/2;
            for (int i=2;i>=1;i--){g.setColor(new Color(255,0,200,20*i));g.fillOval(px-pieceR-i,py-pieceR-i,pieceR*2+i*2,pieceR*2+i*2);}
            g.setColor(new Color(200,0,160)); g.fillOval(px-pieceR,py-pieceR,pieceR*2,pieceR*2);
            g.setColor(new Color(255,0,200,180)); g.drawOval(px-pieceR,py-pieceR,pieceR*2,pieceR*2);
        }
        g.dispose();
        return img;
    }
}
