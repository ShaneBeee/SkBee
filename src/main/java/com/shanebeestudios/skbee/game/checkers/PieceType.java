package com.shanebeestudios.skbee.game.checkers;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;

public enum PieceType {
    WORKER_BEE("/assets/workerbee.png"),
    QUEEN_BEE("/assets/queenbee.png");

    public final Image red;
    public final Image blue;

    PieceType(String location) {
        BufferedImage buffRed = tint(loadImage(location), new Color(248, 57, 57));
        this.red = new ImageIcon(buffRed).getImage();

        BufferedImage buffBlue = tint(loadImage(location), new Color(54, 54, 246));
        this.blue = new ImageIcon(buffBlue).getImage();
    }

    // Found this on stack overflow
    // https://stackoverflow.com/questions/4248104/applying-a-tint-to-an-image-in-java

    public BufferedImage loadImage(String location) {
        URL url = Game.class.getResource(location);
        assert url != null;
        ImageIcon icon = new ImageIcon(url);
        Image image = icon.getImage();

        // Create empty BufferedImage, sized to Image
        BufferedImage buffImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw Image into BufferedImage
        Graphics g = buffImage.getGraphics();
        g.drawImage(image, 0, 0, null);
        return buffImage;
    }

    public static BufferedImage tint(BufferedImage image, Color color) {
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                Color pixelColor = new Color(image.getRGB(x, y), true);
                int r = (pixelColor.getRed() + color.getRed()) / 2;
                int g = (pixelColor.getGreen() + color.getGreen()) / 2;
                int b = (pixelColor.getBlue() + color.getBlue()) / 2;
                int a = pixelColor.getAlpha();
                int rgba = (a << 24) | (r << 16) | (g << 8) | b;
                image.setRGB(x, y, rgba);
            }
        }
        return image;
    }

}
