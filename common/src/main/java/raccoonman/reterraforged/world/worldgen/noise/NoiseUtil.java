package raccoonman.reterraforged.world.worldgen.noise;

public class NoiseUtil {
    public static final int X_PRIME = 1619;
    public static final int Y_PRIME = 31337;
    public static final float CUBIC_2D_BOUNDING = 0.44444445F;
    public static final float PI2 = 6.2831855F;
    public static final float SQRT2;
    private static final int SIN_BITS;
    private static final int SIN_MASK;
    private static final int SIN_COUNT;
    private static final float radFull;
    private static final float radToIndex;
    private static final float degFull;
    private static final float degToIndex;
    public static final Vec2i[] MOORE;
    public static final Vec2f[] GRAD_2D;
    public static final Vec2f[] GRAD_2D_24;
    public static final Vec2f[] CELL_2D;
    private static final float[] SIN;
    
    public static Vec2f cell(int seed, int x, int y) {
        return NoiseUtil.CELL_2D[hash2D(seed, x, y) & 0xFF];
    }

    public static float map(float value, float min, float max, float range) {
    	return map(value, min, max, range, true);
    }
    
    public static float map(float value, float min, float max, float range, boolean clamp) {
        float dif = (clamp ? clamp(value, min, max) : value) - min;
        return (dif >= range && clamp) ? 1.0F : (dif / range);
    }
    
    public static float map(float value, float from, float to, float min, float max) {
        float alpha = (value - min) / (max - min);
        return from + alpha * (to - from);
    }
    
    public static double map(double value, double from, double to, double min, double max) {
    	double alpha = (value - min) / (max - min);
        return from + alpha * (to - from);
    }
    
    public static float clamp(float value, float min, float max) {
        return (value < min) ? min : ((value > max) ? max : value);
    }

    public static double clamp(double value, double min, double max) {
        return (value < min) ? min : ((value > max) ? max : value);
    }
    
    public static float dist2(float x1, float y1, float x2, float y2) {
        float dx = x1 - x2;
        float dy = y1 - y2;
        return dx * dx + dy * dy;
    }
    
    public static float dot(float x0, float y0, float x1, float y1) {
        return x0 * x1 + y0 * y1;
    }
    
    public static float div(int num, int denom) {
        return num / (float) denom;
    }
    
    public static int floor(float f) {
        return (f >= 0.0F) ? ((int) f) : ((int) f - 1);
    }
    
    public static int toInt(float f) {
        int i = Float.floatToRawIntBits(f);
        return i ^ i >> 16;
    }
    
    public static int round(float f) {
        return (f >= 0.0F) ? ((int) (f + 0.5F)) : ((int) (f - 0.5F));
    }
    
    public static float lerp(float a, float b, float alpha) {
        return a + alpha * (b - a);
    }
    
    public static double lerp(double a, double b, double alpha) {
        return a + alpha * (b - a);
    }
    
    public static float lerp(float alphaValue, float alphaMin, float alphaMax, float from, float to) {
		float alpha = (alphaValue - alphaMin) / (alphaMax - alphaMin);
        return from + alpha * (to - from);
	}
    
    public static float interpHermite(final float t) {
        return t * t * (3.0F - 2.0F * t);
    }
    
    public static float interpQuintic(final float t) {
        return t * t * t * (t * (t * 6.0F - 15.0F) + 10.0F);
    }
    
    public static float curve(float t, float steepness) {
        return curve(t, 0.5F, steepness);
    }
    
    public static float curve(float t, float mid, float steepness) {
        return 1.0F / (1.0F + exp(-steepness * (t - mid)));
    }
    
    public static float cubicLerp(float a, float b, float c, float d, float t) {
        float p = d - c - (a - b);
        return t * t * t * p + t * t * (a - b - p) + t * (c - a) + b;
    }
    
    public static float exp(float x) {
        x = 1.0f + x / 256.0F;
        x *= x;
        x *= x;
        x *= x;
        x *= x;
        x *= x;
        x *= x;
        x *= x;
        x *= x;
        return x;
    }
    
    public static float copySign(float value, float sign) {
        if (sign < 0.0F && value > 0.0F) {
            return -value;
        }
        if (sign > 0.0F && value < 0.0F) {
            return -value;
        }
        return value;
    }
    
    public static float pow(float value, int power) {
        if (power == 0) {
            return 1.0F;
        }
        if (power == 1) {
            return value;
        }
        if (power == 2) {
            return value * value;
        }
        if (power == 3) {
            return value * value * value;
        }
        if (power == 4) {
            return value * value * value * value;
        }
        float result = 1.0F;
        for (int i = 0; i < power; ++i) {
            result *= value;
        }
        return result;
    }
    
    public static int hash(int x, int y) {
        int hash = x;
        hash ^= 31337 * y;
        hash = hash * hash * hash * 60493;
        hash ^= hash >> 13;
        return hash;
    }
    
    public static int hash2D(int seed, int x, int y) {
        int hash = seed;
        hash ^= 1619 * x;
        hash ^= 31337 * y;
        hash = hash * hash * hash * 60493;
        hash ^= hash >> 13;
        return hash;
    }
    
    public static float valCoord2D(int seed, int x, int y) {
        int n = seed;
        n ^= 1619 * x;
        n ^= 31337 * y;
        return n * n * n * 60493 / 2.14748365E9F;
    }
    
    public static Vec2f coord2D(int seed, int x, int y) {
        int hash = seed;
        hash ^= 1619 * x;
        hash ^= 31337 * y;
        hash = hash * hash * hash * 60493;
        hash ^= hash >> 13;
        return NoiseUtil.GRAD_2D[hash & 0x7];
    }
    
    public static Vec2f coord2D_24(int seed, int x, int y) {
        int hash = seed;
        hash ^= 1619 * x;
        hash ^= 31337 * y;
        hash = hash * hash * hash * 60493;
        hash ^= hash >> 13;
        int selector24 = (int)((hash & 0x3FFFFF) * 1.3333334F) & 0x1F;
        return NoiseUtil.GRAD_2D_24[selector24];
    }
    
    public static float gradCoord2D(int seed, int x, int y, float xd, float yd) {
        Vec2f g = coord2D(seed, x, y);
        return xd * g.x() + yd * g.y();
    }
    
    public static float gradCoord2D_24(int seed, int x, int y, float xd, float yd) {
        Vec2f g = coord2D_24(seed, x, y);
        return xd * g.x() + yd * g.y();
    }
    
    public static float pow(float value, float power) {
        return (float)Math.pow(value, power);
    }
    
    public static float sqrt(float value) {
        return (float)Math.sqrt(value);
    }
    
    public static float sin(float r) {
        int index = (int)(r * NoiseUtil.radToIndex) & NoiseUtil.SIN_MASK;
        return NoiseUtil.SIN[index];
    }
    
    public static float cos(float r) {
        return sin(r + 1.5708f);
    }
    
    public static long seed(int x, int z) {
        return ((long)x & 0xFFFFFFFFL) | ((long)z & 0xFFFFFFFFL) << 32;
    }
    
    static {
        SQRT2 = (float)Math.sqrt(2.0);
        MOORE = new Vec2i[] { new Vec2i(-1, -1), new Vec2i(0, -1), new Vec2i(1, -1), new Vec2i(-1, 0), new Vec2i(0, 0), new Vec2i(1, 0), new Vec2i(-1, 1), new Vec2i(0, 1), new Vec2i(1, 1) };
        GRAD_2D = new Vec2f[] { new Vec2f(-1.0f, -1.0f), new Vec2f(1.0f, -1.0f), new Vec2f(-1.0f, 1.0f), new Vec2f(1.0f, 1.0f), new Vec2f(0.0f, -1.0f), new Vec2f(-1.0f, 0.0f), new Vec2f(0.0f, 1.0f), new Vec2f(1.0f, 0.0f) };
        GRAD_2D_24 = new Vec2f[] { new Vec2f(0.13052619f, 0.9914449f), new Vec2f(0.38268343f, 0.9238795f), new Vec2f(0.6087614f, 0.7933533f), new Vec2f(0.6087614f, 0.7933533f), new Vec2f(0.7933533f, 0.6087614f), new Vec2f(0.9238795f, 0.38268343f), new Vec2f(0.9914449f, 0.13052619f), new Vec2f(0.9914449f, 0.13052619f), new Vec2f(0.9914449f, -0.13052619f), new Vec2f(0.9238795f, -0.38268343f), new Vec2f(0.7933533f, -0.6087614f), new Vec2f(0.7933533f, -0.6087614f), new Vec2f(0.6087614f, -0.7933533f), new Vec2f(0.38268343f, -0.9238795f), new Vec2f(0.13052619f, -0.9914449f), new Vec2f(0.13052619f, -0.9914449f), new Vec2f(-0.13052619f, -0.9914449f), new Vec2f(-0.38268343f, -0.9238795f), new Vec2f(-0.6087614f, -0.7933533f), new Vec2f(-0.6087614f, -0.7933533f), new Vec2f(-0.7933533f, -0.6087614f), new Vec2f(-0.9238795f, -0.38268343f), new Vec2f(-0.9914449f, -0.13052619f), new Vec2f(-0.9914449f, -0.13052619f), new Vec2f(-0.9914449f, 0.13052619f), new Vec2f(-0.9238795f, 0.38268343f), new Vec2f(-0.7933533f, 0.6087614f), new Vec2f(-0.7933533f, 0.6087614f), new Vec2f(-0.6087614f, 0.7933533f), new Vec2f(-0.38268343f, 0.9238795f), new Vec2f(-0.13052619f, 0.9914449f), new Vec2f(-0.13052619f, 0.9914449f) };
        CELL_2D = new Vec2f[] { new Vec2f(0.06864607f, 0.62819433f), new Vec2f(0.32666832f, 0.9152784f), new Vec2f(0.21780425f, 0.14947817f), new Vec2f(0.21935263f, 0.8517628f), new Vec2f(0.8125509f, 0.17625329f), new Vec2f(0.83830184f, 0.20326465f), new Vec2f(0.0606018f, 0.40289584f), new Vec2f(0.053955644f, 0.44046497f), new Vec2f(0.19777697f, 0.8334085f), new Vec2f(0.28731894f, 0.103431255f), new Vec2f(0.20088434f, 0.83619905f), new Vec2f(0.7293324f, 0.8871778f), new Vec2f(0.9475439f, 0.4530485f), new Vec2f(0.6777518f, 0.9134057f), new Vec2f(0.66885227f, 0.0828802f), new Vec2f(0.4023403f, 0.939275f), new Vec2f(0.58450186f, 0.94199485f), new Vec2f(0.09012395f, 0.31425387f), new Vec2f(0.8476586f, 0.21428421f), new Vec2f(0.164933f, 0.19961673f), new Vec2f(0.729819f, 0.11311084f), new Vec2f(0.48930076f, 0.9498728f), new Vec2f(0.053985864f, 0.4402388f), new Vec2f(0.8650294f, 0.7631607f), new Vec2f(0.15052056f, 0.78348565f), new Vec2f(0.087727934f, 0.68036556f), new Vec2f(0.23267218f, 0.8619887f), new Vec2f(0.822124f, 0.18577698f), new Vec2f(0.7880446f, 0.15426844f), new Vec2f(0.8892171f, 0.27414596f), new Vec2f(0.9492085f, 0.47332188f), new Vec2f(0.050227523f, 0.514308f), new Vec2f(0.6278175f, 0.068534255f), new Vec2f(0.464279f, 0.94858f), new Vec2f(0.0702593f, 0.36649746f), new Vec2f(0.17821822f, 0.8145735f), new Vec2f(0.19428411f, 0.8302087f), new Vec2f(0.08549601f, 0.6751755f), new Vec2f(0.12618601f, 0.75052565f), new Vec2f(0.72368914f, 0.10953468f), new Vec2f(0.5029678f, 0.050009787f), new Vec2f(0.67471284f, 0.0853008f), new Vec2f(0.05762276f, 0.4175235f), new Vec2f(0.2236039f, 0.14488706f), new Vec2f(0.09806141f, 0.2976504f), new Vec2f(0.8871414f, 0.27060616f), new Vec2f(0.06999126f, 0.63263667f), new Vec2f(0.46962425f, 0.051026374f), new Vec2f(0.15138185f, 0.78454417f), new Vec2f(0.54553515f, 0.05230975f), new Vec2f(0.46241972f, 0.94842803f), new Vec2f(0.8266409f, 0.809525f), new Vec2f(0.5654002f, 0.0547778f), new Vec2f(0.5340903f, 0.94870687f), new Vec2f(0.055080622f, 0.56742966f), new Vec2f(0.074406385f, 0.35381493f), new Vec2f(0.9499173f, 0.5086273f), new Vec2f(0.55242604f, 0.94693565f), new Vec2f(0.050469488f, 0.47944975f), new Vec2f(0.37952244f, 0.93357253f), new Vec2f(0.15801361f, 0.2075187f), new Vec2f(0.886532f, 0.73041916f), new Vec2f(0.545061f, 0.0522618f), new Vec2f(0.43716535f, 0.9455915f), new Vec2f(0.89326f, 0.28126147f), new Vec2f(0.94722617f, 0.4501127f), new Vec2f(0.8753571f, 0.2517923f), new Vec2f(0.2263377f, 0.857224f), new Vec2f(0.67004615f, 0.9166345f), new Vec2f(0.91026926f, 0.6848761f), new Vec2f(0.82322717f, 0.18691185f), new Vec2f(0.21176898f, 0.15442386f), new Vec2f(0.70509726f, 0.9005435f), new Vec2f(0.94140863f, 0.41248745f), new Vec2f(0.33152997f, 0.9172743f), new Vec2f(0.49602196f, 0.9499824f), new Vec2f(0.29448664f, 0.9003302f), new Vec2f(0.49390432f, 0.05004129f), new Vec2f(0.38037717f, 0.06619084f), new Vec2f(0.8901528f, 0.2757663f), new Vec2f(0.51723534f, 0.94966984f), new Vec2f(0.19849297f, 0.83405614f), new Vec2f(0.48485738f, 0.05025485f), new Vec2f(0.08574259f, 0.3242422f), new Vec2f(0.30836228f, 0.09284526f), new Vec2f(0.8749249f, 0.74886006f), new Vec2f(0.2762226f, 0.8904147f), new Vec2f(0.08336568f, 0.32995337f), new Vec2f(0.86191714f, 0.7674247f), new Vec2f(0.68911266f, 0.09166631f), new Vec2f(0.1872575f, 0.8235616f), new Vec2f(0.17181921f, 0.80789185f), new Vec2f(0.27051932f, 0.88708997f), new Vec2f(0.15547338f, 0.78948474f), new Vec2f(0.08329046f, 0.33013785f), new Vec2f(0.24210969f, 0.13122827f), new Vec2f(0.13879621f, 0.76838744f), new Vec2f(0.72679967f, 0.88866687f), new Vec2f(0.7071571f, 0.89948213f), new Vec2f(0.58355176f, 0.05782458f), new Vec2f(0.06877667f, 0.62863296f), new Vec2f(0.8257055f, 0.8105091f), new Vec2f(0.6777011f, 0.08657247f), new Vec2f(0.054817468f, 0.5656698f), new Vec2f(0.89551437f, 0.7146355f), new Vec2f(0.0735386f, 0.64363384f), new Vec2f(0.12062004f, 0.25798586f), new Vec2f(0.546176f, 0.052375406f), new Vec2f(0.12859458f, 0.24591732f), new Vec2f(0.7563571f, 0.13016075f), new Vec2f(0.53476644f, 0.948655f), new Vec2f(0.19345456f, 0.82943875f), new Vec2f(0.274302f, 0.8893076f), new Vec2f(0.9116448f, 0.31820747f), new Vec2f(0.20922542f, 0.15656129f), new Vec2f(0.78422785f, 0.15112391f), new Vec2f(0.81145895f, 0.17520264f), new Vec2f(0.94641554f, 0.44331557f), new Vec2f(0.19626659f, 0.16796684f), new Vec2f(0.9079607f, 0.6899159f), new Vec2f(0.15130511f, 0.21554989f), new Vec2f(0.8264822f, 0.80969244f), new Vec2f(0.82111424f, 0.8152549f), new Vec2f(0.51183385f, 0.94984436f), new Vec2f(0.9333844f, 0.6211526f), new Vec2f(0.8118669f, 0.8244057f), new Vec2f(0.22724652f, 0.8579184f), new Vec2f(0.077771366f, 0.34436262f), new Vec2f(0.39903f, 0.061473995f), new Vec2f(0.22588289f, 0.14312494f), new Vec2f(0.3534875f, 0.92548096f), new Vec2f(0.7302279f, 0.11335403f), new Vec2f(0.13005644f, 0.7562065f), new Vec2f(0.6057004f, 0.06259009f), new Vec2f(0.23532864f, 0.86393553f), new Vec2f(0.8521828f, 0.78012013f), new Vec2f(0.31358123f, 0.090429455f), new Vec2f(0.6994493f, 0.09661436f), new Vec2f(0.8937065f, 0.7179339f), new Vec2f(0.17738417f, 0.8137181f), new Vec2f(0.87962353f, 0.74163187f), new Vec2f(0.6482922f, 0.924864f), new Vec2f(0.0925996f, 0.6911149f), new Vec2f(0.9212853f, 0.65817297f), new Vec2f(0.23787028f, 0.86577046f), new Vec2f(0.2463013f, 0.12833217f), new Vec2f(0.28997636f, 0.8979825f), new Vec2f(0.86241525f, 0.76674926f), new Vec2f(0.13549614f, 0.23611188f), new Vec2f(0.73184866f, 0.88567626f), new Vec2f(0.17395431f, 0.81015193f), new Vec2f(0.28699547f, 0.10360491f), new Vec2f(0.8814999f, 0.26134157f), new Vec2f(0.15702268f, 0.79131866f), new Vec2f(0.06441343f, 0.6129794f), new Vec2f(0.28953204f, 0.8977477f), new Vec2f(0.8348365f, 0.19935977f), new Vec2f(0.8430469f, 0.79123676f), new Vec2f(0.27081633f, 0.11273414f), new Vec2f(0.75477076f, 0.1290662f), new Vec2f(0.9236175f, 0.3481836f), new Vec2f(0.34612256f, 0.9228732f), new Vec2f(0.059255064f, 0.59079593f), new Vec2f(0.43194723f, 0.055175513f), new Vec2f(0.9453517f, 0.43548763f), new Vec2f(0.75624645f, 0.13008413f), new Vec2f(0.8278198f, 0.19172388f), new Vec2f(0.08772257f, 0.31964666f), new Vec2f(0.8354091f, 0.19999877f), new Vec2f(0.94663286f, 0.44505385f), new Vec2f(0.33910465f, 0.92025316f), new Vec2f(0.40536046f, 0.9399356f), new Vec2f(0.47362313f, 0.05077371f), new Vec2f(0.9471028f, 0.4490188f), new Vec2f(0.0634329f, 0.6091292f), new Vec2f(0.10401413f, 0.7137643f), new Vec2f(0.07599518f, 0.34926873f), new Vec2f(0.11172053f, 0.72746223f), new Vec2f(0.07163474f, 0.36214787f), new Vec2f(0.8303888f, 0.80552125f), new Vec2f(0.8321435f, 0.19638726f), new Vec2f(0.08697894f, 0.32135618f), new Vec2f(0.5840306f, 0.05791533f), new Vec2f(0.11771172f, 0.7373935f), new Vec2f(0.12876043f, 0.24567503f), new Vec2f(0.9472364f, 0.45020437f), new Vec2f(0.053340882f, 0.5547323f), new Vec2f(0.54862726f, 0.052635074f), new Vec2f(0.07968986f, 0.33925363f), new Vec2f(0.7205361f, 0.8922548f), new Vec2f(0.13750994f, 0.7666476f), new Vec2f(0.09639132f, 0.30100244f), new Vec2f(0.7152728f, 0.8951678f), new Vec2f(0.06406072f, 0.38838938f), new Vec2f(0.9178354f, 0.6670735f), new Vec2f(0.700763f, 0.90273345f), new Vec2f(0.42721933f, 0.055924594f), new Vec2f(0.8644749f, 0.23607183f), new Vec2f(0.06825483f, 0.6268704f), new Vec2f(0.20256355f, 0.8376856f), new Vec2f(0.20013279f, 0.8355289f), new Vec2f(0.23263258f, 0.86195946f), new Vec2f(0.7808423f, 0.85160714f), new Vec2f(0.84989464f, 0.78297305f), new Vec2f(0.27703142f, 0.89087725f), new Vec2f(0.8305823f, 0.80531186f), new Vec2f(0.25633186f, 0.12168023f), new Vec2f(0.46597224f, 0.94871163f), new Vec2f(0.18064117f, 0.81703305f), new Vec2f(0.94546336f, 0.436263f), new Vec2f(0.9483504f, 0.5384954f), new Vec2f(0.05726415f, 0.41947067f), new Vec2f(0.554523f, 0.9466847f), new Vec2f(0.21874392f, 0.8512763f), new Vec2f(0.6266697f, 0.9318041f), new Vec2f(0.12640187f, 0.75084746f), new Vec2f(0.7959708f, 0.1610291f), new Vec2f(0.12856227f, 0.7540355f), new Vec2f(0.095532894f, 0.30275303f), new Vec2f(0.66361654f, 0.080798835f), new Vec2f(0.8289186f, 0.19289646f), new Vec2f(0.2505175f, 0.125489f), new Vec2f(0.5328313f, 0.94880074f), new Vec2f(0.33369392f, 0.081858516f), new Vec2f(0.3931668f, 0.9371346f), new Vec2f(0.5644026f, 0.054632396f), new Vec2f(0.0516769f, 0.5388124f), new Vec2f(0.07862225f, 0.3420735f), new Vec2f(0.5509792f, 0.052896976f), new Vec2f(0.7050584f, 0.09943658f), new Vec2f(0.91780984f, 0.33286256f), new Vec2f(0.14348105f, 0.22541988f), new Vec2f(0.94783986f, 0.5440398f), new Vec2f(0.16000003f, 0.20521191f), new Vec2f(0.8767122f, 0.74614614f), new Vec2f(0.18610656f, 0.82244515f), new Vec2f(0.35379982f, 0.07441157f), new Vec2f(0.89702904f, 0.28817946f), new Vec2f(0.9459149f, 0.4395031f), new Vec2f(0.08951107f, 0.3156123f), new Vec2f(0.6475104f, 0.07486391f), new Vec2f(0.5925803f, 0.94037354f), new Vec2f(0.34103352f, 0.07901347f), new Vec2f(0.7482445f, 0.8753327f), new Vec2f(0.9383624f, 0.39832214f), new Vec2f(0.56242806f, 0.94564867f), new Vec2f(0.7846591f, 0.1514757f), new Vec2f(0.15579724f, 0.21013024f), new Vec2f(0.61981887f, 0.06624496f), new Vec2f(0.2564093f, 0.8783696f), new Vec2f(0.7958191f, 0.16089669f), new Vec2f(0.3835992f, 0.93468475f), new Vec2f(0.62740374f, 0.06841189f), new Vec2f(0.8680473f, 0.7589231f), new Vec2f(0.7451437f, 0.8773653f), new Vec2f(0.06854904f, 0.62786734f) };
        SIN_BITS = 12;
        SIN_MASK = ~(-1 << NoiseUtil.SIN_BITS);
        SIN_COUNT = NoiseUtil.SIN_MASK + 1;
        radFull = 6.2831855f;
        degFull = 360.0f;
        radToIndex = NoiseUtil.SIN_COUNT / NoiseUtil.radFull;
        degToIndex = NoiseUtil.SIN_COUNT / NoiseUtil.degFull;
        SIN = new float[NoiseUtil.SIN_COUNT];
        for (int i = 0; i < NoiseUtil.SIN_COUNT; ++i) {
            NoiseUtil.SIN[i] = (float)Math.sin((i + 0.5f) / NoiseUtil.SIN_COUNT * NoiseUtil.radFull);
        }
        for (int i = 0; i < 360; i += 90) {
            NoiseUtil.SIN[(int)(i * NoiseUtil.degToIndex) & NoiseUtil.SIN_MASK] = (float)Math.sin(i * 3.141592653589793 / 180.0);
        }
    }
    
    public record Vec2f(float x, float y) {
    }
    
    public record Vec2i(int x, int y) {
    }
}
