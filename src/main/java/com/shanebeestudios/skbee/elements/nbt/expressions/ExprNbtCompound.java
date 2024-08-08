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
import ch.njol.skript.util.LiteralUtils;
import ch.njol.skript.util.slot.Slot;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.nbt.NBTApi;
import com.shanebeestudios.skbee.api.nbt.NBTCustom;
import com.shanebeestudios.skbee.api.nbt.NBTCustomBlock;
import com.shanebeestudios.skbee.api.nbt.NBTCustomEntity;
import com.shanebeestudios.skbee.api.nbt.NBTCustomItemStack;
import com.shanebeestudios.skbee.api.nbt.NBTCustomItemType;
import com.shanebeestudios.skbee.api.nbt.NBTCustomSlot;
import com.shanebeestudios.skbee.api.nbt.NBTCustomTileEntity;
import de.tr7zw.changeme.nbtapi.NBTChunk;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("deprecation")
@Name("NBT - Compound of Object")
@Description({"Get the NBT compound of a block/entity/item/file/chunk.",
    "",
    "SPECIAL CASES:",
    "- `full nbt of %item%` = Returns a copy of the FULL NBT of an item (this includes id, count and 'tag/components' compound).",
    "Modifying this will have no effect on the original item. This is useful for serializing items.",
    "- `nbt of %item%` = Returns the original. Modifying this will modify the original item.",
    "\\- (MC 1.20.4-) This will return the 'tag' portion of an items full NBT.",
    "\\- (MC 1.20.5+) This will return the 'components' portion of an item's full NBT.",
    "- `custom nbt of %item%` = Returns the 'minecraft:custom_data' component of an item's NBT. Modifying this will modify the original item. (This is an MC 1.20.5+ feature).",
    "Please see [**Data Component Format**](https://minecraft.wiki/w/Data_component_format) on McWiki for more information on item NBT components.",
    "- `nbt copy of %objects%` = Returns a copy of the original NBT compound. This way you can modify it without",
    "actually modifying the original NBT compound, for example when grabbing the compound from an entity, modifying it and applying to other entities.",
    "",
    "NBT from a file will need to be saved manually using",
    "the 'NBT - Save File' effect. If the file does not yet exist, a new file will be created.",
    "",
    "CHANGERS:",
    "- `add` = Adding a compound to another compound will merge them (This is controlled by Minecraft, results may vary).",
    "- `delete` = Will delete NBT files, or clear the NBT compound (This can break entities/players, be careful!).",
    "- `reset` = Will clear the NBT compound (This can break entities/players, be careful!)"})
@Examples({"set {_n} to nbt of player's tool",
    "set {_n} to full nbt of player's tool",
    "set {_nbt} to nbt of target entity",
    "set {_n} to nbt of chunk at player",
    "set {_n} to nbt copy of player",
    "set {_n} to nbt from \"{components:{\"\"minecraft:enchantments\"\":{levels:{\"\"minecraft:sharpness\"\":3}}},count:1,id:\"\"minecraft:diamond_sword\"\"}\"",
    "set {_nbt} to nbt of file \"world/playerdata/some-uuid.dat\"",
    "",
    "add nbt from \"{NoAI:1}\" to nbt of last spawned entity",
    "add nbt from \"{NoGravity:1}\" to nbt of {_entity}",
    "add nbt from \"{custom:{points:1}}\" to nbt of player",
    "add nbt from \"{\"\"minecraft:enchantments\"\":{levels:{\"\"minecraft:sharpness\"\":10}}}\" to component nbt of player's tool",
    "",
    "delete nbt from file \"plugins/some/file.nbt\"",
    "reset {_n}",
    "reset nbt of player's tool"})
@Since("1.6.0")
public class ExprNbtCompound extends PropertyExpression<Object, NBTCompound> {

    static {
        Skript.registerExpression(ExprNbtCompound.class, NBTCompound.class, ExpressionType.PROPERTY,
            "[(:full|:custom)] nbt [compound] [:copy] (of|from) %objects%",
            "nbt [compound] [:copy] (of|from) file[s] %strings%"
        );
    }

    private boolean isFullItem;
    private boolean isCustom;
    private boolean isCopy;
    private boolean isFile;

    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        Expression<?> expr = LiteralUtils.defendExpression(exprs[0]);
        setExpr(expr);
        this.isFullItem = parseResult.hasTag("full");
        this.isCustom = parseResult.hasTag("custom") && NBTApi.HAS_ITEM_COMPONENTS;
        this.isCopy = parseResult.hasTag("copy");
        this.isFile = matchedPattern == 1;
        return LiteralUtils.canInitSafely(expr);
    }

    @Override
    protected NBTCompound @NotNull [] get(@NotNull Event e, Object @NotNull [] source) {
        return get(source, object -> {
            NBTCompound compound = null;
            if (object instanceof TileState tileState) {
                compound = new NBTCustomTileEntity(tileState);
            } else if (object instanceof Block block) {
                // We shouldn't be adding NBT to air
                if (block.getType().isAir()) return null;
                if (block.getState() instanceof TileState tileState) {
                    compound = new NBTCustomTileEntity(tileState);
                } else {
                    compound = new NBTCustomBlock(block);
                }
            } else if (object instanceof Player player) {
                compound = new NBTCustomEntity(player);
            } else if (object instanceof OfflinePlayer offlinePlayer) {
                compound = NBTApi.getNBTOfflinePlayer(offlinePlayer);
            } else if (object instanceof Entity entity) {
                compound = new NBTCustomEntity(entity);
            } else if (object instanceof ItemType itemType) {
                if (itemType.getMaterial() == Material.AIR) return null;
                if (this.isFullItem) {
                    compound = NBTItem.convertItemtoNBT(itemType.getRandom());
                } else {
                    compound = new NBTCustomItemType(itemType, this.isCustom);
                }
            } else if (object instanceof ItemStack itemStack) {
                if (itemStack.getType() == Material.AIR) return null;
                if (this.isFullItem) {
                    return NBTItem.convertItemtoNBT(itemStack);
                } else {
                    compound = new NBTCustomItemStack(itemStack, this.isCustom);
                }
            } else if (object instanceof Slot slot) {
                ItemStack stack = slot.getItem();
                if (stack == null || stack.getType() == Material.AIR) return null;

                if (this.isFullItem) {
                    compound = NBTItem.convertItemtoNBT(stack);
                } else {
                    compound = new NBTCustomSlot(slot, this.isCustom);
                }
            } else if (object instanceof String nbtString) {
                if (this.isFile) {
                    compound = NBTApi.getNBTFile(nbtString);
                } else {
                    compound = NBTApi.validateNBT(nbtString);
                }
            } else if (object instanceof Chunk chunk) {
                compound = new NBTChunk(chunk).getPersistentDataContainer();
            } else if (object instanceof NBTCompound comp) {
                compound = comp;
            }
            if (compound != null) {
                if (this.isCopy) {
                    if (compound instanceof NBTCustom nbtCustom) {
                        return nbtCustom.getCopy();
                    }
                    NBTContainer emptyContainer = new NBTContainer();
                    emptyContainer.mergeCompound(compound);
                    compound = emptyContainer;
                }
                return compound;
            }
            return null;
        });
    }

    @Override
    public @NotNull Class<? extends NBTCompound> getReturnType() {
        return NBTCompound.class;
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        String full = this.isFullItem ? "full " : this.isCustom ? "custom " : "";
        String copy = this.isCopy ? "copy " : "";
        return full + "nbt " + copy + "from " + getExpr().toString(e, d);
    }

}
