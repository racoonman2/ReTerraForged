package raccoonman.reterraforged.world.worldgen.surface;

import net.minecraft.server.level.WorldGenRegion;

public class SurfaceRegion {
	private static final ThreadLocal<WorldGenRegion> THREAD_LOCAL = new ThreadLocal<>();
	
	public static void set(WorldGenRegion region) {
		THREAD_LOCAL.set(region);
	}
	
	public static WorldGenRegion get() {
		return THREAD_LOCAL.get();
	}
}
