package com.shanebeestudios.skbee.elements.itemcomponent.sections;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.Section;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import ch.njol.util.Math2;
import com.shanebeestudios.skbee.api.util.ItemUtils;
import com.shanebeestudios.skbee.api.util.SimpleEntryValidator;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Fireworks;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;

import java.util.List;

@Name("ItemComponent - Fireworks Component Apply")
@Description({"Apply a fireworks component to a firework rocket.",
    "Requires Paper 1.21.3+",
    "See [**Fireworks Component**](https://minecraft.wiki/w/Data_component_format#fireworks) on McWiki for more info.",
    "",
    "**Entries**:",
    "- `flight_duration` = The flight duration of this firework rocket, i.e. the number of gunpowders used to craft it. " +
        "(Integer between 0 and 255, defaults to 1)",
    "- `explosions` = A section to apply firework explosions (see 'ItemComponent - Firework Explosion Component Apply' section)."})
@Examples({"apply fireworks to {_i}:",
    "\tflight_duration: 3",
    "\texplosions:",
    "\t\tapply firework explosion:",
    "\t\t\tshape: small ball",
    "\t\t\tcolors: red, yellow and white",
    "\t\t\tfade_colors: blue, green and red",
    "\t\t\thas_trail: true",
    "\t\t\thas_twinkle: true",
    "\t\tapply firework explosion:",
    "\t\t\tshape: large ball",
    "\t\t\tcolors: red, white and blue",
    "\t\t\thas_trail: false",
    "\t\t\thas_twinkle: false"})
@Since("3.8.0")
@SuppressWarnings("UnstableApiUsage")
public class SecFireworksComponent extends Section {

    public static class FireworksExplosionsSectionEvent extends Event {

        private final Fireworks.Builder builder;

        public FireworksExplosionsSectionEvent(Fireworks.Builder builder) {
            this.builder = builder;
        }

        public Fireworks.Builder getBuilder() {
            return this.builder;
        }

        @Override
        public @NotNull HandlerList getHandlers() {
            throw new IllegalStateException("FireworksExplosionsSectionEvent should never be called");
        }
    }

    private static final EntryValidator VALIDATOR;

    static {
        VALIDATOR = SimpleEntryValidator.builder()
            .addRequiredSection("explosions")
            .addOptionalEntry("flight_duration", Number.class)
            .build();

        Skript.registerSection(SecFireworksComponent.class,
            "apply fireworks [component] to %itemstacks/itemtypes/slots%");
    }

    private Expression<?> items;
    private Expression<Number> flightDuration;
    private Trigger explosionsSection;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> triggerItems) {
        EntryContainer validate = VALIDATOR.validate(sectionNode);
        if (validate == null) {
            return false;
        }
        this.items = exprs[0];
        this.flightDuration = (Expression<Number>) validate.getOptional("flight_duration", false);

        SectionNode rulesNode = validate.getOptional("explosions", SectionNode.class, false);
        if (rulesNode != null) {
            this.explosionsSection = loadCode(rulesNode, "explosions section", FireworksExplosionsSectionEvent.class);
        }
        return true;
    }

    @Override
    protected @Nullable TriggerItem walk(Event event) {
        Object localVars = Variables.copyLocalVariables(event);
        Fireworks.Builder builder = Fireworks.fireworks();

        if (this.flightDuration != null) {
            Number num = this.flightDuration.getOptionalSingle(event).orElse(1);
            int duration = Math2.fit(0, num.intValue(), 255);
            builder.flightDuration(duration);
        }

        if (this.explosionsSection != null) {
            FireworksExplosionsSectionEvent explosionsSectionEvent = new FireworksExplosionsSectionEvent(builder);
            Variables.setLocalVariables(explosionsSectionEvent, localVars);
            TriggerItem.walk(this.explosionsSection, explosionsSectionEvent);
            Variables.setLocalVariables(event, Variables.copyLocalVariables(explosionsSectionEvent));
            Variables.removeLocals(explosionsSectionEvent);
        }

        Fireworks fireworks = builder.build();
        ItemUtils.modifyItems(this.items.getArray(event), itemStack ->
            itemStack.setData(DataComponentTypes.FIREWORKS, fireworks));
        return super.walk(event, false);
    }

    @Override
    public String toString(Event e, boolean d) {
        return "apply fireworks component to " + this.items.toString(e, d);
    }

}
