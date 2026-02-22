package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.event.Event;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprArmorTrim extends SimpleExpression<ArmorTrim> {

    public static void register(Registration reg) {
        reg.newCombinedExpression(ExprArmorTrim.class, ArmorTrim.class,
                "[new] armor trim (of|from|using) %trimmaterial% (and|with) %trimpattern%")
            .name("ArmorTrim - Create")
            .description("Create a new armor trim to be applied to an item.")
            .examples("set {_trim} to armor trim using gold_material with eye_pattern",
                "add armor trim using gold_material with eye_pattern to armor trim of player's helmet")
            .since("2.13.0")
            .register();
    }

    private Expression<TrimMaterial> trimMaterial;
    private Expression<TrimPattern> trimPattern;

    @SuppressWarnings({"unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.trimMaterial = (Expression<TrimMaterial>) exprs[0];
        this.trimPattern = (Expression<TrimPattern>) exprs[1];
        return true;
    }

    @Override
    protected @Nullable ArmorTrim[] get(Event event) {
        TrimMaterial trimMaterial = this.trimMaterial.getSingle(event);
        TrimPattern trimPattern = this.trimPattern.getSingle(event);
        if (trimMaterial == null || trimPattern == null) return null;

        return new ArmorTrim[]{new ArmorTrim(trimMaterial, trimPattern)};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends ArmorTrim> getReturnType() {
        return ArmorTrim.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "armor trim using " + this.trimMaterial.toString(e, d) + " and " + this.trimPattern.toString(e, d);
    }

}
