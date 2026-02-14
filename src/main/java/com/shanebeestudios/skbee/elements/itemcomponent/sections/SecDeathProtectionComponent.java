package com.shanebeestudios.skbee.elements.itemcomponent.sections;

import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.EffectSection;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.util.ItemUtils;
import com.shanebeestudios.skbee.api.util.SimpleEntryValidator;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.DeathProtection;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class SecDeathProtectionComponent extends EffectSection {

    public static class DeathProtectionEvent extends Event {

        private final DeathProtection.Builder builder;

        public DeathProtectionEvent(DeathProtection.Builder builder) {
            this.builder = builder;
        }

        public DeathProtection.Builder getDeathProtectionBuilder() {
            return this.builder;
        }

        @Override
        public @NotNull HandlerList getHandlers() {
            throw new IllegalStateException("DeathProtectionEvent should never be called");
        }
    }

    private static EntryValidator VALIDATOR;

    public static void register(Registration reg) {
        VALIDATOR = SimpleEntryValidator.builder()
            .addOptionalEntry("death_effect", ConsumeEffect.class)
            .addOptionalSection("death_effects")
            .build();
        reg.newSection(SecDeathProtectionComponent.class,
                "apply death protection [component] to %itemstacks/itemtypes/slots%")
            .name("ItemComponent - Death Protection Component Apply")
            .description("Apply a death protection component to an item.",
                "If present, this item protects the holder from dying by restoring a single health point.",
                "Requires Paper 1.21.3+",
                "See [**Death Protection Component**](https://minecraft.wiki/w/Data_component_format#death_protection) on McWiki for more info.",
                "`death_effect` = A `consume effect` to by applied to the component (supports a list) [Optional].",
                "`death_effects` = A section to apply `consume effects` [Optional].")
            .examples("set {_p::1} to potion effect of night vision for 10 seconds",
                "set {_p::2} to potion effect of slow mining for 5 seconds",
                "set {_effects} to apply_effects({_p::*}, 0.5)",
                "",
                "set {_i} to 1 of stick",
                "apply death protection component to {_i}:",
                "\tdeath_effects: {_effects}",
                "give {_i} to player",
                "",
                "apply death protection to {_i}:",
                "\tdeath_effects:",
                "\t\tapply -> potion effect of slowness for 10 seconds with probability 0.5",
                "\t\tapply -> clear all effects",
                "\t\tapply -> remove effects night vision",
                "\t\tapply -> play sound \"blah.blah\"",
                "\t\tapply -> teleport randomly within 15",
                "\t\tapply -> teleport randomly within 20 meters",
                "\t\tapply -> teleport randomly within 100 blocks")
            .since("3.8.0")
            .register();
    }

    private Expression<Object> items;
    private Expression<ConsumeEffect> effect;
    private Trigger effectsSection;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult, @Nullable SectionNode sectionNode, @Nullable List<TriggerItem> triggerItems) {
        this.items = (Expression<Object>) exprs[0];
        if (sectionNode == null) return true;
        EntryContainer container = VALIDATOR.validate(sectionNode);
        if (container == null) {
            return false;
        }
        this.effect = (Expression<ConsumeEffect>) container.getOptional("death_effect", false);
        SectionNode node = container.getOptional("death_effects", SectionNode.class, false);
        if (node != null) {
            this.effectsSection = loadCode(node, "death_effects", DeathProtectionEvent.class);
        }
        return true;
    }

    @Override
    protected @Nullable TriggerItem walk(Event event) {
        DeathProtection.Builder builder = DeathProtection.deathProtection();
        if (this.effect != null) {
            for (ConsumeEffect consumeEffect : this.effect.getArray(event)) {
                builder.addEffect(consumeEffect);
            }
        }
        if (this.effectsSection != null) {
            DeathProtectionEvent deathProtectionEvent = new DeathProtectionEvent(builder);
            Variables.setLocalVariables(deathProtectionEvent, Variables.copyLocalVariables(event));
            Trigger.walk(this.effectsSection, deathProtectionEvent);
            Variables.setLocalVariables(event, Variables.copyLocalVariables(deathProtectionEvent));
            Variables.copyLocalVariables(deathProtectionEvent);
        }
        DeathProtection deathProtection = builder.build();
        ItemUtils.modifyItems(this.items.getArray(event), itemStack ->
            itemStack.setData(DataComponentTypes.DEATH_PROTECTION, deathProtection));
        return super.walk(event, false);
    }

    @Override
    public String toString(Event e, boolean d) {
        return "apply death protection component to " + this.items.toString(e, d);
    }

}
