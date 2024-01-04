package raccoonman.reterraforged.mixin.terrablender;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.biome.Climate;
import raccoonman.reterraforged.world.worldgen.biome.RTFTargetPoint;
import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil;
import terrablender.api.RegionType;
import terrablender.api.Regions;

@Mixin(
	value = Climate.ParameterList.class,
	priority = 1001
)
class MixinParameterList<T> {
	private int maxIndex;

	@Inject(
		at = @At("HEAD"),
		method = "initializeForTerraBlender",
		require = 1	
	)
    public void initializeForTerraBlender(RegistryAccess registryAccess, RegionType regionType, long seed, CallbackInfo callback) {
    	this.maxIndex = Regions.getCount(regionType) - 1;
    }

	@Redirect(
		method = "findValuePositional",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/biome/Climate$ParameterList;getUniqueness(III)I"
		),
		require = 0	
	)
    public int getUniqueness(Climate.ParameterList<T> parameterList, int x, int y, int z, Climate.TargetPoint targetPoint) {
		if((Object) targetPoint instanceof RTFTargetPoint rtfTargetPoint) {
			double uniqueness = rtfTargetPoint.getUniqueness();
			if(Double.isNaN(uniqueness)) {
				return this.getUniqueness(x, y, z);
			}
			return NoiseUtil.round(this.maxIndex * (float) uniqueness);
		} else {
			throw new IllegalStateException();
		}
    }

	@Shadow
    public int getUniqueness(int x, int y, int z) {
    	throw new UnsupportedOperationException();
    }
}
