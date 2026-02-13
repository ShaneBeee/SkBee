package com.shanebeestudios.skbee;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.Section;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.registrations.Classes;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.registration.Registration.ConditionRegistrar;
import com.shanebeestudios.skbee.api.registration.Registration.EffectRegistrar;
import com.shanebeestudios.skbee.api.registration.Registration.EnumTypeRegistrar;
import com.shanebeestudios.skbee.api.registration.Registration.RegistryTypeRegistrar;
import com.shanebeestudios.skbee.api.registration.Registration.TypeRegistrar;
import com.shanebeestudios.skbee.api.util.Util;
import com.shanebeestudios.skbee.api.wrapper.EnumWrapper;
import com.shanebeestudios.skbee.api.wrapper.RegistryClassInfo;
import org.bukkit.Keyed;
import org.bukkit.Registry;
import org.bukkit.event.Event;
import org.skriptlang.skript.addon.AddonModule;
import org.skriptlang.skript.addon.SkriptAddon;
import org.skriptlang.skript.bukkit.registration.BukkitSyntaxInfos;
import org.skriptlang.skript.lang.structure.Structure;
import org.skriptlang.skript.registration.DefaultSyntaxInfos;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Supplier;

@SuppressWarnings({"rawtypes", "UnstableApiUsage"})
public class SkBeeAddonModule implements AddonModule {

    private final Registration registration;

    public SkBeeAddonModule(Registration registration) {
        this.registration = registration;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void init(SkriptAddon addon) {
        // TYPES
        for (TypeRegistrar type : this.registration.getTypes()) {
            if (!type.isRegistered()) {
                Util.skriptError("Type '" + type.codename + "' is not registered!");
                continue;
            }
            ClassInfo<?> classInfo;
            if (type instanceof EnumTypeRegistrar<?> enumTypeRegistrar) {
                EnumWrapper<?> enumWrapper = new EnumWrapper<>(enumTypeRegistrar.type,
                    enumTypeRegistrar.prefix,
                    enumTypeRegistrar.suffix);

                classInfo = enumWrapper.getClassInfo(enumTypeRegistrar.codename);
            } else if (type instanceof RegistryTypeRegistrar<? extends Keyed> registryTypeRegistrar) {
                Class<? extends Keyed> registryClass = registryTypeRegistrar.type;
                Registry<? extends Keyed> registry = registryTypeRegistrar.registry;
                String codename = registryTypeRegistrar.codename;
                String prefix = registryTypeRegistrar.prefix;
                String suffix = registryTypeRegistrar.suffix;
                boolean usage = registryTypeRegistrar.createUsage;

                classInfo = RegistryClassInfo.create(registry, registryClass, usage, codename, prefix, suffix);
            } else {
                classInfo = new ClassInfo<>(type.type, type.codename);
            }
            if (type.user != null) {
                classInfo.user(type.user);
            }
            if (type.usage != null) {
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

    @SuppressWarnings("unchecked")
    @Override
    public void load(SkriptAddon addon) {
        SyntaxRegistry syntaxInfos = addon.syntaxRegistry();

        // STRUCTURES
        for (Registration.StructureRegistrar<?> structure : this.registration.getStructures()) {
            if (!structure.isRegistered()) {
                Util.skriptError("Structure '%s' is not registered!", structure.structureClass.getSimpleName());
                continue;
            }

            DefaultSyntaxInfos.Structure<Structure> build = DefaultSyntaxInfos.Structure.builder(
                (Class<Structure>) structure.structureClass)
                .addPatterns(structure.patterns)
                .build();

            syntaxInfos.register(SyntaxRegistry.STRUCTURE, build);
        }
        // EVENTS
        for (Registration.EventRegistrar event : this.registration.getEvents()) {
            if (!event.isRegistered()) {
                Util.skriptError("Event '" + event.getDocumentation().getName() + "' is not registered!");
                continue;
            }
            BukkitSyntaxInfos.Event.Builder<? extends BukkitSyntaxInfos.Event.Builder<?, SkriptEvent>, SkriptEvent> builder = BukkitSyntaxInfos.Event.builder(
                (Class<SkriptEvent>) event.skriptEventClass,
                event.getDocumentation().getName());
            for (Class<? extends Event> eventClass : event.eventClasses) {
                builder.addEvent(eventClass);
            }
            builder.addPatterns(event.patterns);

            syntaxInfos.register(BukkitSyntaxInfos.Event.KEY, builder.build());
        }

        // SECTIONS
        for (Registration.SectionRegistrar section : this.registration.getSections()) {
            if (!section.isRegistered()) {
                Util.skriptError("Section '" + section.section.getSimpleName() + "' is not registered!");
                continue;
            }

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
        for (EffectRegistrar effect : this.registration.getEffects()) {
            if (!effect.isRegistered()) {
                Util.skriptError("Effect '" + effect.effect.getSimpleName() + "' is not registered!");
                continue;
            }
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
        for (Registration.ExpressionRegistrar<?, ?> expression : this.registration.getExpressions()) {
            if (!expression.isRegistered()) {
                Util.skriptError("Expression '%s' is not registered!", expression.expressionClass.getSimpleName());
                continue;
            }
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

        // CONDITIONS
        for (ConditionRegistrar condition : this.registration.getConditions()) {
            if (!condition.isRegistered()) {
                Util.skriptError("Condition '" + condition.condition.getSimpleName() + "' is not registered!");
                continue;
            }
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

    @Override
    public String name() {
        return "SkBee";
    }

}
