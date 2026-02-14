package com.shanebeestudios.skbee.elements.dialog.sections.dialogs;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.skript.base.Section;
import com.shanebeestudios.skbee.api.util.Util;
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import com.shanebeestudios.skbee.api.event.dialog.DialogRegisterEvent;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import io.papermc.paper.registry.set.RegistrySet;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;
import org.skriptlang.skript.lang.entry.SectionEntryData;
import org.skriptlang.skript.lang.entry.util.ExpressionEntryData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings({"UnstableApiUsage"})
public class SecDialogListDialogRegister extends Section {

    private static final EntryValidator VALIDATOR;

    static {
        EntryValidator.EntryValidatorBuilder builder = EntryValidator.builder();
        // GENERAL DIALOG
        @SuppressWarnings("unchecked")
        Class<Object>[] compClasses = new Class[]{String.class, ComponentWrapper.class};
        builder.addEntryData(new ExpressionEntryData<>("title", null, false, compClasses));
        builder.addEntryData(new ExpressionEntryData<>("external_title", null, true, compClasses));
        builder.addEntryData(new SectionEntryData("body", null, true));
        builder.addEntryData(new SectionEntryData("inputs", null, true));
        builder.addEntryData(new ExpressionEntryData<>("can_close_with_escape", null, true, Boolean.class));
        builder.addEntryData(new ExpressionEntryData<>("after_action", null, true, String.class));

        // DIALOG LIST DIALOG
        builder.addEntryData(new ExpressionEntryData<>("dialogs", null, false, String.class));
        builder.addEntryData(new SectionEntryData("exit_action", null, true));
        builder.addEntryData(new ExpressionEntryData<>("columns", null, true, Integer.class));
        builder.addEntryData(new ExpressionEntryData<>("button_width", null, true, Integer.class));
        VALIDATOR = builder.build();
    }

    public static void register(Registration reg) {
        reg.newSection(SecDialogListDialogRegister.class, VALIDATOR, "open [new] dialog list dialog to %audiences%")
            .name("Dialog - Dialog List Dialog")
            .description("A dialog screen with scrollable list of buttons leading directly to other dialogs, arranged in columns.",
                "Titles of those buttons will be taken from external_title fields of targeted dialogs.",
                "If `exit_action` is present, a button for it will appear in the footer, otherwise the footer is not present.",
                "`exit_action` is also used for the Escape action.",
                "See [**Dialog List Dialog**](https://minecraft.wiki/w/Dialog#dialog_list) on McWiki for further details.",
                "See [**snippets**](https://github.com/ShaneBeee/SkriptSnippets/tree/master/snippets/dialog) for comprehensive examples.",
                "",
                "You can either register a dialog in the `registry registration` structure, and open it later or you can create/open a dialog on the fly.",
                "**Register**: Register a dialog with an `id` (The id that represents this dialog, accepts a string or NamespacedKey).",
                "**Open**: Create a dialog and directly open it to a player without registration.",
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
                "- `dialogs` = A list of strings of previously defined dialogs. " +
                    "Will accept the ID of the other dialogs, whether registered by you or from datapacks.",
                "- `exit_action` = Action for leaving the dialog. Same as action sections but will only accept one action. [Optional]",
                "- `columns` = Positive integer describing number of columns. Defaults to 2. [Optional]",
                "- `button_width` = Integer value between 1 and 1024 — Width of the button. Defaults to 150.")
            .examples("")
            .since("3.16.0")
            .register();
    }

    // GENERAL DIALOG
    private Expression<Audience> audiences;
    private Expression<?> title;
    private Expression<?> externalTitle;
    private Trigger bodies;
    private Trigger inputs;
    private Expression<Boolean> canCloseWithEscape;
    private Expression<String> afterAction;

    // DIALOG LIST DIALOG
    private Expression<String> dialogs;
    private Trigger exit_action;
    private Expression<Integer> columns;
    private Expression<Integer> buttonWidth;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> triggerItems) {
        this.audiences = (Expression<Audience>) exprs[0];
        EntryContainer container = VALIDATOR.validate(sectionNode);
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

        // DIALOG LIST DIALOG
        this.dialogs = (Expression<String>) container.getOptional("dialogs", false);
        SectionNode exitActionNode = (SectionNode) container.getOptional("exit_action", false);
        if (exitActionNode != null) {
            this.exit_action = loadCode(exitActionNode, "exit_action", DialogRegisterEvent.class);
        }
        this.columns = (Expression<Integer>) container.getOptional("columns", false);
        this.buttonWidth = (Expression<Integer>) container.getOptional("button_width", false);

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
            if (this.bodies != null) {
                Trigger.walk(this.bodies, dialogEvent);
            }
            if (this.inputs != null) {
                Trigger.walk(this.inputs, dialogEvent);
            }
            if (this.exit_action != null) {
                Trigger.walk(this.exit_action, dialogEvent);
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

        // DialogList
        int columns;
        if (this.columns != null) {
            Integer columnsIntvalue = this.columns.getSingle(event);
            columns = Objects.requireNonNullElse(columnsIntvalue, 2);
        } else {
            columns = 2;
        }
        int buttonWidth;
        if (this.buttonWidth != null) {
            Integer buttonWidthIntvalue = this.buttonWidth.getSingle(event);
            buttonWidth = Objects.requireNonNullElse(buttonWidthIntvalue, 150);
        } else {
            buttonWidth = 150;
        }

        List<TypedKey<Dialog>> dialogsList = new ArrayList<>();
        for (String s : this.dialogs.getArray(event)) {
            NamespacedKey namespacedKey = Util.getNamespacedKey(s, false);
            if (namespacedKey == null) continue;

            TypedKey<Dialog> dialogTypedKey = TypedKey.create(RegistryKey.DIALOG, namespacedKey);
            dialogsList.add(dialogTypedKey);
        }

        Dialog dialog = Dialog.create(builder -> builder.empty()
            .base(dialogBaseBuilder.build())
            .type(DialogType.dialogList(
                RegistrySet.keySet(RegistryKey.DIALOG, dialogsList),
                dialogEvent.getExitActionButton(),
                columns,
                buttonWidth)));

        for (Audience audience : this.audiences.getArray(event)) {
            audience.showDialog(dialog);
        }

        return next;
    }

    @Override
    public String toString(@Nullable Event e, boolean d) {
        return "open dialog list dialog";
    }
}
