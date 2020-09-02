package tk.shanebee.bee.elements.nbt.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.effects.EffSpawn;
import ch.njol.skript.entity.EntityType;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.Direction;
import ch.njol.util.Kleenean;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.util.Consumer;
import org.jetbrains.annotations.NotNull;
import tk.shanebee.bee.SkBee;
import tk.shanebee.bee.api.NBTApi;
import tk.shanebee.bee.api.NBTApi.ObjectType;

import javax.annotation.Nullable;

@Name("NBT - Spawn Entity with NBT")
@Description("Spawn an entity at a location with NBT")
@Examples({"spawn sheep at player with nbt \"{NoAI:1b}\"",
        "spawn 1 of zombie at player with nbt \"{NoGravity:1b}\""})
@Since("1.0.0")
public class EffSpawnEntityNBT extends Effect {

    private static final NBTApi NBT_API;

    static {
        Skript.registerEffect(EffSpawnEntityNBT.class,
                "spawn %entitytypes% [%directions% %locations%] with nbt %string/nbtcompound%",
                "spawn %number% of %entitytypes% [%directions% %locations%] with nbt %string/nbtcompound%");
        NBT_API = SkBee.getPlugin().getNbtApi();
    }

    @SuppressWarnings("null")
    private Expression<Location> locations;
    @SuppressWarnings("null")
    private Expression<EntityType> types;
    private Expression<Object> nbt;
    @Nullable
    private Expression<Number> amount;

    @SuppressWarnings({"unchecked", "null"})
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parser) {
        amount = matchedPattern == 0 ? null : (Expression<Number>) (exprs[0]);
        types = (Expression<EntityType>) exprs[matchedPattern];
        locations = Direction.combine((Expression<? extends Direction>) exprs[1 + matchedPattern], (Expression<? extends Location>) exprs[2 + matchedPattern]);
        nbt = (Expression<Object>) exprs[3 + matchedPattern];
        return true;
    }

    @Override
    public void execute(final @NotNull Event event) {
        Object nbtObject = this.nbt.getSingle(event);
        String value = nbtObject instanceof NBTCompound ? nbtObject.toString() : (String) nbtObject;
        final Number a = amount != null ? amount.getSingle(event) : 1;
        if (a == null)
            return;
        final EntityType[] et = types.getArray(event);
        for (final Location loc : locations.getArray(event)) {
            assert loc != null : locations;
            for (final EntityType type : et) {
                for (int i = 0; i < a.doubleValue() * type.getAmount(); i++) {
                    EffSpawn.lastSpawned = spawn(loc, type.data.getType(), value);
                }
            }
        }
    }

    @Override
    public @NotNull String toString(Event e, boolean debug) {
        return "spawn " + (amount != null ? amount.toString(e, debug) + " " : "") + types.toString(e, debug) +
                " " + locations.toString(e, debug) + " " + nbt.toString(e, debug);
    }

    private <T extends Entity> Entity spawn(Location loc, Class<T> type, String nbt) {
        if (Skript.methodExists(World.class, "spawn", Location.class, Class.class, Consumer.class)) {
            return loc.getWorld().spawn(loc, type, ent -> NBT_API.addNBT(ent, nbt, ObjectType.ENTITY));
        }
        Entity e = loc.getWorld().spawn(loc, type);
        NBT_API.addNBT(e, nbt, ObjectType.ENTITY);
        return e;
    }

}
