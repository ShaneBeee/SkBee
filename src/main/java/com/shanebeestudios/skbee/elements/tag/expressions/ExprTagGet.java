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
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Name("Minecraft Tag - Get")
@Description("Get a vanilla Minecraft Tag. This will include custom tags from data packs that are registered to the server.")
@Examples({"set {_key} to namespaced key from \"minecraft:arrows\"",
        "set {_tag} to minecraft item tag from {_key}"})
@Since("2.6.0")
@SuppressWarnings("rawtypes")
public class ExprTagGet extends SimpleExpression<Tag> {

    static {
        Skript.registerExpression(ExprTagGet.class, Tag.class, ExpressionType.COMBINED,
                "minecraft [(item|1¦block|2¦entity type)] tag[s] from %namespacedkeys%",
                "minecraft [(item|1¦block|2¦entity type)] tag[s] %strings%");
    }

    private int parse;
    private int pattern;
    private Expression<String> strings;
    private Expression<NamespacedKey> keys;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.pattern = matchedPattern;
        this.parse = parseResult.mark;
        if (matchedPattern == 1) {
            //Skript.warning("string use will be removed in the future, please use Namespaced Keys instead.");
            this.strings = (Expression<String>) exprs[0];
        } else {
            this.keys = (Expression<NamespacedKey>) exprs[0];
        }
        return true;
    }

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    protected @Nullable Tag[] get(Event event) {
        List<Tag> tags = new ArrayList<>();
        List<NamespacedKey> keys = new ArrayList<>();
        if (pattern == 1) {
            for (String string : this.strings.getArray(event)) {
                keys.add(Util.getMCNamespacedKey(string, true));
            }
        } else {
            keys.addAll(Arrays.asList(this.keys.getArray(event)));
        }
        for (NamespacedKey namespacedKey : keys) {
            Class tagType = parse == 2 ? EntityType.class : Material.class;
            String registry = switch (parse) {
                case 0 -> Tag.REGISTRY_ITEMS;
                case 1 -> Tag.REGISTRY_BLOCKS;
                case 2 -> Tag.REGISTRY_ENTITY_TYPES;
                default -> throw new IllegalStateException("Unexpected value: " + parse);
            };
            tags.add(Bukkit.getTag(registry, namespacedKey, tagType));
        }
        return tags.toArray(new Tag[0]);
    }

    @Override
    public boolean isSingle() {
        if (pattern == 0) {
            return this.keys.isSingle();
        }
        return this.strings.isSingle();
    }

    @Override
    public @NotNull Class<? extends Tag> getReturnType() {
        return Tag.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String type = parse == 1 ? "block" : parse == 2 ? "entity type" : "item";
        String key = pattern == 1 ? this.strings.toString(e, d) : this.keys.toString(e, d);
        return "minecraft " + type + " tag[s] from " + key;
    }

}
