package tk.shanebee.bee.api.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.localization.Language;
import ch.njol.util.StringUtils;

/**
 * @author Peter Güttinger
 * This class is copied from Skript, and modified to allow for
 * not using language nodes
 * https://github.com/SkriptDev/Skript/blob/master/src/main/java/ch/njol/skript/util/EnumUtils.java
 */
public final class EnumUtils<E extends Enum<E>> {

    private final Class<E> c;
    @Nullable
    private final String languageNode;
    private String[] names;
    private final HashMap<String, E> parseMap = new HashMap<>();

    public EnumUtils(@NonNull final Class<E> c, @NonNull final String languageNode) {
        assert c.isEnum();
        assert !languageNode.isEmpty() && !languageNode.endsWith(".") : languageNode;

        this.c = c;
        this.languageNode = languageNode;
        this.names = new String[c.getEnumConstants().length];

        Language.addListener(() -> validate(true));
    }

    public EnumUtils(@NonNull Class<E> c) {
        assert c.isEnum();
        this.c = c;
        this.languageNode = null;
        this.names = new String[c.getEnumConstants().length];

        for (E enumConstant : c.getEnumConstants()) {
            String name = enumConstant.name().toLowerCase(Locale.ROOT).replace("_", " ");
            parseMap.put(name, enumConstant);
            names[enumConstant.ordinal()] = name;
        }
    }

    public EnumUtils(@NonNull Class<E> c, @Nullable String prefix, @Nullable String suffix) {
        assert c.isEnum();
        this.c = c;
        this.languageNode = null;
        this.names = new String[c.getEnumConstants().length];

        for (E enumConstant : c.getEnumConstants()) {
            String name = enumConstant.name().toLowerCase(Locale.ROOT).replace("_", " ");
            if (prefix != null && !name.startsWith(prefix))
                name = prefix + " " + name;
            if (suffix != null && !name.endsWith(suffix))
                name = name + " " + suffix;
            parseMap.put(name, enumConstant);
            names[enumConstant.ordinal()] = name;
        }
    }

    /**
     * Updates the names if the language has changed or the enum was modified (using reflection).
     */
    void validate(final boolean force) {
        boolean update = force;

        final int newL = c.getEnumConstants().length;
        if (newL > names.length) {
            names = new String[newL];
            update = true;
        }

        if (update) {
            parseMap.clear();
            for (final E e : c.getEnumConstants()) {
                if (languageNode != null) {
                    final String[] ls = Language.getList(languageNode + "." + e.name());
                    names[e.ordinal()] = ls[0];
                    for (final String l : ls)
                        parseMap.put(l.toLowerCase(), e);
                } else {
                    String name = e.name().toLowerCase(Locale.ROOT).replace("_", " ");
                    parseMap.put(name, e);
                    names[e.ordinal()] = name;
                }
            }
        }
    }

    @Nullable
    public final E parse(final String s) {
        validate(false);
        return parseMap.get(s.toLowerCase(Locale.ROOT));
    }

    @SuppressWarnings({"null", "unused"})
    public String toString(final E e, final int flags) {
        validate(false);
        return names[e.ordinal()];
    }

    public String getAllNames() {
        validate(false);
        List<String> names = new ArrayList<>();
        Collections.addAll(names, this.names);
        Collections.sort(names);
        return StringUtils.join(names, ", ");
    }

}
