package raccoonman.reterraforged.world.worldgen.noise.function;

import com.mojang.serialization.Codec;

import net.minecraft.util.StringRepresentable;

public enum DistanceFunction implements StringRepresentable {
    EUCLIDEAN("EUCLIDEAN") {
        @Override
        public float apply(float x, float y) {
            return x * x + y * y;
        }
    }, 
    MANHATTAN("MANHATTAN") {
        @Override
        public float apply(float x, float y) {
            return Math.abs(x) + Math.abs(y);
        }
    }, 
    NATURAL("NATURAL") {
        @Override
        public float apply(float x, float y) {
            return Math.abs(x) + Math.abs(y) + (x * x + y * y);
        }
    };
	
	public static final Codec<DistanceFunction> CODEC = StringRepresentable.fromEnum(DistanceFunction::values);
	
	private String name;
	
	private DistanceFunction(String name) {
		this.name = name;
	}
    
	@Override
	public String getSerializedName() {
		return this.name;
	}
	
    public abstract float apply(float x, float z);
}
