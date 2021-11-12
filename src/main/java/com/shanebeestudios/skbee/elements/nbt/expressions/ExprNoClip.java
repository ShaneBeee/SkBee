package com.shanebeestudios.skbee.elements.nbt.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.reflection.McReflection;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;

import javax.annotation.Nullable;

@Name("Entity NoClip")
@Description("Set or get the noClip status of an entity (This will not work on players)")
@Examples({"spawn a zombie at player", "set no clip state of last spawned zombie to true",
        "set {_var} to no clip state of last spawned sheep",
        "loop all entities in radius 5 around player:", "\tset no clip state of loop-entity to true", "\tpush loop-entity up with speed 5"})
@Since("1.0.2")
public class ExprNoClip extends SimpleExpression<Boolean> {

    static {
        Skript.registerExpression(ExprNoClip.class, Boolean.class, ExpressionType.PROPERTY,
                "no[( |-)]clip (state|mode) of %entities%", "%entities%'s no[( |-)]clip (state|mode)");
    }

    private Expression<Entity> entities;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean kleenean, ParseResult parse) {
        entities = (Expression<Entity>) exprs[0];
        return true;
    }

    @Override
    public boolean isSingle() {
        return entities.isSingle();
    }

    @Override
    protected Boolean[] get(Event e) {
        Entity[] ents = entities.getAll(e);
        if (ents.length == 0) return null;
        Boolean[] noClipStates = new Boolean[ents.length];
        int i = 0;
        for (Entity ent : ents) {
            if (ent == null)
                continue;
            noClipStates[i] = !McReflection.getClip(ent);
            i++;
        }
        return noClipStates;
    }

    @Override
    @Nullable
    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        if (mode == ChangeMode.SET) {
            return CollectionUtils.array(Boolean.class);
        }
        return null;
    }

    @Override
    public void change(Event e, @Nullable Object[] delta, ChangeMode mode) {
        Entity[] ents = entities.getAll(e);
        if (ents.length == 0) return;
        if (mode == ChangeMode.SET) {
            Boolean newValue = ((Boolean) delta[0]);
            for (Entity ent : ents) {
                if (ent == null)
                    continue;
                McReflection.setClip(ent, !newValue);
            }
        }
    }

    @Override
    public Class<? extends Boolean> getReturnType() {
        return Boolean.class;
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "the no clip state of " + entities.toString(e, debug);
    }
}
