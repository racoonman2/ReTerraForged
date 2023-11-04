package raccoonman.reterraforged.common.worldgen.data.preset;

import com.mojang.serialization.Codec;

import net.minecraft.util.StringRepresentable;
import raccoonman.reterraforged.common.level.levelgen.noise.Seed;
import raccoonman.reterraforged.common.level.levelgen.test.world.GeneratorContext;
import raccoonman.reterraforged.common.level.levelgen.test.world.continent.Continent;
import raccoonman.reterraforged.common.level.levelgen.test.world.continent.advanced.AdvancedContinentGenerator;
import raccoonman.reterraforged.common.level.levelgen.test.world.continent.fancy.FancyContinentGenerator;
import raccoonman.reterraforged.common.level.levelgen.test.world.continent.simple.MultiContinentGenerator;
import raccoonman.reterraforged.common.level.levelgen.test.world.continent.simple.SingleContinentGenerator;

public enum ContinentType implements StringRepresentable {
    MULTI {
        @Override
        public Continent create(Seed seed, GeneratorContext context) {
            return new MultiContinentGenerator(seed, context);
        }
    }, 
    SINGLE {
        @Override
        public Continent create(Seed seed, GeneratorContext context) {
            return new SingleContinentGenerator(seed, context);
        }
    }, 
    MULTI_IMPROVED {
        @Override
        public Continent create(Seed seed, GeneratorContext context) {
            return new AdvancedContinentGenerator(seed, context);
        }
    }, 
    EXPERIMENTAL {
        @Override
        public Continent create(Seed seed, GeneratorContext context) {
            return new FancyContinentGenerator(seed, context);
        }
    };
	
	public static final Codec<ContinentType> CODEC = StringRepresentable.fromEnum(ContinentType::values);
    
    public abstract Continent create(Seed seed, GeneratorContext context);

    @Override
	public String getSerializedName() {
		return this.name();
	}
}
