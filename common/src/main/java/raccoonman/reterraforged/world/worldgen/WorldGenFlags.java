package raccoonman.reterraforged.world.worldgen;

public class WorldGenFlags {
	private static final ThreadLocal<Boolean> FAST_LOOKUP = ThreadLocal.withInitial(() -> true);
	
	public static void setFastCellLookups(boolean fastLookups) {
		FAST_LOOKUP.set(fastLookups);
	}
	
	public static boolean fastLookups() {
		return FAST_LOOKUP.get();
	}
}
