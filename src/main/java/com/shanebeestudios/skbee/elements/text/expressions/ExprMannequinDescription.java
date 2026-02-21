package com.shanebeestudios.skbee.elements.text.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.skript.base.SimplePropertyExpression;
import com.shanebeestudios.skbee.api.util.Util;
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mannequin;
import org.jetbrains.annotations.Nullable;

public class ExprMannequinDescription extends SimplePropertyExpression<Entity, ComponentWrapper> {

    public static void register(Registration reg) {
        if (Util.IS_RUNNING_MC_1_21_9) {
            reg.newPropertyExpression(ExprMannequinDescription.class, ComponentWrapper.class,
                    "component mannequin description", "entities")
                .name("TextComponent - Mannequin Description")
                .description("Represents the description of a mannequin entity (text below name).",
                    "Delete will completely remove the description and reset will take it back to the default value from the Minecraft lang file.",
                    "Requires Minecraft 1.21.9+")
                .examples("set component mannequin description of last spawned entity to mini message from \"<rainbow>OOOO Imma Mannequin\"",
                    "delete component mannequin description of all mannequins",
                    "set {_d} to component mannequin description of {_entity}")
                .since("3.14.0")
                .register();
        }
    }

    @Override
    public @Nullable ComponentWrapper convert(Entity entity) {
        if (entity instanceof Mannequin mannequin) return ComponentWrapper.fromComponent(mannequin.getDescription());
        return null;
    }

    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET || mode == ChangeMode.DELETE || mode == ChangeMode.RESET)
            return CollectionUtils.array(ComponentWrapper.class);
        return null;
    }

    @Override
    public void change(Entity from, Object @Nullable [] delta, ChangeMode mode) {
        if (!(from instanceof Mannequin mannequin)) return;

        if (mode == ChangeMode.DELETE) {
            mannequin.setDescription(null);
        } else if (mode == ChangeMode.RESET) {
            mannequin.setDescription(Mannequin.defaultDescription());
        } else if (mode == ChangeMode.SET && delta != null && delta.length == 1 && delta[0] instanceof ComponentWrapper comp) {
            mannequin.setDescription(comp.getComponent());
        }
    }

    @Override
    protected String getPropertyName() {
        return "component mannequin description";
    }

    @Override
    public Class<? extends ComponentWrapper> getReturnType() {
        return ComponentWrapper.class;
    }

}
