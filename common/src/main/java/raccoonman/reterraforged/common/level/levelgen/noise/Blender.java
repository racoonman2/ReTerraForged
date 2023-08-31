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

import java.util.IdentityHashMap;
import java.util.Map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.level.levelgen.noise.domain.Domain;
import raccoonman.reterraforged.common.level.levelgen.noise.util.NoiseUtil;
import raccoonman.reterraforged.common.util.storage.WeightMap;

//TODO use correct min/max values
public record Blender(Domain regionWarp, float regionJitter, float regionScale, float blending, WeightMap<Noise> noise, ThreadLocal<Local> local) implements Noise {
	public static final Codec<Blender> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Domain.CODEC.fieldOf("region_warp").forGetter((m) -> m.regionWarp),
		Codec.FLOAT.fieldOf("region_jitter").forGetter((m) -> m.regionJitter),
		Codec.FLOAT.fieldOf("region_scale").forGetter((m) -> m.regionScale),
		Codec.FLOAT.fieldOf("blending").forGetter((m) -> m.blending),
		WeightMap.codec(Noise.HOLDER_HELPER_CODEC).fieldOf("noise").forGetter((m) -> m.noise)
	).apply(instance, Blender::new));

	public Blender(Domain regionWarp, float regionJitter, float regionScale, float blending, WeightMap<Noise> noise) {
		this(regionWarp, regionJitter, regionScale, blending, noise, ThreadLocal.withInitial(Local::new));
	}
	
	@Deprecated
	private static final int REGION_SEED_OFFSET = 21491124;

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

				float dx = NoiseUtil.rand(hash, NoiseUtil.X_PRIME);
				float dz = NoiseUtil.rand(hash, NoiseUtil.Y_PRIME);
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
		public final Map<Noise, Float> cache = new IdentityHashMap<>(9);

		public float getCentreNoiseIndex() {
			return this.getNoiseIndex(this.closestIndex);
		}

		public float getDistance(int index) {
			return NoiseUtil.sqrt(this.distances[index]);
		}

		public float getCentreValue(float x, float y, int seed, WeightMap<Noise> noise) {
			return noise.getValue(this.getCentreNoiseIndex()).getValue(x, y, seed);
		}

		public float getValue(float x, float y, float blending, int seed, WeightMap<Noise> noise) {
			float dist0 = this.getDistance(this.closestIndex);
			float dist1 = this.getDistance(this.closestIndex2);

			float borderDistance = (dist0 + dist1) * 0.5F;
			float blendRange = borderDistance * blending;
			float blendStart = borderDistance - blendRange;

			if (dist0 <= blendStart) {
				return this.getCentreValue(x, y, seed, noise);
			} else {
				return this.getBlendedValue(x, y, dist0, dist1, blendRange, seed, noise);
			}
		}

		public float getBlendedValue(float x, float y, float nearest, float nearest2, float blendRange, int seed, WeightMap<Noise> noise) {
			this.cache.clear();

			float sumNoise = this.getCacheValue(this.closestIndex, x, y, seed, noise);
			float sumWeight = getWeight(nearest, nearest, blendRange);

			float nearestWeight2 = getWeight(nearest2, nearest, blendRange);
			if (nearestWeight2 > 0) {
				sumNoise += this.getCacheValue(this.closestIndex2, x, y, seed, noise) * nearestWeight2;
				sumWeight += nearestWeight2;
			}

			for (int i = 0; i < 9; i++) {
				if (i == this.closestIndex || i == this.closestIndex2)
					continue;

				float weight = getWeight(getDistance(i), nearest, blendRange);
				if (weight > 0) {
					sumNoise += this.getCacheValue(i, x, y, seed, noise) * weight;
					sumWeight += weight;
				}
			}

			return NoiseUtil.clamp(sumNoise / sumWeight, 0, 1);
		}

		private float getCacheValue(int index, float x, float y, int seed, WeightMap<Noise> noise) {
			float noiseIndex = this.getNoiseIndex(index);
			var terrain = noise.getValue(noiseIndex);

			float value = this.cache.getOrDefault(terrain, Float.NaN);
			if (Float.isNaN(value)) {
				value = terrain.getValue(x, y, seed);
				this.cache.put(terrain, value);
			}

			return value;
		}

		private float getNoiseIndex(int index) {
			return NoiseUtil.rand(this.hashes[index]);
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
