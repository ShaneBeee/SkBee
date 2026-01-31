package com.shanebeestudios.skbee.api.wrapper;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.util.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.comparator.Comparators;
import org.skriptlang.skript.lang.comparator.Relation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * Wrapper class for wrapping Enums to be used in Skript
 * <p>
 * This class is copied from Skript, with the language node stripped out and other changes
 * <a href="https://github.com/SkriptLang/Skript/blob/master/src/main/java/ch/njol/skript/util/EnumUtils.java">EnumUtils</a>
 * </p>
 *
 * @author Peter Güttinger (original)
 */
public final class EnumWrapper<E extends Enum<E>> {

    private final Class<E> enumClass;
    private final String[] names;
    private final HashMap<String, E> parseMap = new HashMap<>();

    /**
     * Create a new EnumWrapper.
     *
     * @param enumClass Enum class
     * @param prefix    Optional prefix to prepend to names
     * @param suffix    Optional suffix to append to names
     * @param plurals   Whether to automatically include plurals
     */
    public EnumWrapper(@NotNull Class<E> enumClass, @Nullable String prefix, @Nullable String suffix, boolean plurals) {
        assert enumClass.isEnum();
        this.enumClass = enumClass;
        this.names = new String[enumClass.getEnumConstants().length];

        for (E enumConstant : enumClass.getEnumConstants()) {
            String name = enumConstant.name().toLowerCase(Locale.ROOT);
            String namePlural = plurals ? name + "s" : null;
            if (prefix != null && !name.startsWith(prefix))
                name = prefix + "_" + name;
            if (suffix != null && !name.endsWith(suffix))
                name = name + "_" + suffix;
            parseMap.put(name, enumConstant);
            if (namePlural != null) {
                parseMap.put(namePlural, enumConstant);
            }
            names[enumConstant.ordinal()] = name;
        }
        registerComparator(enumClass);
    }

    public EnumWrapper(@NotNull Class<E> c, @Nullable String prefix, @Nullable String suffix) {
        this(c, prefix, suffix, false);
    }

    public EnumWrapper(@NotNull Class<E> c, boolean plurals) {
        this(c, null, null, plurals);
    }

    public EnumWrapper(@NotNull Class<E> c) {
        this(c, null, null, false);
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

    private String getAllNames() {
        List<String> names = new ArrayList<>();
        Collections.addAll(names, this.names);
        Collections.sort(names);
        return StringUtils.join(names, ", ");
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

    /**
     * Create ClassInfo with default parser and usage
     *
     * @param codeName Name for class info
     * @return ClassInfo with default parser and usage
     */
    public @NotNull ClassInfo<E> getClassInfo(String codeName) {
        return new ClassInfo<>(this.enumClass, codeName).usage(getAllNames()).parser(new EnumParser<>(this))
            .supplier(this.enumClass.getEnumConstants());
    }

    /**
     * Create ClassInfo with default parser and usage
     *
     * @param codeName Name for class info
     * @param consumer Consumer to modify the classinfo before returning
     * @return ClassInfo with default parser and usage
     */
    public @NotNull ClassInfo<E> getClassInfo(String codeName, Consumer<ClassInfo<E>> consumer) {
        ClassInfo<E> classInfo = new ClassInfo<>(this.enumClass, codeName);
        consumer.accept(classInfo);
        if (classInfo.getUsage() == null) {
            classInfo.usage(getAllNames());
        }
        if (classInfo.getParser() == null) {
            classInfo.parser(new EnumParser<>(this));
        }
        return classInfo;
    }

    private void registerComparator(Class<E> c) {
        if (Comparators.exactComparatorExists(c, c)) return;
        Comparators.registerComparator(c, c, (o1, o2) -> Relation.get(o1.equals(o2)));
    }

}
