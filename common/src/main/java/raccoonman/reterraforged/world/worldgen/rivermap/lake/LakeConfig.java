package raccoonman.reterraforged.world.worldgen.rivermap.lake;

import raccoonman.reterraforged.data.preset.settings.RiverSettings;
import raccoonman.reterraforged.world.worldgen.heightmap.Levels;

public class LakeConfig {
    public float depth;
    public float chance;
    public float sizeMin;
    public float sizeMax;
    public float sizeRange;
    public float bankMin;
    public float bankMax;
    public float distanceMin;
    public float distanceMax;
    
    private LakeConfig(Builder builder) {
        this.depth = builder.depth;
        this.chance = builder.chance;
        this.sizeMin = builder.sizeMin;
        this.sizeMax = builder.sizeMax;
        this.sizeRange = this.sizeMax - this.sizeMin;
        this.bankMin = builder.bankMin;
        this.bankMax = builder.bankMax;
        this.distanceMin = builder.distanceMin;
        this.distanceMax = builder.distanceMax;
    }
    
    public static LakeConfig of(RiverSettings.Lake settings, Levels levels) {
        Builder builder = new Builder();
        builder.chance = settings.chance;
        builder.sizeMin = settings.sizeMin;
        builder.sizeMax = settings.sizeMax;
        builder.depth = levels.water(-settings.depth);
        builder.distanceMin = settings.minStartDistance;
        builder.distanceMax = settings.maxStartDistance;
        builder.bankMin = levels.water(settings.minBankHeight);
        builder.bankMax = levels.water(settings.maxBankHeight);
        return new LakeConfig(builder);
    }
    
    public static class Builder {
        public float chance;
        public float depth;
        public float sizeMin;
        public float sizeMax;
        public float bankMin;
        public float bankMax;
        public float distanceMin;
        public float distanceMax;
        
        public Builder() {
            this.depth = 10.0F;
            this.sizeMin = 30.0F;
            this.sizeMax = 100.0F;
            this.bankMin = 1.0F;
            this.bankMax = 8.0F;
            this.distanceMin = 0.025F;
            this.distanceMax = 0.05F;
        }
    }
}
