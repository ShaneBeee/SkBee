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

public class ExprShootBowShotBow extends SimpleExpression<ItemType> {

    public static void register(Registration reg) {
        reg.newSimpleExpression(ExprShootBowShotBow.class, ItemType.class, "shot bow")
                .name("Entity Shoot Bow - Shot Bow")
                .description("Gets the bow Item used to fire the arrow in an entity shoot bow event.")
                .examples("on entity shoot bow:",
                        "\tif name of shot bow != \"Mr Bow\":",
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
            ItemStack bow = shootBowEvent.getBow();
            if (bow != null) return new ItemType[]{new ItemType(bow)};
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
        return "shot bow";
    }

}
