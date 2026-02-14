package com.shanebeestudios.skbee.elements.villager.effects;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.Event;
import org.bukkit.inventory.Merchant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EffOpenMerchant extends Effect {

    public static void register(Registration reg) {
        reg.newEffect(EffOpenMerchant.class, "open merchant %merchant/entity% to %player%")
            .name("Merchant - Open")
            .description("Open a merchant/villager to a player.")
            .examples("set {_m} to new merchant named \"Le Merchant\"",
                "open merchant {_m} to player")
            .since("1.17.0")
            .register();
    }

    private Expression<Object> merchant;
    private Expression<Player> player;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, ParseResult parseResult) {
        this.merchant = (Expression<Object>) exprs[0];
        this.player = (Expression<Player>) exprs[1];
        return true;
    }

    @SuppressWarnings("deprecation") // openMerchant -> new menu api
    @Override
    protected void execute(Event event) {
        Object object = this.merchant.getSingle(event);
        Player player = this.player.getSingle(event);
        if (player == null) return;

        if (object instanceof Villager villager) {
            player.openMerchant(villager, false);
        } else if (object instanceof Merchant merchant) {
            player.openMerchant(merchant, false);
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "open merchant " + this.merchant.toString(e, d) + " to " + this.player.toString(e, d);
    }

}
