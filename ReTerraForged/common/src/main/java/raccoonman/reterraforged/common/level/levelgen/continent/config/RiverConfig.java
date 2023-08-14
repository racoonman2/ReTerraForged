/*
 * MIT License
 *
 * Copyright (c) 2021 TerraForged
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

package raccoonman.reterraforged.common.level.levelgen.continent.config;

public class RiverConfig {
    public float erosion = 0.075f;
    public final FloatRange bedWidth = new FloatRange(1, 7);
    public final FloatRange bankWidth = new FloatRange(3, 30);
    public final FloatRange valleyWidth = new FloatRange(80, 200);

    public final FloatRange bedDepth = new FloatRange(1.25f, 5f);
    public final FloatRange bankDepth = new FloatRange(1.25f, 3f);

    public RiverConfig copy(RiverConfig config) {
        erosion = config.erosion;

        bedDepth.copy(config.bedDepth);
        bankDepth.copy(config.bankDepth);

        bedWidth.copy(config.bedWidth);
        bankWidth.copy(config.bankWidth);
        valleyWidth.copy(config.valleyWidth);

        return this;
    }

    public RiverConfig scale(float frequency) {
        bedWidth.scale(frequency);
        bankWidth.scale(frequency);
        valleyWidth.scale(frequency);
        return this;
    }

    public static RiverConfig lake() {
        var config = new RiverConfig();
        config.bankWidth.min = 30;
        config.bankWidth.max = 45;

        config.bankDepth.min = 1f;
        config.bankDepth.max = 1.5f;

        config.bedWidth.min = 8;
        config.bedWidth.max = 15;

        config.bedDepth.min = 2f;
        config.bedDepth.max = 8f;

        config.valleyWidth.min = 80;
        config.valleyWidth.max = 120;

        return config;
    }
}
