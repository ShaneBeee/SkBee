package com.shanebeestudios.skbee.elements.tag.conditions;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.slot.Slot;
import ch.njol.util.Kleenean;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

@Name("Minecraft Tag - Is Tagged")
@Description("Check if an item/block/entity is tagged with a Minecraft Tag.")
@Examples("if player's tool is tagged as {_mcTag}:")
@Since("2.6.0")
@SuppressWarnings("rawtypes")
public class CondIsMinecraftTagged extends Condition {

    static {
        PropertyCondition.register(CondIsMinecraftTagged.class, PropertyCondition.PropertyType.BE,
                "tagged (with|as) [minecraft[ ]tag[s]] %minecrafttags%",
                "itemtypes/slots/itemstacks/blocks/blockdatas/entities");
    }

    private Expression<Object> objects;
    private Expression<Tag> tags;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.objects = (Expression<Object>) exprs[0];
        this.tags = (Expression<Tag>) exprs[1];
        setNegated(matchedPattern == 1);
        return true;
    }

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean check(Event event) {
        return this.objects.check(event, object -> {
            if (object instanceof Entity entity) {
                return tags.check(event, tag -> tag.isTagged(entity.getType()), isNegated());
            } else {
                Material material = null;
                if (object instanceof ItemStack itemStack) material = itemStack.getType();
                else if (object instanceof ItemType itemType) material = itemType.getMaterial();
                else if (object instanceof Slot slot) {
                    ItemStack item = slot.getItem();
                    if (item != null) {
                        material = item.getType();
                    }
                } else if (object instanceof Block block) material = block.getType();
                else if (object instanceof BlockData blockData) material = blockData.getMaterial();

                if (material != null) {
                    Material finalMaterial = material;
                    return tags.check(event, tag -> tag.isTagged(finalMaterial), isNegated());
                }
            }
            return false;
        });
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String negated = isNegated() ? "is not" : "is";
        return this.objects.toString(e, d) + " " + negated + " tagged with " + this.tags.toString(e, d);
    }

}
