package com.shanebeestudios.skbee.elements.dialog.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.SyntaxStringBuilder;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.skript.base.Effect;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.audience.Audience;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Dialog - Open Dialog")
@Description({"Open a dialog to players.",
    "You can use keys from your own custom dialogs, as well as dialogs from datapacks."})
@Examples({"open dialog with id \"minecraft:my_dialog\" to player",
    "open dialog with id \"my_pack:some_dialog\" to all players"})
@Since("INSERT VERSION")
public class EffOpenDialog extends Effect {

    private static final Registry<Dialog> REGISTRY = RegistryAccess.registryAccess().getRegistry(RegistryKey.DIALOG);

    static {
        Skript.registerEffect(EffOpenDialog.class, "open dialog with id %string/namespacedkey% to %audiences%");
    }

    private Expression<?> id;
    private Expression<Audience> audiences;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.id = exprs[0];
        this.audiences = (Expression<Audience>) exprs[1];
        return true;
    }

    @Override
    protected void execute(Event event) {
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

    @Override
    public String toString(@Nullable Event e, boolean d) {
        return new SyntaxStringBuilder(e, d)
            .append("open dialog with id", this.id)
            .append("to", this.audiences)
            .toString();
    }

}
