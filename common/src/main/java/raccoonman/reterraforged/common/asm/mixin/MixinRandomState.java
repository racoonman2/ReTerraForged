package raccoonman.reterraforged.common.asm.mixin; 

import java.lang.reflect.Field;

import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Desc;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseRouter;
import net.minecraft.world.level.levelgen.RandomState;
import raccoonman.reterraforged.common.ReTerraForged;
import raccoonman.reterraforged.common.asm.extensions.GeneratorHolder;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseLevels;
import raccoonman.reterraforged.common.level.levelgen.terrain.TerrainGenerator;
import raccoonman.reterraforged.common.level.levelgen.terrain.TerrainNoise;
import raccoonman.reterraforged.common.level.levelgen.util.Seed;

@Implements({
	@Interface(iface = GeneratorHolder.class, prefix = ReTerraForged.MOD_ID + "$GeneratorHolder$"),
})
@Mixin(RandomState.class)
public class MixinRandomState {
	private TerrainNoise terrain;
	private TerrainGenerator generator;
	
	public TerrainGenerator reterraforged$GeneratorHolder$getGenerator() {
		return this.generator;
	}
	
	@Redirect(
		at = @At(
			value = "INVOKE",
			desc = @Desc(
				value = "mapAll",
				args = DensityFunction.Visitor.class,
				ret = NoiseRouter.class,
				owner = NoiseRouter.class
			)			
		),
		method = "<init>",
		require = 1
	)
	private NoiseRouter RandomState(NoiseRouter router, DensityFunction.Visitor visitor, NoiseGeneratorSettings settings) {
		return router.mapAll((function) -> {
			if(function instanceof TerrainNoise.Marker marker) {
				if(this.terrain == null) {
					long seed = findField(visitor, long.class);
					this.terrain = new TerrainNoise(new Seed(seed), marker.settings(), NoiseLevels.create(marker.settings().terrain(), settings.seaLevel()), marker.blender());
					this.generator = new TerrainGenerator(this.terrain);
				} 
				return this.terrain;
			}
			return visitor.apply(function);
		});
	}
	
	@SuppressWarnings("unchecked")
	private static <T> T findField(Object instance, Class<T> type) {
		try {
			Field target = null;
			for(Field field : instance.getClass().getDeclaredFields()) {
				if(field.getType().equals(type)) {
					target = field;
				}
			}
			if(target != null) {
				return (T) target.get(instance);
			}
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
		throw new RuntimeException("Couldn't find field of type: " + type);
	}
}
