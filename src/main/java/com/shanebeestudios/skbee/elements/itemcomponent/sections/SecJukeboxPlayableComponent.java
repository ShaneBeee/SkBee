package com.shanebeestudios.skbee.elements.itemcomponent.sections;

import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.skript.base.Section;
import com.shanebeestudios.skbee.api.util.ItemUtils;
import com.shanebeestudios.skbee.api.util.SimpleEntryValidator;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.JukeboxPlayable;
import org.bukkit.JukeboxSong;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class SecJukeboxPlayableComponent extends Section {

    private static EntryValidator VALIDATOR;

    public static void register(Registration reg) {
        VALIDATOR = SimpleEntryValidator.builder()
            .addRequiredEntry("song", JukeboxSong.class)
            .build();
        reg.newSection(SecJukeboxPlayableComponent.class, VALIDATOR,
                "apply jukebox playable [component] to %itemstacks/itemtypes/slots%")
            .name("ItemComponent - JukeboxPlayable Component Apply")
            .description("Apply an jukebox playable component to an item.",
                "When applied, the item can be inserted into a jukebox and plays the specified song.",
                "Requires Paper 1.21.3+",
                "See [**JukeboxPlayable Component**](https://minecraft.wiki/w/Data_component_format#jukebox_playable) on McWiki for more info.",
                "",
                "**Entries**:",
                "- `song` = A jukebox song to be played.")
            .since("3.8.0")
            .register();
    }

    private Expression<?> items;
    private Expression<JukeboxSong> jukeboxSong;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> triggerItems) {
        this.items = exprs[0];
        EntryContainer container = VALIDATOR.validate(sectionNode);
        if (container == null) return false;

        this.jukeboxSong = (Expression<JukeboxSong>) container.getOptional("song", false);
        return true;
    }

    @Override
    protected @Nullable TriggerItem walk(Event event) {
        JukeboxSong jukeboxSong = this.jukeboxSong.getSingle(event);
        if (jukeboxSong == null) {
            error("No jukebox song found");
            return super.walk(event, false);
        }
        JukeboxPlayable jukeboxPlayable = JukeboxPlayable.jukeboxPlayable(jukeboxSong).build();
        ItemUtils.modifyItems(this.items.getArray(event), itemStack ->
            itemStack.setData(DataComponentTypes.JUKEBOX_PLAYABLE, jukeboxPlayable));
        return super.walk(event, false);
    }

    @Override
    public String toString(@Nullable Event e, boolean d) {
        return "apply jukebox playable component to " + this.items.toString(e, d);
    }

}
