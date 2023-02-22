package com.shanebeestudios.skbee.elements.villager.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

@Name("Merchant Recipe - Create")
@Description({"Create a merchant recipe.",
        "\nNOTE: You will need to use the merchant recipe ingredients expression to add ingredients.",
        "\nmax uses = A trade has a maximum number of uses. A Villager may periodically replenish it's trades",
        "by resetting the uses of it's merchant recipes to 0, allowing them to be used again.",
        "\nexperience reward = A trade may or may not reward experience for being completed.",
        "\nDemand & Special Price were added in MC 1.18.x:",
        "\ndemand = This value is periodically updated by the villager that owns this merchant recipe based on",
        "how often the recipe has been used since it has been last restocked in relation to its maximum uses.",
        "The amount by which the demand influences the amount of the first ingredient is scaled by the recipe's",
        "price multiplier, and can never be below zero.",
        "\nspecial price =  This value is dynamically updated whenever a player starts and stops trading with a",
        "villager that owns this merchant recipe. It is based on the player's individual reputation with the villager,",
        "and the player's currently active status effects (ex: hero of the village).",
        "The influence of the player's reputation on the special price is scaled by the recipe's price multiplier."})
@Examples("set {_m} to merchant recipe with result diamond sword with max uses 10")
@Since("1.17.0")
public class ExprMerchantRecipe extends SimpleExpression<MerchantRecipe> {

    private static final boolean SUPPORTS_SPECIAL_PRICE = Skript.methodExists(MerchantRecipe.class, "getDemand");
    static {
        String pattern = "[new] merchant recipe with result %itemtype% with max uses %number% [with uses %-number%] " +
                "[(|1¦with experience reward)] [with villager experience %-number%] [with price multiplier %-number%]";
        if (SUPPORTS_SPECIAL_PRICE) {
            Skript.registerExpression(ExprMerchantRecipe.class, MerchantRecipe.class, ExpressionType.SIMPLE,
                    pattern + " [with demand %-number%] [with special price %-number%]");
        } else {
            Skript.registerExpression(ExprMerchantRecipe.class, MerchantRecipe.class, ExpressionType.SIMPLE,
                    pattern);
        }
    }

    private Expression<ItemType> result;
    private Expression<Number> maxUses;
    private Expression<Number> uses;
    private boolean reward;
    private Expression<Number> villagerExp;
    private Expression<Number> priceMulti;
    private Expression<Number> demand;
    private Expression<Number> specialPrice;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, ParseResult parseResult) {
        this.result = (Expression<ItemType>) exprs[0];
        this.maxUses = (Expression<Number>) exprs[1];
        this.uses = (Expression<Number>) exprs[2];
        this.reward = parseResult.mark == 1;
        this.villagerExp = (Expression<Number>) exprs[3];
        this.priceMulti = (Expression<Number>) exprs[4];
        if (SUPPORTS_SPECIAL_PRICE) {
            this.demand = (Expression<Number>) exprs[5];
            this.specialPrice = (Expression<Number>) exprs[6];
        }
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable MerchantRecipe[] get(Event event) {
        ItemType itemType = this.result.getSingle(event);
        Number maxUses = this.maxUses.getSingle(event);
        Number uses = this.uses != null ? this.uses.getSingle(event) : null;
        Number villagerExp = this.villagerExp != null ? this.villagerExp.getSingle(event) : null;
        Number priceMulti = this.priceMulti != null ? this.priceMulti.getSingle(event) : null;
        Number demand = this.demand != null ? this.demand.getSingle(event) : null;
        Number specialPrice = this.specialPrice != null ? this.specialPrice.getSingle(event) : null;
        if (itemType == null || maxUses == null) return null;

        ItemStack random = itemType.getRandom();
        int maxUsesI = maxUses.intValue();
        int usesI = uses == null ? 0 : uses.intValue();
        int villXP = villagerExp == null ? 0 : villagerExp.intValue();
        float priceMultiF = priceMulti == null ? 0 : priceMulti.floatValue();
        int demandI = demand == null ? 0 : demand.intValue();
        int specialPriceI = specialPrice == null ? 0 : specialPrice.intValue();

        MerchantRecipe merchantRecipe;
        if (SUPPORTS_SPECIAL_PRICE) {
            merchantRecipe = new MerchantRecipe(random, usesI, maxUsesI, reward, villXP, priceMultiF, demandI, specialPriceI);
        } else {
            merchantRecipe = new MerchantRecipe(random, usesI, maxUsesI, reward, villXP, priceMultiF);
        }
        return new MerchantRecipe[]{merchantRecipe};

    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends MerchantRecipe> getReturnType() {
        return MerchantRecipe.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String result = this.result.toString(e, d);
        String max = this.maxUses.toString(e, d);
        String uses = this.uses != null ? " with uses " + this.uses.toString(e, d) : "";
        String reward = this.reward ? " with experience reward" : "";
        String xp = this.villagerExp != null ? " with villager experience " + this.villagerExp.toString(e, d) : "";
        String price = this.priceMulti != null ? " with price multiplier " + this.priceMulti.toString(e, d) : "";
        String demand = this.demand != null ? " with demand " + this.demand.toString(e, d) : "";
        String special = this.specialPrice != null ? " with special price " + this.specialPrice.toString(e, d) : "";
        return "merchant recipe with result " + result + " with max uses " + max + uses + reward + xp + price + demand + special;
    }

}
