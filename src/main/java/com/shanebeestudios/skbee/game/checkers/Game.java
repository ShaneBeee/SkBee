package com.shanebeestudios.skbee.game.checkers;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class Game extends JPanel implements MouseInputListener {
    public ArrayList<int[]> moves = new ArrayList<>();
    public Piece[] pieces = new Piece[64];
    public int selectedX = -1;
    public int selectedY = -1;
    public int offset;
    private int redCount = 12, blueCount = 12;
    private String winner = null;
    public int difficulty = 1;
    public static final String[] DIFF_NAMES = {"EASY", "MEDIUM", "HARD"};
    public Runnable onReturnToMenu = null; // called when player clicks on win screen

    public Game(int realHeight) {
        populateTiles();
        offset = 0;
        setBackground(new Color(8, 8, 20));
        // Timer to drive the blinking prompt on win screen
        new javax.swing.Timer(500, e -> {
            if (winner != null) repaint();
        }).start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Centre the board in the panel
        int cellSize = Math.min(getWidth(), getHeight()) / 8;
        int boardPx = cellSize * 8;
        int ox = (getWidth() - boardPx) / 2;
        int oy = (getHeight() - boardPx) / 2;
        offset = oy; // keep offset in sync for click mapping

        renderTiles(g2, cellSize, ox, oy);
        renderPieces(g2, cellSize, ox, oy);

        // Scanlines over everything
        g2.setColor(new Color(0, 0, 0, 35));
        for (int y = 0; y < getHeight(); y += 4) g2.drawLine(0, y, getWidth(), y);

        // Outer border glow
        for (int i = 3; i >= 1; i--) {
            g2.setColor(new Color(0, 255, 240, 12 * i));
            g2.setStroke(new BasicStroke(i * 1.5f));
            g2.drawRect(i, i, getWidth() - i * 2, getHeight() - i * 2);
        }
        g2.setStroke(new BasicStroke(1));
        g2.setColor(new Color(0, 255, 240, 80));
        g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

        // Win overlay
        if (winner != null) {
            boolean playerWon = winner.equals("YOU WIN!");
            Color wc = playerWon ? new Color(0, 255, 240) : new Color(255, 0, 200);
            // Dark veil
            g2.setColor(new Color(8, 8, 20, 210));
            g2.fillRect(0, 0, getWidth(), getHeight());
            // Scanlines on veil
            g2.setColor(new Color(0, 0, 0, 50));
            for (int y2 = 0; y2 < getHeight(); y2 += 4) g2.drawLine(0, y2, getWidth(), y2);
            // Neon box
            int bw = 360, bh = 180;
            int bx = getWidth() / 2 - bw / 2, by = getHeight() / 2 - bh / 2;
            g2.setColor(new Color(wc.getRed(), wc.getGreen(), wc.getBlue(), 30));
            g2.fillRoundRect(bx, by, bw, bh, 16, 16);
            g2.setColor(new Color(wc.getRed(), wc.getGreen(), wc.getBlue(), 140));
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(bx, by, bw, bh, 16, 16);
            g2.setStroke(new BasicStroke(1));
            // Winner text with glow
            g2.setFont(new Font("Monospaced", Font.BOLD, 64));
            FontMetrics fm = g2.getFontMetrics();
            int tx = getWidth() / 2 - fm.stringWidth(winner) / 2;
            int ty = getHeight() / 2 - 10;
            for (int l = 5; l >= 1; l--) {
                g2.setColor(new Color(wc.getRed(), wc.getGreen(), wc.getBlue(), 16 * l));
                g2.drawString(winner, tx + l, ty + l);
                g2.drawString(winner, tx - l, ty - l);
            }
            g2.setColor(wc);
            g2.drawString(winner, tx, ty);
            // Score line
            g2.setFont(new Font("Monospaced", Font.PLAIN, 16));
            fm = g2.getFontMetrics();
            String scoreStr = "RED: " + redCount + "  BLUE: " + blueCount;
            g2.setColor(new Color(200, 200, 200, 200));
            g2.drawString(scoreStr, getWidth() / 2 - fm.stringWidth(scoreStr) / 2, getHeight() / 2 + 44);
            // Blinking prompt
            if ((System.currentTimeMillis() / 500) % 2 == 0) {
                g2.setFont(new Font("Monospaced", Font.PLAIN, 13));
                fm = g2.getFontMetrics();
                String prompt = "CLICK TO RETURN TO MENU";
                g2.setColor(new Color(180, 180, 180, 180));
                g2.drawString(prompt, getWidth() / 2 - fm.stringWidth(prompt) / 2, getHeight() / 2 + 76);
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        int cellSize = Math.min(getWidth(), getHeight()) / 8;
        int ox = (getWidth() - cellSize * 8) / 2;
        var x = (e.getX() - ox) / cellSize;
        var y = (e.getY() - offset) / cellSize;
        if (x < 0 || x > 7 || y < 0 || y > 7) return;
        if (winner != null) {
            if (onReturnToMenu != null) onReturnToMenu.run();
            return;
        }

        var selectedPiece = getSelectedPiece();
        if (!selectedPiece.isNull() && hasMove(x, y)) {
            boolean wasCapture = Math.abs(selectedPiece.x - x) == 2;
            if (wasCapture) {
                var dx = (x - selectedPiece.x) / 2;
                var dy = (y - selectedPiece.y) / 2;
                clearPiece(getPiece(selectedPiece.x + dx, selectedPiece.y + dy));
            }

            if (y == 0) selectedPiece.type = PieceType.QUEEN_BEE;
            movePiece(selectedPiece, x, y);
            checkWin();
            if (winner != null) {
                repaint();
                return;
            }

            // Multi-jump: if this was a capture and more captures exist, keep turn
            if (wasCapture) {
                var jumpMoves = new ArrayList<int[]>();
                for (var move : selectedPiece.getMoves(this))
                    if (Math.abs(selectedPiece.x - move[0]) == 2) jumpMoves.add(move);
                if (!jumpMoves.isEmpty()) {
                    selectedX = x;
                    selectedY = y;
                    moves = jumpMoves;
                    repaint();
                    return;
                }
            }

            selectedX = -1;
            selectedY = -1;
            moves.clear();

            // AI turn — behaviour depends on difficulty
            if (difficulty == 0) {
                // Easy: pure random, ignores captures
                var hasPiece = false;
                for (var piece : pieces)
                    if (piece.color == PieceColor.BLUE && !piece.getMoves(this).isEmpty()) {
                        hasPiece = true;
                        break;
                    }
                while (hasPiece) {
                    var piece = pieces[(int) (Math.random() * 64)];
                    if (piece.color != PieceColor.BLUE) continue;
                    var aiMoves = piece.getMoves(this);
                    if (aiMoves.isEmpty()) continue;
                    var move = aiMoves.get((int) (Math.random() * aiMoves.size()));
                    if (move[1] == 7) piece.type = PieceType.QUEEN_BEE;
                    movePiece(piece, move[0], move[1]);
                    checkWin();
                    break;
                }
            } else if (difficulty == 1) {
                // Medium: prefer captures, multi-jump
                var allBlueCaptures = getAllCaptures(PieceColor.BLUE);
                if (!allBlueCaptures.isEmpty()) {
                    var cap = allBlueCaptures.get((int) (Math.random() * allBlueCaptures.size()));
                    var aiPiece = getPiece(cap[0], cap[1]);
                    int tx = cap[2], ty = cap[3];
                    clearPiece(getPiece(aiPiece.x + (tx - aiPiece.x) / 2, aiPiece.y + (ty - aiPiece.y) / 2));
                    if (ty == 7) aiPiece.type = PieceType.QUEEN_BEE;
                    movePiece(aiPiece, tx, ty);
                    checkWin();
                    while (winner == null) {
                        var jmoves = new ArrayList<int[]>();
                        for (var mv : aiPiece.getMoves(this)) if (Math.abs(aiPiece.x - mv[0]) == 2) jmoves.add(mv);
                        if (jmoves.isEmpty()) break;
                        var jm = jmoves.get((int) (Math.random() * jmoves.size()));
                        clearPiece(getPiece(aiPiece.x + (jm[0] - aiPiece.x) / 2, aiPiece.y + (jm[1] - aiPiece.y) / 2));
                        if (jm[1] == 7) aiPiece.type = PieceType.QUEEN_BEE;
                        movePiece(aiPiece, jm[0], jm[1]);
                        checkWin();
                    }
                } else {
                    var hasPiece = false;
                    for (var piece : pieces)
                        if (piece.color == PieceColor.BLUE && !piece.getMoves(this).isEmpty()) {
                            hasPiece = true;
                            break;
                        }
                    while (hasPiece) {
                        var piece = pieces[(int) (Math.random() * 64)];
                        if (piece.color != PieceColor.BLUE) continue;
                        var aiMoves = piece.getMoves(this);
                        if (aiMoves.isEmpty()) continue;
                        var move = aiMoves.get((int) (Math.random() * aiMoves.size()));
                        if (move[1] == 7) piece.type = PieceType.QUEEN_BEE;
                        movePiece(piece, move[0], move[1]);
                        checkWin();
                        break;
                    }
                }
            } else {
                // Hard: prefer captures + avoid leaving pieces exposed
                var allBlueCaptures = getAllCaptures(PieceColor.BLUE);
                if (!allBlueCaptures.isEmpty()) {
                    // Same as medium for captures
                    var cap = allBlueCaptures.get((int) (Math.random() * allBlueCaptures.size()));
                    var aiPiece = getPiece(cap[0], cap[1]);
                    int tx = cap[2], ty = cap[3];
                    clearPiece(getPiece(aiPiece.x + (tx - aiPiece.x) / 2, aiPiece.y + (ty - aiPiece.y) / 2));
                    if (ty == 7) aiPiece.type = PieceType.QUEEN_BEE;
                    movePiece(aiPiece, tx, ty);
                    checkWin();
                    while (winner == null) {
                        var jmoves = new ArrayList<int[]>();
                        for (var mv : aiPiece.getMoves(this)) if (Math.abs(aiPiece.x - mv[0]) == 2) jmoves.add(mv);
                        if (jmoves.isEmpty()) break;
                        var jm = jmoves.get((int) (Math.random() * jmoves.size()));
                        clearPiece(getPiece(aiPiece.x + (jm[0] - aiPiece.x) / 2, aiPiece.y + (jm[1] - aiPiece.y) / 2));
                        if (jm[1] == 7) aiPiece.type = PieceType.QUEEN_BEE;
                        movePiece(aiPiece, jm[0], jm[1]);
                        checkWin();
                    }
                } else {
                    // Hard: pick the safest move — one that doesn't expose piece to capture
                    var allMoves = new ArrayList<int[]>(); // {px,py,tx,ty}
                    for (var piece : pieces) {
                        if (piece.color != PieceColor.BLUE) continue;
                        for (var mv : piece.getMoves(this))
                            allMoves.add(new int[]{piece.x, piece.y, mv[0], mv[1]});
                    }
                    if (!allMoves.isEmpty()) {
                        // Filter: prefer moves that don't land where RED can capture next turn
                        var safeMoves = new ArrayList<int[]>();
                        for (var mv : allMoves) {
                            // Simulate the move
                            var aiPiece = getPiece(mv[0], mv[1]);
                            movePiece(aiPiece, mv[2], mv[3]);
                            var redCaptures = getAllCaptures(PieceColor.RED);
                            // Undo
                            movePiece(aiPiece, mv[0], mv[1]);
                            if (redCaptures.isEmpty()) safeMoves.add(mv);
                        }
                        var chosen = safeMoves.isEmpty() ? allMoves : safeMoves;
                        var mv = chosen.get((int) (Math.random() * chosen.size()));
                        var aiPiece = getPiece(mv[0], mv[1]);
                        if (mv[3] == 7) aiPiece.type = PieceType.QUEEN_BEE;
                        movePiece(aiPiece, mv[2], mv[3]);
                        checkWin();
                    }
                }
            }

            repaint();
            return;
        }

        if (selectedX != x || selectedY != y) {
            selectedX = x;
            selectedY = y;
            moves.clear();
            var piece = getPiece(x, y);
            if (!piece.isNull() && piece.color == PieceColor.RED)
                moves = getValidMoves(piece);
        } else {
            selectedX = -1;
            selectedY = -1;
            moves.clear();
        }

        repaint();
    }

    private void populateTiles() {
        for (int i = 0; i < 64; i++) {
            var y = i / 8;
            var x = i - y * 8;

            pieces[i] = new Piece(x, y);

            if (i <= 23 && (x + y) % 2 == 1)
                pieces[i] = new Piece(PieceType.WORKER_BEE, PieceColor.BLUE, x, y);

            if (i >= 40 && (x + y) % 2 == 1)
                pieces[i] = new Piece(PieceType.WORKER_BEE, PieceColor.RED, x, y);
        }
    }

    private void renderPieces(Graphics2D g2, int cellSize, int ox, int oy) {
        for (Piece piece : pieces) {
            if (piece.isNull()) continue;
            var image = switch (piece.color) {
                case RED -> piece.type.red;
                case BLUE -> piece.type.blue;
            };
            g2.drawImage(image, ox + piece.x * cellSize, oy + piece.y * cellSize, cellSize, cellSize, null);
        }
    }

    private void renderTiles(Graphics2D g2, int cellSize, int ox, int oy) {
        Color darkSquare = new Color(8, 8, 20);
        Color lightSquare = new Color(28, 30, 55);
        Color selectedCol = new Color(0, 255, 240, 60);
        Color moveCol = new Color(255, 0, 200, 45);
        int dotR = Math.max(4, cellSize / 8);

        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                int px = ox + x * cellSize, py = oy + y * cellSize;

                g2.setColor((x + y) % 2 == 0 ? lightSquare : darkSquare);
                g2.fillRect(px, py, cellSize, cellSize);

                if (x == selectedX && y == selectedY) {
                    g2.setColor(selectedCol);
                    g2.fillRect(px, py, cellSize, cellSize);
                    g2.setColor(new Color(0, 255, 240, 200));
                    g2.setStroke(new BasicStroke(2));
                    g2.drawRect(px + 1, py + 1, cellSize - 2, cellSize - 2);
                    g2.setStroke(new BasicStroke(1));
                }

                if (hasMove(x, y)) {
                    g2.setColor(moveCol);
                    g2.fillRect(px, py, cellSize, cellSize);
                    int cx = px + cellSize / 2, cy = py + cellSize / 2;
                    for (int l = 3; l >= 1; l--) {
                        g2.setColor(new Color(255, 0, 200, 18 * l));
                        g2.fillOval(cx - dotR - l, cy - dotR - l, (dotR + l) * 2, (dotR + l) * 2);
                    }
                    g2.setColor(new Color(255, 0, 200, 200));
                    g2.fillOval(cx - dotR, cy - dotR, dotR * 2, dotR * 2);
                }

                g2.setColor(new Color(255, 255, 255, 8));
                g2.drawRect(px, py, cellSize, cellSize);
            }
        }
    }

    /**
     * Returns all capture moves available for a given color across all pieces.
     */
    private ArrayList<int[]> getAllCaptures(PieceColor color) {
        var captures = new ArrayList<int[]>();
        for (var piece : pieces) {
            if (piece.color != color) continue;
            for (var move : piece.getMoves(this)) {
                if (Math.abs(piece.x - move[0]) == 2)
                    captures.add(new int[]{piece.x, piece.y, move[0], move[1]});
            }
        }
        return captures;
    }

    /**
     * Returns only capture moves for a specific piece, or all moves if no captures exist for that color.
     */
    private ArrayList<int[]> getValidMoves(Piece piece) {
        var allCaptures = getAllCaptures(PieceColor.RED);
        var pieceMoves = piece.getMoves(this);
        if (allCaptures.isEmpty()) return pieceMoves;
        var forced = new ArrayList<int[]>();
        for (var move : pieceMoves)
            if (Math.abs(piece.x - move[0]) == 2) forced.add(move);
        return forced;
    }

    private Piece getSelectedPiece() {
        if (selectedX == -1 && selectedY == -1)
            return new Piece(selectedX, selectedY);
        return getPiece(selectedX, selectedY);
    }

    private boolean hasMove(int x, int y) {
        for (var move : moves) {
            if (move[0] == x && move[1] == y)
                return true;
        }

        return false;
    }

    private void movePiece(Piece piece, int x, int y) {
        pieces[y * 8 + x] = piece;
        // Just clear the old square without affecting piece counts
        pieces[piece.y * 8 + piece.x] = new Piece(piece.x, piece.y);
        piece.x = x;
        piece.y = y;
    }

    private void clearPiece(Piece piece) {
        pieces[piece.y * 8 + piece.x] = new Piece(piece.x, piece.y);
        if (piece.isNull()) return;
        if (piece.color == PieceColor.RED) redCount--;
        else if (piece.color == PieceColor.BLUE) blueCount--;
    }

    private void checkWin() {
        if (blueCount <= 0) winner = "YOU WIN!";
        if (redCount <= 0) winner = "BLUE WINS!";
    }

    public Piece getPiece(int x, int y) {
        return pieces[y * 8 + x];
    }

    // boilerplate
    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

}
