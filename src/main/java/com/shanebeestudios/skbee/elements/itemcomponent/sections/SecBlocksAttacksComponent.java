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
import com.shanebeestudios.skbee.api.registry.RegistryUtils;
import com.shanebeestudios.skbee.api.skript.base.Section;
import com.shanebeestudios.skbee.api.util.ItemComponentUtils;
import com.shanebeestudios.skbee.config.SkBeeMetrics;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.BlocksAttacks;
import io.papermc.paper.datacomponent.item.blocksattacks.DamageReduction;
import io.papermc.paper.datacomponent.item.blocksattacks.ItemDamageFunction;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.tag.Tag;
import io.papermc.paper.registry.tag.TagKey;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.damage.DamageType;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class SecBlocksAttacksComponent extends Section {

    private static EntryValidator VALIDATOR;

    static class BlocksAttacksEvent extends Event {

        private final List<DamageReduction> reductions = new ArrayList<>();

        public BlocksAttacksEvent() {
        }

        public void addReduction(DamageReduction reduction) {
            this.reductions.add(reduction);
        }

        @Override
        public @NotNull HandlerList getHandlers() {
            throw new IllegalStateException();
        }
    }

    @SuppressWarnings("unchecked")
    public static void register(Registration reg) {
        VALIDATOR = SimpleEntryValidator.builder()
            .addOptionalEntry("block_delay_seconds", Timespan.class)
            .addOptionalEntry("disable_cooldown_scale", Number.class)
            .addOptionalSection("damage_reductions")
            .addOptionalEntry("item_damage_threshold", Number.class)
            .addOptionalEntry("item_damage_base", Number.class)
            .addOptionalEntry("item_damage_factor", Number.class)
            .addOptionalEntry("block_sound", String.class)
            .addOptionalEntry("disabled_sound", String.class)
            .addOptionalEntry("bypassed_by", new Class[]{String.class, TagKey.class})
            .build();
        reg.newSection(SecBlocksAttacksComponent.class, VALIDATOR,
                "apply blocks attacks [component] to %itemstacks/itemtypes/slots%")
            .name("ItemComponent - Blocks Attacks Apply")
            .description("When present, this item can be used like a shield to block attacks to the holding player.",
                "See [**Blocks Attacks Component**](https://minecraft.wiki/w/Data_component_format#blocks_attacks) on McWiki for more info.",
                "",
                "**Entries**:",
                " - `block_delay_seconds` = The amount of time (in seconds) that use must be held before successfully blocking attacks [optional timespan, defaults to 0 seconds].",
                " - `disable_cooldown_scale` = The multiplier applied to the cooldown time for the item when attacked by a disabling attack [optional number, defaults to 1].",
                " - `damage_reductions` = A section to apply a set of rules for how much and what kinds damage should be blocked in a given attack [optional].",
                " - `item_damage_threshold` = The minimum amount of damage dealt by the attack before item damage is applied to the item [optional number, defaults to 0].",
                " - `item_damage_base` = The constant amount of damage applied to the item, if threshold is passed [optional number, defaults to 0].",
                " - `item_damage_factor` = The fraction of the dealt damage that should be applied to the item, if threshold is passed [optional number, defaults to 1.5].",
                " - `block_sound` = A sound to play when the item successfully blocks an attack [optional string, defaults to none].",
                " - `disabled_sound` = A sound to play when the item goes on its disabled cooldown due to an attack [optional string, defaults to none.",
                " - `bypassed_by` = DamageTypes that bypass the blocking [optional String/TagKey]."
            )
            .examples("apply blocks attacks to {_i}:",
                "\tblock_delay_seconds: 0.25 seconds",
                "\tdisable_cooldown_scale: 1.5",
                "\titem_damage_threshold: 1.0",
                "\titem_damage_base: 1.0",
                "\titem_damage_factor: 3.0",
                "\tblock_sound: \"minecraft:item.shield.block\"",
                "\tdisabled_sound: \"minecraft:item.shield.break\"",
                "\tbypassed_by: \"minecraft:bypasses_shield\"",
                "\tdamage_reductions:",
                "\t\tapply damage reduction:",
                "\t\t\ttype: mob attack, campfire",
                "\t\t\tbase: 1.0",
                "\t\t\tfactor: 2.5",
                "\t\t\thorizontal_blocking_angle: 25")
            .since("INSERT VERSION")
            .register();
    }

    private Expression<?> items;
    private Expression<Timespan> blockDelay;
    private Expression<Number> disableCooldownScale;
    private Expression<Number> itemDamageThreshold;
    private Expression<Number> itemDamageBase;
    private Expression<Number> itemDamageFactor;
    private Expression<String> blockSound;
    private Expression<String> disabledSound;
    private Expression<?> bypassedBy;
    private Trigger damageReductions;

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
        this.blockDelay = (Expression<Timespan>) validate.getOptional("block_delay_seconds", false);
        this.disableCooldownScale = (Expression<Number>) validate.getOptional("disable_cooldown_scale", false);
        this.itemDamageThreshold = (Expression<Number>) validate.getOptional("item_damage_threshold", false);
        this.itemDamageBase = (Expression<Number>) validate.getOptional("item_damage_base", false);
        this.itemDamageFactor = (Expression<Number>) validate.getOptional("item_damage_factor", false);
        this.blockSound = (Expression<String>) validate.getOptional("block_sound", false);
        this.disabledSound = (Expression<String>) validate.getOptional("disabled_sound", false);
        this.bypassedBy = (Expression<?>) validate.getOptional("bypassed_by", false);

        SectionNode damageNode = validate.getOptional("damage_reductions", SectionNode.class, false);
        if (damageNode != null) {
            this.damageReductions = loadCode(damageNode, "damage_reductions section", BlocksAttacksEvent.class);
        }

        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected @Nullable TriggerItem walk(Event event) {
        BlocksAttacks.Builder builder = BlocksAttacks.blocksAttacks();

        if (this.blockDelay != null) {
            Timespan timespan = this.blockDelay.getSingle(event);
            if (timespan != null) {
                builder.blockDelaySeconds(timespan.getAs(Timespan.TimePeriod.SECOND));
            }
        }

        if (this.disableCooldownScale != null) {
            Number single = this.disableCooldownScale.getSingle(event);
            if (single != null) {
                builder.disableCooldownScale(Math.max(0, single.floatValue()));
            }
        }

        float threshold = 0;
        float base = 0;
        float factor = 1.5f;
        if (this.itemDamageThreshold != null) {
            Number single = this.itemDamageThreshold.getSingle(event);
            if (single != null) {
                threshold = single.floatValue();
            }
        }
        if (this.itemDamageBase != null) {
            Number single = this.itemDamageBase.getSingle(event);
            if (single != null) {
                base = single.floatValue();
            }
        }
        if (this.itemDamageFactor != null) {
            Number single = this.itemDamageFactor.getSingle(event);
            if (single != null) {
                factor = single.floatValue();
            }
        }
        builder.itemDamage(ItemDamageFunction.itemDamageFunction()
            .threshold(threshold).base(base).factor(factor)
            .build());

        if (this.blockSound != null) {
            String single = this.blockSound.getSingle(event);
            if (single != null) {
                NamespacedKey namespacedKey = Utils.getNamespacedKey(single, false);
                builder.blockSound(namespacedKey);
            }
        }

        if (this.disabledSound != null) {
            String single = this.disabledSound.getSingle(event);
            if (single != null) {
                NamespacedKey namespacedKey = Utils.getNamespacedKey(single, false);
                builder.disableSound(namespacedKey);
            }
        }

        Registry<DamageType> registry = RegistryUtils.getRegistry(RegistryKey.DAMAGE_TYPE);
        if (this.bypassedBy != null) {
            Object o = this.bypassedBy.getSingle(event);
            TagKey<DamageType> damageTypeTagKey = null;
            if (o instanceof String s) {
                NamespacedKey namespacedKey = Utils.getNamespacedKey(s, false);
                if (namespacedKey != null) {
                    damageTypeTagKey = TagKey.create(RegistryKey.DAMAGE_TYPE, namespacedKey);
                }
            } else if (o instanceof TagKey<?> tagKey) {
                damageTypeTagKey = (TagKey<DamageType>) tagKey;
            }
            if (damageTypeTagKey != null) {
                Tag<DamageType> tag = registry.getTag(damageTypeTagKey);
                builder.bypassedBy(tag);
            }
        }

        if (this.damageReductions != null) {
            BlocksAttacksEvent blocksAttacksEvent = new BlocksAttacksEvent();
            TriggerItem.walk(this.damageReductions, blocksAttacksEvent);
            builder.damageReductions(blocksAttacksEvent.reductions);
        }

        BlocksAttacks blocksAttacks = builder.build();
        ItemComponentUtils.modifyComponent(this.items.getArray(event), ChangeMode.SET,
            DataComponentTypes.BLOCKS_ATTACKS, blocksAttacks);

        return super.walk(event, false);
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "apply blocks attacks component to " + this.items.toString(event, debug);
    }

}
