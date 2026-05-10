package com.shanebeestudios.skbee.elements.itemcomponent.sections;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import com.github.shanebeee.skr.Registration;
import com.github.shanebeee.skr.Utils;
import com.github.shanebeee.skr.skript.SimpleEntryValidator;
import com.shanebeestudios.skbee.api.skript.base.Section;
import com.shanebeestudios.skbee.api.util.ItemComponentUtils;
import com.shanebeestudios.skbee.config.SkBeeMetrics;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.KineticWeapon;
import io.papermc.paper.datacomponent.item.KineticWeapon.Condition;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class SecKineticWeaponComponent extends Section {

    private static EntryValidator VALIDATOR;

    public static class KineticWeaponEvent extends Event {

        private Condition condition;

        public void setCondition(Condition condition) {
            this.condition = condition;
        }

        public Condition getCondition() {
            return this.condition;
        }

        @Override
        public @NotNull HandlerList getHandlers() {
            throw new IllegalStateException();
        }
    }

    public static void register(Registration reg) {
        VALIDATOR = SimpleEntryValidator.builder()
            .addOptionalEntry("delay_ticks", Timespan.class)
            .addOptionalSection("damage_conditions")
            .addOptionalSection("dismount_conditions")
            .addOptionalSection("knockback_conditions")
            .addOptionalEntry("forward_movement", Number.class)
            .addOptionalEntry("damage_multiplier", Number.class)
            .addOptionalEntry("sound", String.class)
            .addOptionalEntry("hit_sound", String.class)
            .build();
        reg.newSection(SecKineticWeaponComponent.class, VALIDATOR,
                "apply kinetic weapon [component] to %itemstacks/itemtypes/slots%")
            .name("ItemComponent - Kinetic Weapon Apply")
            .description("Apply a kinetic weapon component to items.",
                "Enables a charge-type attack when using the item where, while being used, " +
                    "the damage is dealt along a ray every tick based on the relative speed of the entities.",
                "See [**Kinetic Weapon Component**](https://minecraft.wiki/w/Data_component_format#kinetic_weapon) on McWiki for more info.",
                "",
                "**Entries**:",
                " - `delay_ticks` = The time in ticks required before weapon is effective [optiona timespan, defaults to 0 ticks].",
                " - `damage_conditions` = The condition under which the charge attack deals damage [optional section].",
                " - `dismount_conditions` = The condition under which the charge attack dismounts the target [optional section].",
                " - `knockback_conditions` = The condition under which the charge attack deals knockback [optional section].",
                " - `forward_movement` = The distance the item moves out of the wielder's hand during its animation [optional number, defaults to 0.0].",
                " - `damage_multiplier` = The multiplier for the final damage from the relative speed [optional number, defaults to 1.0].",
                " - `sound` = Optional sound event to play when the weapon is engaged [optional string].",
                " - `hit_sound` = Optional sound event to play when the weapon hits an entity [optional string].")
            .examples("apply kinetic weapon component to {_i}:",
                "\tdelay_ticks: 13 ticks",
                "\tforward_movement: 0.38",
                "\tdamage_multiplier: 0.82",
                "\tsound: \"minecraft:item.spear.use\"",
                "\thit_sound: \"minecraft:item.spear.hit\"",
                "\tdamage_conditions:",
                "\t\tapply kinetic weapon condition:",
                "\t\t\tmax_duration_ticks: 250 ticks",
                "\t\t\tmin_relative_speed: 4.6",
                "\tdismount_conditions:",
                "\t\tapply kinetic weapon condition:",
                "\t\t\tmax_duration_ticks: 80 ticks",
                "\t\t\tmin_speed: 12.0")
            .since("3.23.0")
            .register();
    }

    private Expression<?> items;
    private Expression<Timespan> delayTicks;
    private Expression<Number> forwardMovement;
    private Expression<Number> damageMultiplier;
    private Expression<String> sound;
    private Expression<String> hitSound;
    private Trigger damageConditionsTrigger;
    private Trigger dismountConditionsTrigger;
    private Trigger knockbackConditionsTrigger;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult,
                        SectionNode sectionNode, List<TriggerItem> triggerItems) {
        SkBeeMetrics.Features.ITEM_COMPONENTS.used();
        if (sectionNode == null) return false;
        EntryContainer validate = VALIDATOR.validate(sectionNode);
        if (validate == null) {
            return false;
        }
        this.items = expressions[0];
        this.delayTicks = (Expression<Timespan>) validate.getOptional("delay_ticks", false);
        this.forwardMovement = (Expression<Number>) validate.getOptional("forward_movement", false);
        this.damageMultiplier = (Expression<Number>) validate.getOptional("damage_multiplier", false);
        this.sound = (Expression<String>) validate.getOptional("sound", false);
        this.hitSound = (Expression<String>) validate.getOptional("hit_sound", false);

        SectionNode damageNode = validate.getOptional("damage_conditions", SectionNode.class, false);
        if (damageNode != null) {
            this.damageConditionsTrigger = loadCode(damageNode, "damage conditions", KineticWeaponEvent.class);
        }
        SectionNode dismountNode = validate.getOptional("dismount_conditions", SectionNode.class, false);
        if (dismountNode != null) {
            this.dismountConditionsTrigger = loadCode(dismountNode, "dismount conditions", KineticWeaponEvent.class);
        }
        SectionNode knockbackNode = validate.getOptional("knockback_conditions", SectionNode.class, false);
        if (knockbackNode != null) {
            this.knockbackConditionsTrigger = loadCode(knockbackNode, "knockback conditions", KineticWeaponEvent.class);
        }

        return true;
    }

    @Override
    protected @Nullable TriggerItem walk(Event event) {
        KineticWeapon.Builder builder = KineticWeapon.kineticWeapon();

        if (this.delayTicks != null) {
            Timespan timespan = this.delayTicks.getSingle(event);
            if (timespan != null) {
                builder.delayTicks((int) timespan.getAs(Timespan.TimePeriod.TICK));
            }
        }

        if (this.forwardMovement != null) {
            Number single = this.forwardMovement.getSingle(event);
            if (single != null) {
                builder.forwardMovement(single.floatValue());
            }
        }
        if (this.damageMultiplier != null) {
            Number single = this.damageMultiplier.getSingle(event);
            if (single != null) {
                builder.damageMultiplier(single.floatValue());
            }
        }
        if (this.sound != null) {
            String single = this.sound.getSingle(event);
            if (single != null) {
                NamespacedKey namespacedKey = Utils.getNamespacedKey(single, false);
                if (namespacedKey != null) {
                    builder.sound(namespacedKey);
                }
            }
        }
        if (this.hitSound != null) {
            String single = this.hitSound.getSingle(event);
            if (single != null) {
                NamespacedKey namespacedKey = Utils.getNamespacedKey(single, false);
                if (namespacedKey != null) {
                    builder.hitSound(namespacedKey);
                }
            }
        }

        if (this.damageConditionsTrigger != null) {
            KineticWeaponEvent kineticWeaponEvent = new KineticWeaponEvent();
            TriggerItem.walk(this.damageConditionsTrigger, kineticWeaponEvent);
            builder.damageConditions(kineticWeaponEvent.getCondition());
        }
        if (this.dismountConditionsTrigger != null) {
            KineticWeaponEvent kineticWeaponEvent = new KineticWeaponEvent();
            TriggerItem.walk(this.dismountConditionsTrigger, kineticWeaponEvent);
            builder.dismountConditions(kineticWeaponEvent.getCondition());
        }
        if (this.knockbackConditionsTrigger != null) {
            KineticWeaponEvent kineticWeaponEvent = new KineticWeaponEvent();
            TriggerItem.walk(this.knockbackConditionsTrigger, kineticWeaponEvent);
            builder.knockbackConditions(kineticWeaponEvent.getCondition());
        }

        KineticWeapon kineticWeapon = builder.build();
        ItemComponentUtils.modifyComponent(this.items.getArray(event), ChangeMode.SET,
            DataComponentTypes.KINETIC_WEAPON, kineticWeapon);

        return super.walk(event, false);
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "apply kinetic weapon component to " + this.items.toString(event, debug);
    }

}
