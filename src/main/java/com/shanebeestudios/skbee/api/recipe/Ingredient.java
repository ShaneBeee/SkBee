package com.shanebeestudios.skbee.api.recipe;

public record Ingredient(char key, Object item) {

    @Override
    public String toString() {
        return key + ":" + item.toString();
    }

}
