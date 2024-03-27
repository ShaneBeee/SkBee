package com.shanebeestudios.skbee.game.checkers;

import javax.swing.*;
import java.awt.*;

public enum PieceType {
    WORKER_BEE(
            new ImageIcon(Game.class.getResource("/assets/workerbeeRED.png")).getImage(),
            new ImageIcon(Game.class.getResource("/assets/workerbeeBLUE.png")).getImage()
    ),
    QUEEN_BEE(
            new ImageIcon(Game.class.getResource("/assets/queenbeeRED.png")).getImage(),
            new ImageIcon(Game.class.getResource("/assets/queenbeeBLUE.png")).getImage()
    );

    public Image red;
    public Image blue;

    PieceType(Image red, Image blue) {
        this.red = red;
        this.blue = blue;
    }
}
