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
import de.tr7zw.changeme.nbtapi.NBTContainer;
import org.bukkit.event.Event;
import tk.shanebee.bee.SkBee;
import tk.shanebee.bee.api.NBTApi;

import javax.annotation.Nullable;

@Name("NBT - Tag")
@Description("Returns the value of the specified tag of the specified NBT. " +
        "Also supports getting nested tags using a semi colon as a delimiter (version 2.11.1+). " +
        "(Currently only supports get. Set may be available in the future)")
@Examples({"set {_tag} to tag \"Invulnerable\" of targeted entity's nbt",
        "send \"Tag: %tag \"\"CustomName\"\" of nbt of target entity%\" to player",
        "set {_tag} to \"Enchantments\" tag of nbt of player's tool",
        "set {_tag} to \"BlockEntityTag;Items\" tag of nbt of target block"})
@Since("1.0.0")
public class ExprTagOfNBT extends SimpleExpression<Object> {

    private static final NBTApi NBT_API;

    static {
        // TODO something about a list
        Skript.registerExpression(ExprTagOfNBT.class, Object.class, ExpressionType.SIMPLE,
                "tag %string% of %string%", "%string% tag of %string%");
        NBT_API = SkBee.getPlugin().getNbtApi();
    }

    private Expression<String> a;
    private Expression<String> b;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parser) {
        this.a = (Expression<String>) expressions[0];
        this.b = (Expression<String>) expressions[1];
        return true;
    }

    @Override
    @Nullable
    protected Object[] get(Event e) {
        String t = a.getSingle(e);
        String n = b.getSingle(e);
        if (t.contains(";")) {
            return getNested(t, n);
        }
        return new Object[]{NBT_API.getTag(t, n)};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public String toString(@Nullable Event e, boolean d) {
        return "Tag \"" + a.toString(e, d) + "\" of " + b.toString(e, d);
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    private Object[] getNested(String tag, String nbt) {
        String[] split = tag.split(";");
        Object nbtNew = nbt;
        for (String s : split) {
            nbtNew = NBT_API.getTag(s, new NBTContainer(nbtNew).toString()); // TODO api for this
        }
        return new Object[]{nbtNew};
    }

}
