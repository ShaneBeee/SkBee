package com.shanebeestudios.skbee.elements.other.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.util.Direction;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.skript.base.Effect;
import io.papermc.paper.math.Position;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EffOpenSign extends Effect {

    private static final boolean HAS_VIRTUAL = Skript.classExists("io.papermc.paper.event.packet.UncheckedSignChangeEvent");

    public static void register(Registration reg) {
        reg.newEffect(EffOpenSign.class,
                "open [:virtual] sign [gui] [(front|back:back)] [(for|of)] [%direction%] %location% to %players%",
                "open [%direction%] %location%'[s] [:virtual] sign [gui] [(front|back:back)] to %players%")
            .name("Open Sign")
            .description("Open a sign's GUI to a player, to allow them to edit it.",
                "`virtual`: (Added in Paper 1.21.5)",
                " - Allows opening a client side sign to a player.",
                " - You will have to send a client side sign to the player.",
                " - The sign must be within 9 blocks of the player.",
                " - You can use the `unchecked sign change` event to listen for changes to virtual signs.",
                "If not using `virtual`, the block must be placed in the world.",
                "Front/Back support added in MC 1.20.")
            .examples("open sign gui of target block to player",
                "open target block's sign gui to player",
                "open sign back of target block to player")
            .since("1.5.2", "2.14.0 (sides)", "3.11.3 (virtual)")
            .register();
    }

    @SuppressWarnings("null")
    private Expression<Location> locations;
    private Expression<Player> players;
    private boolean back;
    private boolean virtual;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        this.locations = Direction.combine((Expression<? extends Direction>) exprs[0], (Expression<? extends Location>) exprs[1]);
        this.players = (Expression<Player>) exprs[2];
        this.back = parseResult.hasTag("back");
        this.virtual = parseResult.hasTag("virtual");
        if (this.virtual && !HAS_VIRTUAL) {
            Skript.error("Virtual sign opening requires Paper 1.21.5+");
            return false;
        }
        return true;
    }

    @SuppressWarnings({"deprecation", "UnstableApiUsage"})
    @Override
    protected void execute(@NotNull Event event) {
        Location location = this.locations.getSingle(event);
        if (location == null) {
            return;
        }

        BlockState block = location.getBlock().getState();
        if (this.virtual) {
            for (Player player : this.players.getArray(event)) {
                player.openVirtualSign(Position.block(location), this.back ? Side.BACK : Side.FRONT);
            }
        } else if (block instanceof Sign sign) {
            for (Player player : this.players.getArray(event)) {
                player.openSign(sign, this.back ? Side.BACK : Side.FRONT);
            }
        } else {
            error("Block is not a sign: " + Classes.toString(location.getBlock()));
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String side = this.back ? "back " : "front ";
        String virtual = this.virtual ? "virtual " : "";
        return "open " + virtual + "sign " + side + "for " + this.locations.toString(e, d) + " to " + this.players.toString(e, d);
    }

}
