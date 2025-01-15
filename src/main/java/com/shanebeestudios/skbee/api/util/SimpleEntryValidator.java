package com.shanebeestudios.skbee.api.util;

import org.skriptlang.skript.lang.entry.EntryValidator;
import org.skriptlang.skript.lang.entry.util.ExpressionEntryData;

public class SimpleEntryValidator {

    public static SimpleEntryValidator builder() {
        return new SimpleEntryValidator();
    }

    private final EntryValidator.EntryValidatorBuilder builder;

    private SimpleEntryValidator() {
        this.builder = EntryValidator.builder();
    }

    public SimpleEntryValidator addOptionalEntry(String key, Class<?> returnType) {
        this.builder.addEntryData(new ExpressionEntryData<>(key, null, true, returnType));
        return this;
    }

    @SafeVarargs
    public final SimpleEntryValidator addOptionalEntry(String key, Class<Object>... returnType) {
        this.builder.addEntryData(new ExpressionEntryData<>(key, null, true, returnType));
        return this;
    }

    public SimpleEntryValidator addRequiredEntry(String key, Class<?> returnType) {
        this.builder.addEntryData(new ExpressionEntryData<>(key, null, false, returnType));
        return this;
    }

    @SafeVarargs
    public final SimpleEntryValidator addRequiredEntry(String key, Class<Object>... returnType) {
        this.builder.addEntryData(new ExpressionEntryData<>(key, null, false, returnType));
        return this;
    }

    public SimpleEntryValidator addOptionalSection(String key) {
        this.builder.addSection(key, true);
        return this;
    }

    public SimpleEntryValidator addRequiredSection(String key) {
        this.builder.addSection(key, false);
        return this;
    }

    public EntryValidator build() {
        return this.builder.build();
    }

}
