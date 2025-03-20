package com.shanebeestudios.skbee.elements.villager.type;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.entity.EntityData;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import com.shanebeestudios.skbee.api.util.SkriptUtils;
import org.bukkit.entity.AbstractVillager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantInventory;
import org.bukkit.inventory.MerchantRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Types {

    static {
        // MERCHANT
        // Only register if no other addons have registered this class
        if (Classes.getExactClassInfo(Merchant.class) == null) {
            Classes.registerClass(new ClassInfo<>(Merchant.class, "merchant")
                .user("merchants?")
                .name("Merchant")
                .description("Represents a merchant.",
                    "A merchant is a special type of inventory which can facilitate custom trades between items.")
                .since("1.17.0")
                .after("entitydata")
                .parser(new Parser<>() {

                    @SuppressWarnings("NullableProblems")
                    @Override
                    public boolean canParse(ParseContext context) {
                        return false;
                    }

                    @Override
                    public @NotNull String toString(Merchant merchant, int i) {
                        if (merchant instanceof AbstractVillager villager) {
                            return EntityData.toString(villager, i);
                        }
                        return "Merchant{trader=" + Classes.toString(merchant.getTrader()) + "}";
                    }

                    @Override
                    public @NotNull String toVariableNameString(Merchant merchant) {
                        return "merchant";
                    }
                }));
        }

        // MERCHANT INVENTORY
        // Only register if no other addons have registered this class
        if (Classes.getExactClassInfo(MerchantInventory.class) == null) {
            Classes.registerClass(new ClassInfo<>(MerchantInventory.class, "merchantinventory")
                .user("merchant ?inventor(y|ies)")
                .name("Merchant Inventory")
                .description("Represents a trading inventory between a player and a merchant.",
                    "The holder of this Inventory is the owning Villager, or null if the player is trading with a custom merchant.")
                .since("1.17.0")
                .parser(SkriptUtils.getDefaultParser()));
        }

        // MERCHANT RECIPE
        // Only register if no other addons have registered this class
        if (Classes.getExactClassInfo(MerchantRecipe.class) == null) {
            Classes.registerClass(new ClassInfo<>(MerchantRecipe.class, "merchantrecipe")
                .user("merchant ?recipes?")
                .name("Merchant Recipe")
                .description("Represents a merchant's trade. Trades can take one or two ingredients, and provide one result.")
                .since("1.17.0")
                .parser(new Parser<>() {

                    @Override
                    public boolean canParse(ParseContext context) {
                        return false;
                    }

                    @Override
                    public @NotNull String toString(MerchantRecipe merchantRecipe, int i) {
                        String result = ItemType.toString(merchantRecipe.getResult());
                        List<String> ingredients = new ArrayList<>();
                        for (ItemStack ingredient : merchantRecipe.getIngredients()) {
                            ingredients.add(ItemType.toString(ingredient));
                        }
                        int maxUses = merchantRecipe.getMaxUses();
                        int uses = merchantRecipe.getUses();
                        boolean reward = merchantRecipe.hasExperienceReward();
                        int villagerXP = merchantRecipe.getVillagerExperience();
                        float priceMultiplier = merchantRecipe.getPriceMultiplier();
                        int demand = merchantRecipe.getDemand();
                        int specialPrice = merchantRecipe.getSpecialPrice();
                        return String.format(
                            "MerchantRecipe{result=%s, maxUses=%s, ingredients=%s, uses=%s, " +
                                "reward=%s, villagerXP=%s, priceMultiplier=%s, demand=%s, specialPrice=%s}",
                            result, maxUses, ingredients, uses, reward, villagerXP, priceMultiplier, demand, specialPrice);
                    }

                    @Override
                    public @NotNull String toVariableNameString(MerchantRecipe merchantRecipe) {
                        return "merchant recipe";
                    }
                }));
        }
    }

}
