package raccoonman.reterraforged.common.registries.data.noise;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.Source;
import raccoonman.reterraforged.common.level.levelgen.noise.Valley;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.EdgeFunction;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.Interpolation;
import raccoonman.reterraforged.common.level.levelgen.noise.domain.Domain;

public final class RTFLandForms {
    private static final float TERRAIN_VERTICAL_SCALE = 0.98F;
	
    public static Noise deepOcean() {
    	final int seaLevel = 63;
        final Noise hills = Source.perlin(150, 3).shift(1).scale(seaLevel * 0.7).bias(Source.perlin(200, 1).shift(2).scale(seaLevel * 0.2f));
        final Noise canyons = Source.perlin(150, 4).shift(3).powCurve(0.2).invert().scale(seaLevel * 0.7).bias(Source.perlin(170, 1).shift(4).scale(seaLevel * 0.15f));
        return Source.perlin(500, 1).shift(5).blend(hills, canyons, 0.6, 0.65).warp(50, 2, 50.0).shift(6);
    }
    
    public static Noise steppe() {
        final int scaleH = Math.round(250.0f);
        final double erosionAmount = 0.45;
        final Noise erosion = Source.build(scaleH * 2, 3).lacunarity(3.75).perlin().shift(7).alpha(erosionAmount);
        final Noise warpX = Source.build(scaleH / 4, 3).lacunarity(3.0).perlin().shift(8);
        final Noise warpY = Source.build(scaleH / 4, 3).lacunarity(3.0).perlin().shift(9);
        final Noise module = Source.perlin(scaleH, 1).mul(erosion).warp(warpX, warpY, Source.constant(scaleH / 4.0f)).warp(256, 1, 200.0).shift(10);
        return module.scale(0.08).bias(-0.02);
    }
    
    public static Noise plains() {
        final int scaleH = Math.round(250.0f);
        final double erosionAmount = 0.45;
        final Noise erosion = Source.build(scaleH * 2, 3).lacunarity(3.75).perlin().shift(11).alpha(erosionAmount);
        final Noise warpX = Source.build(scaleH / 4, 3).lacunarity(3.5).perlin().shift(12);
        final Noise warpY = Source.build(scaleH / 4, 3).lacunarity(3.5).perlin().shift(13);
        final Noise module = Source.perlin(scaleH, 1).shift(14).mul(erosion).warp(warpX, warpY, Source.constant(scaleH / 4.0f)).warp(256, 1, 256.0);
        return module.scale(0.15f * TERRAIN_VERTICAL_SCALE).bias(-0.02);
    }
    
    public static Noise plateau() {
        final Noise valley = Source.ridge(500, 1).shift(15).invert().warp(100, 1, 150.0).shift(16).warp(20, 1, 15.0).shift(17);
        final Noise top = Source.build(150, 3).lacunarity(2.45).ridge().shift(18).warp(300, 1, 150.0).shift(19).warp(40, 2, 20.0).shift(20).scale(0.15).mul(valley.clamp(0.02, 0.1).map(0.0, 1.0));
        final Noise surface = Source.perlin(20, 3).shift(21).scale(0.05).warp(40, 2, 20.0).shift(22);
        final Noise module = valley.mul(Source.cubic(500, 1).shift(23).scale(0.6).bias(0.3)).add(top).terrace(Source.constant(0.9), Source.constant(0.15), Source.constant(0.35), 4, 0.4).add(surface);
        return module.scale(0.475 * TERRAIN_VERTICAL_SCALE);
    }
    
    public static Noise hills1() {
        return Source.perlin(200, 3).shift(24).mul(Source.billow(400, 3).alpha(0.5)).shift(25).warp(30, 3, 20.0).shift(26).warp(400, 3, 200.0).shift(27).scale(0.6f * TERRAIN_VERTICAL_SCALE);
    }
    
    public static Noise hills2() {
        return Source.cubic(128, 2).shift(28).mul(Source.perlin(32, 4).shift(29).alpha(0.075)).warp(30, 3, 20.0).shift(30).warp(400, 3, 200.0).shift(31).mul(Source.ridge(512, 2).shift(32).alpha(0.8)).scale(0.55f * TERRAIN_VERTICAL_SCALE);
    }
    
    public static Noise dales() {
        final Noise hills1 = Source.build(300, 4).gain(0.8).lacunarity(4.0).billow().shift(33).powCurve(0.5).scale(0.75);
        final Noise hills2 = Source.build(350, 3).gain(0.8).lacunarity(4.0).billow().shift(34).pow(1.25);
        final Noise combined = Source.perlin(400, 1).shift(35).clamp(0.3, 0.6).map(0.0, 1.0).blend(hills1, hills2, 0.4, 0.75);
        final Noise hills3 = combined.pow(1.125).warp(300, 1, 100.0).shift(36);
        return hills3.scale(0.4);
    }
    
    public static Noise mountains() {
        final int scaleH = Math.round(410.0f);
        final Noise module = Source.build(scaleH, 4).gain(1.15).lacunarity(2.35).ridge().shift(37).mul(Source.perlin(24, 4).shift(38).alpha(0.075)).warp(350, 1, 150.0).shift(39);
        return makeFancy(module).scale(0.7 * TERRAIN_VERTICAL_SCALE);
    }
    
    public static Noise mountains2() {
        final Noise cell = Source.cellEdge(360, EdgeFunction.DISTANCE_2).shift(40).scale(1.2).clamp(0.0, 1.0).warp(200, 2, 100.0).shift(41);
        final Noise blur = Source.perlin(10, 1).shift(42).alpha(0.025);
        final Noise surface = Source.ridge(125, 4).shift(43).alpha(0.37);
        final Noise mountains = cell.clamp(0.0, 1.0).mul(blur).mul(surface).pow(1.1);
        return makeFancy(mountains).scale(0.645 * TERRAIN_VERTICAL_SCALE);
    }
    
    public static Noise mountains3() {
        final Noise cell = Source.cellEdge(400, EdgeFunction.DISTANCE_2).shift(44).scale(1.2).clamp(0.0, 1.0).warp(200, 2, 100.0).shift(45);
        final Noise blur = Source.perlin(10, 1).shift(46).alpha(0.025);
        final Noise surface = Source.ridge(125, 4).shift(47).alpha(0.37);
        final Noise mountains = cell.clamp(0.0, 1.0).mul(blur).mul(surface).pow(1.1);
        final Noise terraced = mountains.terrace(Source.perlin(50, 1).shift(48).scale(0.5), Source.perlin(100, 1).shift(49).clamp(0.5, 0.95).map(0.0, 1.0), Source.constant(0.45), 0.20000000298023224, 0.44999998807907104, 24, 1);
        return makeFancy(terraced).scale(0.645 * TERRAIN_VERTICAL_SCALE);
    }
    
    public static Noise badlands() {
        final Noise mask = Source.build(270, 3).perlin().shift(50).clamp(0.35, 0.65).map(0.0, 1.0);
        final Noise hills = Source.ridge(275, 4).shift(51).warp(400, 2, 100.0).shift(52).warp(18, 1, 20.0).shift(53).mul(mask);
        final double modulation = 0.4;
        final double alpha = 1.0 - modulation;
        final Noise mod1 = hills.warp(100, 1, 50.0).shift(54).scale(modulation);
        final Noise lowFreq = hills.steps(4, 0.6, 0.7).scale(alpha).add(mod1);
        final Noise highFreq = hills.steps(10, 0.6, 0.7).scale(alpha).add(mod1);
        final Noise detail = lowFreq.add(highFreq);
        final Noise mod2 = hills.mul(Source.perlin(200, 3).shift(55).scale(modulation));
        final Noise shape = hills.steps(4, 0.65, 0.75, Interpolation.CURVE3).scale(alpha).add(mod2).scale(alpha);
        final Noise badlands = shape.mul(detail.alpha(0.5));
        return badlands.scale(0.55).bias(0.025);
    }
    
    public static Noise torridonian() {
        final Noise plains = Source.perlin(100, 3).shift(56).warp(300, 1, 150.0).shift(57).warp(20, 1, 40.0).shift(58).scale(0.15);
        final Noise hills = Source.perlin(150, 4).shift(59).warp(300, 1, 200.0).shift(60).warp(20, 2, 20.0).shift(61).boost();
        final Noise module = Source.perlin(200, 3).shift(62).blend(plains, hills, 0.6, 0.6).terrace(Source.perlin(120, 1).shift(63).scale(0.25), Source.perlin(200, 1).shift(64).scale(0.5).bias(0.5), Source.constant(0.5), 0.0, 0.3, 6, 1).boost();
        return module.scale(0.5);
    }
    
    private static Noise makeFancy(final Noise module) {
    	final Domain warp = Domain.direction(Source.perlin(10, 1).shift(65), Source.constant(2.0));
    	final Valley erosion = new Valley(module, 2, 0.65f, 128.0f, 0.15f, 3.1f, 0.8f, Valley.Mode.CONSTANT);
    	return erosion.warp(warp);
    }
}
