package com.shanebeestudios.skbee.elements.itemcomponent.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.SyntaxStringBuilder;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.skript.Experiments;
import com.shanebeestudios.skbee.api.util.ItemComponentUtils;
import com.shanebeestudios.skbee.api.util.ItemUtils;
import com.shanebeestudios.skbee.api.util.Util;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Repairable;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.set.RegistrySet;
import io.papermc.paper.registry.tag.TagKey;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Tag;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("ItemComponent - Repairable")
@Description({"Represents the items/tags that will be used to repair an item in an anvil.",
    "See [**Repairable Component**](https://minecraft.wiki/w/Data_component_format#repairable) on McWiki for more details.",
    "Requires Paper 1.21.3+ and `item_component` feature.",
    "",
    "**Patterns**:",
    "`repairable items` = A list of items that are used.",
    "`repairable tag` = A single Minecraft item tag that is used.",
    "",
    "**Changers**:",
    "- `set` = Set the items/tag to allow for repairing.",
    "- `reset` = Reset back to default state.",
    "- `delete` = Will delete any value (vanilla or not)."})
@Examples({"set {_items::*} to repairable items of player's tool",
    "set {_tag} to repairable tag of player's tool",
    "set repairable items of player's tool to diamond, iron ingot and emerald",
    "set repairable tag of player's tool to minecraft item tag \"diamond_tool_materials\"",
    "delete repairable tag of player's tool",
    "reset repairable tag of player's tool"})
@Since("3.8.0")
@SuppressWarnings("UnstableApiUsage")
public class ExprRepairableComponent extends SimpleExpression<Object> {

    private static final RegistryAccess REGISTRY_ACCESS = RegistryAccess.registryAccess();
    @SuppressWarnings("NullableProblems")
    private static final Registry<ItemType> ITEM_REGISTRY = REGISTRY_ACCESS.getRegistry(RegistryKey.ITEM);

    static {
        Skript.registerExpression(ExprRepairableComponent.class, Object.class, ExpressionType.COMBINED,
            "repairable [component] items of %itemstacks/itemtypes/slots%",
            "repairable [component] tag of %itemstacks/slots%");
    }

    private Expression<Object> items;
    private boolean tag;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        if (!getParser().hasExperiment(Experiments.ITEM_COMPONENT)) {
            Skript.error("requires '" + Experiments.ITEM_COMPONENT.codeName() + "' feature.");
            return false;
        }
        this.items = (Expression<Object>) exprs[0];
        this.tag = matchedPattern == 1;
        return true;
    }

    @SuppressWarnings({"NullableProblems", "deprecation"})
    @Override
    protected Object @Nullable [] get(Event event) {
        List<Object> repairables = new ArrayList<>();

        for (Object object : this.items.getArray(event)) {
            ItemStack itemStack = ItemUtils.getItemStackFromObjects(object);
            if (itemStack == null || !itemStack.hasData(DataComponentTypes.REPAIRABLE)) continue;

            Repairable data = itemStack.getData(DataComponentTypes.REPAIRABLE);
            if (data == null) continue;

            RegistryKeySet<ItemType> types = data.types();
            if (this.tag && types instanceof io.papermc.paper.registry.tag.Tag<ItemType> paperTag) {
                Key key = paperTag.tagKey().key();
                NamespacedKey nKey = Util.getNamespacedKey(key.toString(), false);
                if (nKey == null) continue;

                Tag<Material> bukkitTag = Bukkit.getTag(Tag.REGISTRY_ITEMS, nKey, Material.class);
                repairables.add(bukkitTag);
            } else if (!this.tag) {
                for (TypedKey<ItemType> type : types) {
                    ItemType itemType = ITEM_REGISTRY.get(type);
                    assert itemType != null;
                    Material material = itemType.asMaterial();
                    if (material == null) continue;
                    repairables.add(new ch.njol.skript.aliases.ItemType(material));
                }
            }
        }

        return repairables.toArray(this.tag ? new Tag[0] : new ch.njol.skript.aliases.ItemType[0]);
    }

    @Override
    public Class<?> @Nullable [] acceptChange(Changer.ChangeMode mode) {
        return switch (mode) {
            case SET -> CollectionUtils.array(ch.njol.skript.aliases.ItemType[].class, Tag.class);
            case DELETE, RESET -> CollectionUtils.array();
            default -> null;
        };
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void change(Event event, Object @Nullable [] delta, Changer.ChangeMode mode) {

        Repairable repairable = null;
        if (delta != null) {
            if (delta[0] instanceof Tag<?> bukkitTag) {
                TagKey<ItemType> itemTypeTagKey = TagKey.create(RegistryKey.ITEM, bukkitTag.key());
                io.papermc.paper.registry.tag.Tag<ItemType> tag = ITEM_REGISTRY.getTag(itemTypeTagKey);
                repairable = Repairable.repairable(tag);
            } else {
                List<TypedKey<ItemType>> keys = new ArrayList<>();
                for (Object object : delta) {
                    if (object instanceof ch.njol.skript.aliases.ItemType itemType) {
                        TypedKey<ItemType> typedKey = TypedKey.create(RegistryKey.ITEM, itemType.getMaterial().key());
                        keys.add(typedKey);
                    }
                }
                if (!keys.isEmpty()) {
                    RegistryKeySet<ItemType> registryKeySet = RegistrySet.keySet(RegistryKey.ITEM, keys);
                    repairable = Repairable.repairable(registryKeySet);
                }
            }
        }

        ItemComponentUtils.modifyComponent(this.items.getArray(event), mode, DataComponentTypes.REPAIRABLE, repairable);
    }

    @Override
    public boolean isSingle() {
        return this.tag;
    }

    @Override
    public Class<?> getReturnType() {
        return this.tag ? Tag.class : ch.njol.skript.aliases.ItemType.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        SyntaxStringBuilder builder = new SyntaxStringBuilder(event, debug);
        builder.append("repairable");
        builder.append(this.tag ? "tag" : "items");
        builder.append("of", this.items);
        return builder.toString();
    }

}
