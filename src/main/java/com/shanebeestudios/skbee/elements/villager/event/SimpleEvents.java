package com.shanebeestudios.skbee.elements.villager.event;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.util.SimpleEvent;
import ch.njol.skript.registrations.EventValues;
import io.papermc.paper.event.player.PlayerPurchaseEvent;
import io.papermc.paper.event.player.PlayerTradeEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.TradeSelectEvent;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantInventory;
import org.bukkit.inventory.MerchantRecipe;

public class SimpleEvents extends SimpleEvent {

    static {
        Skript.registerEvent("Trade Select", SimpleEvents.class, TradeSelectEvent.class, "trade select")
            .description("This event is called whenever a player clicks a new trade on the trades sidebar.",
                "This event allows the user to get the index of the trade, letting them get the MerchantRecipe via the Merchant.",
                "`event-number` = Used to get the index of the trade the player clicked on.",
                "`event-merchantrecipe` = The merchant recipe of the trade that the player clicked on.")
            .examples("")
            .since("1.17.EventValues.TIME_NOW");

        EventValues.registerEventValue(TradeSelectEvent.class, MerchantInventory.class, TradeSelectEvent::getInventory, EventValues.TIME_NOW);
        EventValues.registerEventValue(TradeSelectEvent.class, Number.class, TradeSelectEvent::getIndex, EventValues.TIME_NOW);
        EventValues.registerEventValue(TradeSelectEvent.class, Merchant.class, TradeSelectEvent::getMerchant, EventValues.TIME_NOW);
        EventValues.registerEventValue(TradeSelectEvent.class, MerchantRecipe.class, event -> event.getInventory().getSelectedRecipe(), EventValues.TIME_NOW);
        EventValues.registerEventValue(TradeSelectEvent.class, Player.class, event -> {
            HumanEntity trader = event.getMerchant().getTrader();
            if (trader instanceof Player player) {
                return player;
            }
            return null;
        }, EventValues.TIME_NOW);

        if (Skript.classExists("io.papermc.paper.event.player.PlayerPurchaseEvent")) {
            Skript.registerEvent("Player Purchase", SimpleEvents.class, PlayerPurchaseEvent.class,
                    "player purchase")
                .description("Called when a player trades with a standalone merchant/villager GUI. Requires PaperMC.")
                .examples("on player purchase:",
                    "\tignite event-entity for 1 minute")
                .since("1.17.1");

            EventValues.registerEventValue(PlayerPurchaseEvent.class, MerchantRecipe.class, PlayerPurchaseEvent::getTrade, EventValues.TIME_NOW);
            EventValues.registerEventValue(PlayerPurchaseEvent.class, Entity.class, event -> {
                if (event instanceof PlayerTradeEvent tradeEvent) {
                    return tradeEvent.getVillager();
                }
                return null;
            }, EventValues.TIME_NOW);
        }
    }

}
