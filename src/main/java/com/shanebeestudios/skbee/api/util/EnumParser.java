package com.shanebeestudios.skbee.api.util;

import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import org.jetbrains.annotations.Nullable;

public class EnumParser<T extends Enum<T>> extends Parser<T> {

    EnumUtils<T> enumUtils;

    public EnumParser(EnumUtils<T> enumUtils) {
        this.enumUtils = enumUtils;
    }

    @Nullable
    @Override
    public T parse(String s, ParseContext context) {
        return enumUtils.parse(s);
    }

    @Override
    public String toString(T o, int flags) {
        return enumUtils.toString(o, flags);
    }

    @Override
    public String toVariableNameString(T o) {
        return toString(o, 0);
    }

    @Override
    public String getVariableNamePattern() {
        return "\\S+";
    }

}
