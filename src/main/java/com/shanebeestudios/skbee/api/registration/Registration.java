package com.shanebeestudios.skbee.api.registration;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.ClassInfo;
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
import ch.njol.skript.lang.function.Functions;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.registrations.EventValues;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.Keyed;
import org.bukkit.Registry;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.addon.AddonModule;
import org.skriptlang.skript.addon.SkriptAddon;
import org.skriptlang.skript.bukkit.registration.BukkitSyntaxInfos;
import org.skriptlang.skript.common.function.DefaultFunction;
import org.skriptlang.skript.lang.converter.Converter;
import org.skriptlang.skript.lang.entry.EntryValidator;
import org.skriptlang.skript.lang.structure.Structure;
import org.skriptlang.skript.registration.DefaultSyntaxInfos;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;
import org.skriptlang.skript.util.Priority;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

@SuppressWarnings({"rawtypes", "unchecked"})
public class Registration {

    private final SkriptAddon addon;
    private final RegistrationAddonModule module;
    private final List<Registrar<?>> preRegistrations = new ArrayList<>();
    private final List<TypeRegistrar<?>> types = new ArrayList<>();
    private final List<EffectRegistrar> effects = new ArrayList<>();
    private final List<ConditionRegistrar> conditions = new ArrayList<>();
    private final List<EventRegistrar> events = new ArrayList<>();
    private final List<SectionRegistrar> sections = new ArrayList<>();
    private final List<ExpressionRegistrar> expressions = new ArrayList<>();
    private final List<StructureRegistrar<?>> structures = new ArrayList<>();
    private final List<FunctionRegistrar> functions = new ArrayList<>();
    private final List<EventValueRegistrar<?, ?>> eventValues = new ArrayList<>();

    public Registration(String name, boolean includeLang) {
        this.addon = Skript.instance().registerAddon(RegistrationAddonModule.class, name);
        if (includeLang) {
            this.addon.localizer().setSourceDirectories("lang", null);
        }
        this.module = new RegistrationAddonModule(name, this);
    }

    public SkriptAddon getAddon() {
        return this.addon;
    }

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

    public List<FunctionRegistrar> getFunctions() {
        return this.functions;
    }

    public List<EventValueRegistrar<?, ?>> getEventValues() {
        return this.eventValues;
    }

    @SuppressWarnings("unchecked")
    public class Registrar<T extends Registrar<T>> {
        private final Documentation documentation = new Documentation();
        private boolean registered;

        public Registrar() {
            Registration.this.preRegistrations.add(this);
        }

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
                skriptError("Syntax '%s' is already registered!", this.documentation.getName());
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
        public final @NotNull EnumWrapper<T> enumWrapper;
        public final ClassInfo<T> classInfo;

        public EnumTypeRegistrar(Class<T> type, String codename, String prefix, String suffix, boolean plurals) {
            super(type, codename);
            this.prefix = prefix;
            this.suffix = suffix;
            this.enumWrapper = new EnumWrapper<>(type, prefix, suffix, plurals);
            this.usage = this.enumWrapper.getAllNames();
            this.classInfo = this.enumWrapper.getClassInfo(codename);
        }

        public EnumTypeRegistrar(Class<T> type, @NotNull EnumWrapper<T> enumWrapper, String codename, String prefix, String suffix) {
            super(type, codename);
            this.prefix = prefix;
            this.suffix = suffix;
            this.enumWrapper = enumWrapper;
            this.usage = this.enumWrapper.getAllNames();
            this.classInfo = this.enumWrapper.getClassInfo(codename);
        }
    }

    public <T extends Enum<T>> EnumTypeRegistrar<T> newEnumType(Class<T> type, String codename) {
        return new EnumTypeRegistrar<>(type, codename, null, null, false);
    }

    public <T extends Enum<T>> EnumTypeRegistrar<T> newEnumType(Class<T> type, String codename, boolean plurals) {
        return new EnumTypeRegistrar<>(type, codename, null, null, plurals);
    }

    public <T extends Enum<T>> EnumTypeRegistrar<T> newEnumType(Class<T> type, String codename, String prefix, String suffix) {
        return new EnumTypeRegistrar<>(type, codename, prefix, suffix, false);
    }

    public <T extends Enum<T>> EnumTypeRegistrar<T> newEnumType(Class<T> type, String codename, String prefix, String suffix, boolean plurals) {
        return new EnumTypeRegistrar<>(type, codename, prefix, suffix, plurals);
    }

    public <T extends Enum<T>> EnumTypeRegistrar<T> newEnumType(Class<T> type, EnumWrapper<T> enumWrapper, String codename) {
        return new EnumTypeRegistrar<>(type, enumWrapper, codename, null, null);
    }

    public <T extends Enum<T>> EnumTypeRegistrar<T> newEnumType(Class<T> type, EnumWrapper<T> enumWrapper, String codename, String prefix, String suffix) {
        return new EnumTypeRegistrar<>(type, enumWrapper, codename, prefix, suffix);
    }

    public class RegistryTypeRegistrar<T extends Keyed> extends TypeRegistrar<T> {
        public final Registry<T> registry;
        public final String prefix;
        public final String suffix;
        public final boolean createUsage;
        public final ClassInfo<T> classInfo;

        public RegistryTypeRegistrar(Registry<T> registry, Class<T> type, String codename, boolean createUsage, String prefix, String suffix) {
            super(type, codename);
            this.registry = registry;
            this.prefix = prefix;
            this.suffix = suffix;
            this.createUsage = createUsage;

            this.classInfo = RegistryClassInfo.create(registry, type, createUsage, codename, prefix, suffix);
            @Nullable String[] classInfoUsage = this.classInfo.getUsage();
            if (classInfoUsage != null) {
                this.usage = String.join(", ", classInfoUsage);
            }
        }
    }

    public <T extends Keyed> RegistryTypeRegistrar<T> newRegistryType(Registry<T> registry, Class<T> type, String codename) {
        return new RegistryTypeRegistrar<>(registry, type, codename, true, null, null);
    }

    public <T extends Keyed> RegistryTypeRegistrar<T> newRegistryType(Registry<T> registry, Class<T> type, boolean createUsage, String codename) {
        return new RegistryTypeRegistrar<>(registry, type, codename, createUsage, null, null);
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
        public final @Nullable EntryValidator validator;

        public SectionRegistrar(Class<? extends Section> section, String[] patterns, @Nullable EntryValidator validator) {
            this.section = section;
            this.patterns = patterns;
            this.validator = validator;
        }

        public void register() {
            super.register();
            Registration.this.sections.add(this);
        }
    }

    public SectionRegistrar newSection(Class<? extends Section> section, String... patterns) {
        return new SectionRegistrar(section, patterns, null);
    }

    public SectionRegistrar newSection(Class<? extends Section> section, EntryValidator validator, String... patterns) {
        return new SectionRegistrar(section, patterns, validator);
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

    public <T, E extends Expression<T>> ExpressionRegistrar<T, E> newExpression(Class<E> expressionClass, Class<T> returnType, Priority priority, String... patterns) {
        return new ExpressionRegistrar(expressionClass, returnType, priority, patterns);
    }

    public <T, E extends Expression<T>> ExpressionRegistrar<T, E> newSimpleExpression(Class<E> expressionClass, Class<T> returnType, String... patterns) {
        return new ExpressionRegistrar(expressionClass, returnType, SyntaxInfo.SIMPLE, patterns);
    }

    public <T, E extends Expression<T>> ExpressionRegistrar<T, E> newEventExpression(Class<E> expressionClass, Class<T> returnType, String... patterns) {
        return new ExpressionRegistrar(expressionClass, returnType, EventValueExpression.DEFAULT_PRIORITY, patterns);
    }

    public <T, E extends Expression<T>> ExpressionRegistrar<T, E> newCombinedExpression(Class<E> expressionClass, Class<T> returnType, String... patterns) {
        return new ExpressionRegistrar(expressionClass, returnType, SyntaxInfo.COMBINED, patterns);
    }

    public <T, E extends Expression<T>> ExpressionRegistrar newPropertyExpression(Class<E> expressionClass, Class<T> returnType, String property, String owner) {
        return new ExpressionRegistrar(expressionClass, returnType, SyntaxInfo.SIMPLE,
            SimplePropertyExpression.getPatterns(property, owner));
    }

    public class StructureRegistrar<E extends Structure> extends Registrar<StructureRegistrar<E>> {
        public final Class<E> structureClass;
        public final String[] patterns;
        public final EntryValidator validator;

        public StructureRegistrar(Class<E> structureClass, EntryValidator validator, String[] patterns) {
            this.structureClass = structureClass;
            this.validator = validator;
            this.patterns = patterns;
        }

        public void register() {
            super.register();
            Registration.this.structures.add(this);
        }
    }

    public StructureRegistrar<?> newStructure(Class<? extends Structure> structureClass, String... patterns) {
        return new StructureRegistrar<>(structureClass, null, patterns);
    }

    public StructureRegistrar<?> newStructure(Class<? extends Structure> structureClass, EntryValidator entryValidator, String... patterns) {
        return new StructureRegistrar<>(structureClass, entryValidator, patterns);
    }

    public class FunctionRegistrar<T> extends Registrar<FunctionRegistrar<T>> {
        public final DefaultFunction<T> function;

        public FunctionRegistrar(DefaultFunction<T> function) {
            this.function = function;
        }

        @Override
        public void register() {
            super.register();
            Registration.this.functions.add(this);
        }
    }

    public <T> FunctionRegistrar<T> newFunction(DefaultFunction<T> function) {
        return new FunctionRegistrar<T>(function);
    }

    public class EventValueRegistrar<F extends Event, T> extends Registrar<EventValueRegistrar<F, T>> {
        public final Class<F> eventClass;
        public final Class<T> valueClass;
        public final Converter<F, T> converter;
        public int time;
        private Class<F>[] excludedEvents;
        String excludeErrorMessage;

        public EventValueRegistrar(Class<F> eventClass, Class<T> valueClass, Converter<F, T> converter) {
            this.eventClass = eventClass;
            this.valueClass = valueClass;
            this.converter = converter;
        }

        public EventValueRegistrar<F, T> time(int time) {
            this.time = time;
            return this;
        }

        @SafeVarargs
        public final EventValueRegistrar<F, T> excludes(String excludeErrorMessage, Class<F>... excludedEvents) {
            this.excludeErrorMessage = excludeErrorMessage;
            this.excludedEvents = excludedEvents;
            return this;
        }

        @Override
        public void register() {
            super.register();
            Registration.this.eventValues.add(this);
        }
    }

    public <F extends Event, T> EventValueRegistrar<F, T> newEventValue(Class<F> event, Class<T> value, Converter<F, T> converter) {
        return new EventValueRegistrar<>(event, value, converter);
    }

    public <F extends Event, T> EventValueRegistrar<F, T> newEventValue(Class<F> event, Class<T> value, Converter<F, T> converter, int time) {
        EventValueRegistrar<F, T> ftEventValueRegistrar = new EventValueRegistrar<>(event, value, converter);
        return ftEventValueRegistrar.time(time);
    }

    @Deprecated(forRemoval = true,since = "INSERT VERSION") // Use newEventValue() instead
    public <F extends Event, T> void registerEventValue(Class<F> event, Class<T> value, Converter<F, T> converter) {
        new EventValueRegistrar<>(event, value, converter).register();
    }

    @Deprecated(forRemoval = true,since = "INSERT VERSION") // Use newEventValue() instead
    public <F extends Event, T> void registerEventValue(Class<F> event, Class<T> value, Converter<F, T> converter, int time) {
        EventValueRegistrar<F, T> ftEventValueRegistrar = new EventValueRegistrar<>(event, value, converter);
        ftEventValueRegistrar.time(time).register();
    }

    public void finalizeRegistration() {
        this.addon.loadModules(this.module);
    }

    public void registerInit() {
        // CHECK REGISTRATION
        this.preRegistrations.forEach(registrar -> {
            if (!registrar.isRegistered()) {
                String name = registrar.documentation.getName();
                if (name == null) {
                    name = registrar.getClass().getSimpleName();
                    skriptError("Unnamed registrar in '%s' not registered!", name);
                } else {
                    skriptError("Registrar for '%s' not registered!", name);
                }
            }
        });
        this.preRegistrations.clear();

        // TYPES
        for (TypeRegistrar type : getTypes()) {
            ClassInfo<?> classInfo;
            if (type instanceof EnumTypeRegistrar<?> enumTypeRegistrar) {
                classInfo = enumTypeRegistrar.classInfo;
            } else if (type instanceof RegistryTypeRegistrar<? extends Keyed> registryTypeRegistrar) {
                classInfo = registryTypeRegistrar.classInfo;
            } else {
                classInfo = new ClassInfo<>(type.type, type.codename);
            }
            if (type.user != null) {
                classInfo.user(type.user);
            }
            if (type.usage != null && classInfo.getUsage() == null) {
                classInfo.usage(type.usage);
            }
            if (type.before != null) {
                classInfo.before(type.before);
            }
            if (type.after != null) {
                classInfo.after(type.after);
            }
            if (type.defaultExpression != null) {
                classInfo.defaultExpression(type.defaultExpression);
            }
            if (type.supplier != null) {
                classInfo.supplier(type.supplier);
            }
            if (type.parser != null) {
                classInfo.parser(type.parser);
            }
            if (type.serializer != null) {
                classInfo.serializer(type.serializer);
            }
            if (type.cloner != null) {
                classInfo.cloner(type.cloner);
            }
            if (type.changer != null) {
                classInfo.changer(type.changer);
            }
            Classes.registerClass(classInfo);
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    public void registerLoad() {
        SyntaxRegistry syntaxInfos = this.addon.syntaxRegistry();

        // STRUCTURES
        for (Registration.StructureRegistrar<?> structure : getStructures()) {

            DefaultSyntaxInfos.Structure<Structure> build = DefaultSyntaxInfos.Structure.builder(
                    (Class<Structure>) structure.structureClass)
                .addPatterns(structure.patterns)
                .entryValidator(structure.validator)
                .build();

            syntaxInfos.register(SyntaxRegistry.STRUCTURE, build);
        }
        // EVENTS
        for (Registration.EventRegistrar event : getEvents()) {
            BukkitSyntaxInfos.Event.Builder<? extends BukkitSyntaxInfos.Event.Builder<?, SkriptEvent>, SkriptEvent> builder = BukkitSyntaxInfos.Event.builder(
                (Class<SkriptEvent>) event.skriptEventClass,
                event.getDocumentation().getName());
            for (Class<? extends Event> eventClass : event.eventClasses) {
                builder.addEvent(eventClass);
            }
            builder.addPatterns(event.patterns);

            syntaxInfos.register(BukkitSyntaxInfos.Event.KEY, builder.build());
        }

        // EVENT VALUES
        for (EventValueRegistrar eventValue : getEventValues()) {
            // TODO - use Skript's new registration in 2.15+
            Converter eventValueConverter = EventValues.getExactEventValueConverter(eventValue.eventClass, eventValue.valueClass, eventValue.time);
            if (eventValueConverter != null) {
                debug("An event value has already been registered for %s / %s", eventValue.eventClass.getSimpleName(), eventValue.valueClass.getSimpleName());
                continue;
            }
            if (eventValue.excludedEvents == null) {
                EventValues.registerEventValue(eventValue.eventClass, eventValue.valueClass, eventValue.converter, eventValue.time);
            } else {
                EventValues.registerEventValue(eventValue.eventClass, eventValue.valueClass, eventValue.converter, eventValue.time, eventValue.excludeErrorMessage, eventValue.excludedEvents);
            }
        }

        // SECTIONS
        for (Registration.SectionRegistrar section : getSections()) {

            Supplier<Section> supplier = () -> {
                try {
                    return section.section.getConstructor().newInstance();
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                         NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            };

            syntaxInfos.register(SyntaxRegistry.SECTION, SyntaxInfo.builder((Class<Section>) section.section)
                .supplier(supplier)
                .addPatterns(section.patterns)
                .build());
        }

        // EFFECTS
        for (EffectRegistrar effect : getEffects()) {
            Supplier<Effect> supplier = () -> {
                try {
                    return effect.effect.getConstructor().newInstance();
                } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                         NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            };
            syntaxInfos.register(SyntaxRegistry.EFFECT,
                SyntaxInfo.builder((Class<Effect>) effect.effect)
                    .supplier(supplier)
                    .addPatterns(effect.patterns)
                    .build()
            );
        }

        // EXPRESSIONS
        for (Registration.ExpressionRegistrar<?, ?> expression : getExpressions()) {
            Supplier<Expression> supplier = () -> {
                try {
                    return expression.expressionClass.getConstructor().newInstance();
                } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                         NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            };

            SyntaxInfo info = SyntaxInfo.Expression.builder((Class<? extends Expression>) expression.expressionClass, expression.returnType)
                .supplier(supplier)
                .addPatterns(expression.patterns).build();

            syntaxInfos.register(SyntaxRegistry.EXPRESSION, (SyntaxInfo.Expression<?, ?>) info);
        }

        // FUNCTIONS
        for (FunctionRegistrar function : getFunctions()) {

            Functions.register(function.function);
        }

        // CONDITIONS
        for (ConditionRegistrar condition : getConditions()) {
            Supplier<Condition> supplier = () -> {
                try {
                    return condition.condition.getConstructor().newInstance();
                } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                         NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            };
            syntaxInfos.register(SyntaxRegistry.CONDITION,
                SyntaxInfo.builder((Class<Condition>) condition.condition)
                    .supplier(supplier)
                    .addPatterns(condition.patterns)
                    .build()
            );
        }
    }

    private static void skriptError(String format, Object... args) {
        Util.skriptError(format, args);
    }

    private static void debug(String format, Object... args) {
        Util.debug(format, args);
    }

    public static class RegistrationAddonModule implements AddonModule {

        private final String name;
        private final Registration registration;

        public RegistrationAddonModule(String name, Registration registration) {
            this.name = name;
            this.registration = registration;
        }

        @Override
        public void init(SkriptAddon addon) {
            this.registration.registerInit();
        }

        @Override
        public void load(SkriptAddon addon) {
            this.registration.registerLoad();
        }

        @Override
        public String name() {
            return this.name;
        }

    }

}
