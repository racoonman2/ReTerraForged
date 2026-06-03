package raccoonman.reterraforged.mixin.compat;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.biome.Climate;
import raccoonman.reterraforged.extensions.ExtensionUtil;
import raccoonman.reterraforged.extensions.compat.TBParameterList;
import raccoonman.reterraforged.extensions.compat.TBTargetPoint;
import raccoonman.reterraforged.world.worldgen.compat.TBRegionUtils;
import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil;
import terrablender.api.Region;
import terrablender.api.RegionType;
import terrablender.api.Regions;
import terrablender.worldgen.IExtendedParameterList;

@Mixin(
	value = Climate.ParameterList.class,
	priority = 1001
)
class MixinTBParameterList<T> implements TBParameterList {
	private List<Region> regions;
	private int totalWeight;
	
	@Inject(
		method = "initializeForTerraBlender",
		at = @At("HEAD")
	)
    public void initializeForTerraBlender(RegistryAccess registryAccess, RegionType regionType, long seed, CallbackInfo callback) {
    	this.regions = Regions.get(regionType);
    	this.totalWeight = TBRegionUtils.getTotalWeight(this.regions);
	}

	@Redirect(
		method = "findValuePositional",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/biome/Climate$ParameterList;getUniqueness(III)I"
		)
	)
    public int getUniqueness(Climate.ParameterList<T> parameterList, int x, int y, int z, Climate.TargetPoint targetPoint) {
		return this.getIndex(targetPoint, x, y, z);
    }
	
	@Override
	public int getIndex(Climate.TargetPoint point, int x, int y, int z) {
		TBTargetPoint tbTargetPoint = ExtensionUtil.cast(point);
		IExtendedParameterList<T> extParameterList = ExtensionUtil.cast(this);
		double uniqueness = tbTargetPoint.getUniqueness();
		if(Double.isNaN(uniqueness)) {
			return extParameterList.getUniqueness(x, y, z);
		}
		return TBRegionUtils.getUniqueness(this.regions, NoiseUtil.round(uniqueness * this.totalWeight));
	}
}