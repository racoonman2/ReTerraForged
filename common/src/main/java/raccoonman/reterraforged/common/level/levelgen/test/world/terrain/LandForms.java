package raccoonman.reterraforged.common.level.levelgen.test.world.terrain;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.Seed;
import raccoonman.reterraforged.common.level.levelgen.noise.Source;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.EdgeFunction;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.Interpolation;
import raccoonman.reterraforged.common.level.levelgen.noise.domain.Domain;
import raccoonman.reterraforged.common.level.levelgen.test.module.Ridge;
import raccoonman.reterraforged.common.level.levelgen.test.world.heightmap.Levels;
import raccoonman.reterraforged.common.worldgen.data.preset.TerrainSettings;

public class LandForms {
    private TerrainSettings settings;
    private float terrainVerticalScale;
    private float seaLevel;
    private Noise ground;
    
    public LandForms(TerrainSettings settings, Levels levels, Noise ground) {
        this.settings = settings;
        this.ground = ground;
        this.terrainVerticalScale = settings.general.globalVerticalScale;
        this.seaLevel = levels.water;
    }
    
    public Noise getOceanBase() {
        return Source.ZERO;
    }
    
    public Noise getLandBase() {
        return this.ground;
    }
    
    public Noise deepOcean(int seed) {
        Noise hills = Source.perlin(++seed, 150, 3).scale(this.seaLevel * 0.7).bias(Source.perlin(++seed, 200, 1).scale(this.seaLevel * 0.2f));
        Noise canyons = Source.perlin(++seed, 150, 4).powCurve(0.2).invert().scale(this.seaLevel * 0.7).bias(Source.perlin(++seed, 170, 1).scale(this.seaLevel * 0.15f));
        return Source.perlin(++seed, 500, 1).blend(hills, canyons, 0.6, 0.65).warp(++seed, 50, 2, 50.0);
    }
    
    public Noise steppe(Seed seed) {
        int scaleH = Math.round(250.0f * this.settings.steppe.horizontalScale);
        double erosionAmount = 0.45;
        Noise erosion = Source.build(seed.next(), scaleH * 2, 3).lacunarity(3.75).perlin().alpha(erosionAmount);
        Noise warpX = Source.build(seed.next(), scaleH / 4, 3).lacunarity(3.0).perlin();
        Noise warpY = Source.build(seed.next(), scaleH / 4, 3).lacunarity(3.0).perlin();
        Noise Noise = Source.perlin(seed.next(), scaleH, 1).mul(erosion).warp(warpX, warpY, Source.constant(scaleH / 4.0f)).warp(seed.next(), 256, 1, 200.0);
        return Noise.scale(0.08).bias(-0.02);
    }
    
    public Noise plains(Seed seed) {
        int scaleH = Math.round(250.0f * this.settings.plains.horizontalScale);
        double erosionAmount = 0.45;
        Noise erosion = Source.build(seed.next(), scaleH * 2, 3).lacunarity(3.75).perlin().alpha(erosionAmount);
        Noise warpX = Source.build(seed.next(), scaleH / 4, 3).lacunarity(3.5).perlin();
        Noise warpY = Source.build(seed.next(), scaleH / 4, 3).lacunarity(3.5).perlin();
        Noise Noise = Source.perlin(seed.next(), scaleH, 1).mul(erosion).warp(warpX, warpY, Source.constant(scaleH / 4.0f)).warp(seed.next(), 256, 1, 256.0);
        return Noise.scale(0.15f * this.terrainVerticalScale).bias(-0.02);
    }
    
    public Noise plateau(Seed seed) {
        Noise valley = Source.ridge(seed.next(), 500, 1).invert().warp(seed.next(), 100, 1, 150.0).warp(seed.next(), 20, 1, 15.0);
        Noise top = Source.build(seed.next(), 150, 3).lacunarity(2.45).ridge().warp(seed.next(), 300, 1, 150.0).warp(seed.next(), 40, 2, 20.0).scale(0.15).mul(valley.clamp(0.02, 0.1).map(0.0, 1.0));
        Noise surface = Source.perlin(seed.next(), 20, 3).scale(0.05).warp(seed.next(), 40, 2, 20.0);
        Noise module = valley.mul(Source.cubic(seed.next(), 500, 1).scale(0.6).bias(0.3)).add(top).terrace(Source.constant(0.9), Source.constant(0.15), Source.constant(0.35), 4, 0.4).add(surface);
        return module.scale(0.475 * this.terrainVerticalScale);
    }
    
    public Noise hills1(Seed seed) {
        return Source.perlin(seed.next(), 200, 3).mul(Source.billow(seed.next(), 400, 3).alpha(0.5)).warp(seed.next(), 30, 3, 20.0).warp(seed.next(), 400, 3, 200.0).scale(0.6f * this.terrainVerticalScale);
    }
    
    public Noise hills2(Seed seed) {
        return Source.cubic(seed.next(), 128, 2).mul(Source.perlin(seed.next(), 32, 4).alpha(0.075)).warp(seed.next(), 30, 3, 20.0).warp(seed.next(), 400, 3, 200.0).mul(Source.ridge(seed.next(), 512, 2).alpha(0.8)).scale(0.55f * this.terrainVerticalScale);
    }
    
    public Noise dales(Seed seed) {
        Noise hills1 = Source.build(seed.next(), 300, 4).gain(0.8).lacunarity(4.0).billow().powCurve(0.5).scale(0.75);
        Noise hills2 = Source.build(seed.next(), 350, 3).gain(0.8).lacunarity(4.0).billow().pow(1.25);
        Noise combined = Source.perlin(seed.next(), 400, 1).clamp(0.3, 0.6).map(0.0, 1.0).blend(hills1, hills2, 0.4, 0.75);
        Noise hills3 = combined.pow(1.125).warp(seed.next(), 300, 1, 100.0);
        return hills3.scale(0.4);
    }
    
    public Noise mountains(Seed seed) {
        int scaleH = Math.round(410.0f * this.settings.mountains.horizontalScale);
        Noise Noise = Source.build(seed.next(), scaleH, 4).gain(1.15).lacunarity(2.35).ridge().mul(Source.perlin(seed.next(), 24, 4).alpha(0.075)).warp(seed.next(), 350, 1, 150.0);
        return this.makeFancy(seed, Noise).scale(0.7 * this.terrainVerticalScale);
    }
    
    public Noise mountains2(Seed seed) {
        Noise cell = Source.cellEdge(seed.next(), 360, EdgeFunction.DISTANCE_2).scale(1.2).clamp(0.0, 1.0).warp(seed.next(), 200, 2, 100.0);
        Noise blur = Source.perlin(seed.next(), 10, 1).alpha(0.025);
        Noise surface = Source.ridge(seed.next(), 125, 4).alpha(0.37);
        Noise mountains = cell.clamp(0.0, 1.0).mul(blur).mul(surface).pow(1.1);
        return this.makeFancy(seed, mountains).scale(0.645 * this.terrainVerticalScale);
    }
    
    public Noise mountains3(Seed seed) {
        Noise cell = Source.cellEdge(seed.next(), 400, EdgeFunction.DISTANCE_2).scale(1.2).clamp(0.0, 1.0).warp(seed.next(), 200, 2, 100.0);
        Noise blur = Source.perlin(seed.next(), 10, 1).alpha(0.025);
        Noise surface = Source.ridge(seed.next(), 125, 4).alpha(0.37);
        Noise mountains = cell.clamp(0.0, 1.0).mul(blur).mul(surface).pow(1.1);
        Noise terraced = mountains.terrace(Source.perlin(seed.next(), 50, 1).scale(0.5), Source.perlin(seed.next(), 100, 1).clamp(0.5, 0.95).map(0.0, 1.0), Source.constant(0.45), 0.20000000298023224, 0.44999998807907104, 24, 1);
        return this.makeFancy(seed, terraced).scale(0.645 * this.terrainVerticalScale);
    }
    
    public Noise badlands(Seed seed) {
        Noise mask = Source.build(seed.next(), 270, 3).perlin().clamp(0.35, 0.65).map(0.0, 1.0);
        Noise hills = Source.ridge(seed.next(), 275, 4).warp(seed.next(), 400, 2, 100.0).warp(seed.next(), 18, 1, 20.0).mul(mask);
        double modulation = 0.4;
        double alpha = 1.0 - modulation;
        Noise mod1 = hills.warp(seed.next(), 100, 1, 50.0).scale(modulation);
        Noise lowFreq = hills.steps(4, 0.6, 0.7).scale(alpha).add(mod1);
        Noise highFreq = hills.steps(10, 0.6, 0.7).scale(alpha).add(mod1);
        Noise detail = lowFreq.add(highFreq);
        Noise mod2 = hills.mul(Source.perlin(seed.next(), 200, 3).scale(modulation));
        Noise shape = hills.steps(4, 0.65, 0.75, Interpolation.CURVE3).scale(alpha).add(mod2).scale(alpha);
        Noise badlands = shape.mul(detail.alpha(0.5));
        return badlands.scale(0.55).bias(0.025);
    }
    
    public Noise torridonian(Seed seed) {
        Noise plains = Source.perlin(seed.next(), 100, 3).warp(seed.next(), 300, 1, 150.0).warp(seed.next(), 20, 1, 40.0).scale(0.15);
        Noise hills = Source.perlin(seed.next(), 150, 4).warp(seed.next(), 300, 1, 200.0).warp(seed.next(), 20, 2, 20.0).boost();
        Noise Noise = Source.perlin(seed.next(), 200, 3).blend(plains, hills, 0.6, 0.6).terrace(Source.perlin(seed.next(), 120, 1).scale(0.25), Source.perlin(seed.next(), 200, 1).scale(0.5).bias(0.5), Source.constant(0.5), 0.0, 0.3, 6, 1).boost();
        return Noise.scale(0.5);
    }
    
    private Noise makeFancy(Seed seed, Noise noise) {
        if (this.settings.general.fancyMountains) {
            Domain warp = Domain.direction(Source.perlin(seed.next(), 10, 1), Source.constant(2.0));
            Ridge erosion = new Ridge(seed.next(), 2, 0.65f, 128.0f, 0.15f, 3.1f, 0.8f, Ridge.Mode.CONSTANT);
            return erosion.wrap(noise).warp(warp);
        }
        return noise;
    }
}
