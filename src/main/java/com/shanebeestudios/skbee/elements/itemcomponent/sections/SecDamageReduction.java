package com.shanebeestudios.skbee.elements.itemcomponent.sections;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.util.Kleenean;
import com.github.shanebeee.skr.Registration;
import com.github.shanebeee.skr.Utils;
import com.github.shanebeee.skr.skript.SimpleEntryValidator;
import com.shanebeestudios.skbee.api.registry.RegistryUtils;
import com.shanebeestudios.skbee.api.skript.base.Section;
import io.papermc.paper.datacomponent.item.blocksattacks.DamageReduction;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.set.RegistrySet;
import io.papermc.paper.registry.tag.Tag;
import io.papermc.paper.registry.tag.TagKey;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.damage.DamageType;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class SecDamageReduction extends Section {

    private static EntryValidator VALIDATOR;

    @SuppressWarnings("unchecked")
    public static void register(Registration reg) {
        VALIDATOR = SimpleEntryValidator.builder()
            .addOptionalEntry("type", new Class[]{DamageType.class, String.class, TagKey.class})
            .addRequiredEntry("base", Number.class)
            .addRequiredEntry("factor", Number.class)
            .addOptionalEntry("horizontal_blocking_angle", Number.class)
            .build();
        reg.newSection(SecDamageReduction.class, VALIDATOR, "apply damage reduction")
            .name("ItemComponent - Damage Reduction Rule")
            .description("Apply a rule for how much and what kinds of damage should be blocked in a given attack.",
                "See [**Blocks Attacks Component**](https://minecraft.wiki/w/Data_component_format#blocks_attacks) on McWiki for more info.",
                "",
                "**Entries**:",
                " - `type` = Damage types to block [optional DamageType/String/TagKey, defaults to all damage types].",
                " - `base` = The constant amount of damage to be blocked (required number).",
                " - `factor` = The fraction of the dealt damage to be blocked (required number).",
                " - `horizontal_blocking_angle` = The maximum angle between the users facing direction and the direction of the incoming attack to be blocked [optional positive number, defaults to 90].")
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

    private Expression<?> type;
    private Expression<Number> base;
    private Expression<Number> factor;
    private Expression<Number> horizontalBlockingAngle;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult,
                        SectionNode sectionNode, List<TriggerItem> triggerItems) {
        if (!ParserInstance.get().isCurrentEvent(SecBlocksAttacksComponent.BlocksAttacksEvent.class)) {
            Skript.error("A damage reduction can only be applied in a 'blocks attacks' section");
            return false;
        }
        if (sectionNode == null) return false;
        EntryContainer validate = VALIDATOR.validate(sectionNode);
        if (validate == null) {
            return false;
        }

        this.type = (Expression<?>) validate.getOptional("type", false);
        this.base = (Expression<Number>) validate.getOptional("base", false);
        this.factor = (Expression<Number>) validate.getOptional("factor", false);
        this.horizontalBlockingAngle = (Expression<Number>) validate.getOptional("horizontal_blocking_angle", false);
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected @Nullable TriggerItem walk(Event event) {
        if (!(event instanceof SecBlocksAttacksComponent.BlocksAttacksEvent blocksAttacksEvent)) {
            return super.walk(event, false);
        }

        DamageReduction.Builder builder = DamageReduction.damageReduction();

        if (this.type != null) {
            if (!this.type.isSingle() && this.type.getReturnType().equals(DamageType.class)) {
                List<TypedKey<DamageType>> types = new ArrayList<>();
                for (Object o : this.type.getArray(event)) {
                    if (o instanceof DamageType damageType) {
                        types.add(TypedKey.create(RegistryKey.DAMAGE_TYPE, damageType.key()));
                    }
                }
                RegistryKeySet<DamageType> typedKeys = RegistrySet.keySet(RegistryKey.DAMAGE_TYPE, types);
                builder.type(typedKeys);
            } else {
                Registry<DamageType> registry = RegistryUtils.getRegistry(RegistryKey.DAMAGE_TYPE);

                Object o = this.type.getSingle(event);
                if (o instanceof String s) {
                    NamespacedKey namespacedKey = Utils.getNamespacedKey(s, false);
                    if (namespacedKey != null) {
                        TagKey<DamageType> damageTypeTagKey = TagKey.create(RegistryKey.DAMAGE_TYPE, namespacedKey);
                        Tag<DamageType> tag = registry.getTag(damageTypeTagKey);
                        if (tag != null) {
                            builder.type(tag);
                        }
                    }
                } else if (o instanceof TagKey<?> tagKey) {
                    Tag<DamageType> tag = registry.getTag((TagKey<DamageType>) tagKey);
                    if (tag != null) {
                        builder.type(tag);
                    }
                }
            }
        }

        Number base = this.base.getSingle(event);
        assert base != null;
        builder.base(base.floatValue());

        Number factor = this.factor.getSingle(event);
        assert factor != null;
        builder.factor(factor.floatValue());

        if (this.horizontalBlockingAngle != null) {
            Number angle = this.horizontalBlockingAngle.getSingle(event);
            if (angle != null) {
                builder.horizontalBlockingAngle(Math.max(0, angle.floatValue()));
            }
        }

        blocksAttacksEvent.addReduction(builder.build());
        return super.walk(event, false);
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "apply damage reduction";
    }

}
