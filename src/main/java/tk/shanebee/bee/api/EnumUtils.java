/**
 *   This file is part of Skript.
 *
 *  Skript is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Skript is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Skript.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright Peter Güttinger, SkriptLang team and contributors
 */
package tk.shanebee.bee.api;

import ch.njol.util.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

// This class is copied/derived from Skript's enum utils
// Adjustments have been made to not rely on Skript's lang system
public class EnumUtils<E extends Enum<E>> {

    private final Class<E> c;
    private String[] names;
    private final Map<String, E> parseMap = new HashMap<>();

    public EnumUtils(@NotNull Class<E> enumClass) {
        assert enumClass.isEnum();
        this.c = enumClass;
        this.names = new String[enumClass.getEnumConstants().length];

        for (E enumConstant : enumClass.getEnumConstants()) {
            String name = enumConstant.name().toLowerCase(Locale.ROOT).replace("_", " ");
            parseMap.put(name, enumConstant);
            names[enumConstant.ordinal()] = name;
        }
    }

    public EnumUtils(@NotNull Class<E> enumClass, @Nullable String prefix, @Nullable String suffix) {
        assert enumClass.isEnum();
        this.c = enumClass;
        this.names = new String[enumClass.getEnumConstants().length];

        for (E enumConstant : enumClass.getEnumConstants()) {
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
    final void validate(final boolean force) {
        boolean update = force;

        final int newL = c.getEnumConstants().length;
        if (newL > names.length) {
            names = new String[newL];
            update = true;
        }

        if (update) {
            parseMap.clear();
            for (final E e : c.getEnumConstants()) {
                String name = e.name().toLowerCase(Locale.ROOT).replace("_", " ");
                parseMap.put(name, e);
                names[e.ordinal()] = name;
            }
        }
    }

    @Nullable
    public final E parse(final String s) {
        validate(false);
        return parseMap.get(s.toLowerCase(Locale.ROOT));
    }

    @SuppressWarnings({"null", "unused"})
    public final String toString(final E e, final int flags) {
        validate(false);
        return names[e.ordinal()];
    }

    public final String getAllNames() {
        validate(false);
        List<String> names = new ArrayList<>();
        Collections.addAll(names, this.names);
        Collections.sort(names);
        return StringUtils.join(names, ", ");
    }

}
