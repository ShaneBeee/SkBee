package com.shanebeestudios.skbee.elements.itemcomponent.sections;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.skript.base.Section;
import com.shanebeestudios.skbee.api.util.ItemUtils;
import com.shanebeestudios.skbee.api.util.SimpleEntryValidator;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.MusicInstrument;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;

import java.util.List;

@Name("ItemComponent - Instrument Component Apply")
@Description({"Apply an instrument component to an item that supports it (such as a goat horn).",
    "It may seem silly that this is a section with only 1 value, " +
        "but I'm hoping that Paper updates the API to support all the features Minecraft has for this.",
    "Requires Paper 1.21.3+",
    "See [**Instrument Component**](https://minecraft.wiki/w/Data_component_format#instrument) on McWiki for more info.",
    "",
    "**Entries**:",
    "- `instrument` = The instrument to be played."})
@Examples({"set {_i} to 1 of goat horn",
    "apply instrument to {_i}:",
    "\tinstrument: admire_goat_horn"})
@Since("3.8.0")
public class SecInstrumentComponent extends Section {

    private static final EntryValidator VALIDATOR;

    static {
        VALIDATOR = SimpleEntryValidator.builder()
            .addRequiredEntry("instrument", MusicInstrument.class)
            .build();
        Skript.registerSection(SecInstrumentComponent.class,
            "apply instrument [component] to %itemstacks/itemtypes/slots%");
    }

    private Expression<?> items;
    private Expression<MusicInstrument> instrument;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> triggerItems) {
        this.items = exprs[0];
        EntryContainer container = VALIDATOR.validate(sectionNode);
        if (container == null) return false;

        this.instrument = (Expression<MusicInstrument>) container.getOptional("instrument", false);
        return true;
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    protected @Nullable TriggerItem walk(Event event) {
        MusicInstrument instrument = this.instrument.getSingle(event);
        if (instrument == null) {
            error("No instrument found");
            return super.walk(event, false);
        }
        ItemUtils.modifyItems(this.items.getArray(event), itemStack ->
            itemStack.setData(DataComponentTypes.INSTRUMENT, instrument));
        return super.walk(event, false);
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "apply instrument component to " + this.items.toString(event, debug);
    }

}
