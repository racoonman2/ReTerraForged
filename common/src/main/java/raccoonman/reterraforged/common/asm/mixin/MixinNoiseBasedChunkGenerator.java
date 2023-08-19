package raccoonman.reterraforged.common.asm.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseRouter;
import net.minecraft.world.level.levelgen.RandomState;

@Deprecated
@Mixin(NoiseBasedChunkGenerator.class)
public class MixinNoiseBasedChunkGenerator {
    	
	@Overwrite
	public void addDebugScreenInfo(List<String> list, RandomState randomState, BlockPos blockPos) {
        NoiseRouter noiseRouter = randomState.router();
        DensityFunction.SinglePointContext ctx = new DensityFunction.SinglePointContext(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        
        list.add("Final Density: " + noiseRouter.finalDensity().compute(ctx));
    }
}
