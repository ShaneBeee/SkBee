package com.shanebeestudios.skbee.checkers;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class Game extends JPanel implements MouseInputListener {
    public ArrayList<int[]> moves = new ArrayList<>();
    public Piece[] pieces = new Piece[64];
    public int selectedX = -1;
    public int selectedY = -1;
    public int offset;

    public Game(int realHeight) {
        populateTiles();

        offset = realHeight - 512;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        renderTiles(g);
        renderPieces(g);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        var x = e.getX() / 64;
        var y = (e.getY() - offset) / 64;

        var selectedPiece = getSelectedPiece();
        if (!selectedPiece.isNull() && hasMove(x, y)) {
            if (Math.abs(selectedPiece.x - x) == 2) {
                // Capture!
                var directionX = (x - selectedX) / 2;
                var directionY = (y - selectedY) / 2;
                var capturedPiece = getPiece(selectedX + directionX, selectedY + directionY);

                clearPiece(capturedPiece);
            }

            if (y == 0)
                selectedPiece.type = PieceType.QUEEN_BEE;

            movePiece(selectedPiece, x, y);
            selectedX = -1;
            selectedY = -1;
            moves.clear();

            // not very smart "ai"
            var hasPiece = false;

            for (var piece : pieces) {
                if (piece.color == PieceColor.BLUE) {
                    hasPiece = true;
                    break;
                }
            }

            while (hasPiece) {
                var piece = pieces[(int) Math.floor(Math.random() * 64)];

                if (piece.color != PieceColor.BLUE)
                    continue;

                var moves = piece.getMoves(this);

                if (moves.isEmpty())
                    continue;

                var move = moves.get((int) Math.floor(Math.random() * moves.size()));
                if (Math.abs(piece.x - move[0]) == 2) {
                    // Capture!
                    var directionX = (move[0] - piece.x) / 2;
                    var directionY = (move[1] - piece.y) / 2;
                    var capturedPiece = getPiece(piece.x + directionX, piece.y + directionY);

                    clearPiece(capturedPiece);
                }

                if (move[1] == 7)
                    piece.type = PieceType.QUEEN_BEE;

                movePiece(piece, move[0], move[1]);

                break;
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
                moves = piece.getMoves(this);
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

    private void renderPieces(Graphics graphics) {
        for (Piece piece : pieces) {
            if (piece.isNull())
                continue;

            var image = switch (piece.color) {
                case RED -> piece.type.red;
                case BLUE -> piece.type.blue;
            };

            graphics.drawImage(image, piece.x * 64, piece.y * 64, 64, 64, null);
        }
    }

    private void renderTiles(Graphics graphics) {
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                graphics.setColor((x + y) % 2 == 0 ? Color.YELLOW : Color.BLACK);

                if (hasMove(x, y))
                    graphics.setColor(Color.RED);

                if (x == selectedX && y == selectedY)
                    graphics.setColor(Color.GREEN);

                graphics.fillRect(x * 64, y * 64, 64, 64);
            }
        }
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
        clearPiece(piece);
        piece.x = x;
        piece.y = y;
    }

    private void clearPiece(Piece piece) {
        pieces[piece.y * 8 + piece.x] = new Piece(piece.x, piece.y);
    }

    public Piece getPiece(int x, int y) {
        return pieces[y * 8 + x];
    }

    // boilerplate
    @Override public void mouseDragged(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
    @Override public void mouseMoved(MouseEvent e) {}
    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
}
