package raccoonman.reterraforged.world.worldgen;

import net.minecraft.world.level.levelgen.NoiseRouterData;

public class PVPoints {
	public static final float VALLEY = NoiseRouterData.peaksAndValleys(WeirdnessPoints.VALLEY);
	public static final float LOW_SLICE = NoiseRouterData.peaksAndValleys(WeirdnessPoints.LOW_SLICE);
	public static final float MID_SLICE = NoiseRouterData.peaksAndValleys(WeirdnessPoints.MID_SLICE);
	public static final float HIGH_SLICE = NoiseRouterData.peaksAndValleys(WeirdnessPoints.HIGH_SLICE);
	public static final float PEAK = NoiseRouterData.peaksAndValleys(WeirdnessPoints.PEAK);
	public static final float PEAK_TOP = NoiseRouterData.peaksAndValleys(WeirdnessPoints.PEAK_TOP);
}