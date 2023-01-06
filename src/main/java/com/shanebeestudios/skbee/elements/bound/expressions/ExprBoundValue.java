package com.shanebeestudios.skbee.elements.bound.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.registrations.Classes;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.bound.Bound;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

@Name("Bound - Values")
@Description({"Get/set/delete custom values for bounds.",
        "Some objects will be serialized as their type, others will serialize as just a string.",
        "Numbers/Locations/Booleans/(Offline)Players/Items will be serialized as their type.",
        "Entities will be serialized as a string version of their UUID.",
        "All other objects will be serialized as a string.",
        "All bound values will return a list of the values, all bound keys will return a list of keys for these values.",
        "Deleting all bound values/keys will clear the list."})
@Examples({"set bound value \"king\" of bound with id \"the_garden\" to player",
        "set {_v} to bound value \"king\" of {_bound}",
        "delete bound value \"spawn\" of {_bound}",
        "set {_values::*} to all bound values of bound with id \"ma_bound\"",
        "set {_keys::*} to all bound keys of bound with id \"look_mah_imma_bound\"",
        "delete all bound values from bound with id \"now_im_a_sad_valueless_bound\""})
@Since("2.2.0")
public class ExprBoundValue extends SimpleExpression<Object> {

    static {
        Skript.registerExpression(ExprBoundValue.class, Object.class, ExpressionType.COMBINED,
                "bound value %string% (of|from) %bound%",
                "all [[of] the] bound (values|1¦keys) (of|from) %bound%");
    }

    private int pattern;
    private int parseMark;
    private Expression<String> key;
    private Expression<Bound> bound;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.pattern = matchedPattern;
        this.parseMark = parseResult.mark;
        this.key = pattern == 0 ? (Expression<String>) exprs[0] : null;
        this.bound = (Expression<Bound>) exprs[pattern == 0 ? 1 : 0];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable Object[] get(Event event) {
        Bound bound = this.bound.getSingle(event);
        if (bound == null) return null;

        if (pattern == 0) {
            String key = this.key.getSingle(event);
            if (key != null) {
                Object value = bound.getValue(key);
                if (value instanceof ItemStack itemStack) {
                    value = new ItemType(itemStack);
                }
                return new Object[]{value};
            }
        } else if (pattern == 1) {
            if (parseMark == 0) {
                return bound.getValues().values().toArray(new Object[0]);
            } else {
                return bound.getValues().keySet().toArray(new String[0]);
            }
        }
        return null;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        if (pattern == 0 && (mode == ChangeMode.SET || mode == ChangeMode.DELETE)) {
            return CollectionUtils.array(Object.class);
        } else if (pattern == 1 && mode == ChangeMode.DELETE) {
            return CollectionUtils.array();
        }
        return null;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        Bound bound = this.bound.getSingle(event);
        if (bound == null) return;

        if (pattern == 0 && mode == ChangeMode.SET) {
            String key = this.key.getSingle(event);
            if (key == null) return;

            Object object = delta[0];
            Object changed;
            if (object instanceof ItemStack || object instanceof Boolean || object instanceof Number ||
                    object instanceof String || object instanceof Location) {
                changed = object;
            } else if (object instanceof ItemType itemType) {
                changed = itemType.getRandom();
            } else if (object instanceof OfflinePlayer offlinePlayer) {
                changed = offlinePlayer;
            } else if (object instanceof Entity entity) {
                changed = entity.getUniqueId().toString();
            } else if (object != null) {
                changed = Classes.toString(object);
            } else {
                return;
            }
            bound.setValue(key, changed);
        } else if (mode == ChangeMode.DELETE) {
            if (pattern == 0) {
                String key = this.key.getSingle(event);
                if (key == null) return;

                bound.deleteValue(key);
            } else if (pattern == 1) {
                bound.clearValues();
            }
        }
        SkBee.getPlugin().getBoundConfig().saveBound(bound);
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<?> getReturnType() {
        return Object.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        if (pattern == 1) {
            String parse = parseMark == 0 ? "values" : "keys";
            return "all bound " + parse + " of bound " + this.bound.toString(e, d);
        }
        return "bound value '" + this.key.toString(e, d) + "' of bound " + this.bound.toString(e, d);
    }

}
