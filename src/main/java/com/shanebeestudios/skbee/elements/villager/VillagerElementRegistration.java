package com.shanebeestudios.skbee.elements.villager;

import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.elements.villager.effects.EffOpenMerchant;
import com.shanebeestudios.skbee.elements.villager.effects.EffVillagerEffects;
import com.shanebeestudios.skbee.elements.villager.event.SimpleEvents;
import com.shanebeestudios.skbee.elements.villager.expressions.ExprMerchant;
import com.shanebeestudios.skbee.elements.villager.expressions.ExprMerchantRecipe;
import com.shanebeestudios.skbee.elements.villager.expressions.ExprMerchantRecipeIngredients;
import com.shanebeestudios.skbee.elements.villager.expressions.ExprMerchantRecipeValues;
import com.shanebeestudios.skbee.elements.villager.expressions.ExprMerchantRecipes;
import com.shanebeestudios.skbee.elements.villager.type.Types;

public class VillagerElementRegistration {

    public static void register(Registration reg) {
        // EFFECTS
        EffOpenMerchant.register(reg);
        EffVillagerEffects.register(reg);

        // EVENTS
        SimpleEvents.register(reg);

        // EXPRESSIONS
        ExprMerchant.register(reg);
        ExprMerchantRecipe.register(reg);
        ExprMerchantRecipeIngredients.register(reg);
        ExprMerchantRecipes.register(reg);
        ExprMerchantRecipeValues.register(reg);

        // TYPES
        Types.register(reg);
    }

}
