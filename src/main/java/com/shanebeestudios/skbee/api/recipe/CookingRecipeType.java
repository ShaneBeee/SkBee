package com.shanebeestudios.skbee.api.recipe;

// Reviewer note: you got any better name this enum could be? I don't know how I feel about it.
public enum CookingRecipeType {
    // TODO: update EffCookingRecipe to this, so it's kept up with api till we delete the effect.
    // Other information if needed can be added over time, for now this provides an easier api interface.
    FURNACE(200),
    SMOKER(100),
    BLAST_FURNACE(100),
    CAMPFIRE(600);

    private final int cookTime;

    CookingRecipeType(int cookTime) {
        this.cookTime = cookTime;
    }

    public int getCookTime() {
        return this.cookTime;
    }

}
