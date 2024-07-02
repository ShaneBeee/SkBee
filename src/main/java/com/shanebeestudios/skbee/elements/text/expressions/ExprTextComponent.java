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
import com.shanebeestudios.skbee.api.util.ChatUtil;
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Name("TextComponent - New Text Component")
@Description({"Create a new text component. Can add hover/click events to it.",
    "",
    "**Types:**",
    "Text: Just a plain old text component from a string.",
    "Rawtext: Same as text, but color codes will be visible.",
    "Keybind: Will use Minecraft's keybind system.",
    "Translate: Will use Minecraft's lang file keys. ",
    "  - You can find these in your Minecraft jar 'assets/minecraft/lang/<lang file>.json'.",
    "  - Also supports getting translations for objects such as ItemTypes, Entities and PotionEffectTypes.",
    "  - When sent to the client, the client will translate based on the lang they've picked.",
    "  - Some lang file entries take in other objects, thats what the optional `using %objects%` is for.",
    "Json: Will deserialize a json string back into a component.",
    "  - Minecraft stores components in NBT as json components (ex: name of a held item)."})
@Examples({"set {_comp::1} to text component from \"hi player \"",
    "set {_comp::2} to text component of \"hover over me for a special message!\"",
    "add hover event showing \"OoO look ma I'm hovering!\" to {_comp::2}",
    "send component {_comp::*} to player", "",
    "set {_t} to translate component from player's tool",
    "set {_t} to translate component from \"item.minecraft.milk_bucket\"",
    "set {_death} to translate component from \"death.fell.accident.ladder\" using player's name",
    "set {_assist} to translate component from \"death.fell.assist\" using victim's name and attacker's name",
    "set {_key} to keybind component of \"key.jump\"",
    "set {_name} to json component from (string tag \"custom_name\" of nbt of target block)"})
@Since("1.5.0")
public class ExprTextComponent extends SimpleExpression<ComponentWrapper> {
    static {
        Skript.registerExpression(ExprTextComponent.class, ComponentWrapper.class, ExpressionType.COMBINED,
            "[a] [new] (text|:rawtext) component[s] (from|of) %strings%",
            "[a] [new] key[ ]bind component[s] (from|of) %strings%",
            "[a] [new] translate component[s] (from|of) %objects% [(with|using) %-objects%]",
            "[a] [new] json component (from|of) %strings%");
    }

    private int pattern;
    private Expression<Object> translation;
    private Expression<Object> objects;
    private boolean raw;

    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        this.pattern = matchedPattern;
        this.translation = LiteralUtils.defendExpression(exprs[0]);
        this.objects = pattern == 2 ? LiteralUtils.defendExpression(exprs[1]) : null;
        this.raw = parseResult.hasTag("rawtext");
        if (this.objects != null) {
            return LiteralUtils.canInitSafely(this.translation, this.objects);
        }
        return LiteralUtils.canInitSafely(this.translation);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected ComponentWrapper[] get(@NotNull Event e) {
        List<ComponentWrapper> components = new ArrayList<>();

        for (Object object : this.translation.getArray(e)) {
            if (this.pattern == 0) {
                if (this.raw) components.add(ComponentWrapper.fromRawText((String) object));
                else components.add(ComponentWrapper.fromText((String) object));
            } else if (this.pattern == 1) {
                components.add(ComponentWrapper.fromKeybind((String) object));
            } else if (this.pattern == 2) {
                String translate = ChatUtil.getTranslation(object);
                if (translate != null) {
                    if (this.objects != null) {
                        components.add(ComponentWrapper.fromTranslate(translate, this.objects.getArray(e)));
                    } else {
                        components.add(ComponentWrapper.fromTranslate(translate));
                    }
                }
            } else if (this.pattern == 3) {
                components.add(ComponentWrapper.fromJson((String) object));
            }
        }
        return components.toArray(new ComponentWrapper[0]);
    }

    @Override
    public boolean isSingle() {
        return this.translation.isSingle();
    }

    @Override
    public @NotNull Class<? extends ComponentWrapper> getReturnType() {
        return ComponentWrapper.class;
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        String comp = switch (this.pattern) {
            case 1 -> "keybind";
            case 2 -> "translate";
            case 3 -> "json";
            default -> this.raw ? "rawtext" : "text";
        };
        String trans = this.translation.toString(e, d);
        String obj = this.objects != null ? "using " + this.objects.toString(e, d) : "";
        return String.format("a new %s component from %s %s", comp, trans, obj);
    }

}
