package com.shanebeestudios.skbee.elements.itemcomponent.sections;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.util.Color;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.skript.base.Section;
import com.shanebeestudios.skbee.api.util.ItemUtils;
import com.shanebeestudios.skbee.api.util.SimpleEntryValidator;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.PotionContents;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class SecPotionContentsComponent extends Section {

    public static class PotionContentsEvent extends Event {

        private final PotionContents.Builder potionContents;

        public PotionContentsEvent(PotionContents.Builder potionContents) {
            this.potionContents = potionContents;
        }

        public PotionContents.Builder getPotionContentsBuilder() {
            return this.potionContents;
        }

        @Override
        public @NotNull HandlerList getHandlers() {
            throw new IllegalStateException("PotionContentsEvent should never be called");
        }
    }

    private static EntryValidator VALIDATOR;

    public static void register(Registration reg) {
        VALIDATOR = SimpleEntryValidator.builder()
            .addOptionalEntry("potion", PotionType.class)
            .addOptionalEntry("custom_color", Color.class)
            .addOptionalEntry("custom_name", String.class)
            .addOptionalSection("custom_effects")
            .build();
        reg.newSection(SecPotionContentsComponent.class, VALIDATOR,
                "apply potion contents [component] to %itemstacks/itemtypes/slots%")
            .name("ItemComponent - Potion Contents Component Apply")
            .description("Apply a potion contents component to an item (can be used on a potion/arrow or any consumable item).",
                "See [**Potion Contents Component**](https://minecraft.wiki/w/Data_component_format#potion_contents) on McWiki for more info.",
                "Note: `potion` and `custom_effects` entries cannot be used together.",
                "",
                "**Entries**:",
                "- `potion` = The base potion of the item.",
                "- `custom_color` = The overriding color of this potion texture, and/or the particles of the area effect cloud created.",
                "- `custom_name` = An optional string used to generate containing stack name. (See McWiki for more details on this) [Optional]",
                "- `custom_effects` = A list of the additional effects that this item should apply. [Optional]")
            .examples("apply potion contents to {_i}:",
                "\tpotion: long_swiftness",
                "\tcustom_color: rgb(126, 207, 243)",
                "",
                "apply potion contents component to {_i}:",
                "\tcustom_color: pink",
                "\tcustom_name: \"harming\"",
                "\tcustom_effects:",
                "\t\tapply -> potion effect of night vision for 5 minutes",
                "\t\tapply -> potion effect of slowness for 6 minutes",
                "",
                "set {_i} to 1 of potion",
                "set {_pe::*} to active potion effects of player",
                "apply potion contents to {_i}:",
                "\tcustom_color: rgb(126, 207, 243)",
                "\tcustom_effects:",
                "\t\tapply effects {_pe::*}",
                "give {_i} to player")
            .since("3.8.1")
            .register();
    }

    private Expression<?> items;
    private Expression<PotionType> potionType;
    private Expression<Color> customColor;
    private Expression<String> customName;
    private Trigger customEffects;


    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> triggerItems) {
        EntryContainer container = VALIDATOR.validate(sectionNode);
        if (container == null) {
            return false;
        }
        this.items = exprs[0];
        this.potionType = (Expression<PotionType>) container.getOptional("potion", false);
        this.customColor = (Expression<Color>) container.getOptional("custom_color", false);
        this.customName = (Expression<String>) container.getOptional("custom_name", false);

        SectionNode rulesNode = container.getOptional("custom_effects", SectionNode.class, false);
        if (rulesNode != null) {
            this.customEffects = loadCode(rulesNode, "custom_effects", PotionContentsEvent.class);
        }
        if (this.potionType != null && this.customEffects != null) {
            Skript.error("You cannot have both a 'potion' AND 'custom_effects'");
            return false;
        }
        return true;
    }

    @Override
    protected @Nullable TriggerItem walk(Event event) {
        PotionContents.Builder builder = PotionContents.potionContents();
        if (this.potionType != null) {
            PotionType potionType = this.potionType.getSingle(event);
            if (potionType != null) {
                builder.potion(potionType);
            } else {
                error("Invalid potion type: " + this.potionType.toString(event, true));
            }
        }
        if (this.customColor != null) {
            Color color = this.customColor.getSingle(event);
            if (color != null) {
                builder.customColor(color.asBukkitColor());
            } else {
                error("Invalid color: " + this.customColor.toString(event, true));
            }
        }
        if (this.customName != null) {
            String name = this.customName.getSingle(event);
            if (name != null) {
                builder.customName(name);
            } else {
                error("Invalid name: " + this.customName.toString(event, true));
            }
        }

        if (this.customEffects != null) {
            PotionContentsEvent potionSection = new PotionContentsEvent(builder);
            Variables.setLocalVariables(potionSection, Variables.copyLocalVariables(event));
            Trigger.walk(this.customEffects, potionSection);
            Variables.setLocalVariables(event, Variables.copyLocalVariables(potionSection));
            Variables.copyLocalVariables(potionSection);
        }

        PotionContents potionContents = builder.build();
        ItemUtils.modifyItems(this.items.getArray(event), itemStack ->
            itemStack.setData(DataComponentTypes.POTION_CONTENTS, potionContents));

        return super.walk(event, false);
    }

    @Override
    public String toString(Event e, boolean d) {
        return "apply potion contents to " + this.items.toString(e, d);
    }

}
