/*
 * Decompiled with CFR 0.150.
 */
package raccoonman.reterraforged.common.util;

public class PosUtil {
    private static final long MASK = 0xFFFFFFFFL;

    public static long packMix(int left, float right) {
        return (long)Float.floatToRawIntBits(right) & MASK | ((long)left & MASK) << 32;
    }

    public static long packMix(float left, int right) {
        return (long)right & MASK | ((long)Float.floatToRawIntBits(left) & MASK) << 32;
    }

    public static long pack(int left, int right) {
        return (long)right & MASK | ((long)left & MASK) << 32;
    }

    public static long pack(float left, float right) {
        return PosUtil.pack((int)left, (int)right);
    }

    public static int unpackLeft(long packed) {
        return (int)(packed >>> 32 & MASK);
    }

    public static int unpackRight(long packed) {
        return (int)(packed & MASK);
    }

    public static boolean contains(int x, int z, int x1, int z1, int x2, int z2) {
        return x >= x1 && x < x2 && z >= z1 && z < z2;
    }

    public static boolean contains(float x, float z, float x1, float z1, float x2, float z2) {
        return x >= x1 && x < x2 && z >= z1 && z < z2;
    }
}

