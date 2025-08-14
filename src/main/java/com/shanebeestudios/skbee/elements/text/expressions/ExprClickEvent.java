package com.shanebeestudios.skbee.elements.text.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.log.ErrorQuality;
import ch.njol.util.Kleenean;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.nbt.api.BinaryTagHolder;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.ClickEvent.Action;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Name("TextComponent - Click Event")
@Description({"Create a new click event to add to a text component.",
    "Supports run command, suggest command, open link, copy to clipboard, change book page, open dialog and custom payload.",
    "Open dialog and custom payload require Minecraft 1.21.6+"})
@Examples({"set {_t} to text component from \"Check out my cool website\"",
    "add hover event showing \"Clicky clicky to go to spawn!\" to {_t}",
    "add click event to open url \"https://my.cool.website\" to {_t}",
    "send component {_t} to player",
    "",
    "add click event to show dialog with key \"some:dialog\" to {_t}",
    "add click event to run custom payload with key \"some:key\" with custom data {_nbt} to {_t}"})
@Since("1.5.0")
public class ExprClickEvent extends SimpleExpression<ClickEvent> {

    private static final boolean SUPPORTS_CLIPBOARD;
    private static final boolean SUPPORTS_CUSTOM_PAYLOAD = Skript.methodExists(ClickEvent.class, "custom", Key.class, String.class);

    static {
        SUPPORTS_CLIPBOARD = Skript.fieldExists(Action.class, "COPY_TO_CLIPBOARD");
        Skript.registerExpression(ExprClickEvent.class, ClickEvent.class, ExpressionType.COMBINED,
            "[a] [new] click event to run command %string%",
            "[a] [new] click event to suggest command %string%",
            "[a] [new] click event to open (link|url) %string%",
            "[a] [new] click event to copy %string% to clipboard",
            "[a] [new] click event to change to page %number%",
            "[a] [new] click event to (open|show) dialog [with key] %string/namespacedkey%",
            "[a] [new] click event to run custom payload with key %string/namespacedkey% [and] with [custom] data %nbtcompound%");
    }

    private int pattern;
    private Expression<Object> object;
    private Expression<NBTCompound> nbtData;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.pattern = matchedPattern;
        if (pattern == 3 && !SUPPORTS_CLIPBOARD) {
            Skript.error("'click event to copy %string% to clipboard' is not supported on your server version", ErrorQuality.SEMANTIC_ERROR);
            return false;
        }
        this.object = (Expression<Object>) exprs[0];
        if (this.pattern > 4 && !SUPPORTS_CUSTOM_PAYLOAD) {
            Skript.error("'" + parseResult.expr + "' is not supported on your server version", ErrorQuality.SEMANTIC_ERROR);
            return false;
        }
        if (this.pattern == 6) {
            this.nbtData = (Expression<NBTCompound>) exprs[1];
        }
        return true;
    }

    @SuppressWarnings({"PatternValidation", "NullableProblems", "deprecation"})
    @Override
    protected ClickEvent @Nullable [] get(Event event) {
        if (this.object == null) return null;

        Object value = this.object.getSingle(event);
        if (value == null) return null;

        if (SUPPORTS_CUSTOM_PAYLOAD) {
            ClickEvent clickEvent = switch (this.pattern) {
                case 1 -> ClickEvent.suggestCommand((String) value);
                case 2 -> ClickEvent.openUrl((String) value);
                case 3 -> ClickEvent.copyToClipboard((String) value);
                case 4 -> ClickEvent.changePage(((Number) value).intValue());
                case 5 -> {
                    Key key;
                    if (value instanceof String string) key = Key.key(string);
                    else if (value instanceof NamespacedKey namespacedKey) key = namespacedKey.key();
                    else yield null;

                    Registry<Dialog> dialogRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.DIALOG);
                    if (dialogRegistry == null) yield null;

                    Dialog dialog = dialogRegistry.get(key);
                    if (dialog == null) yield null;

                    yield ClickEvent.showDialog(dialog);
                }
                case 6 -> {
                    Key key;
                    if (value instanceof String string) key = Key.key(string);
                    else if (value instanceof NamespacedKey namespacedKey) key = namespacedKey.key();
                    else yield null;

                    NBTCompound nbtData = this.nbtData.getSingle(event);
                    if (nbtData == null) yield null;

                    BinaryTagHolder nbt = BinaryTagHolder.binaryTagHolder(nbtData.toString());
                    yield ClickEvent.custom(key, nbt);
                }
                default -> ClickEvent.runCommand((String) value);
            };
            return new ClickEvent[]{clickEvent};
        } else {
            // TODO - OLD VERSION ... remove in the future
            Action action;
            switch (this.pattern) {
                case 1 -> action = Action.SUGGEST_COMMAND;
                case 2 -> action = Action.OPEN_URL;
                case 3 -> action = Action.COPY_TO_CLIPBOARD;
                case 4 -> {
                    action = Action.CHANGE_PAGE;
                    value = "" + (((Number) Objects.requireNonNull(object.getSingle(event))).intValue());
                }
                default -> action = Action.RUN_COMMAND;
            }
            if (value == null) {
                return null;
            }
            return new ClickEvent[]{ClickEvent.clickEvent(action, ((String) value))};
        }
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends ClickEvent> getReturnType() {
        return ClickEvent.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        if (this.pattern == 6) {
            String key = this.object.toString(e, d);
            String data = this.nbtData.toString(e, d);
            return "click event to run custom payload with key " + key + " and with data " + data;
        }
        String[] actions = new String[]{"run command", "suggest command", "open url", "copy to clipboard", "change to page", "open dialog"};
        return "click event to " + actions[this.pattern] + " " + this.object.toString(e, d);
    }

}
