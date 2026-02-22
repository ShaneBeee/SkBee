package com.shanebeestudios.skbee.elements.itemcomponent.sections;

import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.skript.base.Section;
import com.shanebeestudios.skbee.api.util.ItemUtils;
import com.shanebeestudios.skbee.api.util.SimpleEntryValidator;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class SecTooltipDisplayComponent extends Section {

    private static EntryValidator VALIDATOR;

    public static void register(Registration reg) {
        VALIDATOR = SimpleEntryValidator.builder()
            .addOptionalEntry("hide_tooltip", Boolean.class)
            .addOptionalEntry("hidden_components", DataComponentType.class)
            .build();
        reg.newSection(SecTooltipDisplayComponent.class, VALIDATOR,
                "apply tooltip display [component] to %itemstacks/itemtypes/slots%")
            .name("ItemComponent - Tooltip Style Component Apply")
            .description("Apply a tooltip style component to any item allowing you to hide the tooltip or specific components.",
                "You will also require a `consumable` component to actually make the item consumable.",
                "Requires Paper 1.21.5+",
                "See [**Tooltip Style Component**](https://minecraft.wiki/w/Data_component_format#tooltip_style) on McWiki for more details.",
                "",
                "**Entries/Sections**:",
                "- `hide_tooltip` = If true, the item will have no tooltip when hovered (optional, boolean).",
                "- `hidden_components` = The tooltips provided by any component in this list will be hidden. " +
                    "If that component provides no tooltip, it will have no effect (optional, data component types).")
            .examples("apply tooltip display component to {_i}:",
                "\thide_tooltip: false",
                "\thidden_components: minecraft:attribute_modifiers, minecraft:enchantments",
                "apply tooltip display component to {_i}:",
                "\thide_tooltip: true")
            .since("3.11.0")
            .register();
    }

    private Expression<Object> items;
    private Expression<Boolean> hideTooltip;
    private Expression<DataComponentType> hiddenComponents;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> triggerItems) {
        EntryContainer container = VALIDATOR.validate(sectionNode);
        if (container == null) return false;

        this.items = (Expression<Object>) exprs[0];
        this.hideTooltip = (Expression<Boolean>) container.getOptional("hide_tooltip", false);
        this.hiddenComponents = (Expression<DataComponentType>) container.getOptional("hidden_components", false);
        return true;
    }

    @Override
    protected @Nullable TriggerItem walk(Event event) {
        TooltipDisplay.Builder builder = TooltipDisplay.tooltipDisplay();

        if (this.hideTooltip != null) {
            Boolean hideTooltip = this.hideTooltip.getOptionalSingle(event).orElse(false);
            builder.hideTooltip(hideTooltip);
        }

        if (this.hiddenComponents != null) {
            builder.addHiddenComponents(this.hiddenComponents.getArray(event));
        }

        TooltipDisplay tooltipDisplay = builder.build();

        ItemUtils.modifyItems(this.items.getArray(event), itemStack -> {
            itemStack.setData(DataComponentTypes.TOOLTIP_DISPLAY, tooltipDisplay);
        });
        return super.walk(event, false);
    }

    @Override
    public String toString(@Nullable Event e, boolean d) {
        return "apply tooltip display to " + this.items.toString(e, d);
    }

}
