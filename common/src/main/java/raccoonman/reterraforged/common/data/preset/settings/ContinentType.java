package raccoonman.reterraforged.common.data.preset.settings;

import com.mojang.serialization.Codec;

import net.minecraft.util.StringRepresentable;
import raccoonman.reterraforged.common.level.levelgen.noise.Noise;

public enum ContinentType implements StringRepresentable {
    MULTI {
        
		@Override
		public Noise create(int seed) {
			throw new UnsupportedOperationException("TODO");
		}
    }, 
    SINGLE {

		@Override
		public Noise create(int seed) {
			throw new UnsupportedOperationException("TODO");
		}
    }, 
    MULTI_IMPROVED {

    	@Override
		public Noise create(int seed) {
			return null;
		}
    }, 
    EXPERIMENTAL {
        
		@Override
		public Noise create(int seed) {
			throw new UnsupportedOperationException("TODO");
		}
    };
	
	public static final Codec<ContinentType> CODEC = StringRepresentable.fromEnum(ContinentType::values);

    public abstract Noise create(int seed);
    
    @Override
    public String getSerializedName() {
    	return this.name();
    }
}
