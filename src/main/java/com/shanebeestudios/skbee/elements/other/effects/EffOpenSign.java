package com.shanebeestudios.skbee.elements.other.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.util.Direction;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.skript.base.Effect;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Open Sign")
@Description({"Open a sign's GUI to a player, to allow them to edit it.",
    "If it wasn't obvious, the sign must actually be placed in the world, it's NOT a virtual sign.",
    "Requires Paper (not sure which version, but at least 1.12.x).",
    "Spigot added support for this in MC 1.18.",
    "Front/Back support added in MC 1.20.",
    "\n**NOTE**: This appears to no longer work in 1.20+ with a distance of 7.5+ (client side issue)."})
@Examples({"open sign gui of target block to player",
    "open target block's sign gui to player",
    "open sign back of target block to player"})
@Since("1.5.2, 2.14.0 (sides)")
public class EffOpenSign extends Effect {

    private static final boolean HAS_SIDES = Util.IS_RUNNING_MC_1_20;

    static {
        String side = HAS_SIDES ? "[(front|back:back)] " : "";
        Skript.registerEffect(EffOpenSign.class,
            "open sign [gui] " + side + "[(for|of)] [%direction%] %location% to %players%",
            "open [%direction%] %location%'[s] sign [gui] " + side + "to %players%");
    }

    @SuppressWarnings("null")
    private Expression<Location> locations;
    private Expression<Player> players;
    private boolean back;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        this.locations = Direction.combine((Expression<? extends Direction>) exprs[0], (Expression<? extends Location>) exprs[1]);
        this.players = (Expression<Player>) exprs[2];
        this.back = parseResult.hasTag("back");
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void execute(@NotNull Event event) {
        Location location = this.locations.getSingle(event);
        if (location == null) {
            return;
        }

        BlockState block = location.getBlock().getState();
        if (block instanceof Sign sign) {
            for (Player player : this.players.getArray(event)) {
                if (HAS_SIDES) {
                    player.openSign(sign, this.back ? Side.BACK : Side.FRONT);
                } else {
                    player.openSign(sign);
                }
            }
        } else {
            error("Block is not a sign: " + Classes.toString(location.getBlock()));
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String side = HAS_SIDES ? (this.back ? "back " : "front ") : "";
        return "open sign " + side + "for " + this.locations.toString(e, d) + " to " + this.players.toString(e, d);
    }

}
