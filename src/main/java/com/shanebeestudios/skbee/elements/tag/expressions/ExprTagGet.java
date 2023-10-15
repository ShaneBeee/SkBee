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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("Minecraft Tag - Get")
@Description("Get a Minecraft Tag. This will include vanilla MC tags and custom tags from data packs that are registered to the server.")
@Examples({"set {_key} to namespaced key from \"minecraft:arrows\"",
        "set {_tag} to minecraft item tag from {_key}",
        "set {_tag} to minecraft block tag \"custom:emerald_ores\"",
        "set {_tag} to minecraft item tag \"arrows\"",
        "set {_tag} to minecraft item tag \"minecraft:arrows\"",
        "set {_tag} to minecraft entity tag \"minecraft:raiders\"",
        "send tag values of {_tag}",
        "loop tag values of {_tag}:"})
@Since("2.6.0")
@SuppressWarnings("rawtypes")
public class ExprTagGet extends SimpleExpression<Tag> {

    static {
        Skript.registerExpression(ExprTagGet.class, Tag.class, ExpressionType.COMBINED,
                "minecraft [(item|1¦block|2¦entity[[ ]type])] tag[s] %strings/namespacedkeys%");
    }

    private int parse;
    private Expression<?> objects;

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.parse = parseResult.mark;
        this.objects = exprs[0];
        return true;
    }

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    protected @Nullable Tag[] get(Event event) {
        List<Tag> tags = new ArrayList<>();
        List<NamespacedKey> keys = new ArrayList<>();
        for (Object object : this.objects.getArray(event)) {
            if (object instanceof NamespacedKey namespacedKey) {
                keys.add(namespacedKey);
            } else if (object instanceof String string) {
                NamespacedKey key = Util.getMCNamespacedKey(string, true);
                if (key != null) keys.add(key);
            }
        }
        for (NamespacedKey namespacedKey : keys) {
            Class tagType = this.parse == 2 ? EntityType.class : Material.class;
            String registry = switch (this.parse) {
                case 0 -> Tag.REGISTRY_ITEMS;
                case 1 -> Tag.REGISTRY_BLOCKS;
                case 2 -> Tag.REGISTRY_ENTITY_TYPES;
                default -> throw new IllegalStateException("Unexpected value: " + this.parse);
            };
            tags.add(Bukkit.getTag(registry, namespacedKey, tagType));
        }
        return tags.toArray(new Tag[0]);
    }

    @Override
    public boolean isSingle() {
        return this.objects.isSingle();
    }

    @Override
    public @NotNull Class<? extends Tag> getReturnType() {
        return Tag.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String type = this.parse == 1 ? "block" : this.parse == 2 ? "entity" : "item";
        String key = this.objects.toString(e, d);
        return "minecraft " + type + " tag[s] from " + key;
    }

}
