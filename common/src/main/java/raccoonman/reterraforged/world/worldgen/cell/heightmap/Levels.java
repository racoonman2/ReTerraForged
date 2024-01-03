package raccoonman.reterraforged.world.worldgen.cell.heightmap;

import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil;

public class Levels {
    public int worldHeight;
    public float unit;
    public int waterY;
    private int groundY;
    public int groundLevel;
    public int waterLevel;
    public float ground;
    public float water;
    private float elevationRange;
    
    public Levels(int height, int seaLevel) {
        this.worldHeight = Math.max(1, height);
        this.unit = NoiseUtil.div(1, this.worldHeight);
        this.waterLevel = seaLevel;
        this.groundLevel = this.waterLevel + 1;
        this.waterY = Math.min(this.waterLevel - 1, this.worldHeight);
        this.groundY = Math.min(this.groundLevel - 1, this.worldHeight);
        this.ground = NoiseUtil.div(this.groundY, this.worldHeight);
        this.water = NoiseUtil.div(this.waterY, this.worldHeight);
        this.elevationRange = 1.0F - this.water;
    }
    
    public int scale(float value) {
//        if (value >= 1.0F) {
//            return this.worldHeight - 1;
//        }
        return (int) (value * this.worldHeight);
    }
    
    public float elevation(float value) {
        if (value <= this.water) {
            return 0.0F;
        }
        return (value - this.water) / this.elevationRange;
    }
    
    public float elevation(int y) {
        if (y <= this.waterY) {
            return 0.0F;
        }
        return this.scale(y - this.waterY) / this.elevationRange;
    }
    
    public float scale(int level) {
        return NoiseUtil.div(level, this.worldHeight);
    }
    
    public float water(int amount) {
        return NoiseUtil.div(this.waterY + amount, this.worldHeight);
    }
    
    public float ground(int amount) {
        return NoiseUtil.div(this.groundY + amount, this.worldHeight);
    }
}
