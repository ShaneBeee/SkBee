package com.shanebeestudios.skbee.elements.itemcomponent.sections;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import com.github.shanebeee.skr.Registration;
import com.github.shanebeee.skr.skript.SimpleEntryValidator;
import com.shanebeestudios.skbee.api.skript.base.Section;
import io.papermc.paper.datacomponent.item.KineticWeapon;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class SecKineticWeaponCondition extends Section {

    private static EntryValidator VALIDATOR;

    public static void register(Registration reg) {
        VALIDATOR = SimpleEntryValidator.builder()
            .addOptionalEntry("max_duration_ticks", Timespan.class)
            .addOptionalEntry("min_speed", Number.class)
            .addOptionalEntry("min_relative_speed", Number.class)
            .build();

        reg.newSection(SecKineticWeaponCondition.class, VALIDATOR,
                "apply kinetic weapon condition")
            .name("ItemComponent - Kinetic Weapon Condition")
            .description("Applies a kinetic weapon condition to the kinetic weapon section.",
                "See [**Kinetic Weapon Component**](https://minecraft.wiki/w/Data_component_format#kinetic_weapon) on McWiki for more info.",
                "",
                "**Entries**:",
                " - `max_duration_ticks` = The time in ticks after which the condition is no longer checked. This starts after delay has elapsed (requird timespan).",
                " - `min_speed` = The minimum speed of the attacker, in blocks per second, along the direction that the attacker is looking. [optional number, defaults to 0.0].",
                " - `min_relative_speed` = The minimum relative speed between the attacker and target, in blocks per second, along the direction that the attacker is looking. [optional number, defaults to 0.0].")
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

    private Expression<Timespan> maxDurationTicks;
    private Expression<Number> minSpeed;
    private Expression<Number> minRelativeSpeed;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> triggerItems) {
        if (!ParserInstance.get().isCurrentEvent(SecKineticWeaponComponent.KineticWeaponEvent.class)) {
            Skript.error("Kinetic weapon condition section can only be used in kinetic weapon section.");
            return false;
        }
        if (sectionNode == null) return false;
        EntryContainer validate = VALIDATOR.validate(sectionNode);
        if (validate == null) {
            return false;
        }

        this.maxDurationTicks = (Expression<Timespan>) validate.getOptional("max_duration_ticks", false);
        this.minSpeed = (Expression<Number>) validate.getOptional("min_speed", false);
        this.minRelativeSpeed = (Expression<Number>) validate.getOptional("min_relative_speed", false);

        return true;
    }

    @Override
    protected @Nullable TriggerItem walk(Event event) {
        if (!(event instanceof SecKineticWeaponComponent.KineticWeaponEvent kineticWeaponEvent)) {
            return super.walk(event, false);
        }
        int maxDurationTicks = 1;
        int minSpeed = 0;
        int minRelativeSpeed = 0;

        if (this.maxDurationTicks != null) {
            Timespan timespan = this.maxDurationTicks.getSingle(event);
            if (timespan != null) {
                maxDurationTicks = (int) timespan.getAs(Timespan.TimePeriod.TICK);
            }
        }
        if (this.minSpeed != null) {
            Number single = this.minSpeed.getSingle(event);
            if (single != null) {
                minSpeed = single.intValue();
            }
        }
        if (this.minRelativeSpeed != null) {
            Number single = this.minRelativeSpeed.getSingle(event);
            if (single != null) {
                minRelativeSpeed = single.intValue();
            }
        }
        KineticWeapon.Condition condition = KineticWeapon.condition(maxDurationTicks, minSpeed, minRelativeSpeed);
        kineticWeaponEvent.setCondition(condition);

        return super.walk(event, false);
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "apply kinetic weapon condition";
    }

}
