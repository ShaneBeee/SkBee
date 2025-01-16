package com.shanebeestudios.skbee.api.skript;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import ch.njol.skript.patterns.PatternCompiler;
import ch.njol.skript.patterns.SkriptPattern;
import org.skriptlang.skript.lang.experiment.Experiment;
import org.skriptlang.skript.lang.experiment.ExperimentRegistry;
import org.skriptlang.skript.lang.experiment.LifeCycle;

/**
 * SkBee's experimental features
 * <p>Mostly copied from {@link ch.njol.skript.registrations.Feature}</p>
 */
public enum Experiments implements Experiment {

    ITEM_COMPONENT("item_component", LifeCycle.EXPERIMENTAL, "item(_| )component"),
    ;

    private final String codeName;
    private final LifeCycle phase;
    private final SkriptPattern compiledPattern;

    Experiments(String codeName, LifeCycle phase, String... patterns) {
        this.codeName = codeName;
        this.phase = phase;
        this.compiledPattern = switch (patterns.length) {
            case 0 -> PatternCompiler.compile(codeName);
            case 1 -> PatternCompiler.compile(patterns[0]);
            default -> PatternCompiler.compile('(' + String.join("|", patterns) + ')');
        };
    }

    public static void init(SkriptAddon addon) {
        ExperimentRegistry experiments = Skript.experiments();
        for (Experiments value : values()) {
            experiments.register(addon, value);
        }
    }

    @Override
    public String codeName() {
        return this.codeName;
    }

    @Override
    public LifeCycle phase() {
        return this.phase;
    }

    @Override
    public SkriptPattern pattern() {
        return this.compiledPattern;
    }

}
