package com.shanebeestudios.skbee.elements.worldcreator.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.util.Util;
import com.shanebeestudios.skbee.elements.worldcreator.objects.BeeWorldCreator;
import org.bukkit.Location;
import org.bukkit.World.Environment;
import org.bukkit.WorldType;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class ExprWorldCreatorOption extends SimplePropertyExpression<BeeWorldCreator, Object> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprWorldCreatorOption.class, Object.class,
                "(environment|1:world type|2:world seed|3:gen[erator] settings|4:generator" +
                    "|5:should gen[erate] structures|6:[is] hardcore|7:keep spawn loaded|8:load on start|9:fixed spawn location) [option]",
                "worldcreator")
            .name("World Creator Options")
            .description("Set different options for world creators. See SkBee wiki for more details.",
                "NOTE: 'load on start' will bypass 'auto-load-custom-worlds' in SkBee config.",
                "`fixed spawn location` = This is used to override Minecraft attempting to find a spawn location (Which takes a LONG time), " +
                    "this option can greatly speed up world creation.")
            .examples("set {_w} to a new world creator named \"my-world\"",
                "set environment option of {_w} to nether",
                "set world type option of {_w} to flat",
                "set should generate structures option of {_w} to true",
                "set load on start option of {_w} to false",
                "set fixed spawn location option of {_w} to location(1,100,1)",
                "load world from creator {_w}")
            .since("1.8.0")
            .register();
    }

    private int pattern;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        this.pattern = parseResult.mark;
        if (this.pattern == 7 && Util.IS_RUNNING_MC_1_21_9) {
            // Minecraft no longer has spawn chunks starting in 1.21.9
            Skript.error("'keep spawn loaded' is no longer used by Minecraft.");
            return false;
        }
        setExpr((Expression<BeeWorldCreator>) exprs[0]);
        return true;
    }

    @Nullable
    @Override
    public Object convert(@NotNull BeeWorldCreator creator) {
        return switch (this.pattern) {
            case 1 -> creator.getWorldType();
            case 2 -> creator.getSeed();
            case 3 -> creator.getGeneratorSettings();
            case 4 -> creator.getGenerator();
            case 5 -> creator.isGenStructures();
            case 6 -> creator.isHardcore();
            case 7 -> creator.isKeepSpawnLoaded();
            case 8 -> creator.isLoadOnStart();
            case 9 -> null;
            default -> creator.getEnvironment();
        };
    }

    @Override
    public Class<?>[] acceptChange(@NotNull ChangeMode mode) {
        if (mode == ChangeMode.SET) {
            return switch (this.pattern) {
                case 1 -> CollectionUtils.array(WorldType.class);
                case 2 -> CollectionUtils.array(Number.class);
                case 3, 4 -> CollectionUtils.array(String.class);
                case 5, 6, 7, 8 -> CollectionUtils.array(Boolean.class);
                case 9 -> CollectionUtils.array(Location.class);
                default -> CollectionUtils.array(Environment.class);
            };
        }
        return null;
    }

    @Override
    public void change(@NotNull Event event, @Nullable Object[] delta, @NotNull ChangeMode mode) {
        Object object = delta != null ? delta[0] : null;
        BeeWorldCreator creator = getExpr().getSingle(event);
        if (creator == null) return;

        switch (this.pattern) {
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
            case 8:
                if (object instanceof Boolean loadOnStart) {
                    creator.setLoadOnStart(loadOnStart);
                }
                break;
            case 9:
                if (object instanceof Location location) {
                    creator.setFixedSpawnLocation(location);
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
        return switch (this.pattern) {
            case 1 -> WorldType.class;
            case 2 -> Number.class;
            case 3, 4 -> String.class;
            case 5, 6, 7 -> Boolean.class;
            default -> Environment.class;
        };
    }

    @Override
    protected @NotNull String getPropertyName() {
        String option = switch (this.pattern) {
            case 1 -> "world type";
            case 2 -> "seed";
            case 3 -> "generator settings";
            case 4 -> "generator";
            case 5 -> "should generate structures";
            case 6 -> "hardcore";
            case 7 -> "keep spawn loaded";
            case 8 -> "load on start";
            case 9 -> "fixed spawn location";
            default -> "environment";
        };
        return option + " option";
    }

}
