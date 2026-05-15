package com.shanebeestudios.skbee.elements.villager.event;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.util.SimpleEvent;
import com.github.shanebeee.skr.Registration;
import io.papermc.paper.event.player.PlayerPurchaseEvent;
import io.papermc.paper.event.player.PlayerTradeEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.event.inventory.TradeSelectEvent;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantInventory;
import org.bukkit.inventory.MerchantRecipe;

public class VillagerEvents extends SimpleEvent {

    public static void register(Registration reg) {
        reg.newEvent(VillagerEvents.class, TradeSelectEvent.class, "trade select")
            .name("Trade Select")
            .description("This event is called whenever a player clicks a new trade on the trades sidebar.",
                "This event allows the user to get the index of the trade, letting them get the MerchantRecipe via the Merchant.",
                "`event-number` = Used to get the index of the trade the player clicked on.",
                "`event-merchantrecipe` = The merchant recipe of the trade that the player clicked on.")
            .examples("")
            .since("1.17.0")
            .register();

        reg.newEventValue(TradeSelectEvent.class, MerchantInventory.class)
            .converter(TradeSelectEvent::getInventory)
            .register();
        reg.newEventValue(TradeSelectEvent.class, Number.class)
            .converter(TradeSelectEvent::getIndex)
            .register();
        reg.newEventValue(TradeSelectEvent.class, Merchant.class)
            .converter(TradeSelectEvent::getMerchant)
            .register();
        reg.newEventValue(TradeSelectEvent.class, MerchantRecipe.class)
            .converter(event -> event.getInventory().getSelectedRecipe())
            .register();
        reg.newEventValue(TradeSelectEvent.class, Player.class)
            .converter(event -> {
                HumanEntity trader = event.getMerchant().getTrader();
                if (trader instanceof Player player) {
                    return player;
                }
                return null;
            })
            .register();

        reg.newEvent(VillagerEvents.class, PlayerPurchaseEvent.class,
                "player purchase")
            .name("Player Purchase")
            .description("Called when a player trades with a standalone merchant/villager GUI. Requires PaperMC.")
            .examples("on player purchase:",
                "\tignite event-entity for 1 minute")
            .since("1.17.1")
            .register();

        reg.newEventValue(PlayerPurchaseEvent.class, MerchantRecipe.class)
            .converter(PlayerPurchaseEvent::getTrade)
            .register();
        reg.newEventValue(PlayerPurchaseEvent.class, Entity.class)
            .converter(event -> {
                if (event instanceof PlayerTradeEvent tradeEvent) {
                    return tradeEvent.getVillager();
                }
                return null;
            })
            .register();

        reg.newEvent(VillagerEvents.class, VillagerAcquireTradeEvent.class,
                "villager acquire trade")
            .name("Villager Acquire Trade")
            .description("Called whenever a villager acquires a new trade.")
            .since("INSERT VERSION")
            .register();

        reg.newEventValue(VillagerAcquireTradeEvent.class, MerchantRecipe.class)
            .description("Represents the recipe to be acquired.")
            .patterns("recipe")
            .converter(VillagerAcquireTradeEvent::getRecipe)
            .changer(ChangeMode.SET, VillagerAcquireTradeEvent::setRecipe)
            .register();
    }

}
