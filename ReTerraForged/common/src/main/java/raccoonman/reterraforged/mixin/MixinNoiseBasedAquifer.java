package raccoonman.reterraforged.mixin;

import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.Aquifer.FluidPicker;
import net.minecraft.world.level.levelgen.Aquifer.FluidStatus;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseChunk;
import raccoonman.reterraforged.extensions.RTFAquifer;

@Implements(@Interface(iface = RTFAquifer.class, prefix = "reterraforged$"))
@Mixin(Aquifer.NoiseBasedAquifer.class)
class MixinNoiseBasedAquifer implements RTFAquifer {
	@Shadow
	@Final
	private static int[][] SURFACE_SAMPLING_OFFSETS_IN_CHUNKS;
	@Shadow
	@Final
	private NoiseChunk noiseChunk;
	@Shadow
	@Final
    private FluidPicker globalFluidPicker;

	private MutableObject<DensityFunction> waterLevel;
	
	@Override
	public void setWaterLevel(MutableObject<DensityFunction> waterLevel) {
		this.waterLevel = waterLevel;
	}
//
//	@Overwrite
//    private FluidStatus computeFluid(int x, int y, int z) {
//        FluidStatus fluidStatus = this.globalFluidPicker.computeFluid(x, y, z);
//        int lowestSurface = Integer.MAX_VALUE;
//        int maxY = y + 12;
//        int minY = y - 12;
//        boolean bl = false;
//        for (int[] is : SURFACE_SAMPLING_OFFSETS_IN_CHUNKS) {
//            FluidStatus fluidStatus2;
//            int o = x + SectionPos.sectionToBlockCoord((int)is[0]);
//            int p = z + SectionPos.sectionToBlockCoord((int)is[1]);
//            int surfaceLevel = this.noiseChunk.preliminarySurfaceLevel(o, p);
//            int r = surfaceLevel + 8;
//            boolean bl2 = is[0] == 0 && is[1] == 0;
//            if (bl2 && minY > r) {
//                return fluidStatus;
//            }
//            boolean bl3 = maxY > r;
//            if ((bl3 || bl2) && !(fluidStatus2 = this.globalFluidPicker.computeFluid(o, r, p)).at(r).isAir()) {
//                if (bl2) {
//                    bl = true;
//                }
//                if (bl3) {
////                    return fluidStatus2;
//                	return new FluidStatus(fluidStatus2.fluidLevel + NoiseUtil.round(this.waterLevel.getValue().compute(new SinglePointContext(x, y, z))), fluidStatus2.fluidType);
//                }
//            }
//            lowestSurface = Math.min(lowestSurface, surfaceLevel);
//        }
//        int surface = this.computeSurfaceLevel(x, y, z, fluidStatus, lowestSurface, bl);
//        return new FluidStatus(surface, this.computeFluidType(x, y, z, fluidStatus, surface));
//    }

	@Shadow
    private int computeSurfaceLevel(int i, int j, int k, FluidStatus fluidStatus, int l, boolean bl) {
    	throw new UnsupportedOperationException();
    }
	
	@Shadow
    private BlockState computeFluidType(int i, int j, int k, FluidStatus fluidStatus, int l) {
    	throw new UnsupportedOperationException();
    }
}
