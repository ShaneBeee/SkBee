package com.shanebeestudios.skbee.elements.worldcreator.expressions;

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
import org.bukkit.World.Environment;
import org.bukkit.WorldType;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import com.shanebeestudios.skbee.elements.worldcreator.objects.BeeWorldCreator;

import javax.annotation.Nullable;

@Name("World Creator Options")
@Description("Set different options for world creators. See SkBee wiki for more details.")
@Examples({"set {_w} to a new world creator named \"my-world\"",
        "set environment of {_w} to nether",
        "set world type of {_w} to flat",
        "set should generate structures of {_w} to true",
        "load world from creator {_w}"})
@Since("1.8.0")
public class ExprWorldCreatorOption extends SimplePropertyExpression<BeeWorldCreator, Object> {

    static {
        register(ExprWorldCreatorOption.class, Object.class,
                "(0¦environment|1¦world type|2¦world seed|3¦gen[erator] settings|4¦generator" +
                        "|5¦should gen[erate] structures|6¦[is] hardcore|7¦keep spawn loaded)",
                "worldcreator");
    }

    private int pattern;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        this.pattern = parseResult.mark;
        setExpr((Expression<BeeWorldCreator>) exprs[0]);
        return true;
    }

    @Nullable
    @Override
    public Object convert(@NotNull BeeWorldCreator creator) {
        switch (pattern) {
            case 1:
                return creator.getWorldType();
            case 2:
                return creator.getSeed();
            case 3:
                return creator.getGeneratorSettings();
            case 4:
                return creator.getGenerator();
            case 5:
                return creator.isGenStructures();
            case 6:
                return creator.isHardcore();
            case 7:
                return creator.isKeepSpawnLoaded();
            default:
                return creator.getEnvironment();
        }
    }

    @Nullable
    @Override
    public Class<?>[] acceptChange(@NotNull ChangeMode mode) {
        if (mode == ChangeMode.SET) {
            switch (pattern) {
                case 1:
                    return CollectionUtils.array(WorldType.class);
                case 2:
                    return CollectionUtils.array(Number.class);
                case 3:
                case 4:
                    return CollectionUtils.array(String.class);
                case 5:
                case 6:
                case 7:
                    return CollectionUtils.array(Boolean.class);
                default:
                    return CollectionUtils.array(Environment.class);
            }
        }
        return null;
    }

    @Override
    public void change(@NotNull Event event, @Nullable Object[] delta, @NotNull ChangeMode mode) {
        Object object = delta != null ? delta[0] : null;
        BeeWorldCreator creator = getExpr().getSingle(event);
        if (creator == null) return;

        switch (pattern) {
            case 1:
                if (object instanceof WorldType) {
                    creator.setWorldType((WorldType) object);
                }
                break;
            case 2:
                if (object instanceof Number) {
                    creator.setSeed(((Number) object).longValue());
                }
                break;
            case 3:
                if (object instanceof String) {
                    creator.setGeneratorSettings(((String) object));
                }
                break;
            case 4:
                if (object instanceof String) {
                    creator.setGenerator(((String) object));
                }
                break;
            case 5:
                if (object instanceof Boolean) {
                    creator.setGenStructures(((Boolean) object));
                }
                break;
            case 6:
                if (object instanceof Boolean) {
                    creator.setHardcore((Boolean) object);
                }
                break;
            case 7:
                if (object instanceof Boolean) {
                    creator.setKeepSpawnLoaded((Boolean) object);
                }
                break;
            default:
                if (object instanceof Environment) {
                    creator.setEnvironment((Environment) object);
                }
        }
    }

    @Override
    public @NotNull Class<?> getReturnType() {
        switch (pattern) {
            case 1:
                return WorldType.class;
            case 2:
                return Number.class;
            case 3:
            case 4:
                return String.class;
            case 5:
            case 6:
            case 7:
                return Boolean.class;
            default:
                return Environment.class;
        }
    }

    @Override
    protected @NotNull String getPropertyName() {
        switch (pattern) {
            case 1:
                return "world type";
            case 2:
                return "seed";
            case 3:
                return "generator settings";
            case 4:
                return "generator";
            case 5:
                return "should generate structures";
            case 6:
                return "hardcore";
            case 7:
                return "keep spawn loaded";
            default:
                return "environment";
        }
    }

}
