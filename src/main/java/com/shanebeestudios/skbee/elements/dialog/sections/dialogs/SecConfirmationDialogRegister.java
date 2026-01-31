package com.shanebeestudios.skbee.elements.dialog.sections.dialogs;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.skript.base.Section;
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import com.shanebeestudios.skbee.api.event.dialog.DialogRegisterEvent;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.DialogBase.DialogAfterAction;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;
import org.skriptlang.skript.lang.entry.SectionEntryData;
import org.skriptlang.skript.lang.entry.util.ExpressionEntryData;

import java.util.List;
import java.util.Objects;

@SuppressWarnings("UnstableApiUsage")
@Name("Dialog - Confirmation Dialog")
@Description({"Open a dialog screen with two action buttons in footer, specified by 'yes' and 'no' actions.",
    "By default, the exit action is 'no' button.",
    "See [**Confirmation Dialog**](https://minecraft.wiki/w/Dialog#confirmation) on McWiki for further details.",
    "See [**snippets**](https://github.com/ShaneBeee/SkriptSnippets/tree/master/snippets/dialog) for comprehensive examples.",
    "",
    "**Entries**:",
    "- `title` = Screen title, appearing at the top of the dialog, accepts a string/text component.",
    "- `external_title` = Name to be used for a button leading to this dialog (for example, on the pause menu), accepts a string.text component. " +
        "If not present, `title` will be used instead. [Optional]",
    "- `body` = Optional section for body elements or a single body element. " +
        "See [**Body Format on SkBee wiki**](https://github.com/ShaneBeee/SkBee/wiki/Dialogs#body-format) " +
        "and [**Body Format on McWiki**](https://minecraft.wiki/w/Dialog#Body_format) for further info.",
    "- `inputs` = Optional section for input controls. " +
        "See [**Input Control on SkBee wiki**](https://github.com/ShaneBeee/SkBee/wiki/Dialogs#input-control)" +
        "and [**Input Control on McWiki**](https://minecraft.wiki/w/Dialog#Input_control_format) for further info.",
    "- `can_close_with_escape` = Can dialog be dismissed with Escape key. Defaults to true. [Optional]",
    "- `after_action` = An additional operation performed on the dialog after click or submit actions (accepts a string)." +
        "Options are \"close\", \"none\" and \"wait_for_response\"." +
        "See [**Common Entries on SkBee wiki**](https://github.com/ShaneBeee/SkBee/wiki/Dialogs#common-entries) for further info.",
    "- `actions` = Section for action buttons." +
        "See [**Action Format on SkBee wiki**](https://github.com/ShaneBeee/SkBee/wiki/Dialogs#action-format)" +
        "and [**Action Format on McWiki**](https://minecraft.wiki/w/Dialog#Action_format) for further info."})
@Examples("")
@Since("3.16.0")
public class SecConfirmationDialogRegister extends Section {

    private static final EntryValidator.EntryValidatorBuilder VALIDATOR = EntryValidator.builder();

    static {
        // GENERAL DIALOG
        @SuppressWarnings("unchecked")
        Class<Object>[] compClasses = new Class[]{String.class, ComponentWrapper.class};
        VALIDATOR.addEntryData(new ExpressionEntryData<>("title", null, false, compClasses));
        VALIDATOR.addEntryData(new ExpressionEntryData<>("external_title", null, true, compClasses));
        VALIDATOR.addEntryData(new SectionEntryData("body", null, true));
        VALIDATOR.addEntryData(new SectionEntryData("inputs", null, true));
        VALIDATOR.addEntryData(new ExpressionEntryData<>("can_close_with_escape", null, true, Boolean.class));
        VALIDATOR.addEntryData(new ExpressionEntryData<>("after_action", null, true, String.class));

        // CONFIRMATION DIALOG
        VALIDATOR.addEntryData(new SectionEntryData("actions", null, false));

        Skript.registerSection(SecConfirmationDialogRegister.class,
            "open [new] confirmation dialog to %audiences%");
    }

    // GENERAL DIALOG
    private Expression<Audience> audiences;
    private Expression<?> title;
    private Expression<?> externalTitle;
    private Trigger bodies;
    private Trigger inputs;
    private Expression<Boolean> canCloseWithEscape;
    private Expression<String> afterAction;

    // CONFIRMATION DIALOG
    private Trigger actions;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> triggerItems) {
        this.audiences = (Expression<Audience>) exprs[0];
        EntryContainer container = VALIDATOR.build().validate(sectionNode);
        if (container == null) return false;

        this.title = (Expression<?>) container.getOptional("title", false);
        this.externalTitle = (Expression<?>) container.getOptional("external_title", false);
        SectionNode bodiesNode = (SectionNode) container.getOptional("body", false);
        if (bodiesNode != null) {
            this.bodies = loadCode(bodiesNode, "bodies", DialogRegisterEvent.class);
        }
        SectionNode inputsNode = (SectionNode) container.getOptional("inputs", false);
        if (inputsNode != null) {
            this.inputs = loadCode(inputsNode, "inputs", DialogRegisterEvent.class);
        }
        this.canCloseWithEscape = (Expression<Boolean>) container.getOptional("can_close_with_escape", false);
        this.afterAction = (Expression<String>) container.getOptional("after_action", false);

        // CONFIRMATION DIALOG
        SectionNode actionsNode = (SectionNode) container.getOptional("actions", false);
        if (actionsNode != null) {
            this.actions = loadCode(actionsNode, "actions", DialogRegisterEvent.class);
        }

        return true;
    }

    @Override
    protected @Nullable TriggerItem walk(Event event) {
        TriggerItem next = getNext();

        // COMMON
        Component title;
        if (this.title == null) {
            error("Missing Title");
            return next;
        } else {
            Object titleSingle = this.title.getSingle(event);
            if (titleSingle instanceof ComponentWrapper cw) {
                title = cw.getComponent();
            } else if (titleSingle instanceof String string) {
                title = ComponentWrapper.fromText(string).getComponent();
            } else {
                error("Title is invalid, no dialog created: " + this.title.toString(event, true));
                return next;
            }
            if (title == null) {
                error("Title is invalid, no dialog created: " + this.title.toString(event, true));
                return next;
            }
        }

        DialogBase.Builder dialogBaseBuilder = DialogBase.builder(title);

        if (this.externalTitle != null) {
            Object single = this.externalTitle.getSingle(event);
            if (single instanceof ComponentWrapper cw) {
                dialogBaseBuilder.externalTitle(cw.getComponent());
            } else if (single instanceof String s) {
                dialogBaseBuilder.externalTitle(ComponentWrapper.fromText(s).getComponent());
            }
        }

        if (this.canCloseWithEscape != null) {
            boolean canCloseWithEscape = Boolean.TRUE.equals(this.canCloseWithEscape.getSingle(event));
            dialogBaseBuilder.canCloseWithEscape(canCloseWithEscape);
        }

        // Sections
        DialogRegisterEvent dialogEvent = new DialogRegisterEvent();
        Variables.withLocalVariables(event, dialogEvent, () -> {
            Trigger.walk(this.actions, dialogEvent);
            if (this.bodies != null) {
                Trigger.walk(this.bodies, dialogEvent);
            }
            if (this.inputs != null) {
                Trigger.walk(this.inputs, dialogEvent);
            }
        });
        dialogBaseBuilder.body(dialogEvent.getBodies());
        dialogBaseBuilder.inputs(dialogEvent.getInputs());


        DialogAfterAction afterAction = this.afterAction == null ? DialogAfterAction.CLOSE : switch (Objects.requireNonNull(this.afterAction.getSingle(event))) {
            case "none" -> DialogAfterAction.NONE;
            case "wait_for_response" -> DialogAfterAction.WAIT_FOR_RESPONSE;
            default -> DialogAfterAction.CLOSE;
        };
        dialogBaseBuilder.afterAction(afterAction);

        // CONFIRMATION DIALOG
        List<ActionButton> clickActions = dialogEvent.getActions();
        if (clickActions.size() != 2) {
            error("Two actions are required for yes and no!");
            return next;
        }

        Dialog dialog = Dialog.create(builder -> builder.empty()
            .base(dialogBaseBuilder.build())
            .type(DialogType.confirmation(clickActions.getFirst(), clickActions.get(1))));

        for (Audience audience : this.audiences.getArray(event)) {
            audience.showDialog(dialog);
        }

        return next;
    }

    @Override
    public String toString(@Nullable Event e, boolean d) {
        return "open confirmation dialog to " + this.audiences.toString(e, d);
    }

}
