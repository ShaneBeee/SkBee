package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.memory.MemoryKey;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprMemoryValue extends SimplePropertyExpression<LivingEntity, Object> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprMemoryValue.class, Object.class, "%*memory% memory", "livingentities")
                .name("Entity Memory")
                .description("Get/set memories of entities.")
                .examples("set {_home} to home memory of {_villager}",
                        "set home memory of last spawned villager to location of player")
                .since("3.4.0")
                .register();
    }

    private Literal<MemoryKey<?>> memory;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.memory = (Literal<MemoryKey<?>>) exprs[matchedPattern];
        setExpr((Expression<? extends LivingEntity>) exprs[matchedPattern == 0 ? 1 : 0]);
        return true;
    }

    @Override
    public @Nullable Object convert(LivingEntity entity) {
        try {
            return entity.getMemory(this.memory.getSingle());
        } catch (IllegalStateException ignore) {
            // Minecraft throws this... reported to PaperMC
            // https://github.com/PaperMC/Paper/issues/12618
            return null;
        }
    }

    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) return CollectionUtils.array(this.memory.getSingle().getMemoryClass());
        return null;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        if (mode == ChangeMode.SET && delta != null && delta[0] != null) {
            MemoryKey memory = this.memory.getSingle();
            Object value = delta[0];
            if (value.getClass().isAssignableFrom(memory.getMemoryClass())) {
                for (LivingEntity livingEntity : getExpr().getArray(event)) {
                    livingEntity.setMemory(memory, value);
                }
            }
        }
    }

    @Override
    public @NotNull Class<?> getReturnType() {
        return this.memory.getSingle().getMemoryClass();
    }

    @Override
    protected @NotNull String getPropertyName() {
        return this.memory.toString() + " memory";
    }

}
