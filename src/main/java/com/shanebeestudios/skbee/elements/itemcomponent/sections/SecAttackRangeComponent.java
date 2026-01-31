package com.shanebeestudios.skbee.elements.itemcomponent.sections;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.skript.base.Section;
import com.shanebeestudios.skbee.api.util.ItemComponentUtils;
import com.shanebeestudios.skbee.api.util.MathUtil;
import com.shanebeestudios.skbee.api.util.SimpleEntryValidator;
import com.shanebeestudios.skbee.api.util.Util;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.AttackRange;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
@Name("ItemComponent - Attack Range Component Apply")
@Description({"Enables a custom attack range when using the item.",
    "See [**Attack Range Component**](https://minecraft.wiki/w/Data_component_format#attack_range) on McWiki for more details.",
    "Requires Minecraft 1.21.11+",
    "",
    "**ENTRIES**:",
    "All entries are optional and will use their defaults when omitted.",
    "- `min_reach` = The minimum distance in blocks from the attacker to the target to be considered valid. Default = 0, valid range = 0 to 64",
    "- `max_reach` = The maximum distance in blocks from the attacker to the target to be considered valid. Default = 3, valid range = 0 to 64",
    "- `min_creative_reach` = The minimum distance in blocks from the attacker to the target to be considered valid in creative mode. Default = 0, valid range = 0 to 64",
    "- `max_creative_reach` = The maximum distance in blocks from the attacker to the target to be considered valid in creative mode. Default = 5, valid range = 0 to 64",
    "- `hitbox_margin` = Tthe margin applied to the target bounding box when checking for valid hitbox collision. Default = 0.3, valid range = 0 to 1",
    "- `mob_factor` = The multiplier applied to the min_range and max_range when checking for valid distance when item is used by a mob. Default = 1, valid range = 0 to 2"})
@Examples({"set {_i} to 1 of diamond shovel",
    "apply attack range component to {_i}:",
    "\tmin_reach: 0.0",
    "\tmax_reach: 64.0",
    "\tmin_creative_reach: 0",
    "\tmax_creative_reach: 64",
    "\thitbox_margin: 0.3",
    "\tmob_factor: 1",
    "",
    "give player 1 of {_i}"})
@Since("INSERT VERSION")
public class SecAttackRangeComponent extends Section {

    private static EntryValidator VALIDATOR;

    static {
        if (Util.IS_RUNNING_MC_1_21_11) {
            VALIDATOR = SimpleEntryValidator.builder()
                .addOptionalEntry("min_reach", Number.class)
                .addOptionalEntry("max_reach", Number.class)
                .addOptionalEntry("min_creative_reach", Number.class)
                .addOptionalEntry("max_creative_reach", Number.class)
                .addOptionalEntry("hitbox_margin", Number.class)
                .addOptionalEntry("mob_factor", Number.class)
                .build();

            Skript.registerSection(SecAttackRangeComponent.class,
                "apply attack range component to %itemstacks/itemtypes/slots%");
        }
    }

    private Expression<?> items;
    private Expression<Number> minReach;
    private Expression<Number> maxReach;
    private Expression<Number> minCreativeReach;
    private Expression<Number> maxCreativeReach;
    private Expression<Number> hitboxMargin;
    private Expression<Number> mobFactor;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> triggerItems) {
        if (sectionNode == null) return false;
        EntryContainer validate = VALIDATOR.validate(sectionNode);
        if (validate == null) {
            return false;
        }
        this.items = exprs[0];

        this.minReach = (Expression<Number>) validate.getOptional("min_reach", false);
        this.maxReach = (Expression<Number>) validate.getOptional("max_reach", false);
        this.minCreativeReach = (Expression<Number>) validate.getOptional("min_creative_reach", false);
        this.maxCreativeReach = (Expression<Number>) validate.getOptional("max_creative_reach", false);
        this.hitboxMargin = (Expression<Number>) validate.getOptional("hitbox_margin", false);
        this.mobFactor = (Expression<Number>) validate.getOptional("mob_factor", false);

        return true;
    }

    @Override
    protected @Nullable TriggerItem walk(Event event) {
        AttackRange.Builder builder = AttackRange.attackRange();

        if (this.minReach != null) {
            Number single = this.minReach.getSingle(event);
            if (single != null) builder.minReach(MathUtil.clamp(single.floatValue(), 0.0f, 64.0f));
        }
        if (this.maxReach != null) {
            Number single = this.maxReach.getSingle(event);
            if (single != null) builder.maxReach(MathUtil.clamp(single.floatValue(), 0.0f, 64.0f));
        }
        if (this.minCreativeReach != null) {
            Number single = this.minCreativeReach.getSingle(event);
            if (single != null) builder.minCreativeReach(MathUtil.clamp(single.floatValue(), 0.0f, 64.0f));
        }
        if (this.maxCreativeReach != null) {
            Number single = this.maxCreativeReach.getSingle(event);
            if (single != null) builder.maxCreativeReach(MathUtil.clamp(single.floatValue(), 0.0f, 64.0f));
        }
        if (this.hitboxMargin != null) {
            Number single = this.hitboxMargin.getSingle(event);
            if (single != null) builder.hitboxMargin(MathUtil.clamp(single.floatValue(), 0.0f, 1.0f));
        }
        if (this.mobFactor != null) {
            Number single = this.mobFactor.getSingle(event);
            if (single != null) builder.mobFactor(MathUtil.clamp(single.floatValue(), 0.0f, 2.0f));
        }

        ItemComponentUtils.modifyComponent(this.items.getArray(event), ChangeMode.SET, DataComponentTypes.ATTACK_RANGE, builder.build());
        return super.walk(event, false);
    }

    @Override
    public String toString(@Nullable Event e, boolean d) {
        return "apply attack range component to " + this.items.toString(e, d);
    }

}
