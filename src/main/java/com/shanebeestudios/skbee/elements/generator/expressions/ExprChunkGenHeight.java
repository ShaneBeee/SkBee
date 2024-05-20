package com.shanebeestudios.skbee.elements.generator.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.generator.event.HeightGenEvent;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("ChunkGenerator - ChunkData Height")
@Description({"Represents the highest point in a chunk used in a `height gen` section.",
        "This is used to tell Minecraft where the highest block is when it needs to generate structures."})
@Examples({"height gen:",
        "\tset {_x} to x coord of event-location",
        "\tset {_z} to z coord of event-location",
        "\tset {_n} to getNoise({_x}, {_z}) # This is just an example of a function you could do to get noise)",
        "\tset chunkdata height to {_n} + 1"})
@Since("3.5.0")
public class ExprChunkGenHeight extends SimpleExpression<Number> {

    static {
        Skript.registerExpression(ExprChunkGenHeight.class, Number.class, ExpressionType.SIMPLE,
                "chunk[ ]data height");
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        if (!ParserInstance.get().isCurrentEvent(HeightGenEvent.class)) {
            Skript.error("'" + parseResult.expr + "' can only be used in a height gen section.");
            return false;
        }
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected Number @Nullable [] get(Event event) {
        if (event instanceof HeightGenEvent heightGenEvent) {
            return new Number[]{heightGenEvent.getHeight()};
        }
        return null;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) return CollectionUtils.array(Number.class);
        return null;
    }

    @SuppressWarnings({"ConstantValue", "NullableProblems"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        if (mode == ChangeMode.SET && event instanceof HeightGenEvent heightGenEvent && delta != null && delta[0] instanceof Number number) {
            heightGenEvent.setHeight(number.intValue());
        }
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        return "chunkdata height";
    }

}
