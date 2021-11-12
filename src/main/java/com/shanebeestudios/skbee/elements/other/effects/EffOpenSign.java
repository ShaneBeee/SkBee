package com.shanebeestudios.skbee.elements.other.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.Direction;
import ch.njol.util.Kleenean;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

@Name("Open Sign")
@Description("Open a sign's GUI to a player, to allow them to edit it. Requires Paper (not sure which version, but at least 1.12.x)")
@Examples({"open sign gui of target block to player",
        "open target block's sign gui to player"})
public class EffOpenSign extends Effect {

    static {
        if (Skript.methodExists(HumanEntity.class, "openSign", Sign.class)) {
            Skript.registerEffect(EffOpenSign.class,
                    "open sign [gui] [(for|of)] [%direction%] %location% to %player%",
                    "open [%direction%] %location%'[s] sign [gui] to %player%");
        }
    }
    @SuppressWarnings("null")
    private Expression<Location> locations;
    private Expression<Player> player;


    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        locations = Direction.combine((Expression<? extends Direction>) exprs[0], (Expression<? extends Location>) exprs[1]);
        player = (Expression<Player>) exprs[2];
        return true;
    }

    @Override
    protected void execute(@NotNull Event e) {
        Location location = locations.getSingle(e);
        Player player = this.player.getSingle(e);
        if (location == null || player == null) return;

        BlockState block = location.getBlock().getState();
        if (block instanceof Sign) {
            player.openSign(((Sign) block));
        }

    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "open sign for " + locations.toString(e, d) + " to " + player.toString(e, d);
    }

}
