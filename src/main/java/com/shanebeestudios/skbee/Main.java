package com.shanebeestudios.skbee;

import com.shanebeestudios.skbee.pong.Pong;

public class Main {
    public static void main(String[] args) {
        var games = new Class<?>[] {
                Pong.class,
                com.shanebeestudios.skbee.checkers.Main.class
        };

        // for now
        new com.shanebeestudios.skbee.checkers.Main();
        return;

//        try {
//            var game = games[(int) Math.floor(Math.random() * games.length)];
//            game.getConstructor().newInstance();
//        } catch (ReflectiveOperationException ignored) {
//            System.out.println("we failed to pick a game :( defaulting to pong");
//            new Pong();
//        }
    }
}
