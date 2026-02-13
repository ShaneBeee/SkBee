package com.shanebeestudios.skbee.elements.other;

import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.elements.other.conditions.CondBlockCanRandomTick;
import com.shanebeestudios.skbee.elements.other.conditions.CondChunkContainsBlockData;
import com.shanebeestudios.skbee.elements.other.conditions.CondCriticalHit;
import com.shanebeestudios.skbee.elements.other.conditions.CondEntityStorageBlockFull;
import com.shanebeestudios.skbee.elements.other.conditions.CondIsLocked;
import com.shanebeestudios.skbee.elements.other.conditions.CondIsOwnedByRegion;
import com.shanebeestudios.skbee.elements.other.conditions.CondIsPlayerListed;
import com.shanebeestudios.skbee.elements.other.conditions.CondPlayerIsTransferred;
import com.shanebeestudios.skbee.elements.other.conditions.CondSpawnerIsActivated;
import com.shanebeestudios.skbee.elements.other.effects.EffAbortSpawn;
import com.shanebeestudios.skbee.elements.other.effects.EffAttributeModifierRemoveByKey;
import com.shanebeestudios.skbee.elements.other.effects.EffBlockLock;
import com.shanebeestudios.skbee.elements.other.effects.EffBlockRandomlyTick;
import com.shanebeestudios.skbee.elements.other.effects.EffBlockstateUpdate;
import com.shanebeestudios.skbee.elements.other.effects.EffBreakBlocksWithEffects;
import com.shanebeestudios.skbee.elements.other.effects.EffChunkRefresh;
import com.shanebeestudios.skbee.elements.other.effects.EffDispatchCommand;
import com.shanebeestudios.skbee.elements.other.effects.EffDropItem;
import com.shanebeestudios.skbee.elements.other.effects.EffEntityBlockStorage;
import com.shanebeestudios.skbee.elements.other.effects.EffEntityDamageEntity;
import com.shanebeestudios.skbee.elements.other.effects.EffEquipmentChange;
import com.shanebeestudios.skbee.elements.other.effects.EffFreezePlayerConnection;
import com.shanebeestudios.skbee.elements.other.effects.EffGiveOrDrop;
import com.shanebeestudios.skbee.elements.other.effects.EffHurtAnimation;
import com.shanebeestudios.skbee.elements.other.effects.EffLoadChunk;
import com.shanebeestudios.skbee.elements.other.effects.EffOpenContainerAnimation;
import com.shanebeestudios.skbee.elements.other.effects.EffOpenRealInventory;
import com.shanebeestudios.skbee.elements.other.effects.EffOpenSign;
import com.shanebeestudios.skbee.elements.other.effects.EffParseEffect;
import com.shanebeestudios.skbee.elements.other.effects.EffPlayerListed;
import com.shanebeestudios.skbee.elements.other.effects.EffRangedAttack;
import com.shanebeestudios.skbee.elements.other.effects.EffResourcePackRemove;
import com.shanebeestudios.skbee.elements.other.effects.EffResourcePackSend;
import com.shanebeestudios.skbee.elements.other.effects.EffSendBlockDamage;
import com.shanebeestudios.skbee.elements.other.effects.EffShowDemo;
import com.shanebeestudios.skbee.elements.other.effects.EffShowHideEntity;
import com.shanebeestudios.skbee.elements.other.effects.EffSleepThread;
import com.shanebeestudios.skbee.elements.other.effects.EffSpawnFallingBlockData;
import com.shanebeestudios.skbee.elements.other.effects.EffSpawnerResetTimer;
import com.shanebeestudios.skbee.elements.other.effects.EffTaskStop;
import com.shanebeestudios.skbee.elements.other.effects.EffTransferCookieStore;
import com.shanebeestudios.skbee.elements.other.effects.EffUpdateRecipeResources;
import com.shanebeestudios.skbee.elements.other.events.AsyncEvents;
import com.shanebeestudios.skbee.elements.other.events.EvtDamageByBlock;
import com.shanebeestudios.skbee.elements.other.events.EvtEntitiesLoad;
import com.shanebeestudios.skbee.elements.other.events.EvtEntityKnockback;
import com.shanebeestudios.skbee.elements.other.events.EvtPlayerInteract;
import com.shanebeestudios.skbee.elements.other.events.EvtPlayerUseUnknown;
import com.shanebeestudios.skbee.elements.other.events.EvtPreSpawn;
import com.shanebeestudios.skbee.elements.other.events.EvtSpawnerSpawn;
import com.shanebeestudios.skbee.elements.other.events.OtherEvents;
import com.shanebeestudios.skbee.elements.other.events.PaperEvents;
import com.shanebeestudios.skbee.elements.other.events.TabEvent;
import com.shanebeestudios.skbee.elements.other.expressions.ExprArmorTrim;
import com.shanebeestudios.skbee.elements.other.expressions.ExprArmorTrimItem;
import com.shanebeestudios.skbee.elements.other.expressions.ExprArmorTrimMatPat;
import com.shanebeestudios.skbee.elements.other.expressions.ExprAttributeModifierOfItem;
import com.shanebeestudios.skbee.elements.other.expressions.ExprAttributeModifierProperties;
import com.shanebeestudios.skbee.elements.other.expressions.ExprAvailableMaterials;
import com.shanebeestudios.skbee.elements.other.expressions.ExprAverageTickTime;
import com.shanebeestudios.skbee.elements.other.expressions.ExprBeaconEntitiesInRange;
import com.shanebeestudios.skbee.elements.other.sections.SecAttributeModifier;
import com.shanebeestudios.skbee.elements.other.sections.SecRunTaskLater;
import com.shanebeestudios.skbee.elements.other.sections.SecSpawnMinecraftEntity;
import com.shanebeestudios.skbee.elements.other.sections.SecTransferCookieRetrieve;
import com.shanebeestudios.skbee.elements.other.sections.SecWhileRunnable;
import com.shanebeestudios.skbee.elements.other.structures.StructTagAliases;
import com.shanebeestudios.skbee.elements.other.type.Comps;
import com.shanebeestudios.skbee.elements.other.type.Types;

public class OtherElementRegistration {

    public static void register(Registration registration) {
        // CONDITIONS
        CondBlockCanRandomTick.register(registration);
        CondChunkContainsBlockData.register(registration);
        CondCriticalHit.register(registration);
        CondEntityStorageBlockFull.register(registration);
        CondIsLocked.register(registration);
        CondIsOwnedByRegion.register(registration);
        CondIsPlayerListed.register(registration);
        CondPlayerIsTransferred.register(registration);
        CondSpawnerIsActivated.register(registration);

        // EFFECTS
        EffAbortSpawn.register(registration);
        EffDropItem.register(registration);
        EffAttributeModifierRemoveByKey.register(registration);
        EffBlockLock.register(registration);
        EffBlockRandomlyTick.register(registration);
        EffBlockstateUpdate.register(registration);
        EffBreakBlocksWithEffects.register(registration);
        EffChunkRefresh.register(registration);
        EffDispatchCommand.register(registration);
        EffEntityBlockStorage.register(registration);
        EffEntityDamageEntity.register(registration);
        EffEquipmentChange.register(registration);
        EffFreezePlayerConnection.register(registration);
        EffGiveOrDrop.register(registration);
        EffHurtAnimation.register(registration);
        EffLoadChunk.register(registration);
        EffOpenContainerAnimation.register(registration);
        EffOpenRealInventory.register(registration);
        EffOpenSign.register(registration);
        EffParseEffect.register(registration);
        EffPlayerListed.register(registration);
        EffRangedAttack.register(registration);
        EffResourcePackRemove.register(registration);
        EffResourcePackSend.register(registration);
        EffSendBlockDamage.register(registration);
        EffShowDemo.register(registration);
        EffShowHideEntity.register(registration);
        EffSleepThread.register(registration);
        EffSpawnerResetTimer.register(registration);
        EffSpawnFallingBlockData.register(registration);
        EffTaskStop.register(registration);
        EffTransferCookieStore.register(registration);
        EffUpdateRecipeResources.register(registration);

        // EVENTS
        AsyncEvents.register(registration);
        EvtDamageByBlock.register(registration);
        EvtEntitiesLoad.register(registration);
        EvtEntityKnockback.register(registration);
        EvtPlayerInteract.register(registration);
        EvtPlayerUseUnknown.register(registration);
        EvtPreSpawn.register(registration);
        EvtSpawnerSpawn.register(registration);
        OtherEvents.register(registration);
        PaperEvents.register(registration);
        TabEvent.register(registration);

        // EXPRESSIONS

        ExprArmorTrim.register(registration);
        ExprArmorTrimItem.register(registration);
        ExprArmorTrimMatPat.register(registration);
        ExprAttributeModifierOfItem.register(registration);
        ExprAttributeModifierProperties.register(registration);
        ExprAvailableMaterials.register(registration);
        ExprAverageTickTime.register(registration);
        ExprBeaconEntitiesInRange.register(registration);

        // SECTIONS
        SecAttributeModifier.register(registration);
        SecRunTaskLater.register(registration);
        SecSpawnMinecraftEntity.register(registration);
        SecTransferCookieRetrieve.register(registration);
        SecWhileRunnable.register(registration);

        // STRUCTURES
        StructTagAliases.register(registration);

        // TYPES
        Comps.register(registration);
        Types.register(registration);
    }

}
