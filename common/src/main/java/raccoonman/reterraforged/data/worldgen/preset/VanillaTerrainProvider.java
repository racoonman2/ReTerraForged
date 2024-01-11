package raccoonman.reterraforged.data.worldgen.preset;

import net.minecraft.util.CubicSpline;
import net.minecraft.util.ToFloatFunction;

// this is only for reference because the original is impossible to read
class TerrainProvider {

    public static <C, I extends ToFloatFunction<C>> CubicSpline<C, I> overworldFactor(I continentalness, I erosion, I ridges, I peaksAndValleys) {
        return CubicSpline.builder(continentalness)
        	.addPoint(-0.19F, 3.95F)
        	.addPoint(-0.15F, TerrainProvider.getErosionFactor(erosion, ridges, peaksAndValleys, 6.25F, true))
        	.addPoint(-0.1F, TerrainProvider.getErosionFactor(erosion, ridges, peaksAndValleys, 5.47F, true))
        	.addPoint(0.03F, TerrainProvider.getErosionFactor(erosion, ridges, peaksAndValleys, 5.08F, true))
        	.addPoint(0.06F, TerrainProvider.getErosionFactor(erosion, ridges, peaksAndValleys, 4.69F, false)).build();
    }

    private static <C, I extends ToFloatFunction<C>> CubicSpline<C, I> getErosionFactor(I erosion, I ridges, I peaksAndValleys, float baseFactor, boolean hasRivers) {
        CubicSpline<C, I> cubicSpline = CubicSpline.builder(ridges)
        	.addPoint(-0.2F, 6.3F)
        	.addPoint(0.2F, baseFactor)
        	.build();
        CubicSpline.Builder<C, I> builder = CubicSpline.builder(erosion)
        	.addPoint(-0.6f, cubicSpline)
        	.addPoint(-0.5f, CubicSpline.builder(ridges)
        		.addPoint(-0.05F, 6.3F).addPoint(0.05F, 2.67F)
        		.build()
        	)
        	.addPoint(-0.35F, cubicSpline)
        	.addPoint(-0.25F, cubicSpline)
        	.addPoint(-0.1F, CubicSpline.builder(ridges)
        		.addPoint(-0.05F, 2.67F)
        		.addPoint(0.05F, 6.3F)
        		.build()
        	)
        	.addPoint(0.03F, cubicSpline);
        if (hasRivers) {
            CubicSpline<C, I> cubicSpline2 = CubicSpline.builder(ridges)
            	.addPoint(0.0F, baseFactor)
            	.addPoint(0.1F, 0.625F)
            	.build();
            CubicSpline<C, I> cubicSpline3 = CubicSpline.builder(peaksAndValleys)
            	.addPoint(-0.9F, baseFactor)
            	.addPoint(-0.69F, cubicSpline2)
            	.build();
            builder.addPoint(0.35F, baseFactor)
            	   .addPoint(0.45F, cubicSpline3)
            	   .addPoint(0.55F, cubicSpline3)
            	   .addPoint(0.62F, baseFactor);
        } else {
            CubicSpline<C, I> cubicSpline2 = CubicSpline.builder(peaksAndValleys)
            	.addPoint(-0.7F, cubicSpline)
            	.addPoint(-0.15F, 1.37F)
            	.build();
            CubicSpline<C, I> cubicSpline3 = CubicSpline.builder(peaksAndValleys)
            	.addPoint(0.45F, cubicSpline)
            	.addPoint(0.7F, 1.56F)
            	.build();
            builder.addPoint(0.05F, cubicSpline3)
            	   .addPoint(0.4F, cubicSpline3)
            	   .addPoint(0.45F, cubicSpline2)
            	   .addPoint(0.55F, cubicSpline2)
            	   .addPoint(0.58F, baseFactor);
        }
        return builder.build();
    }

}

