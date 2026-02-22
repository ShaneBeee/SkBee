package com.shanebeestudios.skbee.elements.itemcomponent.sections;

import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.EffectSection;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.util.Timespan;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.registry.KeyUtils;
import com.shanebeestudios.skbee.api.util.ItemUtils;
import com.shanebeestudios.skbee.api.util.SimpleEntryValidator;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Consumable;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation;
import net.kyori.adventure.key.Key;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;

import java.util.List;
import java.util.Locale;

@SuppressWarnings("UnstableApiUsage")
public class SecConsumableComponent extends EffectSection {

    public static class ConsumeEffectsEvent extends Event {

        private final Consumable.Builder builder;

        public ConsumeEffectsEvent(Consumable.Builder builder) {
            this.builder = builder;
        }

        public Consumable.Builder getConsumableBuilder() {
            return this.builder;
        }

        @Override
        public @NotNull HandlerList getHandlers() {
            throw new IllegalStateException("ConsumeEffectsEvent should never be called");
        }
    }

    private static EntryValidator VALIDATOR;

    public static void register(Registration reg) {
        VALIDATOR = SimpleEntryValidator.builder()
            .addOptionalEntry("consume_seconds", Timespan.class)
            .addOptionalEntry("animation", String.class)
            .addOptionalEntry("sound", String.class)
            .addOptionalEntry("has_consume_particles", Boolean.class)
            .addOptionalEntry("on_consume_effect", ConsumeEffect.class)
            .addOptionalSection("on_consume_effects")
            .build();
        reg.newSection(SecConsumableComponent.class, VALIDATOR,
                "apply consumable [component] to %itemstacks/itemtypes/slots%")
            .name("ItemComponent - Consumable Component Apply")
            .description("Apply a consumable component to an item.",
                "If present, this item can be consumed by the player.",
                "Requires Paper 1.21.3+",
                "See [**Consumable Component**](https://minecraft.wiki/w/Data_component_format#consumable) on McWiki for more info.",
                "**Entries**:",
                "- `consume_seconds` = The amount of time it takes for a player to consume the item. Defaults to 1.6 seconds. [Optional]",
                "- `animation` = The animation used during consumption of the item. [Optional]",
                "  Must be one of \"none\", \"eat\", \"drink\", \"block\", \"bow\", \"spear\", \"crossbow\", \"spyglass\", \"toot_horn\" or \"brush\". Defaults to \"eat\"]",
                "- `sound` = A sound key to player when consumed. Defaults to \"entity.generic.eat\" [Optional]",
                "- `has_consume_particles` = Whether consumption particles are emitted while consuming this item. Defaults to true. [Optional]",
                "- `on_consume_effect` = A `consume effect` to by applied to the component (supports a list) [Optional].",
                "- `on_consume_effects` = A section to apply `consume effects` [Optional].")
            .examples("apply consumable to {_i}:",
                "\tanimation: \"drink\"",
                "\tconsume_seconds: 2.5 seconds",
                "\ton_consume_effects:",
                "\t\tapply -> potion effect of slowness for 10 seconds with probability 0.5",
                "\t\tapply -> clear all effects",
                "\t\tapply -> remove effects night vision",
                "\t\tapply -> play sound \"blah.blah\"",
                "\t\tapply -> teleport randomly within 15",
                "\t\tapply -> teleport randomly within 20 meters",
                "\t\tapply -> teleport randomly within 100 blocks",
                "",
                "set {_effects} to apply_effects(potion effect of night vision for 10 seconds, 0.5)",
                "set {_i} to 1 of stick",
                "apply consumable component to {_i}:",
                "\tconsume_seconds: 3.2 seconds",
                "\tanimation: \"brush\"",
                "\tsound: \"block.stone.break\"",
                "\thas_consume_particles: false",
                "\ton_consume_effect: {_effects}",
                "give {_i} to player")
            .since("3.8.0")
            .register();
    }

    private Expression<Object> items;
    private Expression<Timespan> consumeSeconds;
    private Expression<String> animation;
    private Expression<String> sound;
    private Expression<Boolean> hasConsumeParticles;
    private Expression<ConsumeEffect> onConsumeEffect;
    private Trigger onConsumeEffectsSection;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult, @Nullable SectionNode sectionNode, @Nullable List<TriggerItem> triggerItems) {
        this.items = (Expression<Object>) exprs[0];
        if (sectionNode == null) return true;
        EntryContainer container = VALIDATOR.validate(sectionNode);
        if (container == null) {
            return false;
        }
        this.consumeSeconds = (Expression<Timespan>) container.getOptional("consume_seconds", false);
        this.animation = (Expression<String>) container.getOptional("animation", false);
        this.sound = (Expression<String>) container.getOptional("sound", false);
        this.hasConsumeParticles = (Expression<Boolean>) container.getOptional("has_consume_particles", false);
        this.onConsumeEffect = (Expression<ConsumeEffect>) container.getOptional("on_consume_effect", false);

        SectionNode effectsNode = container.getOptional("on_consume_effects", SectionNode.class, false);
        if (effectsNode != null) {
            this.onConsumeEffectsSection = loadCode(effectsNode, "on_consume_effects", ConsumeEffectsEvent.class);
        }
        return true;
    }

    @Override
    protected @Nullable TriggerItem walk(Event event) {
        Consumable.Builder builder = Consumable.consumable();

        if (this.consumeSeconds != null) {
            Timespan timespan = this.consumeSeconds.getOptionalSingle(event).orElse(new Timespan(1600));
            builder.consumeSeconds((float) timespan.getAs(Timespan.TimePeriod.MILLISECOND) / 1000);
        }
        if (this.animation != null) {
            String animation = this.animation.getOptionalSingle(event).orElse("eat");
            ItemUseAnimation itemUseAnimation = getAnimation(animation);
            builder.animation(itemUseAnimation);
        }
        if (this.sound != null) {
            String soundString = this.sound.getOptionalSingle(event).orElse("entity.generic.eat");
            Key key = KeyUtils.getKey(soundString);
            if (key != null) {
                builder.sound(key);
            }
        }
        if (this.hasConsumeParticles != null) {
            Boolean hasConsumeParticles = this.hasConsumeParticles.getOptionalSingle(event).orElse(true);
            builder.hasConsumeParticles(hasConsumeParticles);
        }
        if (this.onConsumeEffect != null) {
            for (ConsumeEffect consumeEffect : this.onConsumeEffect.getArray(event)) {
                builder.addEffect(consumeEffect);
            }
        }

        if (this.onConsumeEffectsSection != null) {
            ConsumeEffectsEvent consumeEffectsEvent = new ConsumeEffectsEvent(builder);
            Variables.setLocalVariables(consumeEffectsEvent, Variables.copyLocalVariables(event));
            Trigger.walk(this.onConsumeEffectsSection, consumeEffectsEvent);
            Variables.setLocalVariables(event, Variables.copyLocalVariables(consumeEffectsEvent));
            Variables.copyLocalVariables(consumeEffectsEvent);
        }

        Consumable consumable = builder.build();
        ItemUtils.modifyItems(this.items.getArray(event), itemStack ->
            itemStack.setData(DataComponentTypes.CONSUMABLE, consumable));
        return super.walk(event, false);
    }

    @Override
    public String toString(Event e, boolean d) {
        return "apply consumable component to " + this.items.toString(e, d);
    }

    private static ItemUseAnimation getAnimation(String string) {
        string = string.replace(" ", "_").toUpperCase(Locale.ROOT);
        try {
            return ItemUseAnimation.valueOf(string);
        } catch (IllegalArgumentException e) {
            return ItemUseAnimation.EAT;
        }
    }

}
