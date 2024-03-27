package com.shanebeestudios.skbee.game;

import com.shanebeestudios.skbee.game.checkers.Checkers;
import com.shanebeestudios.skbee.game.pong.Pong;

public class GamesMain {
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
