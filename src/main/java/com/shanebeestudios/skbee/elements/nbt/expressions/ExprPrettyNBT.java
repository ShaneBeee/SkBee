package com.shanebeestudios.skbee.elements.nbt.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.reflection.ChatReflection;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

@Name("NBT - Pretty NBT String")
@Description({"Get a 'pretty' NBT string. This is colored the same as when using the vanilla Minecraft '/data' command. ",
        "Splitting it will output kind of like a JSON output. Requires 1.13.2+"})
@Examples({"set {_p} to pretty nbt from nbt compound of player's tool",
        "send pretty nbt from {_nbt} to player",
        "send pretty nbt from {_nbt} with split \" \" to console"})
@Since("1.6.0")
public class ExprPrettyNBT extends PropertyExpression<Object, String> {

    static {
        Skript.registerExpression(ExprPrettyNBT.class, String.class, ExpressionType.PROPERTY,
                "pretty nbt (of|from) %nbtcompounds/strings% [(with|using) split %-string%]",
                "%nbtcompounds/strings%'[s] pretty nbt [(with|using) split %-string%]");
    }

    private Expression<String> split;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        setExpr(exprs[0]);
        split = (Expression<String>) exprs[1];
        return true;
    }

    @Override
    protected String @NotNull [] get(@NotNull Event e, Object @NotNull [] source) {
        return get(source, n -> {
            String split = this.split != null ? this.split.getSingle(e) : null;
            NBTCompound compound;
            if (n instanceof NBTCompound) {
                compound = (NBTCompound) n;
            } else if (n instanceof String) {
                compound = new NBTContainer((String) n);
            } else {
                return null;
            }
            return ChatReflection.getPrettyNBT(compound, split);
        });
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "pretty nbt from " + getExpr().toString(e, d) + (split != null ? " with split " + split.toString(e, d) : "");
    }

}
