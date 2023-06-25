package com.shanebeestudios.skbee.elements.text.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

@Name("Text Component - Entity Name")
@Description({"Get/set the component name of an entity.",
        "\nNAME = the name will only show when crosshair is on entity.",
        "\nDISPLAY NAME = the name will always show."})
@Examples("set component entity name of target entity to translate component from \"entity.minecraft.llama\"")
@Since("2.4.0")
public class ExprNameEntity extends SimplePropertyExpression<Entity, ComponentWrapper> {

    static {
        register(ExprNameEntity.class, ComponentWrapper.class,
                "component entity (name|1¦display name)", "entities");
    }

    private boolean alwaysOn;

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.alwaysOn = parseResult.mark == 1;
        return super.init(exprs, matchedPattern, isDelayed, parseResult);
    }

    @Override
    public @Nullable ComponentWrapper convert(Entity entity) {
        return ComponentWrapper.fromComponent(entity.name());
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) {
            return CollectionUtils.array(ComponentWrapper.class);
        }
        return null;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        if (mode == ChangeMode.SET) {
            if (delta[0] instanceof ComponentWrapper component) {
                for (Entity entity : getExpr().getArray(event)) {
                    component.setEntityName(entity, alwaysOn);
                }
            }
        }
    }

    @Override
    public @NotNull Class<? extends ComponentWrapper> getReturnType() {
        return ComponentWrapper.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        String name = alwaysOn ? "display name" : "name";
        return "component entity " + name;
    }

}
