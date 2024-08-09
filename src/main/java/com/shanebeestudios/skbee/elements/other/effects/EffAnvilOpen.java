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
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;

@Name("Open Anvil Inventory")
@Description({"Open an anvil inventory to players. This is a temporary effect until Skript fixes theirs.",
    "`at %location%` is used to open at an actual anvil block."})
@Examples({"open real anvil inventory to player",
    "open real anvil named \"Mr Anvil\" to player",
    "open real anvil at location(1,1,1) named \"Senor Anvil\" to player"})
@Since("INSERT VERSION")
public class EffAnvilOpen extends Effect {

    static {
        Skript.registerEffect(EffAnvilOpen.class,
            "open real anvil [inventory] [at %-location%] [named %-string%] to %players%");
    }

    private Expression<Location> location;
    private Expression<String> name;
    private Expression<Player> players;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.location = (Expression<Location>) exprs[0];
        this.name = (Expression<String>) exprs[1];
        this.players = (Expression<Player>) exprs[2];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected void execute(Event event) {
        Location location = this.location != null ? this.location.getSingle(event) : null;
        String name = this.name != null ? this.name.getSingle(event) : null;

        for (Player player : this.players.getArray(event)) {
            InventoryView inventoryView = player.openAnvil(location, true);
            if (name != null && inventoryView != null) inventoryView.setTitle(name);
        }
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        String loc = this.location != null ? ("at " + this.location.toString(e, d)) : "";
        String name = this.name != null ? ("named " + this.name.toString(e, d)) : "";
        return "open real anvil inventory " + loc + name + "to " + this.players.toString(e, d);
    }

}
