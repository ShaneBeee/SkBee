package com.shanebeestudios.skbee.elements.itemcomponent.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.skript.base.Effect;
import com.shanebeestudios.skbee.elements.itemcomponent.sections.SecPotionContentsComponent.PotionContentsEvent;
import io.papermc.paper.datacomponent.item.PotionContents;
import org.bukkit.event.Event;
import org.bukkit.potion.PotionEffect;

@Name("ItemComponent - Apply Effects")
@Description("Used to apply a potion effect in a potion contents section's `custom_effects` section.")
@Examples({"apply potion contents to {_i}:",
    "\tpotion: long_swiftness",
    "\tcustom_color: rgb(126, 207, 243)",
    "",
    "apply potion contents component to {_i}:",
    "\tcustom_color: pink",
    "\tcustom_name: \"harming\"",
    "\tcustom_effects:",
    "\t\tapply -> potion effect of night vision for 5 minutes",
    "\t\tapply -> potion effect of slowness for 6 minutes"})
@Since("INSERT VERSION")
public class EffApplyToComponent extends Effect {

    static {
        Skript.registerEffect(EffApplyToComponent.class, "apply (effect[s]|->) %potioneffects%");
    }

    private Expression<PotionEffect> effects;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        if (!getParser().isCurrentEvent(PotionContentsEvent.class)) {
            Skript.error("'apply effect' can only be used in a potion contents 'custom_effects' section.");
            return false;
        }
        this.effects = (Expression<PotionEffect>) exprs[0];
        return true;
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    protected void execute(Event event) {
        if (event instanceof PotionContentsEvent potionContentsEvent) {
            PotionContents.Builder builder = potionContentsEvent.getPotionContentsBuilder();
            for (PotionEffect potionEffect : this.effects.getArray(event)) {
                builder.addCustomEffect(potionEffect);
            }
        }
    }

    @Override
    public String toString(Event e, boolean d) {
        return "apply " + this.effects.toString(e, d);
    }

}
