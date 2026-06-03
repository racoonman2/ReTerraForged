package raccoonman.reterraforged.world.worldgen.noise.curvefunction;

import com.mojang.serialization.Codec;

import net.minecraft.util.StringRepresentable;
import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil;

public enum CellFunction implements StringRepresentable {
    VALUE("value") {
        
    	@Override
        public float apply(int seed, int xc, int yc, float distance, NoiseUtil.Vec2f vec2f) {
            return NoiseUtil.valCoord2D(seed, xc, yc);
        }
    },
    DISTANCE("distance") {
        
    	@Override
        public float apply(int seed, int xc, int yc, float distance, NoiseUtil.Vec2f vec2f) {
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
	
    public abstract float apply(int seed, int xc, int yc, float distance, NoiseUtil.Vec2f vec2f);
    
    public float mapValue(float value, float min, float max, float range) {
        return NoiseUtil.map(value, min, max, range);
    }
}
