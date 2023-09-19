package raccoonman.reterraforged.common.level.levelgen.geology;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import raccoonman.reterraforged.common.level.levelgen.geology.Geology.Layer;
import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseUtil;

public record Stratum(TagKey<Block> materialList, int minLayerCount, int maxLayerCount, float minDepth, float maxDepth, Noise depth) {
	public static final Codec<Stratum> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		TagKey.hashedCodec(Registries.BLOCK).fieldOf("material_list").forGetter(Stratum::materialList),
		Codec.INT.fieldOf("min_layer_count").forGetter(Stratum::minLayerCount),
		Codec.INT.fieldOf("max_layer_count").forGetter(Stratum::minLayerCount),
		Codec.FLOAT.fieldOf("min_depth").forGetter(Stratum::minDepth),
		Codec.FLOAT.fieldOf("max_depth").forGetter(Stratum::maxDepth),
		Noise.HOLDER_HELPER_CODEC.fieldOf("depth").forGetter(Stratum::depth)
	).apply(instance, Stratum::new));
	
	public List<Geology.Layer> generateLayers(RandomSource random) {
		List<Holder<Block>> materials = ImmutableList.copyOf(BuiltInRegistries.BLOCK.getTagOrEmpty(this.materialList));
		
        int lastIndex = -1;
        int layers = NoiseUtil.round(NoiseUtil.lerp(this.minLayerCount, this.maxLayerCount, random.nextFloat()));
        List<Layer> result = new ArrayList<>();
        for (int i = 0; i < layers; i++) {
            int attempts = 3;
            int index = random.nextInt(materials.size());
            while (--attempts >= 0 && index == lastIndex) {
                index = random.nextInt(materials.size());
            }
            if (index != lastIndex) {
                lastIndex = index;
                BlockState material = materials.get(index).value().defaultBlockState();
                float depth = NoiseUtil.lerp(this.minDepth, this.maxDepth, random.nextFloat());
                result.add(new Layer(material, this.depth.shift(random.nextInt()).scale(depth)));
            }
        }
        return result;
	}

}