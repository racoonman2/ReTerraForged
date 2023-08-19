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

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class RiverConfig {
	public static final Codec<RiverConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.FLOAT.fieldOf("erosion").forGetter((c) -> c.erosion),
		FloatRange.CODEC.fieldOf("bed_width").forGetter((c) -> c.bedWidth),
		FloatRange.CODEC.fieldOf("bank_width").forGetter((c) -> c.bankWidth),
		FloatRange.CODEC.fieldOf("valley_width").forGetter((c) -> c.valleyWidth),
		FloatRange.CODEC.fieldOf("bed_depth").forGetter((c) -> c.bedDepth),
		FloatRange.CODEC.fieldOf("bank_depth").forGetter((c) -> c.bankDepth)	
	).apply(instance, RiverConfig::new));
	
    public float erosion;
    public final FloatRange bedWidth;
    public final FloatRange bankWidth;
    public final FloatRange valleyWidth;

    public final FloatRange bedDepth;
    public final FloatRange bankDepth;
	
	public RiverConfig(float erosion, FloatRange bedWidth, FloatRange bankWidth, FloatRange valleyWidth, FloatRange bedDepth, FloatRange bankDepth) {
		this.erosion = erosion;
		this.bedWidth = bedWidth;
		this.bankWidth = bankDepth;
		this.valleyWidth = valleyWidth;
		this.bedDepth = bedDepth;
		this.bankDepth = bankDepth;
	}

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

    public static RiverConfig river() {
    	return new RiverConfig(
    		0.075F,
    		new FloatRange(1, 7),
    		new FloatRange(3, 30),
    		new FloatRange(80, 200),
    		new FloatRange(1.25F, 5.0F),
    		new FloatRange(1.25F, 3.0F)
    	);
    }
    
    public static RiverConfig lake() {
        return new RiverConfig(
        	0.075F,
        	new FloatRange(1, 15),
        	new FloatRange(30, 45),
        	new FloatRange(80, 200),
        	new FloatRange(1.25F, 8.0F),
        	new FloatRange(1.25F, 3.0F)
        );
    }
}
