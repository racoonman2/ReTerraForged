package raccoonman.reterraforged.world.worldgen.structure.rule;

import java.util.function.Function;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import raccoonman.reterraforged.registries.RTFBuiltInRegistries;

public interface StructureRule {
    public static final Codec<StructureRule> DIRECT_CODEC = RTFBuiltInRegistries.STRUCTURE_RULE_TYPE.byNameCodec().dispatch(StructureRule::codec, Function.identity());

	boolean test(WorldGenLevel level, BlockPos pos, BoundingBox boundingBox);
	
	Codec<? extends StructureRule> codec();
}
