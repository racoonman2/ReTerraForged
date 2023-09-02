//// 
//// Decompiled by Procyon v0.5.36
//// 
//
//package raccoonman.reterraforged.common.level.levelgen.noise.continent;
//
//import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
//import raccoonman.reterraforged.common.level.levelgen.noise.Source;
//import raccoonman.reterraforged.common.level.levelgen.noise.curve.DistanceFunction;
//import raccoonman.reterraforged.common.level.levelgen.noise.curve.EdgeFunction;
//import raccoonman.reterraforged.common.level.levelgen.noise.domain.Domain;
//import raccoonman.reterraforged.common.level.levelgen.noise.util.NoiseUtil;
//import raccoonman.reterraforged.common.level.levelgen.noise.util.Vec2f;
//
//public class MultiContinent implements Noise {
//    private final DistanceFunction distanceFunc;
//    private final float inland;
//    private final float clampMin;
//    private final float clampMax;
//    private final float clampRange;
//    private final float offsetAlpha;
//    private final Domain warp;
//    private final Noise shape;
//    
//    public MultiContinent(final GeneratorContext context) {
//        final int tectonicScale = settings.continent.continentScale * 4;
//        this.continentScale = settings.continent.continentScale / 2;
//        this.distanceFunc = settings.continent.continentShape;
//        this.controlPoints = new ControlPoints(settings.controlPoints);
//        this.frequency = 1.0f / tectonicScale;
//        this.clampMin = 0.2f;
//        this.clampMax = 1.0f;
//        this.clampRange = this.clampMax - this.clampMin;
//        this.offsetAlpha = context.settings.world.continent.continentJitter;
//        this.warp = Domain.warp(Source.PERLIN, 20, 2, 20.0).warp(Domain.warp(Source.SIMPLEX, this.continentScale, 3, this.continentScale));
//        this.shape = Source.simplex(settings.continent.continentScale * 2, 1).bias(0.65).clamp(0.0, 1.0);
//    }
//    
//    @Override
//    public float compute(float x, float y, int seed) {
//        final float ox = this.warp.getOffsetX(x, y, seed);
//        final float oz = this.warp.getOffsetY(x, y, seed);
//        float px = x + ox;
//        float py = y + oz;
//        px *= this.frequency;
//        py *= this.frequency;
//        final int xr = NoiseUtil.floor(px);
//        final int yr = NoiseUtil.floor(py);
//        int cellX = xr;
//        int cellY = yr;
//        float centerX = px;
//        float centerY = py;
//        float edgeDistance = 999999.0f;
//        float edgeDistance2 = 999999.0f;
//        for (int dy = -1; dy <= 1; ++dy) {
//            for (int dx = -1; dx <= 1; ++dx) {
//                final int cx = xr + dx;
//                final int cy = yr + dy;
//                final Vec2f vec = NoiseUtil.cell(seed, cx, cy);
//                final float cxf = cx + vec.x() * this.offsetAlpha;
//                final float cyf = cy + vec.y() * this.offsetAlpha;
//                final float distance = this.distanceFunc.apply(cxf - px, cyf - py);
//                if (distance < edgeDistance) {
//                    edgeDistance2 = edgeDistance;
//                    edgeDistance = distance;
//                    centerX = cxf;
//                    centerY = cyf;
//                    cellX = cx;
//                    cellY = cy;
//                }
//                else if (distance < edgeDistance2) {
//                    edgeDistance2 = distance;
//                }
//            }
//        
//        }
//        float continentEdge = this.cellEdgeValue(edgeDistance, edgeDistance2);
//        return continentEdge * this.getShape(x, y, continentEdge, seed);
//    }
//    
//    protected float cellEdgeValue(final float distance, final float distance2) {
//        final EdgeFunction edge = EdgeFunction.DISTANCE_2_DIV;
//        float value = edge.apply(distance, distance2);
//        value = 1.0F - NoiseUtil.map(value, edge.min(), edge.max(), edge.range());
//        if (value <= this.clampMin) {
//            return 0.0F;
//        }
//        if (value >= this.clampMax) {
//            return 1.0F;
//        }
//        return (value - this.clampMin) / this.clampRange;
//    }
//    
//    protected float getShape(final float x, final float z, final float edgeValue, int seed) {
//        if (edgeValue >= this.inland) {
//            return 1.0f;
//        }
//        final float alpha = edgeValue / this.inland;
//        return this.shape.compute(x, z, seed) * alpha;
//    }
//}
