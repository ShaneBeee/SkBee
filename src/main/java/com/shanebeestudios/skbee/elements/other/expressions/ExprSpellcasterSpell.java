package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Spellcaster;
import org.bukkit.entity.Spellcaster.Spell;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

public class ExprSpellcasterSpell extends SimplePropertyExpression<Entity, Spell> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprSpellcasterSpell.class, Spell.class, "[spellcaster] spell", "entities")
            .name("Spellcaster Spell")
            .description("Get/set/reset the spell of a spell casting entity (Illusioner, Evoker).")
            .examples("set spell of target entity to fangs",
                "reset spell of target entity")
            .since("1.17.0")
            .register();
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
