package com.shanebeestudios.skbee.elements.recipe.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

@Name("Recipe - Reset")
@Description({"Reset all recipes or just custom/potion recipes",
        "\nNote: potion requires require PaperMC 1.18+"})
@Examples({"reset all recipes",
        "reset all custom recipes",
        "reset all potion recipes"})
@Since("INSERT VERSION")
public class EffResetRecipes extends Effect {

    static {
        Skript.registerEffect(EffResetRecipes.class, "reset all [-1:custom|1:potion] recipes");
    }

    private Kleenean check;
    private static final boolean SUPPORTS_POTION_MIX = Skript.classExists("io.papermc.paper.potion.PotionMix");

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean kleenean, ParseResult parseResult) {
        check = Kleenean.get(parseResult.mark);
        if (check == Kleenean.TRUE && !SUPPORTS_POTION_MIX) {
            Skript.error("Potion recipes are not supported on your server version.");
            return false;
        }
        return true;
    }

    @Override
    protected void execute(Event event) {
        switch (check) {
            // Resets potion mix recipes
            case TRUE -> Bukkit.getPotionBrewer().resetPotionMixes();
            // Resets vanilla recipes
            case FALSE -> Bukkit.resetRecipes();
            default -> {
                Bukkit.resetRecipes();
                Bukkit.getPotionBrewer().resetPotionMixes();
            }
        }
    }

    private String getResetType() {
        return switch (check) {
            case TRUE -> "potion";
            case FALSE -> "custom";
            default -> "all";
        };
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "reset " + getResetType() + " recipes";
    }

}

