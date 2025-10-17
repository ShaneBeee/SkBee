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
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.skript.base.SimpleExpression;
import com.shanebeestudios.skbee.api.util.Util;
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ObjectComponent;
import net.kyori.adventure.text.object.ObjectContents;
import net.kyori.adventure.text.object.SpriteObjectContents;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("TextComponent - Object Text Component")
@Description({"Create a text component using an atlas and a sprite.",
    "The atlas is optional and will default to the \"minecraft:blocks\" atlas.",
    "See [**Text Component Format on McWiki**](https://minecraft.wiki/w/Text_component_format#Object) for more information."})
@Examples("set {_ds} to object text component with sprite \"item/diamond_sword\"")
@Since("INSERT VERSION")
public class ExprObjectTextComponent extends SimpleExpression<ComponentWrapper> {

    static {
        if (Util.IS_RUNNING_MC_1_21_9) {
            Skript.registerExpression(ExprObjectTextComponent.class, ComponentWrapper.class, ExpressionType.COMBINED,
                "object text component [with atlas %-string% [and]] with sprite %string%");
        }
    }

    private Expression<String> atlas;
    private Expression<String> sprite;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.atlas = (Expression<String>) exprs[0];
        this.sprite = (Expression<String>) exprs[1];
        return true;
    }

    @SuppressWarnings("PatternValidation")
    @Override
    protected ComponentWrapper @Nullable [] get(Event event) {
        Key atlas = SpriteObjectContents.DEFAULT_ATLAS;

        if (this.atlas != null) {
            String atlasString = this.atlas.getSingle(event);
            if (atlasString != null) {
                atlas = Key.key(atlasString);
            }
        }

        if (this.sprite == null) return null;
        String spriteString = this.sprite.getSingle(event);
        if (spriteString == null) return null;
        Key sprite = Key.key(spriteString);

        SpriteObjectContents spriteObject = ObjectContents.sprite(atlas, sprite);
        ObjectComponent objectComponent = Component.object(spriteObject);
        ComponentWrapper componentWrapper = ComponentWrapper.fromComponent(objectComponent);

        return new ComponentWrapper[]{componentWrapper};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends ComponentWrapper> getReturnType() {
        return ComponentWrapper.class;
    }

    @Override
    public String toString(@Nullable Event e, boolean d) {
        SyntaxStringBuilder builder = new SyntaxStringBuilder(e, d)
            .append("object text component");

        if (this.atlas != null) {
            builder.append("with atlas").append(this.atlas);
        }
        builder.append("with sprite", this.sprite);
        return builder.toString();
    }

}
