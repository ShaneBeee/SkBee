package com.shanebeestudios.skbee.elements.nbt.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.slot.Slot;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.NBT.NBTApi;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Name("NBT - Item/Inventory Slot/Entity/Block/File")
@Description({"NBT of items, inventory slots, entities, tile entities (such as a furnace, hopper, brewing stand, banner, etc) or files. ",
        "Supports get, set, add and reset. Reset will only properly work on an item, not entities or blocks. ",
        "Set should not be used on entities or blocks, it's best to use add. Using set can quite often screw up the entity/block's location. ",
        "The optional 'full' part (added in 1.4.10) will only work on items/slots. When using this, it will return the full NBT of said item, ",
        "including the item amount as well as the item type.",
        "\nREMOVED IN 'INSERT VERSION'"})
@Examples({"set {_nbt} to nbt of player's tool", "set {_f} to full nbt of player's tool",
        "add \"{Enchantments:[{id:\"\"sharpness\"\",lvl:5}]}\" to nbt of player's tool",
        "reset nbt of player's tool", "set {_nbt} to nbt of target entity", "set {_nbt} to event-entity",
        "add \"{CustomName:\"\"{\\\"\"text\\\"\":\\\"\"&bMyNewName\\\"\"}\"\"}\" to nbt of target entity",
        "add \"{RequiredPlayerRange:0s}\" to targeted block's nbt", "add \"{SpawnData:{id:\"\"minecraft:wither\"\"}}\" to nbt of clicked block",
        "set {_nbt} to file-nbt of \"world/playerdata/some-uuid-here.dat\""})
@Since("1.0.0")
public class ExprObjectNBT extends SimplePropertyExpression<Object, Object> {

    static {
        register(ExprObjectNBT.class, Object.class, "[(1¦full )][(entity|item|slot|block|tile[(-| )]entity|file)(-| )]nbt",
                "block/entity/itemstack/itemtype/slot/string");
    }

    private boolean full;

    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, ParseResult parseResult) {
        Skript.error("SkBee expression 'nbt of %object%' has been removed. Please use NBT compounds instead.");
        return false;
    }

    @Override
    public String convert(@NotNull Object object) {
        return null;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "object nbt";
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }

}
