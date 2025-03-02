package com.shanebeestudios.skbee.api.property;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.util.coll.CollectionUtils;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class Property<F, T> {

    String name;
    private String[] description;
    private String since;
    private String[] examples;
    private final Class<F> fromType;
    private final Class<T> toType;
    private Boolean canSet, canAdd, canRemove, canDelete;

    public Property(Class<F> fromType, Class<T> toType) {
        this.fromType = fromType;
        this.toType = toType;
        // Initialize changers
        getChangeModes();
    }

    public Property<F, T> description(String... description) {
        this.description = description;
        return this;
    }

    public String getDescription() {
        return String.join("    \n", description);
    }

    public Property<F, T> since(String since) {
        this.since = since;
        return this;
    }

    public String getSince() {
        return this.since;
    }

    public Property<F, T> examples(String... examples) {
        this.examples = examples;
        return this;
    }

    public String[] getExamples() {
        return this.examples;
    }

    public String getName() {
        return this.name;
    }

    public Class<F> getFromType() {
        return this.fromType;
    }

    public Class<T> getReturnType() {
        return this.toType;
    }

    public boolean isArray() {
        return this.toType.isArray();
    }

    public abstract T get(F object);

    public void set(F object, T value) {
    }

    public void add(F object, T value) {
    }

    public void remove(F object, T value) {
    }

    public void delete(F object) {
    }

    @SuppressWarnings("ConstantValue")
    public String getChangeModes() {
        List<String> modes = new ArrayList<>();
        for (ChangeMode mode : ChangeMode.values()) {
            if (mode == ChangeMode.SET) {
                if (this.canSet == null) {
                    try {
                        this.canSet = this.getClass().getDeclaredMethod("set", Object.class, Object.class) != null;
                    } catch (NoSuchMethodException ignore) {
                        this.canSet = false;
                    }
                }
                if (this.canSet) {
                    modes.add("set");
                }
            } else if (mode == ChangeMode.ADD) {
                if (this.canAdd == null) {
                    try {
                        this.canAdd = this.getClass().getDeclaredMethod("add", Object.class, Object.class) != null;
                    } catch (NoSuchMethodException ignore) {
                        this.canAdd = false;
                    }
                }
                if (this.canAdd) {
                    modes.add("add");
                }
            } else if (mode == ChangeMode.REMOVE) {
                if (this.canRemove == null) {
                    try {
                        this.canRemove = this.getClass().getDeclaredMethod("remove", Object.class, Object.class) != null;
                    } catch (NoSuchMethodException ignore) {
                        this.canRemove = false;
                    }
                }
                if (this.canRemove) {
                    modes.add("remove");
                }
            } else if (mode == ChangeMode.DELETE) {
                if (this.canDelete == null) {
                    try {
                        this.canDelete = this.getClass().getDeclaredMethod("delete", Object.class) != null;
                    } catch (NoSuchMethodException ignore) {
                        this.canDelete = false;
                    }
                }
                if (this.canDelete) {
                    modes.add("delete/clear");
                }
            }
        }
        if (modes.isEmpty()) return "*Cannot be changed*";
        return String.join(", ", modes);
    }

    public Class<T> @Nullable [] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET && this.canSet) {
            return CollectionUtils.array(this.toType);
        } else if (mode == ChangeMode.ADD && this.canAdd) {
            return CollectionUtils.array(this.toType);
        } else if (mode == ChangeMode.REMOVE && this.canRemove) {
            return CollectionUtils.array(this.toType);
        } else if (mode == ChangeMode.DELETE && this.canDelete) {
            return CollectionUtils.array();
        }
        return null;
    }

}
