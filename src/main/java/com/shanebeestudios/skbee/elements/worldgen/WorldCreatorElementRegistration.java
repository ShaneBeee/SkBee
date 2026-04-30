package com.shanebeestudios.skbee.elements.worldgen;

import com.github.shanebeee.skr.Registration;
import com.shanebeestudios.skbee.elements.worldgen.conditions.CondWorldExists;
import com.shanebeestudios.skbee.elements.worldgen.effects.EffChunkDataStructurePlace;
import com.shanebeestudios.skbee.elements.worldgen.effects.EffLoadWorld;
import com.shanebeestudios.skbee.elements.worldgen.effects.EffPopulateTree;
import com.shanebeestudios.skbee.elements.worldgen.effects.EffWorldCreatorSetGenerator;
import com.shanebeestudios.skbee.elements.worldgen.expressions.ExprBiomeParamPoint;
import com.shanebeestudios.skbee.elements.worldgen.expressions.ExprBiomeParamValues;
import com.shanebeestudios.skbee.elements.worldgen.expressions.ExprChunkDataBiome;
import com.shanebeestudios.skbee.elements.worldgen.expressions.ExprChunkDataBlock;
import com.shanebeestudios.skbee.elements.worldgen.expressions.ExprChunkDataHighestY;
import com.shanebeestudios.skbee.elements.worldgen.expressions.ExprChunkDataXZ;
import com.shanebeestudios.skbee.elements.worldgen.expressions.ExprChunkGenHeight;
import com.shanebeestudios.skbee.elements.worldgen.expressions.ExprLoadedCustomWorlds;
import com.shanebeestudios.skbee.elements.worldgen.expressions.ExprWorldCreator;
import com.shanebeestudios.skbee.elements.worldgen.expressions.ExprWorldCreatorOption;
import com.shanebeestudios.skbee.elements.worldgen.expressions.ExprWorldCreatorSection;
import com.shanebeestudios.skbee.elements.worldgen.structures.StructChunkGen;
import com.shanebeestudios.skbee.elements.worldgen.types.Types;

public class WorldCreatorElementRegistration {

    public static void register(Registration reg) {
        // CONDTIONS
        CondWorldExists.register(reg);

        // EFFECTS
        EffChunkDataStructurePlace.register(reg);
        EffLoadWorld.register(reg);
        EffPopulateTree.register(reg);
        EffWorldCreatorSetGenerator.register(reg);

        // EXPRESSIONS
        ExprBiomeParamPoint.register(reg);
        ExprBiomeParamValues.register(reg);
        ExprChunkDataBiome.register(reg);
        ExprChunkDataBlock.register(reg);
        ExprChunkDataHighestY.register(reg);
        ExprChunkDataXZ.register(reg);
        ExprChunkGenHeight.register(reg);
        ExprLoadedCustomWorlds.register(reg);
        ExprWorldCreator.register(reg);
        ExprWorldCreatorOption.register(reg);
        ExprWorldCreatorSection.register(reg);

        // STRUCTURES
        StructChunkGen.register(reg);

        // TYPES
        Types.register(reg);
    }

}
