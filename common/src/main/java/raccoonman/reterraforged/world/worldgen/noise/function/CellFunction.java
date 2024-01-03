package raccoonman.reterraforged.world.worldgen.noise.function;

import com.mojang.serialization.Codec;

import net.minecraft.util.StringRepresentable;
import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil;
import raccoonman.reterraforged.world.worldgen.noise.module.Noise;

public enum CellFunction implements StringRepresentable {
    CELL_VALUE("CELL_VALUE") {
        
    	@Override
        public float apply(int seed, int xc, int yc, float distance, NoiseUtil.Vec2f vec2f, Noise lookup) {
            return NoiseUtil.valCoord2D(seed, xc, yc);
        }
    }, 
    NOISE_LOOKUP("NOISE_LOOKUP") {
    	
        @Override
        public float apply(int seed, int xc, int yc, float distance, NoiseUtil.Vec2f vec2f, Noise lookup) {
            return lookup.compute(xc + vec2f.x(), yc + vec2f.y(), seed);
        }
        
        @Override
        public float mapValue(float value, float min, float max, float range) {
            return value;
        }
    }, 
    DISTANCE("DISTANCE") {
        
    	@Override
        public float apply(int seed, int xc, int yc, float distance, NoiseUtil.Vec2f vec2f, Noise lookup) {
            return distance - 1.0F;
        }
        
        @Override
        public float mapValue(float value, float min, float max, float range) {
            return 0.0F;
        }
    };
	
	public static final Codec<CellFunction> CODEC = StringRepresentable.fromEnum(CellFunction::values);
    
	private String name;
	
	private CellFunction(String name) {
		this.name = name;
	}
	
	@Override
	public String getSerializedName() {
		return this.name;
	}
	
    public abstract float apply(int seed, int xc, int yc, float distance, NoiseUtil.Vec2f vec2f, Noise lookup);
    
    public float mapValue(float value, float min, float max, float range) {
        return NoiseUtil.map(value, min, max, range);
    }
}
