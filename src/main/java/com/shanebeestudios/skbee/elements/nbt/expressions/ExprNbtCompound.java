package com.shanebeestudios.skbee.elements.nbt.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.parser.ParsingStack;
import ch.njol.skript.util.LiteralUtils;
import ch.njol.skript.util.slot.Slot;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.nbt.NBTApi;
import com.shanebeestudios.skbee.api.nbt.NBTCustom;
import com.shanebeestudios.skbee.api.nbt.NBTCustomBlock;
import com.shanebeestudios.skbee.api.nbt.NBTCustomEntity;
import com.shanebeestudios.skbee.api.nbt.NBTCustomItemStack;
import com.shanebeestudios.skbee.api.nbt.NBTCustomItemType;
import com.shanebeestudios.skbee.api.nbt.NBTCustomSlot;
import com.shanebeestudios.skbee.api.nbt.NBTCustomTileEntity;
import com.shanebeestudios.skbee.api.skript.base.PropertyExpression;
import com.shanebeestudios.skbee.elements.nbt.conditions.CondHasNBTTag;
import de.tr7zw.changeme.nbtapi.NBTChunk;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import org.bukkit.Bukkit;
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
    "Minecraft versions below 1.20.5, this will just return the NBT of the item.",
    "- `[full] vanilla nbt of %item%` = Will return the same as above except it will include vanilla components which don't normally show in NBT.",
    "- `nbt copy of %objects%` = Returns a copy of the original NBT compound. This way you can modify it without",
    "actually modifying the original NBT compound, for example when grabbing the compound from an entity, modifying it and applying to other entities.",
    "- `custom nbt of %object%` = Returns the custom portion of the NBT of the object " +
        "(Minecraft doesn't natively allow custom NBT, so this is stored in varies spots based on the object).",
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

    private static final boolean ALLOW_UNSAFE_OPERATIONS = SkBee.getPlugin().getPluginConfig().NBT_ALLOW_UNSAFE_OPERATIONS;

    static {
        Skript.registerExpression(ExprNbtCompound.class, NBTCompound.class, ExpressionType.PROPERTY,
            "[:full] [:vanilla] [:custom] nbt [compound] [:copy] (of|from) %objects%",
            "nbt [compound] [:copy] (of|from) file[s] %strings%"
        );
    }

    private boolean isFullItem;
    private boolean isVanilla;
    private boolean isCustom;
    private boolean isCopy;
    private boolean isFile;
    private boolean enforceTagCreationIfMissing;

    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        Expression<?> expr = LiteralUtils.defendExpression(exprs[0]);
        setExpr(expr);
        this.isFullItem = parseResult.hasTag("full");
        this.isVanilla = parseResult.hasTag("vanilla");
        this.isCustom = parseResult.hasTag("custom");
        this.isCopy = parseResult.hasTag("copy");
        this.isFile = matchedPattern == 1;
        // avoid creating new compounds if all we're doing is checking if they exist.
        ParsingStack stack = getParser().getParsingStack();
        this.enforceTagCreationIfMissing = stack.isEmpty() || stack.peek().getSyntaxElementClass() != CondHasNBTTag.class;
        return LiteralUtils.canInitSafely(expr);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected NBTCompound[] get(@NotNull Event e, Object @NotNull [] source) {
        if (!Bukkit.isPrimaryThread() && !ALLOW_UNSAFE_OPERATIONS) {
            error("NBT cannot be retrieved off the main thread.");
            return null;
        }
        return get(source, object -> {
            NBTCompound compound = null;
            if (object instanceof TileState tileState) {
                compound = new NBTCustomTileEntity(tileState);
            } else if (object instanceof Block block) {
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
                compound = new NBTCustomItemType(itemType, this.isVanilla, this.isFullItem);
            } else if (object instanceof ItemStack itemStack) {
                if (itemStack.getType() == Material.AIR) return null;
                compound = new NBTCustomItemStack(itemStack, this.isVanilla, this.isFullItem);
            } else if (object instanceof Slot slot) {
                ItemStack stack = slot.getItem();
                if (stack == null || stack.getType() == Material.AIR) return null;
                compound = new NBTCustomSlot(slot, this.isVanilla, this.isFullItem);
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
                if (this.isCustom && !this.isFullItem && compound instanceof NBTCustom nbtCustom) {
                    compound = nbtCustom.getCustomNBT(enforceTagCreationIfMissing);
                }
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
