package com.shanebeestudios.skbee.elements.other.effects;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.github.shanebeee.skr.Registration;
import com.shanebeestudios.skbee.api.skript.base.Effect;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.Nullable;

public class EffEntityUseItem extends Effect {

    public static void register(Registration reg) {
        reg.newEffect(EffEntityUseItem.class,
                "make %livingentities% start using item [in %-equipmentslot%]",
                "make %livingentities% stop using active item",
                "make %livingentities% complete using active item")
            .name("Entity Use Item")
            .description("Makes a LivingEntity start/stop/complete using an item.",
                "You can optionally specify which hand. Will default to main hand.",
                "**Types**:",
                " - Start = Makes the entity start using an item.",
                " - Stop = Interrupts any ongoing active \"usage\" or consumption or an item.",
                " - Complete = Finishes using the currently active item.",
                "   When, for example, a skeleton is drawing its bow, this will cause it to release and fire the arrow." +
                    "This method does not make any guarantees about the effect of this method as such depends on the entity and its state.")
            .examples("make target entity start using item in main hand slot")
            .since("3.19.0")
            .register();
    }

    private int pattern;
    private Expression<LivingEntity> livingEntities;
    private Expression<EquipmentSlot> equipmentSlot;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.pattern = matchedPattern;
        this.livingEntities = (Expression<LivingEntity>) expressions[0];
        if (this.pattern == 0) {
            this.equipmentSlot = (Expression<EquipmentSlot>) expressions[1];
        }
        return true;
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    protected void execute(Event event) {
        EquipmentSlot slot = this.equipmentSlot != null ? this.equipmentSlot.getSingle(event) : EquipmentSlot.HAND;

        for (LivingEntity livingEntity : this.livingEntities.getArray(event)) {
            if (this.pattern == 0 && (slot == EquipmentSlot.HAND || slot == EquipmentSlot.OFF_HAND)) {
                livingEntity.startUsingItem(slot);
            } else if (this.pattern == 1) {
                livingEntity.clearActiveItem();
            } else if (this.pattern == 2) {
                livingEntity.completeUsingActiveItem();
            }
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        String type = switch (this.pattern) {
            case 1 -> "stop";
            case 2 -> "complete";
            default -> "start";
        };
        if (this.equipmentSlot != null) {
            return "make " + this.livingEntities.toString(event, debug) + " start using item in " + this.equipmentSlot.toString(event, debug);
        }
        return "make " + this.livingEntities.toString(event, debug) + " " + type + " using item";
    }

}
