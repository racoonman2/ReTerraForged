package raccoonman.reterraforged.world.worldgen;

public class WorldGenFlags {
	private static final ThreadLocal<Boolean> FAST_LOOKUP = ThreadLocal.withInitial(() -> true);
	private static boolean CULL_NOISE_SECTIONS = true;
	
	public static void setFastCellLookups(boolean fastLookups) {
		FAST_LOOKUP.set(fastLookups);
	}
	
	public static boolean fastLookups() {
		return FAST_LOOKUP.get();
	}
	
	public static void setCullNoiseSections(boolean cullNoiseSections) {
		CULL_NOISE_SECTIONS = cullNoiseSections;
	}
	
	public static boolean cullNoiseSections() {
		return CULL_NOISE_SECTIONS;
	}
}
