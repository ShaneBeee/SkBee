package com.shanebeestudios.skbee.elements.other.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.registrations.Classes;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.skript.base.Effect;
import com.shanebeestudios.skbee.api.util.ItemUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Name("Give or Drop Item")
@Description("Attempts to give an item to a player and if they dont have room it will drop instead.")
@Examples({"give or drop a diamond to player",
    "give or drop {_items::*} to all players"})
@Since("2.14.0")
public class EffGiveOrDrop extends Effect {

    static {
        Skript.registerEffect(EffGiveOrDrop.class, "give or drop %itemtypes% to %players%");
    }

    private Expression<ItemType> itemTypes;
    private Expression<Player> players;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.itemTypes = (Expression<ItemType>) exprs[0];
        this.players = (Expression<Player>) exprs[1];
        return true;
    }

    @Override
    protected void execute(Event event) {
        ItemType[] itemTypes = this.itemTypes.getArray(event);
        List<ItemStack> itemStacks = ItemUtils.addItemTypesToList(Arrays.asList(itemTypes), null);
        if (itemStacks.isEmpty()) {
            for (ItemType itemType : itemTypes) {
                if (itemType.getMaterial().isAir() || !itemType.getMaterial().isItem()) {
                    error(Classes.toString(itemType) + " cannot be given/dropped.");
                    return;
                }
            }
            error("Missing or invalid items: " + this.itemTypes.toString(event, true));
            return;
        }
        ItemStack[] itemStacksArray = itemStacks.toArray(itemStacks.toArray(new ItemStack[0]));

        for (Player player : this.players.getArray(event)) {
            HashMap<Integer, ItemStack> leftOvers = player.getInventory().addItem(itemStacksArray);
            if (!leftOvers.isEmpty()) {
                Location location = player.getLocation();
                World world = location.getWorld();
                leftOvers.values().forEach(leftOver -> world.dropItem(location, leftOver));
            }
        }
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        return "give or drop " + this.itemTypes.toString(e, d) + " to " + this.players.toString(e, d);
    }

}
