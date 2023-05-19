package com.shanebeestudios.skbee.elements.text.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.LiteralUtils;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.text.BeeComponent;
import com.shanebeestudios.skbee.api.util.ChatUtil;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Name("Text Component - New Text Component")
@Description({"Create a new text component. Can have hover and click events added to it. You can also create a translate component, ",
        "this will send to the client, and the client will translate based on their language. You can use either an item type or a ",
        "translate string, you can find these in your Minecraft jar 'assets/minecraft/lang/<lang file>.json'.",
        "As of Paper 1.17.1, several more objects can translate including GameRules, PotionEffectTypes, Attributes, Difficulty, Enchantments, ",
        "FireworkEffectTypes, Entities and Blocks. KeyBind components will be replaced with the actual key the client is using.",
        "Some components have extra objects, you can use strings or other text components here."})
@Examples({"set {_comp::1} to text component from \"hi player \"",
        "set {_comp::2} to text component of \"hover over me for a special message!\"",
        "set hover event of {_comp::2} to hover event showing \"OoO look ma I'm hovering!\"",
        "send component {_comp::*} to player", "",
        "set {_t} to translate component from player's tool",
        "set {_t} to translate component from \"item.minecraft.milk_bucket\"",
        "set {_death} to translate component from \"death.fell.accident.ladder\" using player's name",
        "set {_assist} to translate component from \"death.fell.assist\" using victim's name and attacker's name",
        "set {_key} to keybind component of \"key.jump\""})
@Since("1.5.0")
public class ExprTextComponent extends SimpleExpression<BeeComponent> {
    static {
        Skript.registerExpression(ExprTextComponent.class, BeeComponent.class, ExpressionType.COMBINED,
                "[a] [new] text component[s] (from|of) %strings%",
                "[a] [new] key[ ]bind component[s] (from|of) %strings%",
                "[a] [new] translate component[s] (from|of) %objects% [(with|using) %-objects%]");
    }

    private int pattern;
    private Expression<Object> translation;
    private Expression<Object> objects;

    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        pattern = matchedPattern;
        translation = LiteralUtils.defendExpression(exprs[0]);
        objects = pattern == 2 ? LiteralUtils.defendExpression(exprs[1]) : null;
        if (objects != null) {
            return LiteralUtils.canInitSafely(translation, objects);
        }
        return LiteralUtils.canInitSafely(translation);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected BeeComponent[] get(@NotNull Event e) {
        List<BeeComponent> components = new ArrayList<>();

        for (Object object : this.translation.getArray(e)) {
            if (pattern == 0) {
                components.add(BeeComponent.fromText(Util.getColString(((String) object))));
            } else if (pattern == 1) {
                components.add(BeeComponent.fromKeybind((String) object));
            } else if (pattern == 2) {
                String translate = ChatUtil.getTranslation(object);
                if (translate != null) {
                    if (this.objects != null) {
                        components.add(BeeComponent.fromTranslate(translate, this.objects.getArray(e)));
                    } else {
                        components.add(BeeComponent.fromTranslate(translate));
                    }

                }
            }
        }
        return components.toArray(new BeeComponent[0]);
    }

    @Override
    public boolean isSingle() {
        return this.translation.isSingle();
    }

    @Override
    public @NotNull Class<? extends BeeComponent> getReturnType() {
        return BeeComponent.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String comp = pattern == 0 ? "text" : pattern == 1 ? "keybind" : "translate";
        String trans = translation.toString(e, d);
        String obj = objects != null ? "using " + objects.toString(e, d) : "";
        return String.format("a new %s component from %s %s", comp, trans, obj);
    }

}
