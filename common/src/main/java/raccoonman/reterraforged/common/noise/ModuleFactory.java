package raccoonman.reterraforged.common.noise;

public interface ModuleFactory<T extends Noise> {
	T apply(int seed);
}
