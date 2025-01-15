package com.shanebeestudios.skbee.elements.testing.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.NoDoc;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.test.runner.SkriptTestEvent;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.skript.base.Section;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@NoDoc
public class SecTestSections extends Section {

    static {
        // See https://github.com/SkriptLang/Skript/issues/7434
        Skript.registerSection(SecTestSections.class,
            "before", "test", "after");
    }

    private String sectionName;
    private Trigger trigger;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> triggerItems) {
        this.sectionName = parseResult.expr;
        if (!getParser().isCurrentEvent(SkriptTestEvent.class)) {
            Skript.error("'" + this.sectionName + "' can only be used in a test event.");
            return false;
        }
        this.trigger = loadCode(sectionNode, this.sectionName, getParser().getCurrentEvents());
        return true;
    }

    @Override
    protected @Nullable TriggerItem walk(Event event) {
        TriggerItem.walk(this.trigger, event);
        return super.walk(event, false);
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return this.sectionName;
    }

}
