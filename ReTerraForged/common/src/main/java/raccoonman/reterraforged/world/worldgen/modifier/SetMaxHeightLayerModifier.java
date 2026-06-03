package raccoonman.reterraforged.world.worldgen.modifier;

import java.util.Set;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.LevelStem;
import raccoonman.reterraforged.extensions.ExtensionUtil;
import raccoonman.reterraforged.extensions.RTFChunkGenerator;
import raccoonman.reterraforged.world.worldgen.layer.Layer;

public record SetMaxHeightLayerModifier(Set<ResourceKey<LevelStem>> levels, Holder<Layer.Factory<Float>> maxHeightLayer) implements LevelModifier {
	public static final MapCodec<SetMaxHeightLayerModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> LevelModifier.addFields(instance).and(
		Layer.<Float>codec().fieldOf("max_height_layer").forGetter(SetMaxHeightLayerModifier::maxHeightLayer)
	).apply(instance, SetMaxHeightLayerModifier::new));
	
	@Override
	public void applyModifier(LevelStem level, RegistryAccess registryAccess) {
		ChunkGenerator chunkGenerator = level.generator();
		RTFChunkGenerator rtfChunkGenerator = ExtensionUtil.cast(chunkGenerator);
		rtfChunkGenerator.setMaxHeightLayer(this.maxHeightLayer.value());
	}
	
	@Override
	public MapCodec<SetMaxHeightLayerModifier> codec() {
		return CODEC;
	}

	@Override
	public int priority() {
		return 0;
	}
}
