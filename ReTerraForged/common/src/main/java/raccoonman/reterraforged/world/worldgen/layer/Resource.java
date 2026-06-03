package raccoonman.reterraforged.world.worldgen.layer;

public interface Resource extends AutoCloseable {
	
	@Override
	void close();
}