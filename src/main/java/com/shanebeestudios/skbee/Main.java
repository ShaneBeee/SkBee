package com.shanebeestudios.skbee;

import com.shanebeestudios.skbee.checkers.Checkers;
import com.shanebeestudios.skbee.pong.Pong;

public class Main {
    public static void main(String[] args) {
        var games = new Class<?>[] {
                Pong.class,
                Checkers.class
        };

        try {
            var game = games[(int) Math.floor(Math.random() * games.length)];
            game.getConstructor().newInstance();
        } catch (ReflectiveOperationException ignored) {
            System.err.println("we failed to pick a game :( defaulting to pong");
            new Pong();
        }
    }
}
