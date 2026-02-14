package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprShootBowShouldConsume extends SimpleExpression<Boolean> {

    public static void register(Registration reg) {
        reg.newSimpleExpression(ExprShootBowShouldConsume.class, Boolean.class, "should consume item")
                .name("Entity Shoot Bow - Should Consume")
                .description("Get/set whether or not the consumable item should be consumed in an entity shoot bow event.")
                .examples("on entity shoot bow:",
                        "\tif shot bow is a bow:",
                        "\t\tset should consume item to false")
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
    protected @Nullable Boolean[] get(Event event) {
        if (event instanceof EntityShootBowEvent shootBowEvent) return new Boolean[]{shootBowEvent.shouldConsumeItem()};
        return null;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) return CollectionUtils.array(Boolean.class);
        return null;
    }

    @SuppressWarnings({"NullableProblems", "ConstantValue", "UnstableApiUsage", "deprecation"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        if (!(event instanceof EntityShootBowEvent shootBowEvent)) return;
        if (mode != ChangeMode.SET) return; // this shouldn't happen
        if (delta != null && delta[0] instanceof Boolean shouldConsume) {
            shootBowEvent.setConsumeItem(shouldConsume);
            if (!shouldConsume && shootBowEvent.getEntity() instanceof Player player) {
                // Appears to be a bug where the player's
                // inventory does not update
                player.updateInventory();
            }
        }
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends Boolean> getReturnType() {
        return Boolean.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "should consume item";
    }

}
