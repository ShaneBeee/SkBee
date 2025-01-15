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
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registry.KeyUtils;
import com.shanebeestudios.skbee.api.util.ItemUtils;
import com.shanebeestudios.skbee.api.util.SimpleEntryValidator;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.UseCooldown;
import net.kyori.adventure.key.Key;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;

import java.util.List;

@Name("ItemComponent - Use Cooldown")
@Description({"Apply a cooldown to all items of the same type when it has been used.",
    "Requires Paper 1.21.3+",
    "See [**Use Cooldown Component**](https://minecraft.wiki/w/Data_component_format#use_cooldown) on McWiki for more details.",
    "",
    "**Entries**:",
    "- `seconds` = The cooldown duration (timespan).",
    "- `group` = The unique key to identify this cooldown group. " +
        "If present, the item is included in a cooldown group and no longer shares cooldowns with its base item type, " +
        "but instead with any other items that are part of the same cooldown group. [Optional]"})
@Examples({"apply use cooldown to {_item}:",
    "\tseconds: 5 seconds",
    "\tgroup: \"blah:special_apple\""})
@Since("INSERT VERSION")
@SuppressWarnings("UnstableApiUsage")
public class SecUseCooldownComponent extends Section {

    private static final EntryValidator VALIDATOR;

    static {
        VALIDATOR = SimpleEntryValidator.builder()
            .addRequiredEntry("seconds", Timespan.class)
            .addOptionalEntry("group", String.class)
            .build();
        Skript.registerSection(SecUseCooldownComponent.class, "apply use cooldown [component] to %itemstacks/itemtypes/slots%");
    }

    private Expression<Object> items;
    private Expression<Timespan> seconds;
    private Expression<String> group;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> triggerItems) {
        EntryContainer container = VALIDATOR.validate(sectionNode);
        if (container == null) return false;

        this.items = (Expression<Object>) exprs[0];
        this.seconds = (Expression<Timespan>) container.getOptional("seconds", false);
        this.group = (Expression<String>) container.getOptional("group", false);
        return true;
    }

    @Override
    protected @Nullable TriggerItem walk(Event event) {
        float seconds = 0;
        if (this.seconds != null) {
            Timespan timespan = this.seconds.getSingle(event);
            if (timespan != null) seconds = (float) timespan.getAs(Timespan.TimePeriod.MILLISECOND) / 1000;
        }

        UseCooldown.Builder builder = UseCooldown.useCooldown(seconds);

        if (this.group != null) {
            Key key = KeyUtils.getKey(this.group.getSingle(event));
            if (group != null) builder.cooldownGroup(key);
        }

        UseCooldown useCooldown = builder.build();

        ItemUtils.modifyItems(this.items.getArray(event), itemStack -> {
            itemStack.setData(DataComponentTypes.USE_COOLDOWN, useCooldown);
        });
        return super.walk(event, false);
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        return "apply use cooldown component to " + this.items.toString(e, d);
    }

}
