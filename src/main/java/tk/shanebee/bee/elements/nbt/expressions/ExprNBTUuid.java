package tk.shanebee.bee.elements.nbt.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import tk.shanebee.bee.api.util.Util;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Name("NBT - UUID for NBT")
@Description({"Allows you to get an entity's UUID which can be represented in an NBT compound. Prior to 1.16 UUIDs were represented ",
        "as most/least significant bits. As of 1.16, they are now represented as int arrays."})
@Examples({"set {_u::*} to uuid int array of player",
        "set {_u} to uuid int array as string from player",
        "set {_m} to uuid most from target entity",
        "set {_l} to uuid least bits of event-entity"})
@Since("INSERT VERSION")
public class ExprNBTUuid extends SimpleExpression<Object> {

    static {
        Skript.registerExpression(ExprNBTUuid.class, Object.class, ExpressionType.SIMPLE,
                "uuid (int array[(1¦ as string)]|2¦most[ bits]|3¦least[ bits]) (from|of) %offlineplayer/entity%");
    }

    private int pattern;
    private Expression<Object> entity;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, ParseResult parseResult) {
        pattern = parseResult.mark;
        entity = (Expression<Object>) exprs[0];
        return true;
    }

    @Nullable
    @Override
    protected Object[] get(@NotNull Event e) {
        Object object = this.entity.getSingle(e);
        if (object == null) return null;
        UUID uuid;
        if (object instanceof OfflinePlayer) {
            uuid = ((OfflinePlayer) object).getUniqueId();
        } else {
            uuid = ((Entity) object).getUniqueId();
        }

        if (pattern < 2) {
            List<Integer> test = new ArrayList<>();
            StringBuilder builder = new StringBuilder("[I;");
            int[] t = Util.uuidToIntArray(uuid);
            for (int i = 0; i < 4; i++) {
                test.add(t[i]);
                if (i != 0) {
                    builder.append(",");
                }
                builder.append(t[i]);
            }
            if (pattern == 0) {
                return test.toArray();
            } else {
                return new String[]{builder.append("]").toString()};
            }
        } else if (pattern == 2) {
            return new Long[]{uuid.getMostSignificantBits()};
        } else {
            return new Long[]{uuid.getLeastSignificantBits()};
        }
    }

    @Override
    public boolean isSingle() {
        return pattern != 0;
    }

    @Override
    public @NotNull Class<?> getReturnType() {
        return Object.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String[] pat = new String[]{"int array", "int array as strings", "most bits", "least bit"};
        return "uuid " + pat[pattern] + " from " + entity.toString(e, d);
    }

}
