package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.elements.other.type.Types;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("ArmorTrim - Material/Pattern")
@Description({"Represents the material and pattern of an armor trim.",
        "These cannot be changed, you'd have to create a new armor trim."})
@Examples("set {_pattern} to trim pattern of armor trim of player's tool")
@Since("2.13.0")
public class ExprArmorTrimMatPat extends SimplePropertyExpression<ArmorTrim, Object> {

    static {
        if (Types.HAS_ARMOR_TRIM) {
            register(ExprArmorTrimMatPat.class, Object.class,
                    "[armor ]trim (material|pat:pattern)", "armortrims");
        }
    }

    private boolean pattern;

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.pattern = parseResult.hasTag("pat");
        return super.init(exprs, matchedPattern, isDelayed, parseResult);
    }

    @Override
    public @Nullable Object convert(ArmorTrim armorTrim) {
        return this.pattern ? armorTrim.getPattern() : armorTrim.getMaterial();
    }

    @Override
    public @NotNull Class<?> getReturnType() {
        return this.pattern ? TrimPattern.class : TrimMaterial.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        String trim = this.pattern ? "pattern" : "material";
        return "armor trim " + trim;
    }

}
