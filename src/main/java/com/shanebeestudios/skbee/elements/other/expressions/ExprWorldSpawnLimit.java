package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.github.shanebeee.skr.Registration;
import com.shanebeestudios.skbee.api.skript.base.SimpleExpression;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.SpawnCategory;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ExprWorldSpawnLimit extends SimpleExpression<Number> {

    public static void register(Registration reg) {
        reg.newCombinedExpression(ExprWorldSpawnLimit.class, Number.class,
                "[the] server wide %spawncategory% spawn limit",
                "[the] %spawncategory% spawn limit[s] [of %-worlds%]")
            .name("World Spawn Limit")
            .description("Get/set the spawn limit of a world.",
                "If the world is ommited, the server spawn limit will be returned.",
                "World spawn limits can be changed, server spawn limits cannot.",
                "If set to a negative number the world will use the server-wide spawn limit instead, reset will do the same.",
                "You can read more about [**Mob Caps**](https://minecraft.wiki/w/Mob_spawning#Java_Edition_mob_cap) on McWiki",
                "Note: The `misc` category is not supported by this expression.",
                "Note: Changes are not persistent thru server restarts.")
            .examples("set the monster spawn limit of player's world to 10",
                "add 1 to animal spawn limit of world(\"world\")",
                "reset ambient spawn limit of all worlds",
                "set {_limit} to the water animal spawn limit")
            .since("3.21.0")
            .register();
    }

    private Expression<SpawnCategory> category;
    private Expression<World> worlds;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.category = (Expression<SpawnCategory>) expressions[0];
        if (matchedPattern == 1) {
            this.worlds = (Expression<World>) expressions[1];
        }
        return true;
    }

    @Override
    protected Number @Nullable [] get(Event event) {
        SpawnCategory category = this.category.getSingle(event);
        if (category == null || category == SpawnCategory.MISC) return null;

        List<Integer> limits = new ArrayList<>();

        if (this.worlds != null) {
            for (World world : this.worlds.getArray(event)) {
                limits.add(world.getSpawnLimit(category));
            }
        } else {
            limits.add(Bukkit.getSpawnLimit(category));
        }

        return limits.toArray(new Number[0]);
    }

    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        if (this.worlds != null) {
            return switch (mode) {
                case SET, ADD, REMOVE, RESET -> CollectionUtils.array(Number.class);
                default -> null;
            };
        } else {
            Skript.error("Cannot change spawn limit of the server.");
            return null;
        }
    }

    @Override
    public void change(Event event, Object @Nullable [] delta, ChangeMode mode) {
        SpawnCategory category = this.category.getSingle(event);
        if (category == null || category == SpawnCategory.MISC) return;

        int changeValue = -1;
        if (delta != null && delta[0] instanceof Number number) changeValue = number.intValue();

        for (World world : this.worlds.getArray(event)) {
            int oldValue = world.getSpawnLimit(category);

            if (mode == ChangeMode.SET || mode == ChangeMode.RESET) {
                world.setSpawnLimit(category, changeValue);
            } else if (mode == ChangeMode.ADD) {
                world.setSpawnLimit(category, oldValue + changeValue);
            } else if (mode == ChangeMode.REMOVE) {
                world.setSpawnLimit(category, oldValue - changeValue);
            }
        }
    }

    @Override
    public boolean isSingle() {
        return this.worlds == null || this.worlds.isSingle();
    }

    @Override
    public Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        if (this.worlds != null) {
            return this.category.toString(event, debug) + " spawn limit of " + this.worlds.toString(event, debug);
        }
        return "server wide " + this.category.toString(event, debug) + " spawn limit";
    }

}
