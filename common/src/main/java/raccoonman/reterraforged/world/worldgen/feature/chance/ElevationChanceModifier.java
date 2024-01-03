package raccoonman.reterraforged.world.worldgen.feature.chance;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

class ElevationChanceModifier extends RangeChanceModifier {
	public static final Codec<ElevationChanceModifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.FLOAT.fieldOf("from").forGetter((o) -> o.from),
		Codec.FLOAT.fieldOf("to").forGetter((o) -> o.to),
		Codec.BOOL.fieldOf("exclusive").forGetter((o) -> o.exclusive)
	).apply(instance, ElevationChanceModifier::new));
	
	public ElevationChanceModifier(float from, float to, boolean exclusive) {
		super(from, to, exclusive);
	}

	@Override
	public Codec<ElevationChanceModifier> codec() {
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
//			return rtfRandomState.getHeightmap().levels().elevation(chunk.getCell(x, z).height);
//		} else {
//			throw new UnsupportedOperationException();
//		}
		return 1.0F;
	}
}
