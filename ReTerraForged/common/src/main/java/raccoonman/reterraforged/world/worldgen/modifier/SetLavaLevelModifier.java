package raccoonman.reterraforged.world.worldgen.modifier;

import java.util.Set;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import raccoonman.reterraforged.mixin.accessors.GlobalFluidPickerAccessor;

public record SetLavaLevelModifier(Set<ResourceKey<LevelStem>> levels, int lavaLevel) implements LevelModifier {
	public static final MapCodec<SetLavaLevelModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> LevelModifier.addFields(instance).and(
		Codec.INT.fieldOf("lava_level").forGetter(SetLavaLevelModifier::lavaLevel)
	).apply(instance, SetLavaLevelModifier::new));
	
	@Override
	public void applyModifier(LevelStem level, RegistryAccess registryAccess) {
		if(level.generator() instanceof NoiseBasedChunkGenerator generator) {
			((GlobalFluidPickerAccessor) (Object) generator).setGlobalFluidPicker(() -> {
				return this.createFluidPicker(generator.generatorSettings().value());
			});
		}
	}
	
	@Override
	public MapCodec<SetLavaLevelModifier> codec() {
		return CODEC;
	}

	@Override
	public int priority() {
		return 0;
	}
	
    private Aquifer.FluidPicker createFluidPicker(NoiseGeneratorSettings noiseGeneratorSettings) {
        Aquifer.FluidStatus lava = new Aquifer.FluidStatus(this.lavaLevel, Blocks.LAVA.defaultBlockState());
        int seaLevel = noiseGeneratorSettings.seaLevel();
        Aquifer.FluidStatus water = new Aquifer.FluidStatus(seaLevel, noiseGeneratorSettings.defaultFluid());
        return (x, y, z) -> {
            if (y < Math.min(this.lavaLevel, seaLevel)) {
                return lava;
            }
            return water;
        };
    }
}
