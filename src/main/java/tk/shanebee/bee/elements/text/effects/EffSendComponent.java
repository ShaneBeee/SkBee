package tk.shanebee.bee.elements.text.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import javax.annotation.Nullable;

@Name("TextComponent - Send")
@Description("Send text components to players.")
@Examples({"set {_comp::1} to text component of \"hi player \"",
        "set {_comp::2} to text component of \"hover over me for a special message!\"",
        "set hover event of {_comp::2} to hover event to show \"OoO look ma I'm hovering!\"",
        "send component {_comp::*} to player"})
@Since("INSERT VERSION")
public class EffSendComponent extends Effect {

    static {
        Skript.registerEffect(EffSendComponent.class, "send [text] component[s] %basecomponents% to %players%");
    }

    private Expression<BaseComponent> components;
    private Expression<Player> players;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        components = (Expression<BaseComponent>) exprs[0];
        players = (Expression<Player>) exprs[1];
        return true;
    }

    @Override
    protected void execute(Event e) {
        if (components == null || players == null) return;

        BaseComponent[] components = this.components.getArray(e);
        for (Player player : this.players.getArray(e)) {
            player.spigot().sendMessage(components);
        }
    }

    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return null;
    }

}
