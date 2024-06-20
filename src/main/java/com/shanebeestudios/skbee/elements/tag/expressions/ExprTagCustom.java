package com.shanebeestudios.skbee.elements.tag.expressions;

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
import com.destroystokyo.paper.MaterialSetTag;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("Minecraft Tag - Custom Tag")
@Description("Create your own custom tag with items of your choosing. Requires a PaperMC server.")
@Examples({"set {_key} to namespaced key from \"custom:diamond_items\"",
        "set {_tag} to custom minecraft tag with id {_id} to include (all items where [\"%input%\" contains \"diamond\"])",
        "set {_tag} to custom minecraft tag with id \"custom:swords\" to include every sword"})
@Since("2.6.0")
@SuppressWarnings("rawtypes")
public class ExprTagCustom extends SimpleExpression<Tag> {

    static {
        Skript.registerExpression(ExprTagCustom.class, Tag.class, ExpressionType.COMBINED,
                "custom minecraft tag with (key|id) %namespacedkey/string% to include %itemtypes%");
    }

    private Expression<?> object;
    private Expression<ItemType> itemTypes;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        if (!Skript.classExists("com.destroystokyo.paper.MaterialSetTag")) {
            Skript.error("Custom Minecraft Tags require a PaperMC server.");
            return false;
        }
        this.object = exprs[0];
        this.itemTypes = (Expression<ItemType>) exprs[1];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable Tag[] get(Event event) {
        List<Material> materials = new ArrayList<>();

        NamespacedKey key;
        Object object = this.object.getSingle(event);
        if (object instanceof NamespacedKey namespacedKey) key = namespacedKey;
        else if (object instanceof String string) key = Util.getNamespacedKey(string, false);
        else return null;

        if (key != null) {
            for (ItemType itemType : this.itemTypes.getArray(event)) {
                itemType.getAll().forEach(itemStack -> {
                    Material material = itemStack.getType();
                    if (!materials.contains(material)) materials.add(material);
                });
            }
            if (!materials.isEmpty()) {
                return new Tag[]{new MaterialSetTag(key, materials)};
            }
        }
        return null;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends Tag> getReturnType() {
        return Tag.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "custom minecraft tag with key " + this.object.toString(e, d) +
                " to include items " + this.itemTypes.toString(e, d);
    }

}
