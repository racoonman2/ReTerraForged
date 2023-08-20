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

package raccoonman.reterraforged.common.level.levelgen.noise;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import raccoonman.reterraforged.common.noise.Noise;
import raccoonman.reterraforged.common.noise.domain.Domain;
import raccoonman.reterraforged.common.noise.util.NoiseUtil;
import raccoonman.reterraforged.common.util.MathUtil;
import raccoonman.reterraforged.common.util.storage.Object2FloatCache;
import raccoonman.reterraforged.common.util.storage.WeightMap;

public class Blender implements Noise {
	public static final Codec<Blender> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Domain.CODEC.fieldOf("region_warp").forGetter((m) -> m.regionWarp),
		Codec.FLOAT.fieldOf("region_jitter").forGetter((m) -> m.regionJitter),
		Codec.FLOAT.fieldOf("region_scale").forGetter((m) -> m.regionScale),
		Codec.FLOAT.fieldOf("blending").forGetter((m) -> m.blending),
		WeightMap.codec(Noise.CODEC).fieldOf("noise").forGetter((m) -> m.noise)
	).apply(instance, Blender::new));

	private static final int REGION_SEED_OFFSET = 21491124;

	private final Domain regionWarp;
	private final float regionScale;
	private final float regionJitter;
	private final float blending;

	private final WeightMap<Holder<Noise>> noise;
	private final ThreadLocal<Local> local = ThreadLocal.withInitial(Local::new);

	public Blender(Domain regionWarp, float regionJitter, float regionScale, float blending, WeightMap<Holder<Noise>> noise) {
		this.regionWarp = regionWarp;
		this.regionScale = regionScale;
		this.regionJitter = regionJitter;
		this.blending = blending;
		this.noise = noise;
	}

	@Override
	public float getValue(float x, float y, int seed) {
		float regionX = this.regionWarp.getX(x, y, seed) * (1.0F / this.regionScale);
		float regionZ = this.regionWarp.getY(x, y, seed) * (1.0F / this.regionScale);
		Blender.Local local = this.local.get();
		getCell(seed + REGION_SEED_OFFSET, regionX, regionZ, this.regionJitter, local);
		return local.getValue(x, y, this.blending, seed, this.noise);
	}

	@Override
	public Codec<Blender> codec() {
		return CODEC;
	}

	private static void getCell(int seed, float x, float y, float jitter, Local local) {
		int maxX = NoiseUtil.floor(x) + 1;
		int maxY = NoiseUtil.floor(y) + 1;

		local.closestIndex = 0;
		local.closestIndex2 = 0;

		int nearestIndex = -1;
		int nearestIndex2 = -1;

		float nearestDistance = Float.MAX_VALUE;
		float nearestDistance2 = Float.MAX_VALUE;

		for (int cz = maxY - 2, i = 0; cz <= maxY; cz++) {
			for (int cx = maxX - 2; cx <= maxX; cx++, i++) {
				int hash = NoiseUtil.hash2D(seed, cx, cz);

				float dx = MathUtil.rand(hash, NoiseUtil.X_PRIME);
				float dz = MathUtil.rand(hash, NoiseUtil.Y_PRIME);

				float px = cx + dx * jitter;
				float pz = cz + dz * jitter;
				float dist = NoiseUtil.dist2(x, y, px, pz);

				local.hashes[i] = hash;
				local.distances[i] = dist;

				if (dist < nearestDistance) {
					nearestDistance2 = nearestDistance;
					nearestDistance = dist;
					nearestIndex2 = nearestIndex;
					nearestIndex = i;
				} else if (dist < nearestDistance2) {
					nearestDistance2 = dist;
					nearestIndex2 = i;
				}
			}
		}

		local.closestIndex = nearestIndex;
		local.closestIndex2 = nearestIndex2;
	}

	private static class Local {
		public int closestIndex;
		public int closestIndex2;

		public final int[] hashes = new int[9];
		public final float[] distances = new float[9];
		public final Object2FloatCache<Holder<Noise>> cache = new Object2FloatCache<>(9);

		public float getCentreNoiseIndex() {
			return getNoiseIndex(this.closestIndex);
		}

		public float getDistance(int index) {
			return NoiseUtil.sqrt(this.distances[index]);
		}

		public float getCentreValue(float x, float y, int seed, WeightMap<Holder<Noise>> noise) {
			return noise.getValue(this.getCentreNoiseIndex()).value().getValue(x, y, seed);
		}

		public float getValue(float x, float y, float blending, int seed, WeightMap<Holder<Noise>> noise) {
			float dist0 = getDistance(closestIndex);
			float dist1 = getDistance(closestIndex2);

			float borderDistance = (dist0 + dist1) * 0.5F;
			float blendRange = borderDistance * blending;
			float blendStart = borderDistance - blendRange;

			if (dist0 <= blendStart) {
				return getCentreValue(x, y, seed, noise);
			} else {
				return getBlendedValue(x, y, dist0, dist1, blendRange, seed, noise);
			}
		}

		public float getBlendedValue(float x, float y, float nearest, float nearest2, float blendRange, int seed, WeightMap<Holder<Noise>> noise) {
			cache.clear();

			float sumNoise = getCacheValue(closestIndex, x, y, seed, noise);
			float sumWeight = getWeight(nearest, nearest, blendRange);

			float nearestWeight2 = getWeight(nearest2, nearest, blendRange);
			if (nearestWeight2 > 0) {
				sumNoise += getCacheValue(closestIndex2, x, y, seed, noise) * nearestWeight2;
				sumWeight += nearestWeight2;
			}

			for (int i = 0; i < 9; i++) {
				if (i == closestIndex || i == closestIndex2)
					continue;

				float weight = getWeight(getDistance(i), nearest, blendRange);
				if (weight > 0) {
					sumNoise += getCacheValue(i, x, y, seed, noise) * weight;
					sumWeight += weight;
				}
			}

			return NoiseUtil.clamp(sumNoise / sumWeight, 0, 1);
		}

		private float getCacheValue(int index, float x, float y, int seed, WeightMap<Holder<Noise>> noise) {
			float noiseIndex = getNoiseIndex(index);
			var terrain = noise.getValue(noiseIndex);

			float value = cache.get(terrain);
			if (Float.isNaN(value)) {
				value = terrain.value().getValue(x, y, seed);
				cache.put(terrain, value);
			}

			return value;
		}

		private float getNoiseIndex(int index) {
			return MathUtil.rand(this.hashes[index]);
		}

		private static float getWeight(float dist, float origin, float blendRange) {
			float delta = dist - origin;
			if (delta <= 0.0F)
				return 1.0F;
			if (delta >= blendRange)
				return 0.0F;

			float weight = 1.0F - (delta / blendRange);
			return weight * weight;
		}
	}
}
