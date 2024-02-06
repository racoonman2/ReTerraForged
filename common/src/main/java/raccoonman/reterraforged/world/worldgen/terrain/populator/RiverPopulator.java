package raccoonman.reterraforged.world.worldgen.terrain.populator;

import java.util.Random;

import raccoonman.reterraforged.world.worldgen.biome.Erosion;
import raccoonman.reterraforged.world.worldgen.cell.Cell;
import raccoonman.reterraforged.world.worldgen.heightmap.Levels;
import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil;
import raccoonman.reterraforged.world.worldgen.noise.function.CurveFunction;
import raccoonman.reterraforged.world.worldgen.noise.function.CurveFunctions;
import raccoonman.reterraforged.world.worldgen.noise.module.Line;
import raccoonman.reterraforged.world.worldgen.rivermap.river.Range;
import raccoonman.reterraforged.world.worldgen.rivermap.river.River;
import raccoonman.reterraforged.world.worldgen.rivermap.river.RiverConfig;
import raccoonman.reterraforged.world.worldgen.rivermap.river.RiverWarp;
import raccoonman.reterraforged.world.worldgen.terrain.TerrainType;

public class RiverPopulator implements Comparable<RiverPopulator> {
    public boolean main;
    private boolean connecting;
    private float fade;
    private float fadeInv;
    private Range bedWidth;
    private Range banksWidth;
    private Range valleyWidth;
    private Range bedDepth;
    private Range banksDepth;
    private float waterLine;
    public River river;
    public RiverWarp warp;
    public RiverConfig config;
    public CurveFunction valleyCurve;
    
    public RiverPopulator(River river, RiverWarp warp, RiverConfig config, Settings settings, Levels levels) {
        this.fade = settings.fadeIn;
        this.fadeInv = 1.0F / settings.fadeIn;
        this.bedWidth = new Range(0.25F, (float)(config.bedWidth * config.bedWidth));
        this.banksWidth = new Range(1.5625F, (float)(config.bankWidth * config.bankWidth));
        this.valleyWidth = new Range(settings.valleySize * settings.valleySize, settings.valleySize * settings.valleySize);
        this.river = river;
        this.warp = warp;
        this.config = config;
        this.main = config.main;
        this.connecting = settings.connecting;
        this.waterLine = levels.water;
        this.bedDepth = new Range(levels.water, config.bedHeight);
        this.banksDepth = new Range(config.minBankHeight, config.maxBankHeight);
        this.valleyCurve = settings.valleyCurve;
    }

    @Override
    public int compareTo(RiverPopulator o) {
        return Integer.compare(this.config.order, o.config.order);
    }
    
    public void apply(Cell cell, float px, float pz, float pt, float x, float z, float t) {
        float d2 = this.getDistance2(x, z, t);
        float pd2 = this.getDistance2(px, pz, pt);
        float valleyAlpha = this.getDistanceAlpha(pt, Math.min(d2, pd2), this.valleyWidth);
        if (valleyAlpha == 0.0F) {
            return;
        }
        float bankHeight = this.getScaledSize(t, this.banksDepth);
        valleyAlpha = this.valleyCurve.apply(valleyAlpha);

        float mouthModifier = getMouthModifier(cell);
        float bedHeight = this.getScaledSize(t, this.bedDepth);
        
        cell.riverMask = Math.min(cell.riverMask, 1.0F - valleyAlpha);
        cell.height = Math.min(NoiseUtil.lerp(cell.height, bankHeight, valleyAlpha), cell.height);

        float banksAlpha = this.getDistanceAlpha(t, d2 * mouthModifier, this.banksWidth);
        if (banksAlpha == 0.0F) {
            return;
        }
        if (cell.height > bedHeight) {
            cell.height = Math.min(NoiseUtil.lerp(cell.height, bedHeight, banksAlpha), cell.height);
            this.tag(cell, bedHeight);
        }
        
        float bedAlpha = this.getDistanceAlpha(t, d2, this.bedWidth);

        if (bedAlpha != 0.0F && cell.height > bedHeight) {
            cell.height = NoiseUtil.lerp(cell.height, bedHeight, bedAlpha);
            this.tag(cell, bedHeight);
        }
    }

    public RiverConfig createForkConfig(float t, Levels levels) {
        int bedHeight = levels.scale(this.getScaledSize(t, this.bedDepth));
        int bedWidth = (int)Math.round(Math.sqrt(this.getScaledSize(t, this.bedWidth)) * 0.75);
        int bankWidth = (int)Math.round(Math.sqrt(this.getScaledSize(t, this.banksWidth)) * 0.75);
        bedWidth = Math.max(1, bedWidth);
        bankWidth = Math.max(bedWidth + 1, bankWidth);
        return this.config.createFork(bedHeight, bedWidth, bankWidth, levels);
    }
    
    private float getDistance2(float x, float y, float t) {
        if (t <= 0.0F) {
            return Line.distSq(x, y, this.river.x1, this.river.z1);
        }
        if (t >= 1.0F) {
            return Line.distSq(x, y, this.river.x2, this.river.z2);
        }
        float px = this.river.x1 + t * this.river.dx;
        float py = this.river.z1 + t * this.river.dz;
        return Line.distSq(x, y, px, py);
    }
    
    private float getDistanceAlpha(float t, float dist2, Range range) {
        float size2 = this.getScaledSize(t, range);
        if (dist2 >= size2) {
            return 0.0F;
        }
        return 1.0F - dist2 / size2;
    }
    
    private float getScaledSize(float t, Range range) {
        if (t < 0.0F) {
            return range.min();
        }
        if (t > 1.0F) {
            return range.max();
        }
        if (range.min() == range.max()) {
            return range.min();
        }
        if (t >= this.fade) {
            return range.max();
        }
        return NoiseUtil.lerp(range.min(), range.max(), t * this.fadeInv);
    }
    
    private void tag(Cell cell, float bedHeight) {
        if (cell.terrain.overridesRiver() && (cell.height < bedHeight || cell.height > this.waterLine)) {
            return;
        }
        cell.erosionMask = true;
        if (cell.height <= this.waterLine) {
            cell.terrain = TerrainType.RIVER;
        }
    }
    
    private static float getMouthModifier(Cell cell) {
        float modifier = NoiseUtil.map(cell.continentEdge, 0.0F, 0.5F, 0.5F);
        modifier *= modifier;
        return modifier;
    }
    
    public static CurveFunction getValleyType(Random random) {
        int value = random.nextInt(100);
        if (value < 5) {
            return CurveFunctions.scurve(0.4F, 1.0F);
        }
        if (value < 30) {
            return CurveFunctions.scurve(4.0F, 5.0F);
        }
        if (value < 50) {
            return CurveFunctions.scurve(3.0F, 0.25F);
        }
        return CurveFunctions.scurve(2.0F, -0.5F);
    }
    
    public static RiverPopulator create(float x1, float z1, float x2, float z2, RiverConfig config, Levels levels, Random random) {
        River river = new River(x1, z1, x2, z2);
        RiverWarp warp = RiverWarp.create(0.35F, random);
        float valleyWidth = 275.0F * River.MAIN_VALLEY.next(random);
        Settings settings = creatSettings(random);
        settings.connecting = false;
        settings.fadeIn = config.fade;
        settings.valleySize = valleyWidth;
        return new RiverPopulator(river, warp, config, settings, levels);
    }
    
    private static Settings creatSettings(Random random) {
        Settings settings = new Settings();
        settings.valleyCurve = getValleyType(random);
        return settings;
    }
    
    public static class Settings {
        public float valleySize;
        public float fadeIn;
        public boolean connecting;
        public CurveFunction valleyCurve;
        
        public Settings() {
            this.valleySize = 275.0F;
            this.fadeIn = 0.7F;
            this.connecting = false;
            this.valleyCurve = CurveFunctions.scurve(2.0F, -0.5F);
        }
    }
}
