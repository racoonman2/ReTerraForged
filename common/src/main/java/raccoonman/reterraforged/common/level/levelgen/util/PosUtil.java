package raccoonman.reterraforged.common.level.levelgen.util;

public final class PosUtil {
	private static final long MASK = 0xFFFFFFFFL;

	public static long pack(int left, int right) {
		return (long) right & MASK | ((long) left & MASK) << 32;
	}

	public static int unpackLeft(long packed) {
		return (int) (packed >>> 32 & MASK);
	}

	public static int unpackRight(long packed) {
		return (int) (packed & MASK);
	}

	public static long packf(float left, float right) {
		return (long) Float.floatToRawIntBits(right) & MASK | ((long) Float.floatToRawIntBits(left) & MASK) << 32;
	}

	public static float unpackLeftf(long packed) {
		return Float.intBitsToFloat((int) (packed >>> 32 & MASK));
	}

	public static float unpackRightf(long packed) {
		return Float.intBitsToFloat((int) (packed & MASK));
	}
}
