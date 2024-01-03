package raccoonman.reterraforged.world.worldgen.feature.chance;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

class BiomeEdgeChanceModifier extends RangeChanceModifier {
	public static final Codec<BiomeEdgeChanceModifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.FLOAT.fieldOf("from").forGetter((o) -> o.from),
		Codec.FLOAT.fieldOf("to").forGetter((o) -> o.to),
		Codec.BOOL.fieldOf("exclusive").forGetter((o) -> o.exclusive)
	).apply(instance, BiomeEdgeChanceModifier::new));
	
	public BiomeEdgeChanceModifier(float from, float to, boolean exclusive) {
		super(from, to, exclusive);
	}

	@Override
	public Codec<BiomeEdgeChanceModifier> codec() {
		return CODEC;
	}

	@Override
	protected float getValue(ChanceContext chanceCtx, FeaturePlaceContext<?> placeCtx) {
//		BlockPos pos = placeCtx.origin();
//		@Nullable
//		TileProvider tileCache;
//		if((Object) placeCtx.level().getLevel().getChunkSource().randomState() instanceof RTFRandomState rtfRandomState && (tileCache = rtfRandomState.getTileCache()) != null) {
//			int x = pos.getX();
//			int z = pos.getZ();
//			ChunkReader chunk = tileCache.getChunk(SectionPos.blockToSectionCoord(x), SectionPos.blockToSectionCoord(z));
//			return chunk.getCell(x, z).biomeRegionEdge;
//		} else {
//			throw new UnsupportedOperationException();
//		}
		return 1.0F;
	}
}
