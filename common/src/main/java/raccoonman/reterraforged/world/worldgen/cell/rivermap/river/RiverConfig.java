package raccoonman.reterraforged.world.worldgen.cell.rivermap.river;

import raccoonman.reterraforged.world.worldgen.cell.heightmap.Levels;
import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil;

public class RiverConfig {
    public int order;
    public boolean main;
    public int bedWidth;
    public int bankWidth;
    public float bedHeight;
    public float minBankHeight;
    public float maxBankHeight;
    public int length;
    public int length2;
    public float fade;
    
    private RiverConfig(Builder builder) {
        this.main = builder.main;
        this.order = builder.order;
        this.bedWidth = builder.bedWidth;
        this.bankWidth = builder.bankWidth;
        this.bedHeight = builder.levels.water(-builder.bedDepth);
        this.minBankHeight = builder.levels.water(builder.minBankHeight);
        this.maxBankHeight = builder.levels.water(builder.maxBankHeight);
        this.length = builder.length;
        this.length2 = builder.length * builder.length;
        this.fade = builder.fade;
    }
    
    public RiverConfig(boolean main, int order, int bedWidth, int bankWidth, float bedHeight, float minBankHeight, float maxBankHeight, int length, int length2, float fade) {
        this.main = main;
        this.order = order;
        this.bedWidth = bedWidth;
        this.bankWidth = bankWidth;
        this.bedHeight = bedHeight;
        this.minBankHeight = minBankHeight;
        this.maxBankHeight = maxBankHeight;
        this.length = length;
        this.length2 = length2;
        this.fade = fade;
    }
    
    public RiverConfig createFork(float connectWidth, Levels levels) {
        if (this.bankWidth < connectWidth) {
            return this;
        }
        float scale = this.bankWidth / connectWidth;
        return this.createFork(levels.scale(this.bedHeight), NoiseUtil.round(this.bedWidth / scale), NoiseUtil.round(this.bankWidth / scale), levels);
    }
    
    public RiverConfig createFork(int bedHeight, int bedWidth, int bankWidth, Levels levels) {
        int minBankHeight = Math.max(levels.groundLevel, levels.scale(this.minBankHeight) - 1);
        int maxBankHeight = Math.max(minBankHeight, levels.scale(this.maxBankHeight) - 1);
        return new RiverConfig(false, this.order + 1, bedWidth, bankWidth, levels.scale(bedHeight), levels.scale(minBankHeight), levels.scale(maxBankHeight), this.length, this.length2, this.fade);
    }
    
    public static Builder builder(Levels levels) {
        return new Builder(levels);
    }
    
    public static class Builder {
        private boolean main;
        private int order;
        private int bedWidth;
        private int bankWidth;
        private int bedDepth;
        private int maxBankHeight;
        private int minBankHeight;
        private int length;
        private float fade;
        private Levels levels;
        
        private Builder(Levels levels) {
            this.main = false;
            this.order = 0;
            this.bedWidth = 4;
            this.bankWidth = 15;
            this.bedDepth = 5;
            this.maxBankHeight = 1;
            this.minBankHeight = 1;
            this.length = 1000;
            this.fade = 0.2F;
            this.levels = levels;
        }
        
        public Builder order(int order) {
            this.order = order;
            return this;
        }
        
        public Builder main(boolean value) {
            this.main = value;
            return this;
        }
        
        public Builder bedWidth(int value) {
            this.bedWidth = value;
            return this;
        }
        
        public Builder bankWidth(int value) {
            this.bankWidth = value;
            return this;
        }
        
        public Builder bedDepth(int depth) {
            this.bedDepth = depth;
            return this;
        }
        
        public Builder bankHeight(int min, int max) {
            this.minBankHeight = Math.min(min, max);
            this.maxBankHeight = Math.max(min, max);
            return this;
        }
        
        public Builder length(int value) {
            this.length = value;
            return this;
        }
        
        public Builder fade(float value) {
            this.fade = value;
            return this;
        }
        
        public RiverConfig build() {
            return new RiverConfig(this);
        }
    }
}
