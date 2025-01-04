package com.shanebeestudios.skbee.elements.itemcomponent.sections;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.EffectSection;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.util.ItemUtils;
import com.shanebeestudios.skbee.api.util.Util;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Consumable;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;
import org.skriptlang.skript.lang.entry.util.ExpressionEntryData;

import java.util.List;
import java.util.Locale;

@Name("ItemComponent - Consumable Component Apply")
@Description({"Apply a consumable component to an item.",
    "If present, this item can be consumed by the player.",
    "Requires Paper 1.21.3+",
    "See [**Consumable Component**](https://minecraft.wiki/w/Data_component_format#consumable) on McWiki for more info.",
    "**Entries**:",
    "- `consume_seconds` = The amount of time it takes for a player to consume the item. Defaults to 1.6 seconds. [Optional]",
    "- `animation` = The animation used during consumption of the item. [Optional]",
    "  Must be one of \"none\", \"eat\", \"drink\", \"block\", \"bow\", \"spear\", \"crossbow\", \"spyglass\", \"toot_horn\" or \"brush\". Defaults to \"eat\"]",
    "- `sound` = A sound key to player when consumed. Defaults to \"entity.generic.eat\" [Optional]",
    "- `has_consume_particles` = Whether consumption particles are emitted while consuming this item. Defaults to true. [Optional]",
    "- `on_consume_effects` = A `consume effect` to by applied to the component (supports a list) [Optional]."})
@Examples({"set {_effects} to apply_effects(potion effect of night vision for 10 seconds, 0.5)",
    "",
    "set {_i} to 1 of stick",
    "apply consumable component to {_i}:",
    "\tconsume_seconds: 3.2 seconds",
    "\tanimation: \"brush\"",
    "\tsound: \"block.stone.break\"",
    "\thas_consume_particles: false",
    "\ton_consume_effects: {_effects}",
    "give {_i} to player"})
@Since("INSERT VERSION")
@SuppressWarnings("UnstableApiUsage")
public class SecConsumableComponent extends EffectSection {

    private static final EntryValidator VALIDATOR;

    static {
        VALIDATOR = EntryValidator.builder()
            .addEntryData(new ExpressionEntryData<>("consume_seconds", null, true, Timespan.class))
            .addEntryData(new ExpressionEntryData<>("animation", null, true, String.class))
            .addEntryData(new ExpressionEntryData<>("sound", null, true, String.class))
            .addEntryData(new ExpressionEntryData<>("has_consume_particles", null, true, Boolean.class))
            .addEntryData(new ExpressionEntryData<>("on_consume_effects", null, true, ConsumeEffect.class))
            .build();
        Skript.registerSection(SecConsumableComponent.class,
            "apply consumable [component] to %itemstacks/itemtypes/slots%");
    }

    private Expression<Object> items;
    private Expression<Timespan> consumeSeconds;
    private Expression<String> animation;
    private Expression<String> sound;
    private Expression<Boolean> hasConsumeParticles;
    private Expression<ConsumeEffect> onConsumeEffects;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult, @Nullable SectionNode sectionNode, @Nullable List<TriggerItem> triggerItems) {
        this.items = (Expression<Object>) exprs[0];
        if (sectionNode != null) {
            EntryContainer container = VALIDATOR.validate(sectionNode);
            if (container != null) {
                this.consumeSeconds = (Expression<Timespan>) container.getOptional("consume_seconds", false);
                this.animation = (Expression<String>) container.getOptional("animation", false);
                this.sound = (Expression<String>) container.getOptional("sound", false);
                this.hasConsumeParticles = (Expression<Boolean>) container.getOptional("has_consume_particles", false);
                this.onConsumeEffects = (Expression<ConsumeEffect>) container.getOptional("on_consume_effects", false);
            }
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
            NamespacedKey namespacedKey = Util.getNamespacedKey(soundString, false);
            if (namespacedKey != null) {
                builder.sound(namespacedKey);
            }
        }
        if (this.hasConsumeParticles != null) {
            Boolean hasConsumeParticles = this.hasConsumeParticles.getOptionalSingle(event).orElse(true);
            builder.hasConsumeParticles(hasConsumeParticles);
        }
        if (this.onConsumeEffects != null) {
            for (ConsumeEffect consumeEffect : this.onConsumeEffects.getArray(event)) {
                builder.addEffect(consumeEffect);
            }
        }

        Consumable consumable = builder.build();
        ItemUtils.modifyItems(this.items.getArray(event), itemStack -> {
            itemStack.setData(DataComponentTypes.CONSUMABLE, consumable);
        });
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
