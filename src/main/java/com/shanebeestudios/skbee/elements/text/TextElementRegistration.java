package com.shanebeestudios.skbee.elements.text;

import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.elements.text.effects.EffComponentReplace;
import com.shanebeestudios.skbee.elements.text.effects.EffSendComponent;
import com.shanebeestudios.skbee.elements.text.effects.EffSendComponentTitle;
import com.shanebeestudios.skbee.elements.text.effects.EffSendSignChange;
import com.shanebeestudios.skbee.elements.text.events.EvtChat;
import com.shanebeestudios.skbee.elements.text.expressions.ExprAsyncChatMessage;
import com.shanebeestudios.skbee.elements.text.expressions.ExprAsyncChatViewers;
import com.shanebeestudios.skbee.elements.text.expressions.ExprBookPages;
import com.shanebeestudios.skbee.elements.text.expressions.ExprChestInventory;
import com.shanebeestudios.skbee.elements.text.expressions.ExprClickEvent;
import com.shanebeestudios.skbee.elements.text.expressions.ExprComponentChildren;
import com.shanebeestudios.skbee.elements.text.expressions.ExprComponentFormat;
import com.shanebeestudios.skbee.elements.text.expressions.ExprComponentToJson;
import com.shanebeestudios.skbee.elements.text.expressions.ExprEffectiveName;
import com.shanebeestudios.skbee.elements.text.expressions.ExprHoverEvent;
import com.shanebeestudios.skbee.elements.text.expressions.ExprClickEventOf;
import com.shanebeestudios.skbee.elements.text.expressions.ExprHoverEventOf;
import com.shanebeestudios.skbee.elements.text.expressions.ExprItemLore;
import com.shanebeestudios.skbee.elements.text.expressions.ExprItemLoreLine;
import com.shanebeestudios.skbee.elements.text.expressions.ExprItemName;
import com.shanebeestudios.skbee.elements.text.expressions.ExprMannequinDescription;
import com.shanebeestudios.skbee.elements.text.expressions.ExprMergeComponents;
import com.shanebeestudios.skbee.elements.text.expressions.ExprMessageComponent;
import com.shanebeestudios.skbee.elements.text.expressions.ExprMiniMessage;
import com.shanebeestudios.skbee.elements.text.expressions.ExprNameBlock;
import com.shanebeestudios.skbee.elements.text.expressions.ExprNameEntity;
import com.shanebeestudios.skbee.elements.text.expressions.ExprNameInventory;
import com.shanebeestudios.skbee.elements.text.expressions.ExprObjectTextComponent;
import com.shanebeestudios.skbee.elements.text.expressions.ExprObjectiveScoreCustomName;
import com.shanebeestudios.skbee.elements.text.expressions.ExprPlayerListName;
import com.shanebeestudios.skbee.elements.text.expressions.ExprSignLines;
import com.shanebeestudios.skbee.elements.text.expressions.ExprSignedMessage;
import com.shanebeestudios.skbee.elements.text.expressions.ExprTeamPrefixComp;
import com.shanebeestudios.skbee.elements.text.expressions.ExprTextComponent;
import com.shanebeestudios.skbee.elements.text.expressions.ExprTooltipLines;
import com.shanebeestudios.skbee.elements.text.sections.SecClickEventCallback;
import com.shanebeestudios.skbee.elements.text.type.Types;

public class TextElementRegistration {

    public static void register(Registration reg) {
        // EFFECTS
        EffComponentReplace.register(reg);
        EffSendComponent.register(reg);
        EffSendComponentTitle.register(reg);
        EffSendSignChange.register(reg);

        // EVENTS
        EvtChat.register(reg);

        // EXPRESSIONS
        ExprAsyncChatMessage.register(reg);
        ExprAsyncChatViewers.register(reg);
        ExprBookPages.register(reg);
        ExprChestInventory.register(reg);
        ExprClickEventOf.register(reg);
        ExprClickEvent.register(reg);
        ExprComponentChildren.register(reg);
        ExprComponentFormat.register(reg);
        ExprComponentToJson.register(reg);
        ExprEffectiveName.register(reg);
        ExprHoverEvent.register(reg);
        ExprHoverEventOf.register(reg);
        ExprItemLore.register(reg);
        ExprItemLoreLine.register(reg);
        ExprItemName.register(reg);
        ExprMannequinDescription.register(reg);
        ExprMergeComponents.register(reg);
        ExprMessageComponent.register(reg);
        ExprMiniMessage.register(reg);
        ExprNameBlock.register(reg);
        ExprNameEntity.register(reg);
        ExprNameInventory.register(reg);
        ExprObjectiveScoreCustomName.register(reg);
        ExprObjectTextComponent.register(reg);
        ExprPlayerListName.register(reg);
        ExprSignedMessage.register(reg);
        ExprSignLines.register(reg);
        ExprTeamPrefixComp.register(reg);
        ExprTextComponent.register(reg);
        ExprTooltipLines.register(reg);

        // SECTIONS
        SecClickEventCallback.register(reg);

        // TYPES
        Types.register(reg);
    }
}
