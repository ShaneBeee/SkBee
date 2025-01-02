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
import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;

@Name("Open Real Inventory")
@Description({"Open real inventories to players.",
    "This will open a real inventory object instead of a custom inventory object to players.",
    "Most of these (except enchanting and workbench) require a PaperMC server.",
    "`at %location%` is used to open an inventory at an actual block (Optional, will default to player's location).",
    "Some inventories require a location of a block for extra functionality," +
        "such as an enchanting table uses nearby bookshelves to determine enchantment level."})
@Examples({"open real anvil inventory to player",
    "open real anvil named \"Mr Anvil\" to player",
    "open real anvil at location(1,1,1) named \"Senor Anvil\" to player"})
@Since("3.6.0")
public class EffOpenRealInventory extends Effect {

    private static final boolean HAS_PAPER = Skript.methodExists(HumanEntity.class, "openAnvil", Location.class, boolean.class);

    enum InventoryViewType {
        ANVIL("anvil", true),
        CARTOGRAPHY("cartography [table]", true),
        ENCHANTING("enchanting [table]", false),
        GRINDSTONE("grindstone", true),
        LOOM("loom", true),
        SMITHING("smithing [table]", true),
        STONECUTTER("stonecutter", true),
        WORKBENCH("workbench", false);

        private final String name;
        private final boolean canUse;

        InventoryViewType(String name, boolean requiresPaper) {
            this.name = name;
            this.canUse = !requiresPaper || HAS_PAPER;
        }
    }

    static {
        InventoryViewType[] viewTypes = InventoryViewType.values();
        int size = viewTypes.length;
        String[] patterns = new String[size];
        for (int i = 0; i < size; i++) {
            patterns[i] = "open real " + viewTypes[i].name + " [inventory] [at %-location%] [named %-string%] to %players%";
        }
        Skript.registerEffect(EffOpenRealInventory.class, patterns);
    }

    private InventoryViewType viewType;
    private Expression<Location> location;
    private Expression<String> name;
    private Expression<Player> players;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.viewType = InventoryViewType.values()[matchedPattern];
        if (!this.viewType.canUse) {
            Skript.error("'open real " + this.viewType.name + "' requires PaperMC.");
            return false;
        }
        this.location = (Expression<Location>) exprs[0];
        this.name = (Expression<String>) exprs[1];
        this.players = (Expression<Player>) exprs[2];
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void execute(Event event) {
        Location location = this.location != null ? this.location.getSingle(event) : null;
        String name = this.name != null ? this.name.getSingle(event) : null;

        for (Player player : this.players.getArray(event)) {
            InventoryView inventoryView = switch (this.viewType) {
                case ANVIL -> player.openAnvil(location, true);
                case CARTOGRAPHY -> player.openCartographyTable(location, true);
                case ENCHANTING -> player.openEnchanting(location, true);
                case GRINDSTONE -> player.openGrindstone(location, true);
                case LOOM -> player.openLoom(location, true);
                case SMITHING -> player.openSmithingTable(location, true);
                case STONECUTTER -> player.openStonecutter(location, true);
                case WORKBENCH -> player.openWorkbench(location, true);
            };
            if (inventoryView != null && name != null) inventoryView.setTitle(name);
        }
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        String loc = this.location != null ? ("at " + this.location.toString(e, d)) : "";
        String name = this.name != null ? ("named " + this.name.toString(e, d)) : "";
        return "open real " + this.viewType.name + " inventory " + loc + name + "to " + this.players.toString(e, d);
    }

}
