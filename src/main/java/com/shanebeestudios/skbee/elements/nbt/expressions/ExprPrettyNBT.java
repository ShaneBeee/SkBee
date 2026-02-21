package com.shanebeestudios.skbee.elements.nbt.expressions;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.nbt.NBTReflection;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.skript.base.PropertyExpression;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprPrettyNBT extends PropertyExpression<Object, String> {

    public static void register(Registration reg) {
        reg.newCombinedExpression(ExprPrettyNBT.class, String.class,
                "pretty nbt (of|from) %nbtcompounds% [(with|using) split %-string%]",
                "%nbtcompounds%'[s] pretty nbt [(with|using) split %-string%]")
            .name("NBT - Pretty NBT String")
            .description("Get a 'pretty' NBT string. This is colored the same as when using the vanilla Minecraft '/data' command. ",
                "Splitting it will output kind of like a JSON output. Requires 1.13.2+")
            .examples("set {_pretty} to pretty nbt from nbt compound of player's tool",
                "send pretty nbt from nbt compound of player's tool",
                "set {_nbt} to nbt of player's tool",
                "send pretty nbt from {_nbt} to player",
                "send pretty nbt from {_nbt} with split \" \" to console")
            .since("1.6.0")
            .register();
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
    protected String[] get(Event event, Object[] source) {
        String split = this.split != null ? this.split.getSingle(event) : null;
        return get(source, object -> {
            if (object instanceof NBTCompound compound) return NBTReflection.getPrettyNBT(compound, split);
            return null;
        });
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "pretty nbt from " + getExpr().toString(e, d) +
            (this.split != null ? " with split " + this.split.toString(e, d) : "");
    }

}
