package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.block.Beacon;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Beacon - Effect Range")
@Description({"Gets the effect range of a beacon.",
        "A negative range value means the beacon is using its default range based on tier."})
@Examples({"send effect range of event-block",
        "add 100 to beacon effect range of event-block",
        "loop 11 times:",
        "\tremove 10 from beacon effect range of event-block",
        "reset effect range of event-block"})
@Since("2.16.0")
public class ExprBeaconEffectRange extends SimplePropertyExpression<Block, Number> {

    private static final boolean SUPPORTS_EFFECT_RANGE = Skript.methodExists(Beacon.class, "getEffectRange");

    static {
        register(ExprBeaconEffectRange.class, Number.class, "[beacon] effect range", "blocks");
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        if (!SUPPORTS_EFFECT_RANGE) {
            Skript.error("The 'effect range' expression can only be used on a PaperMC server!");
            return false;
        }
        return super.init(exprs, matchedPattern, isDelayed, parseResult);
    }

    @Override
    public @Nullable Number convert(Block block) {
        if (block.getState() instanceof Beacon beacon)
            return beacon.getEffectRange();
        return null;
    }

    @Override
    @Nullable
    public Class<?>[] acceptChange(ChangeMode mode) {
        return switch (mode) {
            case SET, REMOVE, ADD -> CollectionUtils.array(Number.class);
            case RESET -> CollectionUtils.array();
            default -> null;
        };
    }

    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        for (Block block : getExpr().getArray(event)) {
            if (block.getState() instanceof Beacon beacon) {
                if (delta == null) {
                    beacon.resetEffectRange();
                    beacon.update();
                    continue;
                }
                double effectRange = beacon.getEffectRange();
                double newEffectRange = ((Number) delta[0]).doubleValue();
                switch (mode) {
                    case SET -> effectRange = newEffectRange;
                    case ADD -> effectRange += newEffectRange;
                    case REMOVE -> effectRange -= newEffectRange;
                }
                beacon.setEffectRange(effectRange);
                beacon.update();
            }
        }
    }

    @Override
    public Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    protected String getPropertyName() {
        return "effect range";
    }

}
