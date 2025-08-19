package com.shanebeestudios.skbee.elements.text.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("TextComponent - Sign Line")
@Description({"Get/set lines of a sign with text components. Optionally set the front/back of a sign. (Defaults to front)",
    "\nNOTE: Setting the back of a sign requires Minecraft 1.20+"})
@Examples({"set sign line 1 of target block to mini message from \"<rainbow>LINE ONE\"",
    "set sign line 2 of target block to translate component from \"item.minecraft.diamond_sword\"",
    "set {_line1} to sign line 1 of target block",
    "set {_line1} to front sign line 1 of target block",
    "set back sign line 1 of {_sign} to mini message from \"<rainbow>LINE ONE\""})
@Since("2.4.0, 2.11.0 (front|back)")
public class ExprSignLines extends PropertyExpression<Block, ComponentWrapper> {

    static {
        register(ExprSignLines.class, ComponentWrapper.class, "[(front|:back)] sign line %number%", "blocks");
    }

    private Expression<Number> signLine;
    private boolean front;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        setExpr((Expression<? extends Block>) exprs[1]);
        this.signLine = (Expression<Number>) exprs[0];
        this.front = !parseResult.hasTag("back");
        return true;
    }

    @Override
    protected ComponentWrapper[] get(Event event, Block[] source) {
        Number signLineSingle = this.signLine.getSingle(event);
        if (signLineSingle == null) return null;
        int signLine = signLineSingle.intValue();
        if (signLine > 4 || signLine < 1) return null;

        return get(source, block -> ComponentWrapper.getSignLine(block, signLine - 1, front));
    }

    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) {
            return CollectionUtils.array(ComponentWrapper.class);
        }
        return null;
    }

    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        if (mode == ChangeMode.SET) {
            if (!(delta[0] instanceof ComponentWrapper component)) return;
            Number signLineSingle = this.signLine.getSingle(event);
            if (signLineSingle == null) return;
            int signLine = signLineSingle.intValue();
            if (signLine > 4 || signLine < 1) return;
            signLine--;

            for (Block block : getExpr().getArray(event)) {
                component.setBlockLine(block, signLine, this.front);
            }
        }
    }

    @Override
    public @NotNull Class<? extends ComponentWrapper> getReturnType() {
        return ComponentWrapper.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String front = this.front ? "front" : "back";
        return front + " sign line " + this.signLine.toString(e, d) + " of " + getExpr().toString(e, d);
    }

}
