package com.shanebeestudios.skbee.elements.tag.expressions;

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
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Name("Minecraft Tag - All")
@Description("Represents all Minecraft Tags registered to the server.")
@Examples({"send all minecraft tags",
        "loop all minecraft block tags:",
        "set {_tags::*} to all minecraft item tags"})
@Since("INSERT VERSION")
@SuppressWarnings("rawtypes")
public class ExprTagAll extends SimpleExpression<Tag> {

    static {
        Skript.registerExpression(ExprTagAll.class, Tag.class, ExpressionType.SIMPLE,
                "[all] minecraft [(item|1¦block|2¦entity)] tags");
    }

    private int parse;

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.parse = parseResult.mark;
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable Tag<?>[] get(Event event) {
        List<Tag<?>> tags = new ArrayList<>();
        if (parse == 1) {
            Bukkit.getTags(Tag.REGISTRY_BLOCKS, Material.class).forEach(tags::add);
        } else if (parse == 2) {
            Bukkit.getTags(Tag.REGISTRY_ENTITY_TYPES, EntityType.class).forEach(tags::add);
        } else {
            Bukkit.getTags(Tag.REGISTRY_ITEMS, Material.class).forEach(tags::add);
        }
        return tags.toArray(new Tag[0]);
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public @NotNull Class<? extends Tag> getReturnType() {
        return Tag.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String type = parse == 1 ? "block" : parse == 2 ? "entity type" : "item";
        return "all minecraft " + type + " tags";
    }

}
