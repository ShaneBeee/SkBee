package com.shanebeestudios.skbee.elements.recipe.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
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
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Name("Minecraft Tag")
@Description({"Represents a Minecraft tag. This includes all vanilla item/block tags registered to the server.",
        "Bukkit provides 2 lists, item tags and block tags. Some tags might be in both lists but still have the same effect.",
        "If you are unsure of all tag options, just return a list of all tags to see what is available.",
        "You can also get a list of all itemtypes that are tagged by a tag.",
        "The 'minecraft:' namespace is optional. Requires Minecraft 1.13+"})
@Examples({"set {_i} to minecraft tag \"doors\"",
        "set {_tag} to minecraft tag \"trapdoors\"",
        "set {_tags::*} to minecraft tags \"wall_signs\" and \"wooden_doors\"",
        "set {_tag} to \"minecraft:climbable\"", "",
        "loop minecraft tags:",
        "\tsend \"-%loop-value%\" to console"})
@Since("1.10.0")
public class ExprMinecraftTag extends SimpleExpression<Object> {

    private static final String[] TAG_TYPE = new String[]{Tag.REGISTRY_ITEMS, Tag.REGISTRY_BLOCKS};

    static {
        Skript.registerExpression(ExprMinecraftTag.class, Object.class, ExpressionType.COMBINED,
                "minecraft [(0¦item|1¦block)] tag[s] %strings%",
                "[all] minecraft [(0¦item|1¦block)] tags",
                "[all] item[type]s of minecraft [(0¦item|1¦block)] tag[s] %strings%");
    }

    private int pattern;
    private Expression<String> strings;
    private int tagPattern;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        pattern = matchedPattern;
        if (pattern != 1) {
            strings = (Expression<String>) exprs[0];
        }
        tagPattern = parseResult.mark;
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Nullable
    @Override
    protected Object[] get(Event event) {
        List<Tag<?>> tags = new ArrayList<>();
        if (pattern == 1) {
            Bukkit.getTags(TAG_TYPE[tagPattern], Material.class).forEach(tags::add);
        } else {
            for (String s : strings.getArray(event)) {
                NamespacedKey key = Util.getNamespacedKey(s.toLowerCase(Locale.ROOT), true);
                if (key == null) continue;

                Tag<Material> tag = Bukkit.getTag(TAG_TYPE[tagPattern], key, Material.class);
                if (tag != null) {
                    tags.add(tag);
                }
            }
            if (pattern == 2) {
                List<ItemType> items = new ArrayList<>();
                tags.forEach(tag -> tag.getValues().forEach(value -> {
                    if (value instanceof Material) {
                        items.add(new ItemType(((Material) value)));
                    }
                }));
                return items.toArray(new ItemType[0]);
            }
        }
        return tags.toArray(new Tag[0]);
    }

    @Override
    public boolean isSingle() {
        if (pattern > 0) {
            return false;
        }
        return strings.isSingle();
    }

    @Override
    public @NotNull Class<? extends Object> getReturnType() {
        return pattern == 2 ? ItemType.class : Tag.class;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public String toString(@Nullable Event e, boolean d) {
        return switch (pattern) {
            case 0 -> String.format("minecraft %s tag[s] %s", TAG_TYPE[tagPattern], strings.toString(e, d));
            case 1 -> String.format("all minecraft %s tag[s]", TAG_TYPE[tagPattern]);
            case 2 ->
                    String.format("all itemtypes of minecraft %s tag[s] %s", TAG_TYPE[tagPattern], strings.toString(e, d));
            default -> null;
        };
    }

}
