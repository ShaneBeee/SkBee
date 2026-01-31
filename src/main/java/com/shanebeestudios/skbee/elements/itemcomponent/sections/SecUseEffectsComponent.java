package com.shanebeestudios.skbee.elements.itemcomponent.sections;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
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
import com.shanebeestudios.skbee.api.util.SimpleEntryValidator;
import com.shanebeestudios.skbee.api.util.Util;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.UseEffects;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;

import java.util.List;

@SuppressWarnings({"unchecked", "UnstableApiUsage"})
@Name("ItemComponent - Use Effects Component Apply")
@Description({"Controls how the player behaves when using an item (right mouse click).",
    "See [**Use Effects Component**](https://minecraft.wiki/w/Data_component_format#use_effects) on McWiki for more details.",
    "Requires Minecraft 1.21.11+",
    "",
    "**ENTRIES**:",
    "All entries are optional and will use their defaults when omitted.",
    "- `can_sprint` = Boolean, whether the player can sprint during use. Defaults to false",
    "- `speed_multiplier` = A ranged float (0.0-1.0 inclusive) speed multiplier inflicted during use. Defaults to 0.2",
    "- `interact_vibrations` = Boolean, whether using this item emits the `minecraft:item_interact_start` and `minecraft:item_interact_finish` game events. Defaults to true"})
@Examples({"set {_i} to 1 of shield",
    "apply use effects component to {_i}:",
    "\tcan_sprint: true",
    "\tspeed_multiplier: 0.7",
    "\tinteract_vibrations: false",
    "",
    "give player 1 of {_i}"})
@Since("3.16.0")
public class SecUseEffectsComponent extends Section {

    private static EntryValidator VALIDATOR;

    static {
        if (Util.IS_RUNNING_MC_1_21_11) {
            VALIDATOR = SimpleEntryValidator.builder()
                .addOptionalEntry("can_sprint", Boolean.class)
                .addOptionalEntry("speed_multiplier", Number.class)
                .addOptionalEntry("interact_vibrations", Boolean.class)
                .build();

            Skript.registerSection(SecUseEffectsComponent.class,
                "apply use effects component to %itemstacks/itemtypes/slots%");
        }
    }

    private Expression<?> items;
    private Expression<Boolean> canSprint;
    private Expression<Number> speedMultiplier;
    private Expression<Boolean> interactVibrations;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> triggerItems) {
        if (sectionNode == null) return false;
        EntryContainer validate = VALIDATOR.validate(sectionNode);
        if (validate == null) {
            return false;
        }
        this.items = exprs[0];

        this.canSprint = (Expression<Boolean>) validate.getOptional("can_sprint", false);
        this.speedMultiplier = (Expression<Number>) validate.getOptional("speed_multiplier", false);
        this.interactVibrations = (Expression<Boolean>) validate.getOptional("interact_vibrations", false);
        return true;
    }

    @Override
    protected @Nullable TriggerItem walk(Event event) {
        UseEffects.Builder builder = UseEffects.useEffects();

        if (this.canSprint != null) {
            Boolean bool = this.canSprint.getSingle(event);
            if (bool != null) builder.canSprint(bool);
        }
        if (this.speedMultiplier != null) {
            Number num = this.speedMultiplier.getSingle(event);
            if (num != null) builder.speedMultiplier(num.floatValue());

        }
        if (this.interactVibrations != null) {
            Boolean bool = this.interactVibrations.getSingle(event);
            if (bool != null) builder.interactVibrations(bool);
        }

        ItemComponentUtils.modifyComponent(this.items.getArray(event), Changer.ChangeMode.SET,
            DataComponentTypes.USE_EFFECTS, builder.build());

        return super.walk(event, false);
    }

    @Override
    public String toString(@Nullable Event e, boolean d) {
        return "apply use effects component to " + this.items.toString(e, d);
    }

}
