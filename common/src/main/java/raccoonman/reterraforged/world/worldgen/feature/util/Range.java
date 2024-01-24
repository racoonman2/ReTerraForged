package raccoonman.reterraforged.world.worldgen.feature.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record Range(float from, float to, float max, float range, boolean exclusive) {
	public static final Codec<Range> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.FLOAT.fieldOf("from").forGetter(Range::from),
		Codec.FLOAT.fieldOf("to").forGetter(Range::to),
		Codec.BOOL.fieldOf("exclusive").forGetter(Range::exclusive)
	).apply(instance, Range::of));

    public float apply(float value) {
        if (this.from < this.to) {
            if (value <= this.from) {
                return 0.0F;
            }
            if (value >= this.to) {
                return this.max;
            }
            return (value - this.from) / this.range;
        } else if (this.from > this.to) {
            if (value <= this.to) {
                return this.max;
            }
            if (value >= this.from) {
                return 0.0F;
            }
            return 1.0F - ((value - this.to) / this.range);
        }
        return 0.0F;
    }
	
    public static Range of(float from, float to, boolean exclusive) {
        return new Range(from, to, exclusive ? 0.0F : 1.0F, Math.abs(to - from), exclusive);
    }
}
