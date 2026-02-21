package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprShootBowConsumable extends SimpleExpression<ItemType> {

    public static void register(Registration reg) {
        reg.newSimpleExpression(ExprShootBowConsumable.class, ItemType.class, "consumed item")
                .name("Entity Shoot Bow - Consumable")
                .description("Get the Item to be consumed in an entity shoot bow event (if any).")
                .examples("on entity shoot bow:",
                        "\tif consumed item is not an arrow:",
                        "\t\tcancel event")
                .since("2.16.0")
                .register();
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        if (!ParserInstance.get().isCurrentEvent(EntityShootBowEvent.class)) {
            Skript.error("'" + parseResult.expr + "' can only be used in the entity shoot bow event.");
            return false;
        }
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable ItemType[] get(Event event) {
        if (event instanceof EntityShootBowEvent shootBowEvent) {
            ItemStack consumable = shootBowEvent.getConsumable();
            if (consumable != null) return new ItemType[]{new ItemType(consumable)};
        }
        return null;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends ItemType> getReturnType() {
        return ItemType.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "consumed item";
    }

}
