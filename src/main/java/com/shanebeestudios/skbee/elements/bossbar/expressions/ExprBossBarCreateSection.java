package com.shanebeestudios.skbee.elements.bossbar.expressions;

import ch.njol.skript.config.SectionNode;
import ch.njol.skript.expressions.base.SectionExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.util.Color;
import ch.njol.skript.util.SkriptColor;
import ch.njol.util.Kleenean;
import com.github.shanebeee.skr.Registration;
import com.shanebeestudios.skbee.api.util.BossBarUtils;
import com.github.shanebeee.skr.skript.SimpleEntryValidator;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;

import java.util.ArrayList;
import java.util.List;

public class ExprBossBarCreateSection extends SectionExpression<BossBar> {

    private static EntryValidator VALIDATOR;

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void register(Registration reg) {
        Class[] idClasses = new Class[]{String.class, NamespacedKey.class};
        SimpleEntryValidator builder = SimpleEntryValidator.builder();
        builder.addRequiredEntry("id", idClasses);
        builder.addOptionalEntry("title", String.class);
        builder.addOptionalEntry("color", Color.class);
        builder.addOptionalEntry("style", BarStyle.class);
        builder.addOptionalEntry("fog", Boolean.class);
        builder.addOptionalEntry("darken_sky", Boolean.class);
        builder.addOptionalEntry("play_boss_music", Boolean.class);
        builder.addOptionalEntry("progress", Number.class);
        builder.addOptionalEntry("visible", Boolean.class);

        VALIDATOR = builder.build();
        reg.newCombinedExpression(ExprBossBarCreateSection.class, BossBar.class,
                "create [a] new boss[ ]bar")
            .validator(VALIDATOR)
            .name("BossBar - Create Section")
            .description("Create a new BossBar with some optional values.",
                "If a BossBar with the same ID already exists, it will be updated with the new values.",
                "**Entries**:",
                " - `id` = ID for the bossbar (required String/NamespacedKey).",
                " - `title` = Title of the bossbar [optional string].",
                " - `color` = Color of the bossbar [optional SkriptColor, defaults to purple].",
                " - `style` = Style of the bossbar [optional BarStyle, defaults to solid].",
                " - `fog` = Whether to enable fog effect [optional Boolean, defaults to false].",
                " - `darken_sky` = Whether to darken the sky [optional Boolean, defaults to false].",
                " - `play_boss_music` = Whether to play boss music [optional Boolean, defaults to false].",
                " - `progress` = Progress of the bossbar [optional Number between 0 and 100].",
                " - `visible` = Whether the bossbar is visible [optional Boolean, defaults to true].")
            .examples("set {_bar} to create new bossbar:",
                "\tid: \"some_id_other\"",
                "\ttitle: \"My Bossbar\"",
                "\tcolor: green",
                "\tstyle: segmented_20",
                "\tfog: true",
                "\tdarken_sky: true",
                "\tplay_boss_music: false",
                "\tprogress: 75",
                "",
                "add all players to {_bar}")
            .since("3.21.0")
            .register();
    }

    private Expression<?> id;
    private Expression<String> title;
    private Expression<Color> color;
    private Expression<BarStyle> style;
    private Expression<Boolean> fog;
    private Expression<Boolean> darkenSky;
    private Expression<Boolean> playBossMusic;
    private Expression<Number> progress;
    private Expression<Boolean> visible;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int pattern, Kleenean delayed, ParseResult result,
                        @Nullable SectionNode node, @Nullable List<TriggerItem> triggerItems) {
        EntryContainer validate = VALIDATOR.validate(node);
        if (validate == null) {
            return false;
        }

        this.id = (Expression<?>) validate.getOptional("id", false);
        this.title = (Expression<String>) validate.getOptional("title", false);
        this.color = (Expression<Color>) validate.getOptional("color", false);
        this.style = (Expression<BarStyle>) validate.getOptional("style", false);
        this.fog = (Expression<Boolean>) validate.getOptional("fog", false);
        this.darkenSky = (Expression<Boolean>) validate.getOptional("darken_sky", false);
        this.playBossMusic = (Expression<Boolean>) validate.getOptional("play_boss_music", false);
        this.progress = (Expression<Number>) validate.getOptional("progress", false);
        this.visible = (Expression<Boolean>) validate.getOptional("visible", false);
        return true;
    }

    @Override
    protected BossBar @Nullable [] get(Event event) {
        Object o = this.id.getSingle(event);
        NamespacedKey key;
        if (o instanceof String s) {
            key = Util.getNamespacedKey(s, false);
        } else if (o instanceof NamespacedKey nsk) {
            key = nsk;
        } else {
            return null;
        }

        if (key == null) {
            return null;
        }

        String title = null;
        if (this.title != null) {
            title = this.title.getSingle(event);
        }
        BarColor barColor = BarColor.PURPLE;
        if (this.color != null) {
            Color color = this.color.getSingle(event);
            if (color instanceof SkriptColor sc) {
                barColor = BossBarUtils.getBossBarColor(sc);
            }
        }
        BarStyle style = BarStyle.SOLID;
        if (this.style != null) {
            style = this.style.getOptionalSingle(event).orElse(BarStyle.SOLID);
        }

        List<BarFlag> flags = new ArrayList<>();
        if (this.fog != null) {
            if (Boolean.TRUE.equals(this.fog.getSingle(event))) {
                flags.add(BarFlag.CREATE_FOG);
            }
        }
        if (this.darkenSky != null) {
            if (Boolean.TRUE.equals(this.darkenSky.getSingle(event))) {
                flags.add(BarFlag.DARKEN_SKY);
            }
        }
        if (this.playBossMusic != null) {
            if (Boolean.TRUE.equals(this.playBossMusic.getSingle(event))) {
                flags.add(BarFlag.PLAY_BOSS_MUSIC);
            }
        }

        KeyedBossBar bossBar;
        KeyedBossBar old = Bukkit.getBossBar(key);
        if (old != null) {
            bossBar = old;
            bossBar.setTitle(title);
            bossBar.setColor(barColor);
            bossBar.setStyle(style);
            flags.forEach(bossBar::addFlag);
        } else {
            bossBar = Bukkit.createBossBar(key, title, barColor, style, flags.toArray(BarFlag[]::new));
        }

        if (this.progress != null) {
            Number num = this.progress.getSingle(event);
            if (num != null) {
                float progress = Math.clamp(num.floatValue() / 100, 0, 1);
                bossBar.setProgress(progress);
            }
        }

        if (this.visible != null) {
            Boolean visible = this.visible.getSingle(event);
            if (visible != null) {
                bossBar.setVisible(visible);
            }
        }

        return new BossBar[]{bossBar};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public boolean isSectionOnly() {
        return true;
    }

    @Override
    public Class<? extends BossBar> getReturnType() {
        return BossBar.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "create new bossbar";
    }

}
