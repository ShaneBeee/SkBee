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
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Name("Minecraft Tag - Custom Tag")
@Description("Create your own custom tag with items of your choosing. Requires a PaperMC server.")
@Examples({"set {_key} to namespaced key from \"custom:diamond_items\"",
        "set {_tag} to custom minecraft tag with id {_id} to include (all items where [\"%input%\" contains \"diamond\"])"})
@Since("INSERT VERSION")
@SuppressWarnings("rawtypes")
public class ExprTagCustom extends SimpleExpression<Tag> {

    static {
        Skript.registerExpression(ExprTagCustom.class, Tag.class, ExpressionType.COMBINED,
                "custom minecraft tag with (key|id) %namespacedkey% to include %itemtypes%");
    }

    private Expression<NamespacedKey> key;
    private Expression<ItemType> itemTypes;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        if (!Skript.classExists("com.destroystokyo.paper.MaterialSetTag")) {
            Skript.error("Custom Minecraft Tags require a PaperMC server.");
            return false;
        }
        this.key = (Expression<NamespacedKey>) exprs[0];
        this.itemTypes = (Expression<ItemType>) exprs[1];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable Tag[] get(Event event) {
        List<Material> materials = new ArrayList<>();
        NamespacedKey key = this.key.getSingle(event);
        if (key != null) {
            for (ItemType itemType : this.itemTypes.getArray(event)) {
                materials.add(itemType.getMaterial());
            }
            if (materials.size() > 0) {
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
        return "custom minecraft tag with key " + this.key.toString(e, d) +
                " to include items " + this.itemTypes.toString(e, d);
    }

}
