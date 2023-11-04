package raccoonman.reterraforged.common.level.levelgen.test.world.rivermap.wetland;

import raccoonman.reterraforged.common.level.levelgen.noise.NoiseUtil;
import raccoonman.reterraforged.common.level.levelgen.test.util.Variance;
import raccoonman.reterraforged.common.worldgen.data.preset.RiverSettings;

public class WetlandConfig {
    public int skipSize;
    public Variance length;
    public Variance width;
    
    public WetlandConfig(RiverSettings.Wetland settings) {
        this.skipSize = Math.max(1, NoiseUtil.round((1.0F - settings.chance) * 10.0F));
        this.length = Variance.of(settings.sizeMin, settings.sizeMax);
        this.width = Variance.of(50.0F, 150.0F);
    }
}
