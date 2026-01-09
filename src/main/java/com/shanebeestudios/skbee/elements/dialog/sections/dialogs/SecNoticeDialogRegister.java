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
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;
import org.skriptlang.skript.lang.entry.EntryValidator.EntryValidatorBuilder;
import org.skriptlang.skript.lang.entry.SectionEntryData;
import org.skriptlang.skript.lang.entry.util.ExpressionEntryData;

import java.util.List;
import java.util.Objects;

@SuppressWarnings("UnstableApiUsage")
@Name("Dialog - Notice Dialog")
@Description({"Open a dialog screen with a single action button in footer, specified by an action button.",
    "By default, the exit action (which returns the player back to gameplay) is the same as the action button.",
    "See [**Notice Dialog**](https://minecraft.wiki/w/Dialog#notice) on McWiki for further details.",
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
    "- `action` = Click action. Defaults to button with `gui.ok` label and no action or tooltip." +
        "This is a section where you'll use an [**aAction Format Section**](https://github.com/ShaneBeee/SkBee/wiki/Dialogs#action-format) to add your action[s]."})
@Examples("")
@Since("INSERT VERSION")
public class SecNoticeDialogRegister extends Section {

    private static final EntryValidatorBuilder VALIDATOR = EntryValidator.builder();

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

        // NOTICE DIALOG STUFF
        VALIDATOR.addEntryData(new SectionEntryData("action", null, true));

        Skript.registerSection(SecNoticeDialogRegister.class,
            "register [new] notice dialog with id %string/namespacedkey%",
            "open [new] notice dialog to %audiences%");
    }

    // GENERAL DIALOG
    private Expression<Audience> audiences;
    private Expression<?> title;
    private Expression<?> externalTitle;
    private Trigger bodies;
    private Trigger inputs;
    private Expression<Boolean> canCloseWithEscape;
    private Expression<String> afterAction;

    // NOTICE DIALOG
    private Trigger action;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> triggerItems) {
        this.audiences = (Expression<Audience>) exprs[0];
        EntryContainer container = VALIDATOR.build().validate(sectionNode);
        if (container == null) return false;

        // GENERAL DIALOG
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

        // MULTI ACTION
        SectionNode actionNode = (SectionNode) container.getOptional("action", false);
        if (actionNode != null) {
            this.action = loadCode(actionNode, "action", DialogRegisterEvent.class);
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
            if (this.action != null) {
                Trigger.walk(this.action, dialogEvent);
            }
            if (this.bodies != null) {
                Trigger.walk(this.bodies, dialogEvent);
            }
            if (this.inputs != null) {
                Trigger.walk(this.inputs, dialogEvent);
            }
        });
        dialogBaseBuilder.body(dialogEvent.getBodies());
        dialogBaseBuilder.inputs(dialogEvent.getInputs());

        DialogBase.DialogAfterAction afterAction = this.afterAction == null ? DialogBase.DialogAfterAction.CLOSE : switch (Objects.requireNonNull(this.afterAction.getSingle(event))) {
            case "none" -> DialogBase.DialogAfterAction.NONE;
            case "wait_for_response" -> DialogBase.DialogAfterAction.WAIT_FOR_RESPONSE;
            default -> DialogBase.DialogAfterAction.CLOSE;
        };
        dialogBaseBuilder.afterAction(afterAction);

        List<ActionButton> actions = dialogEvent.getActions();
        ActionButton actionButton = actions.isEmpty() ? defaultOkActionButton() : actions.getFirst();
        Dialog dialog = Dialog.create(builder -> builder.empty()
            .base(dialogBaseBuilder.build())
            .type(DialogType.notice(actionButton)));


        for (Audience audience : this.audiences.getArray(event)) {
            audience.showDialog(dialog);
        }

        return next;
    }

    @Override
    public String toString(@Nullable Event e, boolean d) {
        return "open notice dialog to " + this.audiences.toString(e, d);
    }

    private static ActionButton defaultOkActionButton() {
        return ActionButton.create(Component.translatable("gui.ok"), null, 150, null);
    }

}
