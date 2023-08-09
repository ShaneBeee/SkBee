package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.Nullable;

@Name("NamespacedKey - Namespace/Key")
@Description({"Gets the namespace or namespace key of a NamespacedKey object."})
@Examples({"set {_items::*} to namespacedkey of every sword",
		"loop {_items::*}:",
		"\tbroadcast loop-value",
		"\tbroadcast namespace of loop-value",
		"\tbroadcast namespace key of loop-value"})
@Since("INSERT VERSION")
public class ExprNamespaceKeyOf extends SimplePropertyExpression<NamespacedKey, String> {

	static {
		register(ExprNamespaceKeyOf.class, String.class, "namespace [:key]", "namespacedkeys");
	}

	private boolean key;

	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		key = parseResult.hasTag("key");
		return super.init(exprs, matchedPattern, isDelayed, parseResult);
	}

	@Override
	@Nullable
	public String convert(NamespacedKey namespacedKey) {
		return key ? namespacedKey.getKey() : namespacedKey.getNamespace();
	}

	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}

	@Override
	protected String getPropertyName() {
		return "namespace" + (key ? " key" : "");
	}

}
