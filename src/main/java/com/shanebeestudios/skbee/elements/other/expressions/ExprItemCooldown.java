package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Name("Item Cooldown")
@Description({"Get/set a cooldown on the specified material for a certain timespan.",
        "Cooldowns are used by the server for items such as ender pearls and shields to prevent them from being used repeatedly.",
        "Note that cooldowns will not by themselves stop an item from being used for attacking.",
        "This is per player and per MATERIAL, not per actual item."})
@Examples({"set item cooldown of player's tool for player to 1 second",
        "set item cooldown of nether star for all players to 2 seconds",
        "reset item cooldown of player's tool"})
@Since("INSERT VERSION")
public class ExprItemCooldown extends SimpleExpression<Timespan> {

    static {
        Skript.registerExpression(ExprItemCooldown.class, Timespan.class, ExpressionType.COMBINED,
                "item cooldown (of|for) %itemtype% [(of|for) %players%]");
    }

    private Expression<ItemType> itemType;
    private Expression<Player> players;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.itemType = (Expression<ItemType>) exprs[0];
        this.players = (Expression<Player>) exprs[1];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable Timespan[] get(Event event) {
        ItemType itemType = this.itemType.getSingle(event);
        List<Timespan> nums = new ArrayList<>();
        if (itemType != null) {
            Material material = itemType.getMaterial();
            for (Player player : this.players.getArray(event)) {
                int cooldown = player.getCooldown(material);
                nums.add(Timespan.fromTicks_i(cooldown));
            }
        }
        return nums.toArray(new Timespan[0]);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET || mode == ChangeMode.RESET) {
            return CollectionUtils.array(Timespan.class);
        }
        return null;
    }

    @SuppressWarnings({"NullableProblems", "ConstantConditions"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        ItemType itemType = this.itemType.getSingle(event);
        if (itemType == null) return;

        Timespan timespan = (delta != null && delta[0] != null) ? ((Timespan) delta[0]) : Timespan.fromTicks_i(0);
        long ticks = mode == ChangeMode.SET ? timespan.getTicks_i() : 0;
        Material material = itemType.getMaterial();
        if (!material.isItem()) return;

        for (Player player : this.players.getArray(event)) {
            player.setCooldown(material, (int) ticks);
        }
    }

    @Override
    public boolean isSingle() {
        return this.players.isSingle();
    }

    @Override
    public @NotNull Class<? extends Timespan> getReturnType() {
        return Timespan.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "item cooldown of " + this.itemType.toString(e, d) + " for " + this.players.toString(e, d);
    }

}
