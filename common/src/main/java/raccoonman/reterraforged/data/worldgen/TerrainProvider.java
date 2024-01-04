package raccoonman.reterraforged.data.worldgen;

import net.minecraft.util.CubicSpline;
import net.minecraft.util.ToFloatFunction;

public class TerrainProvider {

    public static <C, I extends ToFloatFunction<C>> CubicSpline<C, I> overworldFactor(I continent, I erosion, I weirdness, I peaksAndValleys) {
        return CubicSpline.builder(continent)
        	.addPoint(-0.19f, 3.95f)
        	.addPoint(-0.15f, TerrainProvider.getErosionFactor(erosion, weirdness, peaksAndValleys, 6.25f, true))
        	.addPoint(-0.1f, TerrainProvider.getErosionFactor(erosion, weirdness, peaksAndValleys, 5.47f, true))
        	.addPoint(0.03f, TerrainProvider.getErosionFactor(erosion, weirdness, peaksAndValleys, 5.08f, true))
        	.addPoint(0.06f, TerrainProvider.getErosionFactor(erosion, weirdness, peaksAndValleys, 4.69f, false))
        	.build();
    }

    private static <C, I extends ToFloatFunction<C>> CubicSpline<C, I> getErosionFactor(I erosion, I weirdness, I peaksAndValleys, float baseFactor, boolean lerpContinent) {
        CubicSpline<C, I> cubicSpline = CubicSpline.builder(weirdness)
        	.addPoint(-0.2f, 6.3f)
        	.addPoint(0.2f, baseFactor)
        	.build();
        CubicSpline.Builder<C, I> builder = CubicSpline.builder(erosion)
        	.addPoint(-0.6f, cubicSpline)
        	.addPoint(-0.5f, CubicSpline.builder(weirdness).
        		addPoint(-0.05f, 6.3f)
        		.addPoint(0.05f, 2.67f)
        		.build()
        	)
        	.addPoint(-0.35f, cubicSpline)
        	.addPoint(-0.25f, cubicSpline)
        	.addPoint(-0.1f, CubicSpline.builder(weirdness)
        		.addPoint(-0.05f, 2.67f)
        		.addPoint(0.05f, 6.3f)
        		.build()
        	)
        	.addPoint(0.03f, cubicSpline);
        if (lerpContinent) {
            CubicSpline<C, I> river = CubicSpline.builder(weirdness)
            	   .addPoint(0.0F, baseFactor)
            	   .addPoint(0.1F, 0.625F)
            	   .build();
            CubicSpline<C, I> riverBanks = CubicSpline.builder(peaksAndValleys)
            	   .addPoint(-0.9F, baseFactor)
            	   .addPoint(-0.69F, river)
            	   .build();
            builder.addPoint(0.35F, baseFactor)
            	   .addPoint(0.45F, riverBanks)
            	   .addPoint(0.55F, riverBanks)
            	   .addPoint(0.62F, baseFactor);
        } else {
            CubicSpline<C, I> cubicSpline2 = CubicSpline.builder(peaksAndValleys)
            	.addPoint(-0.7f, cubicSpline)
            	.addPoint(-0.15f, 1.37f)
            	.build();
            CubicSpline<C, I> cubicSpline3 = CubicSpline.builder(peaksAndValleys)
            	.addPoint(0.45f, cubicSpline)
            	.addPoint(0.7f, 1.56f)
            	.build();
            builder
            	.addPoint(0.05f, cubicSpline3)
            	.addPoint(0.4f, cubicSpline3)
            	.addPoint(0.45f, cubicSpline2)
            	.addPoint(0.55f, cubicSpline2)
            	.addPoint(0.58f, baseFactor);
        }
        return builder.build();
    }

}

