package com.shanebeestudios.skbee.elements.registry.expression;

import ch.njol.skript.config.SectionNode;
import ch.njol.skript.expressions.base.SectionExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import com.github.shanebeee.skr.Registration;
import com.github.shanebeee.skr.Utils;
import com.github.shanebeee.skr.skript.SimpleEntryValidator;
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.data.InstrumentRegistryEntry;
import net.kyori.adventure.text.Component;
import org.bukkit.MusicInstrument;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class SecExprMusicalInstrument extends SectionExpression<MusicInstrument> {

    private static EntryValidator VALIDATOR;

    @SuppressWarnings("unchecked")
    public static void register(Registration reg) {
        VALIDATOR = SimpleEntryValidator.builder()
            .addOptionalEntry("description", new Class[]{String.class, ComponentWrapper.class, Component.class})
            .addRequiredEntry("sound_event", new Class[]{String.class, TypedKey.class, NamespacedKey.class})
            .addOptionalEntry("use_duration", Timespan.class)
            .addOptionalEntry("range", Number.class)
            .build();

        reg.newSimpleExpression(SecExprMusicalInstrument.class, MusicInstrument.class,
                "new [music] instrument")
            .validator(VALIDATOR)
            .name("Registry - Instrument")
            .description("Create a new instrument with specified properties.",
                "This can be used in the [**Instrument Component**](https://minecraft.wiki/w/Data_component_format#instrument).",
                "See [**Instrument Definition**](https://minecraft.wiki/w/Instrument_definition) on McWiki for more information.")
            .examples("set {_m} to new instrument:",
                "\tdescription: formatted \"<red>Toot Toot\"",
                "\tsound_event: \"minecraft:entity.ender_dragon.death\"",
                "\tuse_duration: 1 second",
                "\trange: 10",
                "",
                "set {_i} to 1 of goat horn",
                "apply instrument component to {_i}:",
                "\tinstrument: {_m}")
            .since("INSERT VERSION")
            .register();
    }

    private Expression<?> description;
    private Expression<?> soundEvent;
    private Expression<Timespan> useDuration;
    private Expression<Number> range;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int pattern, Kleenean delayed, ParseResult result,
                        @Nullable SectionNode node, @Nullable List<TriggerItem> triggerItems) {
        EntryContainer container = VALIDATOR.validate(node);
        if (container == null) return false;

        this.description = (Expression<?>) container.getOptional("description", false);
        this.soundEvent = (Expression<?>) container.getOptional("sound_event", false);
        this.useDuration = (Expression<Timespan>) container.getOptional("use_duration", false);
        this.range = (Expression<Number>) container.getOptional("range", false);
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected MusicInstrument @Nullable [] get(Event event) {
        TypedKey<Sound> soundEvent;
        Object o = this.soundEvent.getSingle(event);
        switch (o) {
            case String s -> {
                NamespacedKey namespacedKey = Utils.getNamespacedKey(s, false);
                if (namespacedKey != null) {
                    soundEvent = TypedKey.create(RegistryKey.SOUND_EVENT, namespacedKey);
                } else {
                    soundEvent = null;
                }
            }
            case NamespacedKey namespacedKey -> soundEvent = TypedKey.create(RegistryKey.SOUND_EVENT, namespacedKey);
            case TypedKey<?> typedKey when typedKey.registryKey() == RegistryKey.SOUND_EVENT ->
                soundEvent = (TypedKey<Sound>) typedKey;
            case null, default -> {
                return null;
            }
        }
        if (soundEvent == null) {
            return null;
        }

        MusicInstrument musicInstrument = MusicInstrument.create(factory -> {
            Component description = Component.empty();
            if (this.description != null) {
                Object object = this.description.getSingle(event);
                if (object instanceof String string) {
                    description = Component.text(string);
                } else if (object instanceof Component component) {
                    description = component;
                } else if (object instanceof ComponentWrapper cw) {
                    description = cw.getComponent();
                }
            }

            InstrumentRegistryEntry.Builder empty = factory.empty();
            if (description != null) {
                empty.description(description);
            }

            empty.soundEvent(soundEvent);

            float range = 256;
            if (this.range != null) {
                Number single = this.range.getSingle(event);
                if (single != null) {
                    range = single.floatValue();
                }
            }
            empty.range(Math.max(0, range));

            float duration = 7;
            if (this.useDuration != null) {
                Timespan timespan = this.useDuration.getSingle(event);
                if (timespan != null) {
                    duration = (float) timespan.getAs(Timespan.TimePeriod.TICK) / 20;
                }
            }
            empty.duration(Math.max(0, duration));
        });

        return new MusicInstrument[]{musicInstrument};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends MusicInstrument> getReturnType() {
        return MusicInstrument.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "new music instrument";
    }

}
