package com.shanebeestudios.skbee.api.recipe;

public enum CookingRecipeType {
    // TODO: update EffCookingRecipe to this, so it's kept up with api till we delete the effect.
    // Other information if needed can be added over time, for now this provides an easier api interface.
    FURNACE(200),
    SMOKING(100),
    BLASTING(100),
    CAMPFIRE(600);

    private final int cookTime;

    CookingRecipeType(int cookTime) {
        this.cookTime = cookTime;
    }

    public int getCookTime() {
        return this.cookTime;
    }

}
