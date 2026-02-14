package com.shanebeestudios.skbee.elements.dialog.effects;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.SyntaxStringBuilder;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.skript.base.Effect;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.audience.Audience;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public class EffOpenDialog extends Effect {

    private static final Registry<Dialog> REGISTRY = RegistryAccess.registryAccess().getRegistry(RegistryKey.DIALOG);

    public static void register(Registration reg) {
        reg.newEffect(EffOpenDialog.class, "close dialog[s] of %audiences%",
                "open dialog with id %string/namespacedkey% to %audiences%")
            .name("Dialog - Open/Close Dialog")
            .description("Open a dialog to players.",
                "You can use keys from your own custom dialogs, as well as dialogs from datapacks.",
                "You can also close the currently open dialog of a player.")
            .examples("open dialog with id \"minecraft:my_dialog\" to player",
                "open dialog with id \"my_pack:some_dialog\" to all players",
                "close dialogs of all players",
                "close dialogs of world \"world\" #closes dialogs of all players in that world")
            .since("3.16.0")
            .register();
    }

    private boolean close;
    private Expression<?> id;
    private Expression<Audience> audiences;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.close = matchedPattern == 0;
        if (this.close) {
            this.id = exprs[0];
        }
        this.audiences = (Expression<Audience>) exprs[matchedPattern];
        return true;
    }

    @Override
    protected void execute(Event event) {
        if (this.close) {
            for (Audience audience : this.audiences.getArray(event)) {
                audience.closeDialog();
            }
        } else {
            Object singleId = this.id.getSingle(event);

            NamespacedKey key;
            if (singleId instanceof String string) {
                key = NamespacedKey.fromString(string);
            } else if (singleId instanceof NamespacedKey nsk) {
                key = nsk;
            } else {
                return;
            }
            if (key == null) return;


            Dialog dialog = REGISTRY.get(key);
            if (dialog == null) return;

            for (Audience audience : this.audiences.getArray(event)) {
                audience.showDialog(dialog);
            }
        }
    }

    @Override
    public String toString(@Nullable Event e, boolean d) {
        if (this.close) {
            return "close dialogs of " + this.audiences.toString(e, d);
        }
        return new SyntaxStringBuilder(e, d)
            .append("open dialog with id", this.id)
            .append("to", this.audiences)
            .toString();
    }

}
