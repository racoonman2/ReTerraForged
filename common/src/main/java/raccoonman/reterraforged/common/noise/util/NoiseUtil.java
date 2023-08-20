/*
 *
 * MIT License
 *
 * Copyright (c) 2020 TerraForged
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package raccoonman.reterraforged.common.noise.util;

/**
 * Partly derived from FastNoise_Java
 * https://github.com/Auburns/FastNoise_Java
 * https://github.com/Auburn/FastNoise_Java/blob/master/LICENSE
 *
 * MIT License
 *
 * Copyright (c) 2017 Jordan Peck
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
public class NoiseUtil {

    public static final int X_PRIME = 1619;
    public static final int Y_PRIME = 31337;
    public static final float CUBIC_2D_BOUNDING = 1 / (float) (1.5 * 1.5);
    public static final float PI2 = (float) (Math.PI * 2.0);
    public static final float SQRT2 = (float) Math.sqrt(2);

    private static final int SIN_BITS, SIN_MASK, SIN_COUNT;
    private static final float radFull, radToIndex;
    private static final float degFull, degToIndex;

    public static final Vec2i[] MOORE = {
            new Vec2i(-1, -1), new Vec2i(0, -1), new Vec2i(1, -1),
            new Vec2i(-1, 0), new Vec2i(0, 0), new Vec2i(1, 0),
            new Vec2i(-1, 1), new Vec2i(0, 1), new Vec2i(1, 1),
    };

    public static final Vec2f[] GRAD_2D = {
            new Vec2f(-1, -1), new Vec2f(1, -1), new Vec2f(-1, 1), new Vec2f(1, 1),
            new Vec2f(0, -1), new Vec2f(-1, 0), new Vec2f(0, 1), new Vec2f(1, 0),
    };

    public static final Vec2f[] GRAD_2D_24 = {
            new Vec2f( 0.130526192220052f, 0.99144486137381f),
            new Vec2f( 0.38268343236509f,  0.923879532511287f),
            new Vec2f( 0.608761429008721f, 0.793353340291235f),
            new Vec2f( 0.608761429008721f, 0.793353340291235f), // Repeat due to selector math
            new Vec2f( 0.793353340291235f, 0.608761429008721f),
            new Vec2f( 0.923879532511287f, 0.38268343236509f),
            new Vec2f( 0.99144486137381f,  0.130526192220051f),
            new Vec2f( 0.99144486137381f,  0.130526192220051f), // Repeat
            new Vec2f( 0.99144486137381f, -0.130526192220051f),
            new Vec2f( 0.923879532511287f,-0.38268343236509f),
            new Vec2f( 0.793353340291235f,-0.60876142900872f),
            new Vec2f( 0.793353340291235f,-0.60876142900872f), // Repeat
            new Vec2f( 0.608761429008721f,-0.793353340291235f),
            new Vec2f( 0.38268343236509f, -0.923879532511287f),
            new Vec2f( 0.130526192220052f,-0.99144486137381f),
            new Vec2f( 0.130526192220052f,-0.99144486137381f), // Repeat
            new Vec2f(-0.130526192220052f,-0.99144486137381f),
            new Vec2f(-0.38268343236509f, -0.923879532511287f),
            new Vec2f(-0.608761429008721f,-0.793353340291235f),
            new Vec2f(-0.608761429008721f,-0.793353340291235f), // Repeat
            new Vec2f(-0.793353340291235f,-0.608761429008721f),
            new Vec2f(-0.923879532511287f,-0.38268343236509f),
            new Vec2f(-0.99144486137381f, -0.130526192220052f),
            new Vec2f(-0.99144486137381f, -0.130526192220052f), // Repeat
            new Vec2f(-0.99144486137381f,  0.130526192220051f),
            new Vec2f(-0.923879532511287f, 0.38268343236509f),
            new Vec2f(-0.793353340291235f, 0.608761429008721f),
            new Vec2f(-0.793353340291235f, 0.608761429008721f), // Repeat
            new Vec2f(-0.608761429008721f, 0.793353340291235f),
            new Vec2f(-0.38268343236509f,  0.923879532511287f),
            new Vec2f(-0.130526192220052f, 0.99144486137381f),
            new Vec2f(-0.130526192220052f, 0.99144486137381f) // Repeat
    };

    public static final Vec2f[] CELL_2D = {
            new Vec2f(0.06864607F, 0.62819433F), new Vec2f(0.32666832F, 0.9152784F), new Vec2f(0.21780425F, 0.14947817F), new Vec2f(0.21935263F, 0.8517628F), new Vec2f(0.8125509F, 0.17625329F), new Vec2f(0.83830184F, 0.20326465F), new Vec2f(0.0606018F, 0.40289584F), new Vec2f(0.053955644F, 0.44046497F),
            new Vec2f(0.19777697F, 0.8334085F), new Vec2f(0.28731894F, 0.103431255F), new Vec2f(0.20088434F, 0.83619905F), new Vec2f(0.7293324F, 0.8871778F), new Vec2f(0.9475439F, 0.4530485F), new Vec2f(0.6777518F, 0.9134057F), new Vec2f(0.66885227F, 0.0828802F), new Vec2f(0.4023403F, 0.939275F),
            new Vec2f(0.58450186F, 0.94199485F), new Vec2f(0.09012395F, 0.31425387F), new Vec2f(0.8476586F, 0.21428421F), new Vec2f(0.164933F, 0.19961673F), new Vec2f(0.729819F, 0.11311084F), new Vec2f(0.48930076F, 0.9498728F), new Vec2f(0.053985864F, 0.4402388F), new Vec2f(0.8650294F, 0.7631607F),
            new Vec2f(0.15052056F, 0.78348565F), new Vec2f(0.087727934F, 0.68036556F), new Vec2f(0.23267218F, 0.8619887F), new Vec2f(0.822124F, 0.18577698F), new Vec2f(0.7880446F, 0.15426844F), new Vec2f(0.8892171F, 0.27414596F), new Vec2f(0.9492085F, 0.47332188F), new Vec2f(0.050227523F, 0.514308F),
            new Vec2f(0.6278175F, 0.068534255F), new Vec2f(0.464279F, 0.94858F), new Vec2f(0.0702593F, 0.36649746F), new Vec2f(0.17821822F, 0.8145735F), new Vec2f(0.19428411F, 0.8302087F), new Vec2f(0.08549601F, 0.6751755F), new Vec2f(0.12618601F, 0.75052565F), new Vec2f(0.72368914F, 0.10953468F),
            new Vec2f(0.5029678F, 0.050009787F), new Vec2f(0.67471284F, 0.0853008F), new Vec2f(0.05762276F, 0.4175235F), new Vec2f(0.2236039F, 0.14488706F), new Vec2f(0.09806141F, 0.2976504F), new Vec2f(0.8871414F, 0.27060616F), new Vec2f(0.06999126F, 0.63263667F), new Vec2f(0.46962425F, 0.051026374F),
            new Vec2f(0.15138185F, 0.78454417F), new Vec2f(0.54553515F, 0.05230975F), new Vec2f(0.46241972F, 0.94842803F), new Vec2f(0.8266409F, 0.809525F), new Vec2f(0.5654002F, 0.0547778F), new Vec2f(0.5340903F, 0.94870687F), new Vec2f(0.055080622F, 0.56742966F), new Vec2f(0.074406385F, 0.35381493F),
            new Vec2f(0.9499173F, 0.5086273F), new Vec2f(0.55242604F, 0.94693565F), new Vec2f(0.050469488F, 0.47944975F), new Vec2f(0.37952244F, 0.93357253F), new Vec2f(0.15801361F, 0.2075187F), new Vec2f(0.886532F, 0.73041916F), new Vec2f(0.545061F, 0.0522618F), new Vec2f(0.43716535F, 0.9455915F),
            new Vec2f(0.89326F, 0.28126147F), new Vec2f(0.94722617F, 0.4501127F), new Vec2f(0.8753571F, 0.2517923F), new Vec2f(0.2263377F, 0.857224F), new Vec2f(0.67004615F, 0.9166345F), new Vec2f(0.91026926F, 0.6848761F), new Vec2f(0.82322717F, 0.18691185F), new Vec2f(0.21176898F, 0.15442386F),
            new Vec2f(0.70509726F, 0.9005435F), new Vec2f(0.94140863F, 0.41248745F), new Vec2f(0.33152997F, 0.9172743F), new Vec2f(0.49602196F, 0.9499824F), new Vec2f(0.29448664F, 0.9003302F), new Vec2f(0.49390432F, 0.05004129F), new Vec2f(0.38037717F, 0.06619084F), new Vec2f(0.8901528F, 0.2757663F),
            new Vec2f(0.51723534F, 0.94966984F), new Vec2f(0.19849297F, 0.83405614F), new Vec2f(0.48485738F, 0.05025485F), new Vec2f(0.08574259F, 0.3242422F), new Vec2f(0.30836228F, 0.09284526F), new Vec2f(0.8749249F, 0.74886006F), new Vec2f(0.2762226F, 0.8904147F), new Vec2f(0.08336568F, 0.32995337F),
            new Vec2f(0.86191714F, 0.7674247F), new Vec2f(0.68911266F, 0.09166631F), new Vec2f(0.1872575F, 0.8235616F), new Vec2f(0.17181921F, 0.80789185F), new Vec2f(0.27051932F, 0.88708997F), new Vec2f(0.15547338F, 0.78948474F), new Vec2f(0.08329046F, 0.33013785F), new Vec2f(0.24210969F, 0.13122827F),
            new Vec2f(0.13879621F, 0.76838744F), new Vec2f(0.72679967F, 0.88866687F), new Vec2f(0.7071571F, 0.89948213F), new Vec2f(0.58355176F, 0.05782458F), new Vec2f(0.06877667F, 0.62863296F), new Vec2f(0.8257055F, 0.8105091F), new Vec2f(0.6777011F, 0.08657247F), new Vec2f(0.054817468F, 0.5656698F),
            new Vec2f(0.89551437F, 0.7146355F), new Vec2f(0.0735386F, 0.64363384F), new Vec2f(0.12062004F, 0.25798586F), new Vec2f(0.546176F, 0.052375406F), new Vec2f(0.12859458F, 0.24591732F), new Vec2f(0.7563571F, 0.13016075F), new Vec2f(0.53476644F, 0.948655F), new Vec2f(0.19345456F, 0.82943875F),
            new Vec2f(0.274302F, 0.8893076F), new Vec2f(0.9116448F, 0.31820747F), new Vec2f(0.20922542F, 0.15656129F), new Vec2f(0.78422785F, 0.15112391F), new Vec2f(0.81145895F, 0.17520264F), new Vec2f(0.94641554F, 0.44331557F), new Vec2f(0.19626659F, 0.16796684F), new Vec2f(0.9079607F, 0.6899159F),
            new Vec2f(0.15130511F, 0.21554989F), new Vec2f(0.8264822F, 0.80969244F), new Vec2f(0.82111424F, 0.8152549F), new Vec2f(0.51183385F, 0.94984436F), new Vec2f(0.9333844F, 0.6211526F), new Vec2f(0.8118669F, 0.8244057F), new Vec2f(0.22724652F, 0.8579184F), new Vec2f(0.077771366F, 0.34436262F),
            new Vec2f(0.39903F, 0.061473995F), new Vec2f(0.22588289F, 0.14312494F), new Vec2f(0.3534875F, 0.92548096F), new Vec2f(0.7302279F, 0.11335403F), new Vec2f(0.13005644F, 0.7562065F), new Vec2f(0.6057004F, 0.06259009F), new Vec2f(0.23532864F, 0.86393553F), new Vec2f(0.8521828F, 0.78012013F),
            new Vec2f(0.31358123F, 0.090429455F), new Vec2f(0.6994493F, 0.09661436F), new Vec2f(0.8937065F, 0.7179339F), new Vec2f(0.17738417F, 0.8137181F), new Vec2f(0.87962353F, 0.74163187F), new Vec2f(0.6482922F, 0.924864F), new Vec2f(0.0925996F, 0.6911149F), new Vec2f(0.9212853F, 0.65817297F),
            new Vec2f(0.23787028F, 0.86577046F), new Vec2f(0.2463013F, 0.12833217F), new Vec2f(0.28997636F, 0.8979825F), new Vec2f(0.86241525F, 0.76674926F), new Vec2f(0.13549614F, 0.23611188F), new Vec2f(0.73184866F, 0.88567626F), new Vec2f(0.17395431F, 0.81015193F), new Vec2f(0.28699547F, 0.10360491F),
            new Vec2f(0.8814999F, 0.26134157F), new Vec2f(0.15702268F, 0.79131866F), new Vec2f(0.06441343F, 0.6129794F), new Vec2f(0.28953204F, 0.8977477F), new Vec2f(0.8348365F, 0.19935977F), new Vec2f(0.8430469F, 0.79123676F), new Vec2f(0.27081633F, 0.11273414F), new Vec2f(0.75477076F, 0.1290662F),
            new Vec2f(0.9236175F, 0.3481836F), new Vec2f(0.34612256F, 0.9228732F), new Vec2f(0.059255064F, 0.59079593F), new Vec2f(0.43194723F, 0.055175513F), new Vec2f(0.9453517F, 0.43548763F), new Vec2f(0.75624645F, 0.13008413F), new Vec2f(0.8278198F, 0.19172388F), new Vec2f(0.08772257F, 0.31964666F),
            new Vec2f(0.8354091F, 0.19999877F), new Vec2f(0.94663286F, 0.44505385F), new Vec2f(0.33910465F, 0.92025316F), new Vec2f(0.40536046F, 0.9399356F), new Vec2f(0.47362313F, 0.05077371F), new Vec2f(0.9471028F, 0.4490188F), new Vec2f(0.0634329F, 0.6091292F), new Vec2f(0.10401413F, 0.7137643F),
            new Vec2f(0.07599518F, 0.34926873F), new Vec2f(0.11172053F, 0.72746223F), new Vec2f(0.07163474F, 0.36214787F), new Vec2f(0.8303888F, 0.80552125F), new Vec2f(0.8321435F, 0.19638726F), new Vec2f(0.08697894F, 0.32135618F), new Vec2f(0.5840306F, 0.05791533F), new Vec2f(0.11771172F, 0.7373935F),
            new Vec2f(0.12876043F, 0.24567503F), new Vec2f(0.9472364F, 0.45020437F), new Vec2f(0.053340882F, 0.5547323F), new Vec2f(0.54862726F, 0.052635074F), new Vec2f(0.07968986F, 0.33925363F), new Vec2f(0.7205361F, 0.8922548F), new Vec2f(0.13750994F, 0.7666476F), new Vec2f(0.09639132F, 0.30100244F),
            new Vec2f(0.7152728F, 0.8951678F), new Vec2f(0.06406072F, 0.38838938F), new Vec2f(0.9178354F, 0.6670735F), new Vec2f(0.700763F, 0.90273345F), new Vec2f(0.42721933F, 0.055924594F), new Vec2f(0.8644749F, 0.23607183F), new Vec2f(0.06825483F, 0.6268704F), new Vec2f(0.20256355F, 0.8376856F),
            new Vec2f(0.20013279F, 0.8355289F), new Vec2f(0.23263258F, 0.86195946F), new Vec2f(0.7808423F, 0.85160714F), new Vec2f(0.84989464F, 0.78297305F), new Vec2f(0.27703142F, 0.89087725F), new Vec2f(0.8305823F, 0.80531186F), new Vec2f(0.25633186F, 0.12168023F), new Vec2f(0.46597224F, 0.94871163F),
            new Vec2f(0.18064117F, 0.81703305F), new Vec2f(0.94546336F, 0.436263F), new Vec2f(0.9483504F, 0.5384954F), new Vec2f(0.05726415F, 0.41947067F), new Vec2f(0.554523F, 0.9466847F), new Vec2f(0.21874392F, 0.8512763F), new Vec2f(0.6266697F, 0.9318041F), new Vec2f(0.12640187F, 0.75084746F),
            new Vec2f(0.7959708F, 0.1610291F), new Vec2f(0.12856227F, 0.7540355F), new Vec2f(0.095532894F, 0.30275303F), new Vec2f(0.66361654F, 0.080798835F), new Vec2f(0.8289186F, 0.19289646F), new Vec2f(0.2505175F, 0.125489F), new Vec2f(0.5328313F, 0.94880074F), new Vec2f(0.33369392F, 0.081858516F),
            new Vec2f(0.3931668F, 0.9371346F), new Vec2f(0.5644026F, 0.054632396F), new Vec2f(0.0516769F, 0.5388124F), new Vec2f(0.07862225F, 0.3420735F), new Vec2f(0.5509792F, 0.052896976F), new Vec2f(0.7050584F, 0.09943658F), new Vec2f(0.91780984F, 0.33286256F), new Vec2f(0.14348105F, 0.22541988F),
            new Vec2f(0.94783986F, 0.5440398F), new Vec2f(0.16000003F, 0.20521191F), new Vec2f(0.8767122F, 0.74614614F), new Vec2f(0.18610656F, 0.82244515F), new Vec2f(0.35379982F, 0.07441157F), new Vec2f(0.89702904F, 0.28817946F), new Vec2f(0.9459149F, 0.4395031F), new Vec2f(0.08951107F, 0.3156123F),
            new Vec2f(0.6475104F, 0.07486391F), new Vec2f(0.5925803F, 0.94037354F), new Vec2f(0.34103352F, 0.07901347F), new Vec2f(0.7482445F, 0.8753327F), new Vec2f(0.9383624F, 0.39832214F), new Vec2f(0.56242806F, 0.94564867F), new Vec2f(0.7846591F, 0.1514757F), new Vec2f(0.15579724F, 0.21013024F),
            new Vec2f(0.61981887F, 0.06624496F), new Vec2f(0.2564093F, 0.8783696F), new Vec2f(0.7958191F, 0.16089669F), new Vec2f(0.3835992F, 0.93468475F), new Vec2f(0.62740374F, 0.06841189F), new Vec2f(0.8680473F, 0.7589231F), new Vec2f(0.7451437F, 0.8773653F), new Vec2f(0.06854904F, 0.62786734F)
    };

    private static final float[] SIN;

    public static Vec2f cell(int seed, int x, int y) {
        return CELL_2D[hash2D(seed, x, y) & 255];
    }

    public static float map(float value, float min, float max, float range) {
        float dif = clamp(value, min, max) - min;
        if (dif > range) {
            return 1.0F;
        }
        return dif / range;
    }
    
    public static float map(float value, float min, float max) {
    	return map(value, min, max, Math.abs(max - min));
    }

    public static float clamp(float value, float min, float max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
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
        return (float) num / denom;
    }

    public static int floor(float f) {
        return (f >= 0 ? (int) f : (int) f - 1);
    }

    public static int toInt(float f) {
        int i = Float.floatToRawIntBits(f);
        return i ^ (i >> 16);
    }

    public static int round(float f) {
        return (f >= 0) ? (int) (f + (float) 0.5) : (int) (f - (float) 0.5);
    }

    public static float lerp(float a, float b, float alpha) {
        return a + alpha * (b - a);
    }

    public static float interpHermite(float t) {
        return t * t * (3 - 2 * t);
    }

    public static float interpQuintic(float t) {
        return t * t * t * (t * (t * 6 - 15) + 10);
    }

    public static float curve(float t, float steepness) {
        return curve(t, 0.5F, steepness);
    }

    public static float curve(float t, float mid, float steepness) {
        return 1F / (1F + NoiseUtil.exp(-steepness * (t - mid)));
    }

    public static float cubicLerp(float a, float b, float c, float d, float t) {
        float p = (d - c) - (a - b);
        return t * t * t * p + t * t * ((a - b) - p) + t * (c - a) + b;
    }

    public static float exp(float x) {
        x = 1F + x / 256F;
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
        if (sign < 0 && value > 0) {
            return -value;
        }
        if (sign > 0 && value < 0) {
            return -value;
        }
        return value;
    }

    public static float pow(float value, int power) {
        if (power == 0) {
            return 1;
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
        float result = 1;
        for (int i = 0; i < power; i++) {
            result *= value;
        }
        return result;
    }

    public static int hash(int x, int y) {
        int hash = x;
        hash ^= Y_PRIME * y;
        hash = hash * hash * hash * 60493;
        hash = (hash >> 13) ^ hash;
        return hash;
    }

    public static int hash2D(int seed, int x, int y) {
        int hash = seed;
        hash ^= X_PRIME * x;
        hash ^= Y_PRIME * y;

        hash = hash * hash * hash * 60493;
        hash = (hash >> 13) ^ hash;

        return hash;
    }

    public static float valCoord2D(int seed, int x, int y) {
        int n = seed;
        n ^= X_PRIME * x;
        n ^= Y_PRIME * y;
        //     (n * n * n * 60493) / 2147483648.0F;
        return (n * n * n * 60493) / 2147483648.0F;
    }

    public static Vec2f coord2D(int seed, int x, int y) {
        int hash = seed;
        hash ^= X_PRIME * x;
        hash ^= Y_PRIME * y;
        hash = hash * hash * hash * 60493;
        hash = (hash >> 13) ^ hash;
        return GRAD_2D[hash & 7];
    }

    public static Vec2f coord2D_24(int seed, int x, int y) {
        int hash = seed;
        hash ^= X_PRIME * x;
        hash ^= Y_PRIME * y;
        hash = hash * hash * hash * 60493;
        hash = (hash >> 13) ^ hash;
        
        // Fairly selects 24 gradients if you repeat every third one.
        int selector24 = (int)((hash & 0x3FFFFF) * 1.3333333333333333f) & 31;
        return GRAD_2D_24[selector24];
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
        return (float) Math.pow(value, power);
    }

    public static float sqrt(float value) {
        return (float) Math.sqrt(value);
    }

    public static float sin(float r) {
        int index = (int) (r * radToIndex) & SIN_MASK;
        return SIN[index];
    }

    public static float cos(float r) {
        return sin(r + 1.5708F);
    }

    public static long seed(int x, int z) {
        return (long) x & 4294967295L | ((long) z & 4294967295L) << 32;
    }

    static {
        SIN_BITS = 12;
        SIN_MASK = ~(-1 << SIN_BITS);
        SIN_COUNT = SIN_MASK + 1;

        radFull = (float) (Math.PI * 2.0);
        degFull = (float) (360.0);
        radToIndex = SIN_COUNT / radFull;
        degToIndex = SIN_COUNT / degFull;

        SIN = new float[SIN_COUNT];

        for (int i = 0; i < SIN_COUNT; i++) {
            SIN[i] = (float) Math.sin((i + 0.5f) / SIN_COUNT * radFull);
        }

        // Four cardinal directions (credits: Nate)
        for (int i = 0; i < 360; i += 90) {
            SIN[(int) (i * degToIndex) & SIN_MASK] = (float) Math.sin(i * Math.PI / 180.0);
        }
    }
}