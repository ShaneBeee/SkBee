package com.shanebeestudios.skbee.checkers;

import java.util.ArrayList;

public class Piece {
    public PieceType type;
    public PieceColor color;
    public int x;
    public int y;

    public Piece(int x, int y) {
        this(null, null, x, y);
    }

    public Piece(PieceType type, PieceColor color, int x, int y) {
        this.type = type;
        this.color = color;
        this.x = x;
        this.y = y;
    }

    // we don't actually need to look at this method, move on folks
    public ArrayList<int[]> getMoves(Game game) {
        var direction = color == PieceColor.RED ? -1 : 1;
        var moves = new ArrayList<int[]>();

        Piece piece = new Piece(-1, -1);

        if ((direction == -1 && y > 0) || (direction == 1 && y < 7)) {
            if (x > 0) piece = game.getPiece(x - 1, y + direction);
            if (x > 0 && piece.isNull())
                moves.add(new int[] {x - 1, y + direction});
            else if (x > 1 && ((direction == -1 && y > 1) || (direction == 1 && y < 6)) && game.getPiece(x - 2, y + direction * 2).isNull() && piece.color != color)
                moves.add(new int[] {x - 2, y + direction * 2});

            if (x < 7) piece = game.getPiece(x + 1, y + direction);
            if (x < 7 && piece.isNull())
                moves.add(new int[]{x + 1, y + direction});
            else if (x < 6 && ((direction == -1 && y > 1) || (direction == 1 && y < 6)) && game.getPiece(x + 2, y + direction * 2).isNull() && piece.color != color)
                moves.add(new int[]{x + 2, y + direction * 2});
        }

        if (type == PieceType.QUEEN_BEE && ((direction == -1 && y < 7) || (direction == 1 && y > 0))) {
            if (x > 0) piece = game.getPiece(x - 1, y - direction);
            if (x > 0 && piece.isNull())
                moves.add(new int[] {x - 1, y - direction});
            else if (x > 1 && ((-direction == -1 && y > 1) || (-direction == 1 && y < 6)) && game.getPiece(x - 2, y - direction * 2).isNull() && piece.color != color)
                moves.add(new int[] {x - 2, y - direction * 2});

            if (x < 7) piece = game.getPiece(x + 1, y - direction);
            if (x < 7 && piece.isNull())
                moves.add(new int[] {x + 1, y - direction});
            else if (x < 6 && (-direction == -1 && y > 1) || (-direction == 1 && y < 6) && game.getPiece(x + 2, y - direction * 2).isNull() && piece.color != color)
                moves.add(new int[] {x + 2, y - direction * 2});
        }

        return moves;
    }

    public boolean isNull() {
        return type == null || color == null;
    }
}
