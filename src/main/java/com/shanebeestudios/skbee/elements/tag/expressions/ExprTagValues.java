package com.shanebeestudios.skbee.elements.tag.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.entity.EntityData;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Name("Minecraft Tag - Values")
@Description("Get the values of a Minecraft Tag.")
@Examples({"loop tag values of {_tag}:",
        "set {_values::*} to tag values of {_mctag}"})
@Since("2.6.0")
public class ExprTagValues extends SimpleExpression<Object> {

    static {
        Skript.registerExpression(ExprTagValues.class, Object.class, ExpressionType.PROPERTY,
                "tag values of [minecraft[ ]tag[s]] %minecrafttags%");
    }

    private Expression<Tag<?>> tags;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.tags = (Expression<Tag<?>>) exprs[0];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable Object[] get(Event event) {
        List<Object> objects = new ArrayList<>();
        for (Tag<?> tag : this.tags.getArray(event)) {
            tag.getValues().forEach(value -> {
                if (value instanceof Material material) {
                    objects.add(new ItemType(material));
                } else if (value instanceof EntityType entityType) {
                    Class<? extends Entity> entityClass = entityType.getEntityClass();
                    if (entityClass != null) {
                        objects.add(EntityData.fromClass(entityClass));
                    }
                }
            });
        }
        return objects.toArray(new Object[0]);
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public @NotNull Class<?> getReturnType() {
        return Object.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "tag values of " + this.tags.toString(e, d);
    }

}
