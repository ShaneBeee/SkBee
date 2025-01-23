package com.shanebeestudios.skbee.elements.text.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.SyntaxStringBuilder;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.util.ItemUtils;
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import io.papermc.paper.inventory.tooltip.TooltipContext;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("TextComponent - Tooltip Lines")
@Description({"Computes the tooltip lines of an item and returns as a list of text components.",
    "**Disclaimer**: Tooltip contents are not guaranteed to be consistent across different Minecraft versions.",
    "`advanced` = Whether the context is for advanced tooltips.",
    "`creative` = Whether the context is for the creative inventory.",
    "`for player` = Creates player specific tooltips."})
@Examples({"set {_lines::*} to advanced tooltip lines of player's tool",
    "set {_lines::*} to creative tooltip lines of player's tool",
    "set {_lines::*} to advanced and creative tooltip lines of player's tool",
    "set {_lines::*} to advanced tooltip lines of 1 of diamond sword for player"})
@Since("3.8.0")
public class ExprTooltipLines extends SimpleExpression<ComponentWrapper> {

    static {
        Skript.registerExpression(ExprTooltipLines.class, ComponentWrapper.class, ExpressionType.COMBINED,
            "[advanced:advanced] [creative:[and] creative] [computed] tool[ ]tip lines of %itemstack/itemtype/slot% [player:for %-player%]");
    }

    private Expression<Object> items;
    private Expression<Player> player;
    private boolean isAdvanced;
    private boolean isCreative;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.items = (Expression<Object>) exprs[0];
        this.player = (Expression<Player>) exprs[1];
        this.isAdvanced = parseResult.hasTag("advanced");
        this.isCreative = parseResult.hasTag("creative");
        return true;
    }

    @Override
    protected ComponentWrapper @Nullable [] get(Event event) {
        Player player = this.player != null ? this.player.getOptionalSingle(event).orElse(null) : null;

        TooltipContext tooltipContext = TooltipContext.create(this.isAdvanced, this.isCreative);

        List<ComponentWrapper> components = ItemUtils.getValue(this.items.getSingle(event), itemStack -> {
            List<ComponentWrapper> list = new ArrayList<>();
            for (Component component : itemStack.computeTooltipLines(tooltipContext, player)) {
                list.add(ComponentWrapper.fromComponent(component));
            }
            return list;
        });
        if (components == null) return null;
        return components.toArray(new ComponentWrapper[0]);
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<? extends ComponentWrapper> getReturnType() {
        return ComponentWrapper.class;
    }

    @Override
    public String toString(Event e, boolean d) {
        SyntaxStringBuilder builder = new SyntaxStringBuilder(e, d);
        builder.append("computed");
        if (this.isAdvanced) builder.append("advanced");
        if (this.isCreative) builder.append("creative");
        builder.append("tip lines", this.items);
        if (this.player != null) builder.append("for player", this.player);

        return builder.toString();
    }

}
