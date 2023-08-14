/*
 * Decompiled with CFR 0.150.
 */
package raccoonman.reterraforged.common.level.levelgen.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class Seed {
	public static final Codec<Seed> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.INT.fieldOf("value").forGetter(Seed::get)
	).apply(instance, Seed::new));
	
    private final int root;
    private int value;

    public Seed(long value) {
        this((int) value);
    }

    public Seed(int value) {
        this.root = value;
        this.value = value;
    }

    public int next() {
        return this.value++;
    }

    public int get() {
        return this.value;
    }

    public int root() {
        return this.root;
    }

    public Seed split() {
        return new Seed(this.root);
    }

    public Seed offset(int offset) {
        return new Seed(this.root + offset);
    }
}

