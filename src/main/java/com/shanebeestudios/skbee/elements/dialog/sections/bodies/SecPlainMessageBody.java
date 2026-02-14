package com.shanebeestudios.skbee.elements.dialog.sections.bodies;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.lang.util.SimpleLiteral;
import ch.njol.skript.registrations.Classes;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.skript.base.Section;
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import com.shanebeestudios.skbee.api.event.dialog.DialogRegisterEvent;
import com.shanebeestudios.skbee.api.event.dialog.PlainMessageEvent;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.body.PlainMessageDialogBody;
import net.kyori.adventure.text.Component;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;
import org.skriptlang.skript.lang.entry.util.ExpressionEntryData;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class SecPlainMessageBody extends Section {

    private static final EntryValidator.EntryValidatorBuilder VALIDATOR = EntryValidator.builder();

    static {
        @SuppressWarnings("unchecked")
        Class<Object>[] compClasses = new Class[]{String.class, ComponentWrapper.class};
        VALIDATOR.addEntryData(new ExpressionEntryData<>("contents", null, false, compClasses));
        VALIDATOR.addEntryData(new ExpressionEntryData<>("width", new SimpleLiteral<>(200, true), true, Integer.class));
    }

    public static void register(Registration reg) {
        reg.newSection(SecPlainMessageBody.class, "add (text|plain message) body")
            .name("Dialog - Plain Message Body")
            .description("A multiline label for a dialog.",
                "See [**Plain Message**](https://minecraft.wiki/w/Dialog#plain_message) on McWiki for further details.",
                "**Entries**:",
                "- `contents` = A string/text component that will be seen in a dialog.",
                "- `width` = Integer value between 1 and 1024 — Maximum width of message. Defaults to 200. [Optional]")
            .examples("add plain message body:",
                "\tcontents: mini message from \"Do you want to break this block?\"",
                "\twidth: 150")
            .since("3.16.0")
            .register();
    }

    private Expression<?> contents;
    private Expression<Integer> width;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> triggerItems) {
        if (!getParser().isCurrentEvent(DialogRegisterEvent.class, PlainMessageEvent.class)) {
            Skript.error("Plain message body can only be applied in a 'body' section or 'description' section of an item body.");
            return false;
        }
        EntryContainer container = VALIDATOR.build().validate(sectionNode);
        if (container == null) return false;

        this.contents = (Expression<?>) container.getOptional("contents", false);
        this.width = (Expression<Integer>) container.getOptional("width", true);
        return true;
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    protected @Nullable TriggerItem walk(Event event) {
        TriggerItem next = getNext();

        Component contents;
        Object contentsSingle = this.contents.getSingle(event);
        if (contentsSingle instanceof ComponentWrapper cw) {
            contents = cw.getComponent();
        } else if (contentsSingle instanceof String string) {
            contents = ComponentWrapper.fromText(string).getComponent();
        } else {
            error("Unknown contents object: " + Classes.toString(contentsSingle));
            return next;
        }
        int width = this.width.getSingle(event);

        PlainMessageDialogBody plainMessage = DialogBody.plainMessage(contents, width);

        if (event instanceof DialogRegisterEvent dialogRegisterEvent) {
            dialogRegisterEvent.addBody(plainMessage);
        } else if (event instanceof PlainMessageEvent plainMessageEvent) {
            plainMessageEvent.setPlainMessage(plainMessage);
        }

        return next;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "add plain message body";
    }

}
