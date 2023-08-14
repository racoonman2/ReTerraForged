/*
 * Decompiled with CFR 0.150.
 */
package raccoonman.reterraforged.common.util.pos;

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

    public static long packf(float left, float right) {
        return (long)Float.floatToRawIntBits(right) & MASK | ((long)Float.floatToRawIntBits(left) & MASK) << 32;
    }

    public static float unpackLeftf(long packed) {
        return Float.intBitsToFloat((int)(packed >>> 32 & MASK));
    }

    public static float unpackRightf(long packed) {
        return Float.intBitsToFloat((int)(packed & MASK));
    }

    public static <T> void iterate(int startX, int startZ, int width, int depth, T ctx, Visitor<T> visitor) {
        int size = width * depth;
        for (int i = 0; i < size; ++i) {
            int dz = i / width;
            int dx = i - dz * width;
            int x = startX + dx;
            int z = startZ + dz;
            visitor.visit(x, z, ctx);
        }
    }

    public static boolean contains(int x, int z, int x1, int z1, int x2, int z2) {
        return x >= x1 && x < x2 && z >= z1 && z < z2;
    }

    public static boolean contains(float x, float z, float x1, float z1, float x2, float z2) {
        return x >= x1 && x < x2 && z >= z1 && z < z2;
    }

    public static interface Visitor<T> {
        public void visit(int var1, int var2, T var3);
    }
}

