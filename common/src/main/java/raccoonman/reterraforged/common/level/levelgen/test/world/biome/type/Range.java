package raccoonman.reterraforged.common.level.levelgen.test.world.biome.type;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.Source;

interface Range {
	float min();
	
	float max();

	default float mid() {
		return (this.min() + this.max()) / 2.0F;
	}
	
	default Noise source() {
		return Source.constant(this.mid());
	}
}
