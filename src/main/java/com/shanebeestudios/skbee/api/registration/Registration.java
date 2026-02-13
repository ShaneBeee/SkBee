package com.shanebeestudios.skbee.api.registration;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.Cloner;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.classes.Serializer;
import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.conditions.base.PropertyCondition.PropertyType;
import ch.njol.skript.expressions.base.EventValueExpression;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.DefaultExpression;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.Section;
import ch.njol.skript.lang.SkriptEvent;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.Keyed;
import org.bukkit.Registry;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.structure.Structure;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.util.Priority;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

@SuppressWarnings({"rawtypes", "unchecked"})
public class Registration {

    private final List<TypeRegistrar<?>> types = new ArrayList<>();
    private final List<EffectRegistrar> effects = new ArrayList<>();
    private final List<ConditionRegistrar> conditions = new ArrayList<>();
    private final List<EventRegistrar> events = new ArrayList<>();
    private final List<SectionRegistrar> sections = new ArrayList<>();
    private final List<ExpressionRegistrar> expressions = new ArrayList<>();
    private final List<StructureRegistrar<?>> structures = new ArrayList<>();

    public List<TypeRegistrar<?>> getTypes() {
        return this.types;
    }

    public List<EffectRegistrar> getEffects() {
        return this.effects;
    }

    public List<ConditionRegistrar> getConditions() {
        return this.conditions;
    }

    public List<EventRegistrar> getEvents() {
        return this.events;
    }

    public List<SectionRegistrar> getSections() {
        return this.sections;
    }

    public List<ExpressionRegistrar> getExpressions() {
        return this.expressions;
    }

    public List<StructureRegistrar<?>> getStructures() {
        return this.structures;
    }

    @SuppressWarnings("unchecked")
    public static class Registrar<T extends Registrar<T>> {
        private final Documentation documentation = new Documentation();
        private boolean registered;

        public T noDoc() {
            this.documentation.setNoDoc(true);
            return (T) this;
        }

        public T name(String name) {
            this.documentation.setName(name);
            return (T) this;
        }

        public T description(String... description) {
            this.documentation.setDescription(description);
            return (T) this;
        }

        public T examples(String... examples) {
            this.documentation.setExamples(examples);
            return (T) this;
        }

        public T keywords(String... keywords) {
            this.documentation.setKeywords(keywords);
            return (T) this;
        }

        public T since(String... since) {
            this.documentation.setSince(since);
            return (T) this;
        }

        public Documentation getDocumentation() {
            return this.documentation;
        }

        public boolean isRegistered() {
            return this.registered;
        }

        public void register() {
            if (this.registered) {
                Util.skriptError("Syntax '%s' is already registered!", this.documentation.getName());
                return;
            }
            this.registered = true;
        }
    }

    public class TypeRegistrar<T> extends Registrar<TypeRegistrar<T>> {
        public final Class<T> type;
        public final String codename;
        public String[] user;
        public String[] after;
        public String[] before;
        public String usage;
        public DefaultExpression<T> defaultExpression;
        public @Nullable Supplier<Iterator<T>> supplier;
        public Parser<? extends T> parser;
        public Serializer<? super T> serializer;
        public Cloner<T> cloner;
        public Changer<? super T> changer;

        public TypeRegistrar(Class<T> type, String codename) {
            this.type = type;
            this.codename = codename;
        }

        public TypeRegistrar<T> user(String... user) {
            this.user = user;
            return this;
        }

        public TypeRegistrar<T> after(String... after) {
            this.after = after;
            return this;
        }

        public TypeRegistrar<T> before(String... before) {
            this.before = before;
            return this;
        }

        public TypeRegistrar<T> usage(String usage) {
            this.usage = usage;
            return this;
        }

        public TypeRegistrar<T> defaultExpression(DefaultExpression<T> defaultExpression) {
            this.defaultExpression = defaultExpression;
            return this;
        }

        public TypeRegistrar<T> supplier(Supplier<Iterator<T>> supplier) {
            this.supplier = supplier;
            return this;
        }

        public TypeRegistrar<T> parser(Parser<? extends T> parser) {
            this.parser = parser;
            return this;
        }

        public TypeRegistrar<T> serializer(Serializer<? super T> serializer) {
            this.serializer = serializer;
            return this;
        }

        public TypeRegistrar<T> cloner(Cloner<T> cloner) {
            this.cloner = cloner;
            return this;
        }

        public TypeRegistrar<T> changer(Changer<? super T> changer) {
            this.changer = changer;
            return this;
        }

        public void register() {
            super.register();
            Registration.this.types.add(this);
        }
    }

    public <T> TypeRegistrar<T> newType(Class<T> type, String codename) {
        return new TypeRegistrar<>(type, codename);
    }

    public class EnumTypeRegistrar<T extends Enum<T>> extends TypeRegistrar<T> {
        public final String prefix;
        public final String suffix;

        public EnumTypeRegistrar(Class<T> type, String codename, String prefix, String suffix) {
            super(type, codename);
            this.prefix = prefix;
            this.suffix = suffix;
        }
    }

    public <T extends Enum<T>> EnumTypeRegistrar<T> newEnumType(Class<T> type, String codename) {
        return new EnumTypeRegistrar<>(type, codename, null, null);
    }

    public <T extends Enum<T>> EnumTypeRegistrar<T> newEnumType(Class<T> type, String codename, String prefix, String suffix) {
        return new EnumTypeRegistrar<>(type, codename, prefix, suffix);
    }

    public class RegistryTypeRegistrar<T extends Keyed> extends TypeRegistrar<T> {
        public final Registry<T> registry;
        public final String prefix;
        public final String suffix;
        public final boolean createUsage;

        public RegistryTypeRegistrar(Registry<T> registry, Class<T> type, String codename, boolean createUsage, String prefix, String suffix) {
            super(type, codename);
            this.registry = registry;
            this.prefix = prefix;
            this.suffix = suffix;
            this.createUsage = createUsage;
        }
    }

    public <T extends Keyed> RegistryTypeRegistrar<T> newRegistryType(Registry<T> registry, Class<T> type, String codename) {
        return new RegistryTypeRegistrar<>(registry, type, codename, true, null, null);
    }

    public <T extends Keyed> RegistryTypeRegistrar<T> newRegistryType(Registry<T> registry, Class<T> type, String codename, String prefix, String suffix) {
        return new RegistryTypeRegistrar<>(registry, type, codename, true, prefix, suffix);
    }

    public class EffectRegistrar extends Registrar<EffectRegistrar> {
        public final Class<? extends Effect> effect;
        public final String[] patterns;

        public EffectRegistrar(Class<? extends Effect> effect, String[] patterns) {
            this.effect = effect;
            this.patterns = patterns;
        }

        public void register() {
            super.register();
            Registration.this.effects.add(this);
        }
    }

    public EffectRegistrar newEffect(Class<? extends Effect> effect, String... patterns) {
        return new EffectRegistrar(effect, patterns);
    }

    public class SectionRegistrar extends Registrar<SectionRegistrar> {
        public final Class<? extends Section> section;
        public final String[] patterns;

        public SectionRegistrar(Class<? extends Section> section, String[] patterns) {
            this.section = section;
            this.patterns = patterns;
        }

        public void register() {
            super.register();
            Registration.this.sections.add(this);
        }
    }

    public SectionRegistrar newSection(Class<? extends Section> section, String... patterns) {
        return new SectionRegistrar(section, patterns);
    }

    public class ConditionRegistrar extends Registrar<ConditionRegistrar> {
        public final Class<? extends Condition> condition;
        public final String[] patterns;

        public ConditionRegistrar(Class<? extends Condition> condition, String[] patterns) {
            this.condition = condition;
            this.patterns = patterns;
        }

        public void register() {
            super.register();
            Registration.this.conditions.add(this);
        }
    }

    public ConditionRegistrar newCondition(Class<? extends Condition> condition, String... patterns) {
        return new ConditionRegistrar(condition, patterns);
    }

    public class PropertyConditionRegistrar extends ConditionRegistrar {
        public PropertyConditionRegistrar(Class<? extends Condition> condition, PropertyType type, String property, String owner) {
            super(condition, PropertyCondition.getPatterns(type, property, owner));
        }
    }

    public PropertyConditionRegistrar newPropertyCondition(Class<? extends Condition> condition, PropertyType type, String property, String owner) {
        return new PropertyConditionRegistrar(condition, type, property, owner);
    }

    public PropertyConditionRegistrar newPropertyCondition(Class<? extends Condition> condition, String property, String owner) {
        return new PropertyConditionRegistrar(condition, PropertyType.BE, property, owner);
    }

    public class EventRegistrar extends Registrar<EventRegistrar> {
        public final Class<? extends SkriptEvent> skriptEventClass;
        public final Class<? extends Event>[] eventClasses;
        public final String[] patterns;

        public EventRegistrar(Class<? extends SkriptEvent> skriptEventClass, Class<? extends Event>[] eventClass, String[] patterns) {
            this.skriptEventClass = skriptEventClass;
            this.eventClasses = eventClass;
            this.patterns = patterns;
        }

        public void register() {
            super.register();
            Registration.this.events.add(this);
        }
    }

    public EventRegistrar newEvent(Class<? extends SkriptEvent> skriptEventClass, Class<? extends Event> eventClass, String... patterns) {
        return new EventRegistrar(skriptEventClass, new Class[]{eventClass}, patterns);
    }

    public EventRegistrar newEvent(Class<? extends SkriptEvent> skriptEventClass, Class<? extends Event>[] eventClasses, String... patterns) {
        return new EventRegistrar(skriptEventClass, eventClasses, patterns);
    }

    public class ExpressionRegistrar<T, E extends Expression<T>> extends Registrar<ExpressionRegistrar<T, E>> {
        public final Class<E> expressionClass;
        public final Class<T> returnType;
        public final Priority priority;
        public final String[] patterns;

        public ExpressionRegistrar(Class<E> expressionClass, Class<T> returnType, Priority priority, String[] patterns) {
            this.expressionClass = expressionClass;
            this.returnType = returnType;
            this.priority = priority;
            this.patterns = patterns;
        }

        public void register() {
            super.register();
            Registration.this.expressions.add(this);
        }
    }

    public ExpressionRegistrar<?,?> newExpression(Class<? extends Expression<?>> expressionClass, Class<?> returnType, Priority priority, String... patterns) {
        return new ExpressionRegistrar(expressionClass, returnType, priority, patterns);
    }

    public ExpressionRegistrar<?,?> newSimpleExpression(Class<? extends Expression<?>> expressionClass, Class<?> returnType, String... patterns) {
        return new ExpressionRegistrar(expressionClass, returnType, SyntaxInfo.SIMPLE, patterns);
    }

    public ExpressionRegistrar<?,?> newEventExpression(Class<? extends Expression<?>> expressionClass, Class<?> returnType, String... patterns) {
        return new ExpressionRegistrar(expressionClass, returnType, EventValueExpression.DEFAULT_PRIORITY, patterns);
    }

    public ExpressionRegistrar<?,?> newCombinedExpression(Class<? extends Expression<?>> expressionClass, Class<?> returnType, String... patterns) {
        return new ExpressionRegistrar(expressionClass, returnType, SyntaxInfo.COMBINED, patterns);
    }

    public ExpressionRegistrar newPropertyExpression(Class<? extends Expression<?>> expressionClass, Class<?> returnType, String property, String owner) {
        return new ExpressionRegistrar(expressionClass, returnType, SyntaxInfo.SIMPLE,
            SimplePropertyExpression.getPatterns(property, owner));
    }

    public class StructureRegistrar<E extends Structure> extends Registrar<StructureRegistrar<E>> {
        public final Class<E> structureClass;
        public final String[] patterns;

        public StructureRegistrar(Class<E> structureClass, String[] patterns) {
            this.structureClass = structureClass;
            this.patterns = patterns;
        }

        public void register() {
            super.register();
            Registration.this.structures.add(this);
        }
    }

    public StructureRegistrar<?> newStructure(Class<? extends Structure> structureClass, String... patterns) {
        return new StructureRegistrar<>(structureClass, patterns);
    }

}
