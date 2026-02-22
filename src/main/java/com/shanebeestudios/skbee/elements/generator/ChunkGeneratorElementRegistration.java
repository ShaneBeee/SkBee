package com.shanebeestudios.skbee.elements.generator;

import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.elements.generator.effects.EffChunkDataStructurePlace;
import com.shanebeestudios.skbee.elements.generator.effects.EffPopulateTree;
import com.shanebeestudios.skbee.elements.generator.effects.EffWorldCreatorSetGenerator;
import com.shanebeestudios.skbee.elements.generator.expressions.ExprBiomeParamPoint;
import com.shanebeestudios.skbee.elements.generator.expressions.ExprBiomeParamValues;
import com.shanebeestudios.skbee.elements.generator.expressions.ExprChunkDataBiome;
import com.shanebeestudios.skbee.elements.generator.expressions.ExprChunkDataBlock;
import com.shanebeestudios.skbee.elements.generator.expressions.ExprChunkDataHighestY;
import com.shanebeestudios.skbee.elements.generator.expressions.ExprChunkDataXZ;
import com.shanebeestudios.skbee.elements.generator.expressions.ExprChunkGenHeight;
import com.shanebeestudios.skbee.elements.generator.structure.StructChunkGen;
import com.shanebeestudios.skbee.elements.generator.type.GenEventValues;

public class ChunkGeneratorElementRegistration {

    public static void register(Registration reg) {
        // EFFECTS
        EffChunkDataStructurePlace.register(reg);
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

        // STRUCTURE
        StructChunkGen.register(reg);

        // TYPE
        GenEventValues.register(reg);
    }

}
