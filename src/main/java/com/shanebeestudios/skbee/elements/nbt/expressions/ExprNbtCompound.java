package com.shanebeestudios.skbee.elements.nbt.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.slot.Slot;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.NBT.NBTApi;
import com.shanebeestudios.skbee.api.NBT.NBTCustomBlock;
import com.shanebeestudios.skbee.api.NBT.NBTCustomEntity;
import com.shanebeestudios.skbee.api.NBT.NBTCustomTileEntity;
import com.shanebeestudios.skbee.api.NBT.NBTItemType;
import de.tr7zw.changeme.nbtapi.NBTChunk;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

@Name("NBT - Compound of")
@Description({"Get the nbt compound of a block/entity/item/file/chunk. Optionally you can return a copy of the compound. This way you can modify it without",
        "actually modifying the original NBT compound, for example when grabbing the compound from an entity, modifying it and applying to",
        "other entities.",
        "\n'nbt compound' from an item will return a copy of the FULL nbt of an item (this includes id, count and 'tag' compound).",
        "Modifying this will have no effect on the original item.",
        "\n'nbt item compound' from an item will be the original. This will return the 'tag' portion of an items full NBT.",
        "Modifying this will modify the original item.",
        "\nNBT from a file will need to be saved manually using",
        "the 'NBT - Save File effect'. If the file does not yet exist, a new file will be created."})
@Examples({"set {_n} to nbt item compound of player's tool",
        "set {_nbt} to nbt compound of target entity",
        "set {_n} to nbt compound of \"{id:\"\"minecraft:diamond_sword\"\",tag:{Damage:0,Enchantments:[{id:\"\"minecraft:sharpness\"\",lvl:3s}]},Count:1b}\"",
        "set {_nbt} to nbt compound of file \"world/playerdata/some-uuid.dat\"",
        "set {_n} to nbt compound of chunk at player"})
@Since("1.6.0")
public class ExprNbtCompound extends PropertyExpression<Object, NBTCompound> {

    static {
        Skript.registerExpression(ExprNbtCompound.class, NBTCompound.class, ExpressionType.PROPERTY,
                "nbt item compound [(1¦copy)] (of|from) %itemtypes/itemstacks/slots%",
                "nbt compound [(1¦copy)] (of|from) %blocks/entities/itemtypes/itemstacks/slots/strings/chunks%",
                "nbt compound [(1¦copy)] (of|from) file[s] %strings%");
    }

    private boolean isItem;
    private boolean isCopy;
    private boolean isFile;

    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        setExpr(exprs[0]);
        isItem = matchedPattern == 0;
        isCopy = parseResult.mark == 1;
        isFile = matchedPattern == 2;
        return true;
    }

    @Override
    protected NBTCompound @NotNull [] get(@NotNull Event e, Object @NotNull [] source) {
        return get(source, object -> {
            NBTCompound compound = null;
            if (object instanceof Block block) {
                if (block.getState() instanceof TileState tileState) {
                    compound = new NBTCustomTileEntity(tileState);
                } else if (NBTApi.supportsBlockNBT()) {
                    compound = new NBTCustomBlock(block).getData();
                }
            } else if (object instanceof Entity entity) {
                compound = new NBTCustomEntity(entity);
            } else if (object instanceof ItemType itemType) {
                if (isItem) {
                    if (itemType.getMaterial() == Material.AIR) return null;
                    compound = new NBTItemType(itemType);
                } else {
                    compound = NBTItem.convertItemtoNBT(itemType.getRandom());
                }
            } else if (object instanceof ItemStack itemStack) {
                if (isItem) {
                    if (itemStack.getType() == Material.AIR) return null;
                    compound = new NBTItem(itemStack, true);
                } else {
                    return NBTItem.convertItemtoNBT(itemStack);
                }
            } else if (object instanceof Slot slot) {
                ItemStack stack = slot.getItem();
                if (stack == null) return null;

                if (isItem) {
                    if (stack.getType() == Material.AIR) return null;
                    compound = new NBTItem(stack, true);
                } else {
                    compound = NBTItem.convertItemtoNBT(stack);
                }
            } else if (object instanceof String nbtString) {
                if (isFile) {
                    compound = NBTApi.getNBTFile(nbtString);
                } else {
                    compound = NBTApi.validateNBT(nbtString);
                }
            } else if (object instanceof Chunk chunk) {
                compound = new NBTChunk(chunk).getPersistentDataContainer();
            }
            if (compound != null) {
                if (isCopy) {
                    return new NBTContainer(compound.toString());
                } else {
                    return compound;
                }
            }
            return null;
        });
    }

    @Override
    public @NotNull Class<? extends NBTCompound> getReturnType() {
        return NBTCompound.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String item = this.isItem ? "item" : "";
        String copy = this.isCopy ? "copy" : "";
        return "nbt " + item + " compound " + copy + " from " + getExpr().toString(e, d);
    }

}
