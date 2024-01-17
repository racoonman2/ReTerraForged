package raccoonman.reterraforged.world.worldgen.tile.filter;

import raccoonman.reterraforged.world.worldgen.cell.Cell;
import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil;

public interface Modifier {
    float getValueModifier(float value);
    
    default float modify(Cell cell, float value) {
    	float strengthModifier = 1.0F;
        float erosionModifier = cell.terrain.erosionModifier();
        if (erosionModifier != 1.0F) {
            float alpha = NoiseUtil.map(cell.terrainRegionEdge, 0.0F, 0.15F, 0.15F);
            strengthModifier = NoiseUtil.lerp(1.0F, erosionModifier, alpha);
        }
        if (cell.riverMask < 0.1F) {
            strengthModifier *= NoiseUtil.map(cell.riverMask, 0.002F, 0.1F, 0.098F);
        }
        return this.getValueModifier(cell.height) * strengthModifier * value;
    }
    
    default Modifier invert() {
        return v -> 1.0F - this.getValueModifier(v);
    }
    
    public static Modifier range(float minValue, float maxValue) {
        return new Modifier() {
            private final float min = minValue;
            private final float max = maxValue;
            private final float range = maxValue - minValue;
            
            @Override
            public float getValueModifier(float value) {
                if (value > this.max) {
                    return 1.0F;
                }
                if (value < this.min) {
                    return 0.0F;
                }
                return (value - this.min) / this.range;
            }
        };
    }
}
