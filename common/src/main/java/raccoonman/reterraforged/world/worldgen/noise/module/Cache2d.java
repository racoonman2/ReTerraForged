package raccoonman.reterraforged.world.worldgen.noise.module;

import com.mojang.serialization.Codec;

import raccoonman.reterraforged.world.worldgen.util.PosUtil;

@Deprecated
public record Cache2d(Noise noise, ThreadLocal<Cached> cache) implements Noise {
	public static final Codec<Cache2d> CODEC = Noise.HOLDER_HELPER_CODEC.xmap(Cache2d::new, Cache2d::noise);
	
	public Cache2d(Noise noise) {
		this(noise, ThreadLocal.withInitial(() -> {
			return new Cached(noise);
		}));
	}
	
	@Override
	public float compute(float x, float z, int seed) {
		return this.cache.get().compute(x, z, seed);
	}

	@Override
	public float minValue() {
		return this.noise.minValue();
	}

	@Override
	public float maxValue() {
		return this.noise.maxValue();
	}

	@Override
	public Noise mapAll(Visitor visitor) {
		return visitor.apply(new Cache2d(this.noise.mapAll(visitor)));
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof Cache2d other && other.noise.equals(this.noise);
	}

	@Override
	public Codec<Cache2d> codec() {
		return CODEC;
	}
	
	public static class Cached implements Noise {
		public Noise noise;
		public long lastPos;
		public float value;
		
		public Cached(Noise noise) {
			this.noise = noise;
		}
		
		@Override
		public float compute(float x, float z, int seed) {
			long newPos = PosUtil.packf(x, z);
			if(this.lastPos != newPos) {
				this.value = this.noise.compute(x, z, seed);
				this.lastPos = newPos;
			}
			return this.value;
		}

		@Override
		public float minValue() {
			return this.noise.minValue();
		}

		@Override
		public float maxValue() {
			return this.noise.maxValue();
		}

		@Override
		public Noise mapAll(Visitor visitor) {
			return visitor.apply(this);
		}

		@Override
		public Codec<Cached> codec() {
			throw new UnsupportedOperationException();
		}
	}
}
