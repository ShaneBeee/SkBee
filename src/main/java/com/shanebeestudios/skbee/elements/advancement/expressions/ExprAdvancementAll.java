package com.shanebeestudios.skbee.elements.advancement.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.iterator.IteratorIterable;
import org.bukkit.Bukkit;
import org.bukkit.advancement.Advancement;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Name("Advancement - All Available")
@Description("Get a list of all available advancements currently registered on the server.")
@Examples({"set {_a::*} to all available advancements",
        "loop all available advancements:"})
@Since("1.17.0")
public class ExprAdvancementAll extends SimpleExpression<Advancement> {

    static {
        Skript.registerExpression(ExprAdvancementAll.class, Advancement.class, ExpressionType.SIMPLE,
                "[all] available advancements");
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] expr, int i, Kleenean kleenean, ParseResult parseResult) {
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable Advancement[] get(Event event) {
        List<Advancement> advancements = new ArrayList<>();
        Iterator<? extends Advancement> iterator = iterator(event);
        if (iterator == null) {
            return new Advancement[0];
        }
        for (Advancement advancement : new IteratorIterable<>(iterator)) {
            advancements.add(advancement);
        }
        return advancements.toArray(new Advancement[0]);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable Iterator<? extends Advancement> iterator(Event event) {
        return Bukkit.advancementIterator();
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public @NotNull Class<? extends Advancement> getReturnType() {
        return Advancement.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean b) {
        return "all available advancements";
    }

}
