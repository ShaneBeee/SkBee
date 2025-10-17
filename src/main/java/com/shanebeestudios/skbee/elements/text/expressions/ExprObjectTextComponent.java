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
import com.shanebeestudios.skbee.api.util.legacy.ObjectTextComponentUtils;
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ObjectComponent;
import net.kyori.adventure.text.object.ObjectContents;
import net.kyori.adventure.text.object.PlayerHeadObjectContents;
import net.kyori.adventure.text.object.SpriteObjectContents;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@Name("TextComponent - Object Text Component")
@Description({"Create a text component using an atlas/sprite or a player head.",
    "The atlas is optional and will default to the \"minecraft:blocks\" atlas.",
    "Requires Minecraft 1.21.9+",
    "See [**Text Component Format on McWiki**](https://minecraft.wiki/w/Text_component_format#Object) for more information."})
@Examples({"set {_ds} to object text component with sprite \"item/diamond_sword\"",
    "set {_head} to object text component with player head from player",
    "set {_head} to object text component with player head from \"Notch\"",
    "set {_head} to object text component with player head from {_uuid}"})
@Since("INSERT VERSION")
public class ExprObjectTextComponent extends SimpleExpression<ComponentWrapper> {

    static {
        if (Util.IS_RUNNING_MC_1_21_9) {
            Skript.registerExpression(ExprObjectTextComponent.class, ComponentWrapper.class, ExpressionType.COMBINED,
                "object text component [with atlas %-string% [and]] with sprite %string%",
                "object text component with player head (from|of) %string/player/offlineplayer/uuid%");
        }
    }

    private Expression<String> atlasData;
    private Expression<String> spriteData;
    private Expression<?> playerData;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        if (matchedPattern == 0) {
            this.atlasData = (Expression<String>) exprs[0];
            this.spriteData = (Expression<String>) exprs[1];
        } else {
            this.playerData = exprs[0];
        }
        return true;
    }

    @SuppressWarnings("PatternValidation")
    @Override
    protected ComponentWrapper @Nullable [] get(Event event) {
        if (this.spriteData != null) {
            Key atlas = SpriteObjectContents.DEFAULT_ATLAS;

            if (this.atlasData != null) {
                String atlasString = this.atlasData.getSingle(event);
                if (atlasString != null) {
                    atlas = Key.key(atlasString);
                }
            }

            if (this.spriteData == null) return null;
            String spriteString = this.spriteData.getSingle(event);
            if (spriteString == null) return null;
            Key sprite = Key.key(spriteString);

            ComponentWrapper componentWrapper = ObjectTextComponentUtils.getSpriteObject(atlas, sprite);
            return new ComponentWrapper[]{componentWrapper};
        } else if (this.playerData != null) {
            Object playerData = this.playerData.getSingle(event);
            ComponentWrapper componentWrapper = ObjectTextComponentUtils.getPlayerHead(playerData);
            return new ComponentWrapper[]{componentWrapper};
        }
        return null;
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

        if (this.spriteData != null) {
            if (this.atlasData != null) {
                builder.append("with atlas").append(this.atlasData);
            }
            builder.append("with sprite", this.spriteData);
        } else if (this.playerData != null) {
            builder.append("with player head from", this.playerData);
        }
        return builder.toString();
    }

}
