package raccoonman.reterraforged.common.level.levelgen.test.cell;

import com.mojang.serialization.Codec;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.test.concurrent.Resource;

public interface Populator extends Noise {
    void apply(Cell cell, float x, float z, int seed);
    
    @Override
    default float compute(float x, float z, int seed) {
        try (Resource<Cell> cell = Cell.getResource()) {
            this.apply(cell.get(), x, z, seed);
            return cell.get().value;
        }
    }
    
    @Override
    default Codec<Populator> codec() {
    	return Codec.unit(this);
    }

	@Override
	default Noise mapAll(Visitor visitor) {
		return visitor.apply(this);
	}
}
