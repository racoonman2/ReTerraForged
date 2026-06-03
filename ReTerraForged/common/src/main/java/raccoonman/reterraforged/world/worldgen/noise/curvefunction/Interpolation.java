package raccoonman.reterraforged.world.worldgen.noise.curvefunction;

import com.mojang.serialization.MapCodec;

import net.minecraft.util.StringRepresentable;
import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil;

public enum Interpolation implements CurveFunction, StringRepresentable {
    LINEAR("linear") {
    	
        @Override
        public float apply(float f) {
            return f;
        }
    }, 
    CURVE3("curve3") {
    	
        @Override
        public float apply(float f) {
        	return NoiseUtil.interpHermite(f);
        }
    }, 
    CURVE4("curve4") {

        @Override
        public float apply(float f) {
        	return NoiseUtil.interpQuintic(f);
        }
    };
	
	public static final MapCodec<Interpolation> CODEC = StringRepresentable.fromEnum(Interpolation::values).fieldOf("interpolation");
	
	private String name;
	
	private Interpolation(String name) {
		this.name = name;
	}

	@Override
	public String getSerializedName() {
		return this.name;
	}
	
	@Override
	public MapCodec<Interpolation> codec()	{
		return CODEC;
	}
}
