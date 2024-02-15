package com.shanebeestudios.skbee.api.property;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.entity.EntityData;
import ch.njol.skript.registrations.Classes;
import ch.njol.util.StringUtils;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"ClassEscapesDefinedScope", "UnusedReturnValue"})
public class Property<P, T> {

    static abstract class Getter<O, R> {
        @Nullable
        public abstract R get(O object);
    }

    static abstract class Setter<O, R> {
        public abstract void set(O object, R value);
    }

    static abstract class Deleter<O, R> {
        public abstract void delete(O object);
    }

    static abstract class Adder<O, R> {
        public abstract void add(O object, R value);
    }

    static abstract class Remover<O, R> {
        public abstract void remove(O object, R value);
    }

    static abstract class Resetter<O, R> {
        public abstract void reset(O object);
    }

    private final Class<P> propertyClass;
    private final List<Class<?>> propertyClasses = new ArrayList<>();
    private final Class<T> returnType;
    private final String propertyName;
    private String description;
    private String usableOn;
    private boolean isSingle = true;

    private Getter<P, T> getter;
    private Setter<P, T> setter;
    private Deleter<P, T> deleter;
    private Adder<P, T> adder;
    private Remover<P, T> remover;
    private Resetter<P, T> resetter;

    public Property(Class<P> propertyClass, Class<T> returnType, String propertyName) {
        this.propertyClass = propertyClass;
        this.returnType = returnType;
        this.propertyName = propertyName;
    }

    void addPorperties(List<Class<?>> classes) {
        this.propertyClasses.addAll(classes);
    }

    /**
     * Register getter for this property
     *
     * @param getter Getter to register
     * @return self
     */
    public Property<P, T> getter(Getter<P, T> getter) {
        this.getter = getter;
        return this;
    }

    /**
     * Register a setter for this property
     *
     * @param setter Setter to register
     * @return self
     */
    public Property<P, T> setter(Setter<P, T> setter) {
        this.setter = setter;
        return this;
    }

    /**
     * Register a deleter for this property
     *
     * @param deleter Deleter to register
     * @return self
     */
    public Property<P, T> deleter(Deleter<P, T> deleter) {
        this.deleter = deleter;
        return this;
    }

    /**
     * Register an adder for this property
     *
     * @param adder Deleter to register
     * @return self
     */
    public Property<P, T> adder(Adder<P, T> adder) {
        this.adder = adder;
        return this;
    }

    /**
     * Register a remover for this property
     *
     * @param remover Deleter to register
     * @return self
     */
    public Property<P, T> remover(Remover<P, T> remover) {
        this.remover = remover;
        return this;
    }

    /**
     * Register a resetter for this property
     *
     * @param resetter Deleter to register
     * @return self
     */
    public Property<P, T> resetter(Resetter<P, T> resetter) {
        this.resetter = resetter;
        return this;
    }

    /**
     * A description for this property
     *
     * @param description Description
     * @return self
     */
    public Property<P, T> description(String description) {
        this.description = description;
        return this;
    }

    /**
     * Create an override of what this property can be used on
     * <p>This is strictly for doc use</p>
     *
     * @param usableOn What this property can be used on
     * @return self
     */
    public Property<P, T> usableOn(String usableOn) {
        this.usableOn = usableOn;
        return this;
    }

    /**
     * Get the class this property can be used on
     *
     * @return Class this property can be used on
     */
    public Class<P> getPropertyClass() {
        return propertyClass;
    }

    public boolean canBeUsedOn(Class<?> objectClass) {
        // Due to expressions like `last spawned entity` returning Entity.class,
        // we want to make sure the property will still parse for subclasses
        // #checkUsable() will actually ignore other classes at runtime
        if (this.propertyClasses.isEmpty()) {
            return objectClass == Entity.class || this.propertyClass.isAssignableFrom(objectClass);
        }
        for (Class<?> aClass : this.propertyClasses) {
            if (objectClass == Entity.class) return true;
            if (aClass.isAssignableFrom(objectClass)) return true;
        }
        return false;
    }

    private boolean checkUsable(Class<?> objectClass) {
        if (this.propertyClasses.isEmpty()) {
            return this.propertyClass.isAssignableFrom(objectClass);
        }
        for (Class<?> aClass : this.propertyClasses) {
            if (aClass.isAssignableFrom(objectClass)) return true;
        }
        return false;
    }

    /**
     * Get the class this property returns
     *
     * @return Class this property returns
     */
    public Class<T> getReturnType() {
        return this.returnType;
    }

    /**
     * Get the name of this property
     *
     * @return Name of this property
     */
    public String getPropertyName() {
        return propertyName;
    }

    /**
     * Stringified version of objects this property may be used on
     * <p>Used for docs and error messages</p>
     *
     * @return Objects this property may be used on
     */
    public String getUsedOn() {
        String usedOn;
        if (this.usableOn != null) {
            usedOn = this.usableOn;
        } else if (this.propertyClasses.isEmpty()) {
            usedOn = getClassInfoName(this.propertyClass);
        } else {
            List<String> names = new ArrayList<>();
            this.propertyClasses.forEach(pClass -> names.add(getClassInfoName(pClass)));
            usedOn = StringUtils.join(names, "/");
        }
        return usedOn;
    }

    @SuppressWarnings("unchecked")
    private String getClassInfoName(Class<?> clazz) {
        if (Entity.class.isAssignableFrom(clazz)) {
            return EntityData.toString((Class<? extends Entity>) clazz);
        } else {
            return Classes.getSuperClassInfo(clazz).getName().toString();
        }
    }

    public boolean isSingle() {
        return this.isSingle;
    }

    public String getDescription() {
        return description;
    }

    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        return switch (mode) {
            case ADD -> this.adder != null ? CollectionUtils.array(this.returnType) : null;
            case SET -> this.setter != null ? CollectionUtils.array(this.returnType) : null;
            case REMOVE -> this.remover != null ? CollectionUtils.array(this.returnType) : null;
            case DELETE -> this.deleter != null ? CollectionUtils.array() : null;
            case RESET -> this.resetter != null ? CollectionUtils.array() : null;
            default -> null;
        };
    }

    public void change(ChangeMode mode, Object object, Object value) {
        switch (mode) {
            case SET -> set(object, value);
            case DELETE -> delete(object);
            case ADD -> add(object, value);
            case REMOVE -> remove(object, value);
            case RESET -> reset(object);
        }
    }

    public Property<P, T> isSingle(boolean isSingle) {
        this.isSingle = isSingle;
        return this;
    }

    @SuppressWarnings("unchecked")
    public @Nullable T get(Object object) {
        if (checkUsable(object.getClass())) {
            return this.getter.get((P) object);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public void set(Object object, Object value) {
        if (checkUsable(object.getClass()) && returnType.isAssignableFrom(value.getClass())) {
            this.setter.set((P) object, (T) value);
        }
    }

    @SuppressWarnings("unchecked")
    public void delete(Object object) {
        if (checkUsable(object.getClass())) {
            this.deleter.delete((P) object);
        }
    }

    @SuppressWarnings("unchecked")
    public void add(Object object, Object value) {
        if (checkUsable(object.getClass()) && returnType.isAssignableFrom(value.getClass())) {
            this.adder.add((P) object, (T) value);
        }
    }

    @SuppressWarnings("unchecked")
    public void remove(Object object, Object value) {
        if (checkUsable(object.getClass()) && returnType.isAssignableFrom(value.getClass())) {
            this.remover.remove((P) object, (T) value);
        }
    }

    @SuppressWarnings("unchecked")
    public void reset(Object object) {
        if (checkUsable(object.getClass())) {
            this.resetter.reset((P) object);
        }
    }

}
