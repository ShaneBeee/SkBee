package com.shanebeestudios.skbee.elements.itemcomponent;

import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.elements.itemcomponent.conditions.CondHasComponent;
import com.shanebeestudios.skbee.elements.itemcomponent.effects.EffApplyToComponent;
import com.shanebeestudios.skbee.elements.itemcomponent.effects.EffClearComponent;
import com.shanebeestudios.skbee.elements.itemcomponent.expressions.ExprBundleContents;
import com.shanebeestudios.skbee.elements.itemcomponent.expressions.ExprChargedProjectilesComponent;
import com.shanebeestudios.skbee.elements.itemcomponent.expressions.ExprDamageTypeComponent;
import com.shanebeestudios.skbee.elements.itemcomponent.expressions.ExprDyedColorComponent;
import com.shanebeestudios.skbee.elements.itemcomponent.expressions.ExprEnchantableComponent;
import com.shanebeestudios.skbee.elements.itemcomponent.expressions.ExprEnchantmentGlintOverride;
import com.shanebeestudios.skbee.elements.itemcomponent.expressions.ExprFoodComponentProperties;
import com.shanebeestudios.skbee.elements.itemcomponent.expressions.ExprGliderComponent;
import com.shanebeestudios.skbee.elements.itemcomponent.expressions.ExprIntangibleProjectileComponent;
import com.shanebeestudios.skbee.elements.itemcomponent.expressions.ExprItemModel;
import com.shanebeestudios.skbee.elements.itemcomponent.expressions.ExprMaxStackSizeComponent;
import com.shanebeestudios.skbee.elements.itemcomponent.expressions.ExprMinAttackChargeComponent;
import com.shanebeestudios.skbee.elements.itemcomponent.expressions.ExprRepairCost;
import com.shanebeestudios.skbee.elements.itemcomponent.expressions.ExprRepairableComponent;
import com.shanebeestudios.skbee.elements.itemcomponent.expressions.ExprTooltipStyleComponent;
import com.shanebeestudios.skbee.elements.itemcomponent.expressions.ExprUseRemainderComponent;
import com.shanebeestudios.skbee.elements.itemcomponent.sections.SecAdventureComponent;
import com.shanebeestudios.skbee.elements.itemcomponent.sections.SecAttackRangeComponent;
import com.shanebeestudios.skbee.elements.itemcomponent.sections.SecConsumableComponent;
import com.shanebeestudios.skbee.elements.itemcomponent.sections.SecCustomModelDataComponent;
import com.shanebeestudios.skbee.elements.itemcomponent.sections.SecDeathProtectionComponent;
import com.shanebeestudios.skbee.elements.itemcomponent.sections.SecEquippableComponent;
import com.shanebeestudios.skbee.elements.itemcomponent.sections.SecFireworkExplosionComponent;
import com.shanebeestudios.skbee.elements.itemcomponent.sections.SecFireworksComponent;
import com.shanebeestudios.skbee.elements.itemcomponent.sections.SecFoodComponent;
import com.shanebeestudios.skbee.elements.itemcomponent.sections.SecInstrumentComponent;
import com.shanebeestudios.skbee.elements.itemcomponent.sections.SecJukeboxPlayableComponent;
import com.shanebeestudios.skbee.elements.itemcomponent.sections.SecPiercingWeapon;
import com.shanebeestudios.skbee.elements.itemcomponent.sections.SecPotionContentsComponent;
import com.shanebeestudios.skbee.elements.itemcomponent.sections.SecSwingAnimationComponent;
import com.shanebeestudios.skbee.elements.itemcomponent.sections.SecToolComponent;
import com.shanebeestudios.skbee.elements.itemcomponent.sections.SecToolRule;
import com.shanebeestudios.skbee.elements.itemcomponent.sections.SecTooltipDisplayComponent;
import com.shanebeestudios.skbee.elements.itemcomponent.sections.SecUseCooldownComponent;
import com.shanebeestudios.skbee.elements.itemcomponent.sections.SecUseEffectsComponent;
import com.shanebeestudios.skbee.elements.itemcomponent.types.Types;

public class ItemComponentElementRegistration {

    public static void register(Registration reg) {
        // CONDITIONS
        CondHasComponent.register(reg);

        // EFFECTS
        EffApplyToComponent.register(reg);
        EffClearComponent.register(reg);

        // EXPRESSIONS
        ExprBundleContents.register(reg);
        ExprChargedProjectilesComponent.register(reg);
        ExprDamageTypeComponent.register(reg);
        ExprDyedColorComponent.register(reg);
        ExprEnchantableComponent.register(reg);
        ExprEnchantmentGlintOverride.register(reg);
        ExprFoodComponentProperties.register(reg);
        ExprGliderComponent.register(reg);
        ExprIntangibleProjectileComponent.register(reg);
        ExprItemModel.register(reg);
        ExprMaxStackSizeComponent.register(reg);
        ExprMinAttackChargeComponent.register(reg);
        ExprRepairableComponent.register(reg);
        ExprRepairCost.register(reg);
        ExprTooltipStyleComponent.register(reg);
        ExprUseRemainderComponent.register(reg);

        // SECTIONS
        SecAdventureComponent.register(reg);
        SecAttackRangeComponent.register(reg);
        SecConsumableComponent.register(reg);
        SecCustomModelDataComponent.register(reg);
        SecDeathProtectionComponent.register(reg);
        SecEquippableComponent.register(reg);
        SecFireworkExplosionComponent.register(reg);
        SecFireworksComponent.register(reg);
        SecFoodComponent.register(reg);
        SecInstrumentComponent.register(reg);
        SecJukeboxPlayableComponent.register(reg);
        SecPiercingWeapon.register(reg);
        SecPotionContentsComponent.register(reg);
        SecSwingAnimationComponent.register(reg);
        SecToolComponent.register(reg);
        SecToolRule.register(reg);
        SecTooltipDisplayComponent.register(reg);
        SecUseCooldownComponent.register(reg);
        SecUseEffectsComponent.register(reg);

        // TYPES
        Types.register(reg);
    }

}
