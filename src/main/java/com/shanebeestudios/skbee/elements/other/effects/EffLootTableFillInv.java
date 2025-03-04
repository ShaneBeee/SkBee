package com.shanebeestudios.skbee.elements.other.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

@Name("LootTable - Fill Inventory")
@Description({"Fill an inventory with a predefined LootTable.",
    "This inventory must belong to a block or entity (custom inventories will not work).",
    "Optionals:",
    "Some loot tables will require some of these values whereas others may not.",
    "`seed` = Represents the random seed used to generate loot (if not provided will generate randomly).",
    "`looting modifier` = Set the looting enchant level equivalent to use when generating loot.",
    "Values less than or equal to 0 will force the LootTable to only return a single Item per pool.",
    "`luck` = How much luck to have when generating loot.",
    "`killer/looter` = The Player that killed/looted. This Player will be used to get the looting level if looting modifier is not set.",
    "`looted entity` = The Entity that was killed/looted."})
@Examples({"set {_loottable} to loottable from key \"minecraft:chests/ancient_city\"",
    "fill inventory of target block from {_loottable}",
    "fill inventory of player from (loottable from key \"minecraft:gameplay/fishing\")"})
@Since("3.4.0")
public class EffLootTableFillInv extends Effect {

    static {
        Skript.registerEffect(EffLootTableFillInv.class,
            "fill %inventories% from %loottable% [with seed %-number%] [with looting modifier %-number%] " +
                "[with luck %-number%] [with (killer|looter) %-player%] [with looted entity %-entity%]");
    }

    private Expression<Inventory> inventories;
    private Expression<LootTable> lootTable;
    private Expression<Number> seed;
    private Expression<Number> lootingModifier;
    private Expression<Number> luck;
    private Expression<Player> killer;
    private Expression<Entity> lootedEntity;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.inventories = (Expression<Inventory>) exprs[0];
        this.lootTable = (Expression<LootTable>) exprs[1];
        this.seed = (Expression<Number>) exprs[2];
        this.lootingModifier = (Expression<Number>) exprs[3];
        if (Util.IS_RUNNING_MC_1_21 && this.lootingModifier != null) {
            Skript.error("'with looting modifier' is no longer functional!");
            return false;
        }
        this.luck = (Expression<Number>) exprs[4];
        this.killer = (Expression<Player>) exprs[5];
        this.lootedEntity = (Expression<Entity>) exprs[6];
        return true;
    }

    @SuppressWarnings({"NullableProblems", "CallToPrintStackTrace", "deprecation"})
    @Override
    protected void execute(Event event) {
        LootTable lootTable = this.lootTable.getSingle(event);
        Number seed = this.seed != null ? this.seed.getSingle(event) : null;
        Number lootingModifier = this.lootingModifier != null ? this.lootingModifier.getSingle(event) : null;
        Number luck = this.luck != null ? this.luck.getSingle(event) : null;
        Player killer = this.killer != null ? this.killer.getSingle(event) : null;
        Entity lootedEntity = this.lootedEntity != null ? this.lootedEntity.getSingle(event) : null;
        if (lootTable == null) return;

        for (Inventory inventory : this.inventories.getArray(event)) {
            Location location = inventory.getLocation();
            if (location == null) continue;

            Random random = seed != null ? new Random(seed.longValue()) : new Random();

            LootContext.Builder builder = new LootContext.Builder(location);
            if (lootingModifier != null) builder.lootingModifier(lootingModifier.intValue());
            if (luck != null) builder.luck(luck.floatValue());
            if (killer != null) {
                builder.killer(killer);
            } else if (inventory instanceof PlayerInventory playerInventory) {
                // Default the player as the killer/looter
                builder.killer(playerInventory.getHolder());
            }
            if (lootedEntity != null) builder.lootedEntity(lootedEntity);

            try {
                lootTable.fillInventory(inventory, random, builder.build());
            } catch (IllegalArgumentException ex) {
                // Minecraft likes to throw errors if things are missing, suck as killer's tool
                if (SkBee.isDebug()) {
                    ex.printStackTrace();
                }
            }
        }
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String inv = this.inventories.toString(e, d);
        String lootTable = this.lootTable.toString(e, d);
        String seed = this.seed != null ? (" with seed " + this.seed.toString(e, d)) : "";
        String mod = this.lootingModifier != null ? (" with lootiing modifier " + this.lootingModifier.toString(e, d)) : "";
        String luck = this.luck != null ? (" with luck " + this.luck.toString(e, d)) : "";
        String killer = this.killer != null ? (" with killer " + this.killer.toString(e, d)) : "";
        String looted = this.lootedEntity != null ? (" with looted entity " + this.lootedEntity.toString(e, d)) : "";
        return String.format("fill %s from %s %s%s%s%s%s", inv, lootTable, seed, mod, luck, killer, looted);
    }

}
