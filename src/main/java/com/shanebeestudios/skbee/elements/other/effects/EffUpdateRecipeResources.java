package com.shanebeestudios.skbee.elements.other.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EffUpdateRecipeResources extends Effect {

    private static final boolean HAS_METHODS = Skript.methodExists(Bukkit.class, "updateRecipes");

    public static void register(Registration reg) {
        reg.newEffect(EffUpdateRecipeResources.class,
                "update server recipes", "update server resources")
            .name("Update Server Recipes/Resources")
            .description("Update recipes will update recipe data and the recipe book for all connected clients.",
                "Useful for updating clients to new recipes.",
                "Update resources will update all advancement, tag, and recipe data for all connected clients.",
                "Useful for updating clients to new advancements/recipes/tags.")
            .examples("update server recipes", "update server resources")
            .since("2.17.0")
            .register();
    }

    private int pattern;

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        if (!HAS_METHODS) {
            Skript.error("'" + parseResult.expr + "' requires PaperMC 1.20.1+");
            return false;
        }
        this.pattern = matchedPattern;
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected void execute(Event event) {
        if (this.pattern == 0) Bukkit.updateRecipes();
        else Bukkit.updateResources();
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return this.pattern == 0 ? "update server recipes" : "update server resources";
    }

}
