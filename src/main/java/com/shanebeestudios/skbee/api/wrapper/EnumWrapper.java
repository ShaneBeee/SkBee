package com.shanebeestudios.skbee.api.wrapper;

import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.util.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Wrapper class for wrapping Enums to be used in Skript
 * <p>
 * This class is copied from Skript, with the language node stripped out and other changes
 * <a href="https://github.com/SkriptDev/Skript/blob/master/src/main/java/ch/njol/skript/util/EnumUtils.java">EnumUtils</a>
 * </p>
 *
 * @author Peter Güttinger (original)
 */
public final class EnumWrapper<E extends Enum<E>> {

    private final String[] names;
    private final HashMap<String, E> parseMap = new HashMap<>();

    public EnumWrapper(@NotNull Class<E> c) {
        assert c.isEnum();
        this.names = new String[c.getEnumConstants().length];

        for (E enumConstant : c.getEnumConstants()) {
            String name = enumConstant.name().toLowerCase(Locale.ROOT);
            parseMap.put(name, enumConstant);
            names[enumConstant.ordinal()] = name;
        }
    }

    public EnumWrapper(@NotNull Class<E> c, @Nullable String prefix, @Nullable String suffix) {
        assert c.isEnum();
        this.names = new String[c.getEnumConstants().length];

        for (E enumConstant : c.getEnumConstants()) {
            String name = enumConstant.name().toLowerCase(Locale.ROOT);
            if (prefix != null && !name.startsWith(prefix))
                name = prefix + "_" + name;
            if (suffix != null && !name.endsWith(suffix))
                name = name + "_" + suffix;
            parseMap.put(name, enumConstant);
            names[enumConstant.ordinal()] = name;
        }
    }

    @Nullable
    public E parse(final String s) {
        return parseMap.get(s.toLowerCase(Locale.ROOT).replace(" ", "_"));
    }

    /**
     * Replace a specific key with another
     * <br>Useful to prevent conflicts
     *
     * @param toReplace   Key to replace
     * @param replacement New replacement key
     */
    public void replace(String toReplace, String replacement) {
        if (parseMap.containsKey(toReplace)) {
            E e = parseMap.get(toReplace);
            replacement = replacement.replace(" ", "_");
            parseMap.put(replacement, e);
            parseMap.remove(toReplace);
            names[e.ordinal()] = replacement;
        }
    }

    @SuppressWarnings("unused")
    public String toString(final E e, final int flags) {
        return names[e.ordinal()];
    }

    public String getAllNames() {
        List<String> names = new ArrayList<>();
        Collections.addAll(names, this.names);
        Collections.sort(names);
        return StringUtils.join(names, ", ");
    }

    /**
     * Get an instance of the {@link EnumParser} for this Enum
     *
     * @return EnumParser for this Enum
     */
    public EnumParser<E> getParser() {
        return new EnumParser<>(this);
    }

    static class EnumParser<T extends Enum<T>> extends Parser<T> {

        private final EnumWrapper<T> enumWrapper;

        public EnumParser(EnumWrapper<T> enumWrapper) {
            this.enumWrapper = enumWrapper;
        }

        @Nullable
        @Override
        public T parse(@NotNull String s, @NotNull ParseContext context) {
            return enumWrapper.parse(s);
        }

        @Override
        public @NotNull String toString(T o, int flags) {
            return enumWrapper.toString(o, flags);
        }

        @Override
        public @NotNull String toVariableNameString(T o) {
            return toString(o, 0);
        }

    }

}
