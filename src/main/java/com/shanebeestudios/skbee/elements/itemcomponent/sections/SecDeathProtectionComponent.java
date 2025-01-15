package com.shanebeestudios.skbee.elements.itemcomponent.sections;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.EffectSection;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.util.ItemUtils;
import com.shanebeestudios.skbee.api.util.SimpleEntryValidator;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.DeathProtection;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;

import java.util.List;

@Name("ItemComponent - Death Protection Component Apply")
@Description({"Apply a death protection component to an item.",
    "If present, this item protects the holder from dying by restoring a single health point.",
    "Requires Paper 1.21.3+",
    "See [**Death Protection Component**](https://minecraft.wiki/w/Data_component_format#death_protection) on McWiki for more info.",
    "`death_effects` = A `consume effect` to by applied to the component (supports a list)."})
@Examples({"set {_p::1} to potion effect of night vision for 10 seconds",
    "set {_p::2} to potion effect of slow mining for 5 seconds",
    "set {_effects} to apply_effects({_p::*}, 0.5)",
    "",
    "set {_i} to 1 of stick",
    "apply death protection component to {_i}:",
    "\tdeath_effects: {_effects}",
    "give {_i} to player"})
@Since("3.8.0")
@SuppressWarnings("UnstableApiUsage")
public class SecDeathProtectionComponent extends EffectSection {

    private static final EntryValidator VALIDATOR;

    static {
        VALIDATOR = SimpleEntryValidator.builder()
            .addOptionalEntry("death_effects", ConsumeEffect.class)
            .build();
        Skript.registerSection(SecDeathProtectionComponent.class,
            "apply death protection [component] to %itemstacks/itemtypes/slots%");
    }

    private Expression<Object> items;
    private Expression<ConsumeEffect> effects;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult, @Nullable SectionNode sectionNode, @Nullable List<TriggerItem> triggerItems) {
        this.items = (Expression<Object>) exprs[0];
        if (sectionNode != null) {
            EntryContainer container = VALIDATOR.validate(sectionNode);
            if (container != null) {
                this.effects = (Expression<ConsumeEffect>) container.getOptional("death_effects", false);
            }
        }
        return true;
    }

    @Override
    protected @Nullable TriggerItem walk(Event event) {
        DeathProtection.Builder builder = DeathProtection.deathProtection();
        if (this.effects != null) {
            for (ConsumeEffect consumeEffect : this.effects.getArray(event)) {
                builder.addEffect(consumeEffect);
            }
        }
        DeathProtection deathProtection = builder.build();
        ItemUtils.modifyItems(this.items.getArray(event), itemStack -> {
            itemStack.setData(DataComponentTypes.DEATH_PROTECTION, deathProtection);
        });
        return super.walk(event, false);
    }

    @Override
    public String toString(Event e, boolean d) {
        return "apply death protection component to " + this.items.toString(e, d);
    }

}
