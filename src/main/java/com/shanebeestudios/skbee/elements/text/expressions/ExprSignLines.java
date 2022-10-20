package com.shanebeestudios.skbee.elements.text.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.Getter;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.text.BeeComponent;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("NullableProblems")
@Name("Text Component - Sign Line")
@Description("Get/set lines of signs with text components.")
@Examples({"set sign line 1 of target block to mini message from \"<rainbow>LINE ONE\"",
        "set sign line 2 of target block to translate component from \"item.minecraft.diamond_sword\"",
        "set {_line1} to sign line 1 of target block"})
@Since("2.4.0")
public class ExprSignLines extends PropertyExpression<Block, BeeComponent> {

    static {
        register(ExprSignLines.class, BeeComponent.class, "sign line %number%", "blocks");
    }

    private Expression<Number> signLine;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        setExpr((Expression<? extends Block>) exprs[1]);
        this.signLine = (Expression<Number>) exprs[0];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected BeeComponent[] get(Event event, Block[] source) {
        Number signLineSingle = this.signLine.getSingle(event);
        if (signLineSingle == null) return null;
        int signLine = signLineSingle.intValue();
        if (signLine > 4 || signLine < 1) return null;

        return get(source, new Getter<>() {
            @Override
            public @Nullable BeeComponent get(Block block) {
                return BeeComponent.getSignLine(block, signLine - 1);
            }
        });
    }

    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) {
            return CollectionUtils.array(BeeComponent.class);
        }
        return null;
    }

    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        if (mode == ChangeMode.SET) {
            if (!(delta[0] instanceof BeeComponent component)) return;
            Number signLineSingle = this.signLine.getSingle(event);
            if (signLineSingle == null) return;
            int signLine = signLineSingle.intValue();
            if (signLine > 4 || signLine < 1) return;
            signLine--;

            for (Block block : getExpr().getArray(event)) {
                component.setBlockLine(block, signLine);
            }
        }
    }

    @Override
    public @NotNull Class<? extends BeeComponent> getReturnType() {
        return BeeComponent.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "sign line " + this.signLine.toString(e, d) + " of " + getExpr().toString(e, d);
    }

}
