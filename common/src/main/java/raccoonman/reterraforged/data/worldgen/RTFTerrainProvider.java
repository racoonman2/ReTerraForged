package raccoonman.reterraforged.data.worldgen;

import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import raccoonman.reterraforged.data.worldgen.preset.Preset;
import raccoonman.reterraforged.data.worldgen.preset.TerrainSettings;
import raccoonman.reterraforged.data.worldgen.preset.WorldSettings;

public class RTFTerrainProvider {

	public static DensityFunction createOffsetSpline(Preset preset, DensityFunction continent, DensityFunction erosion, DensityFunction ridge) {
		WorldSettings worldSettings = preset.world();
		WorldSettings.Properties properties = worldSettings.properties;
		WorldSettings.ControlPoints controlPoints = worldSettings.controlPoints;
		
		TerrainSettings terrainSettings = preset.terrain();
		
//		return LinearSplineFunction.builder(ridge)
//			.addPoint(-1.0D, -0.4D)
//			.build();
		return DensityFunctions.constant(0.0D);
	}
}
