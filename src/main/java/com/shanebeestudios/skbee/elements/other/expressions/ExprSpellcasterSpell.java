package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Spellcaster;
import org.bukkit.entity.Spellcaster.Spell;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

@Name("Spellcaster Spell")
@Description("Get/set/reset the spell of a spell casting entity (Illusioner, Evoker).")
@Examples({"set spell of target entity to fangs",
        "reset spell of target entity"})
@Since("1.17.0")
public class ExprSpellcasterSpell extends SimplePropertyExpression<Entity, Spell> {

    static {
        register(ExprSpellcasterSpell.class, Spell.class, "[spellcaster] spell", "entities");
    }

    @Override
    public @Nullable Spell convert(Entity entity) {
        if (entity instanceof Spellcaster spellcaster) {
            return spellcaster.getSpell();
        }
        return null;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET || mode == ChangeMode.RESET) {
            return CollectionUtils.array(Spell.class);
        }
        return null;
    }

    @SuppressWarnings({"NullableProblems", "ConstantConditions"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        Spell spell = delta != null ? ((Spell) delta[0]) : Spell.NONE;

        for (Entity entity : getExpr().getArray(event)) {
            if (entity instanceof Spellcaster spellcaster) {
                spellcaster.setSpell(spell);
            }
        }
    }

    @Override
    public @NotNull Class<? extends Spell> getReturnType() {
        return Spell.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "spell";
    }

}
