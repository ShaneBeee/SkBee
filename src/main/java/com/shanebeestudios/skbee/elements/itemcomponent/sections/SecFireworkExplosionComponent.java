package com.shanebeestudios.skbee.elements.itemcomponent.sections;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.Section;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.util.Color;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.skript.Experiments;
import com.shanebeestudios.skbee.api.util.ItemUtils;
import com.shanebeestudios.skbee.api.util.SimpleEntryValidator;
import com.shanebeestudios.skbee.elements.itemcomponent.sections.SecFireworksComponent.FireworksExplosionsSectionEvent;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.FireworkEffect;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;

import java.util.ArrayList;
import java.util.List;

@Name("ItemComponent - Firework Explosion Component Apply")
@Description({"Apply a firework explosion effect to a firework star.",
    "This can also be used within the Fireworks Component's `explosions:` section.",
    "Requires Paper 1.21.3+ and `item_component` feature.",
    "See [**Firework Explosion Component**](https://minecraft.wiki/w/Data_component_format#firework_explosion) on McWiki for more info.",
    "",
    "**Entries**:",
    "- `shape` = The [firework type](https://docs.skriptlang.org/classes.html#FireworkType) of the explosion.",
    "- `colors` = The colors of the initial particles of the explosion, randomly selected from.",
    "- `fade_colors` = The colors of the fading particles of the explosion, randomly selected from.",
    "- `has_trail` = Whether or not the explosion has a trail effect (diamond).",
    "- `has_twinkle` = Whether or not the explosion has a twinkle effect (glowstone dust)."})
@Examples({
    "# Apply to item",
    "apply firework explosion to {_i}:",
    "\tshape: small ball",
    "\tcolors: red, yellow and white",
    "\tfade_colors: blue, green and red",
    "\thas_trail: true",
    "\thas_twinkle: true",
    "",
    "# Apply as explosion in fireworks",
    "apply fireworks to {_i}:",
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
public class SecFireworkExplosionComponent extends Section {

    private static final EntryValidator VALIDATOR;

    static {
        VALIDATOR = SimpleEntryValidator.builder()
            .addOptionalEntry("shape", FireworkEffect.Type.class)
            .addOptionalEntry("colors", Color.class)
            .addOptionalEntry("fade_colors", Color.class)
            .addOptionalEntry("has_trail", Boolean.class)
            .addOptionalEntry("has_twinkle", Boolean.class)
            .build();

        Skript.registerSection(SecFireworkExplosionComponent.class,
            "apply firework explosion [component] [to %-itemstacks/itemtypes/slots%]");
    }

    private Expression<?> items;
    private Expression<FireworkEffect.Type> shape;
    private Expression<Color> colors;
    private Expression<Color> fadeColors;
    private Expression<Boolean> hasTrail;
    private Expression<Boolean> hasTwinkle;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> triggerItems) {
        if (!getParser().hasExperiment(Experiments.ITEM_COMPONENT)) {
            Skript.error("requires '" + Experiments.ITEM_COMPONENT.codeName() + "' feature.");
            return false;
        }
        EntryContainer validate = VALIDATOR.validate(sectionNode);
        if (validate == null) {
            return false;
        }
        this.items = exprs[0];
        if (this.items == null && !getParser().isCurrentEvent(FireworksExplosionsSectionEvent.class)) {
            Skript.error("This section needs to be either applied to items or used in a Fireworks Component's 'explosions:' section");
            return false;
        }
        this.shape = (Expression<FireworkEffect.Type>) validate.getOptional("shape", false);
        this.colors = (Expression<Color>) validate.getOptional("colors", false);
        this.fadeColors = (Expression<Color>) validate.getOptional("fade_colors", false);
        this.hasTrail = (Expression<Boolean>) validate.getOptional("has_trail", false);
        this.hasTwinkle = (Expression<Boolean>) validate.getOptional("has_twinkle", false);
        return true;
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    protected @Nullable TriggerItem walk(Event event) {
        FireworkEffect.Builder builder = FireworkEffect.builder();

        if (this.shape != null) {
            FireworkEffect.Type type = this.shape.getOptionalSingle(event).orElse(FireworkEffect.Type.BALL);
            builder.with(type);
        }
        if (this.colors != null) {
            List<org.bukkit.Color> colors = new ArrayList<>();
            for (Color color : this.colors.getArray(event)) {
                colors.add(color.asBukkitColor());
            }
            builder.withColor(colors);
        }
        if (this.fadeColors != null) {
            List<org.bukkit.Color> colors = new ArrayList<>();
            for (Color color : this.fadeColors.getArray(event)) {
                colors.add(color.asBukkitColor());
            }
            builder.withFade(colors);
        }
        if (this.hasTrail != null) {
            this.hasTrail.getOptionalSingle(event).ifPresent(builder::trail);
        }
        if (this.hasTwinkle != null) {
            this.hasTwinkle.getOptionalSingle(event).ifPresent(builder::flicker);
        }

        FireworkEffect fireworkEffect = builder.build();
        if (this.items != null) {
            ItemUtils.modifyItems(this.items.getArray(event), itemStack ->
                itemStack.setData(DataComponentTypes.FIREWORK_EXPLOSION, fireworkEffect));
        } else if (event instanceof FireworksExplosionsSectionEvent section) {
            section.getBuilder().addEffect(fireworkEffect);
        }
        return super.walk(event, false);
    }

    @Override
    public String toString(Event e, boolean d) {
        String items = this.items != null ? " to " + this.items.toString(e, d) : " in 'explosions' section";
        return "apply firework effects component" + items;
    }

}
