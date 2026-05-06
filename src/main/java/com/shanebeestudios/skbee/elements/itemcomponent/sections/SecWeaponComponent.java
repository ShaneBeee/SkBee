package com.shanebeestudios.skbee.elements.itemcomponent.sections;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import com.github.shanebeee.skr.Registration;
import com.github.shanebeee.skr.skript.SimpleEntryValidator;
import com.shanebeestudios.skbee.api.skript.base.EffectSection;
import com.shanebeestudios.skbee.api.util.ItemComponentUtils;
import com.shanebeestudios.skbee.config.SkBeeMetrics;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Weapon;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class SecWeaponComponent extends EffectSection {

    private static EntryValidator VALIDATOR;

    public static void register(Registration reg) {
        VALIDATOR = SimpleEntryValidator.builder()
            .addOptionalEntry("item_damage", Number.class)
            .addOptionalEntry("disable_blocking", Timespan.class)
            .build();
        reg.newSection(SecWeaponComponent.class, VALIDATOR, "apply weapon component to %itemstacks/itemtypes/slots%")
            .name("ItemComponent - Weapon")
            .description("Apply a weapon component to items.",
                "See [**Weapon Component**](https://minecraft.wiki/w/Data_component_format#weapon) on McWiki for more details.",
                "",
                "**Entries**:",
                " - `item_damage` - Controls the amount of durability to remove each time the weapon is used to attack [optional number, defaults to 0].",
                " - `disable_blocking` - The amount of seconds that this item can disable a blocking shield on successful attack [optional TimeSpan].")
            .examples("apply weapon component to player's tool:",
                "\titem_damage: 2",
                "\tdisable_blocking: 5 seconds")
            .since("INSERT VERSION")
            .register();
    }

    private Expression<Object> items;
    private Expression<Number> itemDamage;
    private Expression<Timespan> disableBlocking;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult,
                        SectionNode sectionNode, List<TriggerItem> triggerItems) {
        SkBeeMetrics.Features.ITEM_COMPONENTS.used();
        this.items = (Expression<Object>) expressions[0];
        if (sectionNode != null) {
            EntryContainer container = VALIDATOR.validate(sectionNode);
            if (container != null) {
                this.itemDamage = (Expression<Number>) container.getOptional("item_damage", false);
                this.disableBlocking = (Expression<Timespan>) container.getOptional("disable_blocking", false);
            }
        }

        return true;
    }

    @Override
    protected @Nullable TriggerItem walk(Event event) {
        Weapon.Builder weaponBuilder = Weapon.weapon();
        if (this.itemDamage != null) {
            Number damage = this.itemDamage.getSingle(event);
            if (damage != null) {
                weaponBuilder.itemDamagePerAttack(Math.max(0, damage.intValue()));
            }
        }
        if (this.disableBlocking != null) {
            Timespan timespan = this.disableBlocking.getSingle(event);
            if (timespan != null) {
                weaponBuilder.disableBlockingForSeconds(timespan.getAs(Timespan.TimePeriod.SECOND));
            }
        }

        Weapon weapon = weaponBuilder.build();

        ItemComponentUtils.modifyComponent(
            this.items.getArray(event),
            ChangeMode.SET,
            DataComponentTypes.WEAPON,
            weapon
        );

        return super.walk(event, false);
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "apply weapon component to " + this.items.toString(event, debug);
    }

}
