///*
// * MIT License
// *
// * Copyright (c) 2021 TerraForged
// *
// * Permission is hereby granted, free of charge, to any person obtaining a copy
// * of this software and associated documentation files (the "Software"), to deal
// * in the Software without restriction, including without limitation the rights
// * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// * copies of the Software, and to permit persons to whom the Software is
// * furnished to do so, subject to the following conditions:
// *
// * The above copyright notice and this permission notice shall be included in all
// * copies or substantial portions of the Software.
// *
// * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// * SOFTWARE.
// */
//
package raccoonman.reterraforged.common.level.levelgen.noise.river;
//
//import raccoonman.reterraforged.common.level.levelgen.continent.ContinentPoints;
//import raccoonman.reterraforged.common.level.levelgen.continent.config.ContinentConfig;
//import raccoonman.reterraforged.common.level.levelgen.continent.config.RiverConfig;
//import raccoonman.reterraforged.common.level.levelgen.noise.NoiseLevels;
//import raccoonman.reterraforged.common.level.levelgen.noise.terrain.TerrainSample;
//import raccoonman.reterraforged.common.noise.Noise;
//import raccoonman.reterraforged.common.noise.Source;
//import raccoonman.reterraforged.common.noise.util.NoiseUtil;
//import raccoonman.reterraforged.common.util.MathUtil;
//
//public class RiverCarver {
//    private static final int SEED_OFFSET = 21221;
//    private static final double EROSION_FREQ = 128;
//    private static final float BORDER_OFFSET = 0.05f;
//    private static final float BORDER_RANGE = 1f - BORDER_OFFSET;
//
//    private final NoiseLevels levels;
//    private final Noise erosionNoise;
//    private final RiverConfig riverConfig = RiverConfig.river();
//    private final RiverConfig lakeConfig = RiverConfig.river();
//
//    public RiverCarver(NoiseLevels levels, ContinentConfig config) {
//        float frequency = levels.frequency * (1f / config.shape.scale);
//        this.levels = levels;
//        this.riverConfig.copy(config.rivers.rivers).scale(frequency);
//        this.lakeConfig.copy(config.rivers.lakes).scale(frequency);
//        this.erosionNoise = Source.builder()
//        	.frequency(EROSION_FREQ)
//        	.octaves(2)
//        	.ridge()
//        	.shift(config.rivers.seed + SEED_OFFSET);
//    }
//
//    public float carve(float x, float y, TerrainSample sample, RiverSample carverSample, int seed) {
//        float erosion = erosionNoise.getValue(x, y, seed);
//        float baseModifier = getBaseModifier(sample);
//
//        float baseNoise = sample.baseNoise * baseModifier;
//        baseNoise = carve(sample, carverSample.river(), riverConfig, baseNoise, baseModifier, erosion);
//        baseNoise = carve(sample, carverSample.lake(), lakeConfig, baseNoise, baseModifier, erosion);
//        return clipRiverNoise(sample);
//    }
//
//    private float carve(TerrainSample sample, NodeSample nodeSample, RiverConfig config, float baseNoise, float baseModifier, float erosion) {
//        float modifiedBaseNoise = getBaseNoise(sample, nodeSample, config, baseModifier);
//        if (modifiedBaseNoise == -1f) return baseNoise;
//
//        float baseLevel = levels.toHeightNoise(modifiedBaseNoise, 0f);
//        carve(baseLevel, erosion, sample, nodeSample, config);
//
//        return modifiedBaseNoise;
//    }
//
//    private void carve(float baseLevel, float erosion, TerrainSample sample, NodeSample nodeSample, RiverConfig config) {
//        if (nodeSample.isInvalid()) return;
//
//        float height = sample.heightNoise;
//        float position = nodeSample.position;
//        float distance = nodeSample.distance;
//
//        float valleyWidth = config.valleyWidth.at(position);
//        float bankWidth = config.bankWidth.at(position);
//        float bankDepth = config.bankDepth.at(position);
//        float bedWidth = config.bedWidth.at(position);
//        float bedDepth = config.bedDepth.at(position);
//
//        float bedLevel = baseLevel - bedDepth * levels.unit;
//        float bankLevel = baseLevel + bankDepth * levels.unit;
//
//        float valleyAlpha = getValleyAlpha(distance, bankWidth, valleyWidth, sample.baseNoise);
//        if (valleyAlpha < 1.0f) {
//            float level = Math.min(bankLevel, height);
//            float modifier = getErosionModifier(erosion * config.erosion, valleyAlpha);
//            height = NoiseUtil.lerp(level, height, valleyAlpha * modifier);
//            sample.waterNoise *= getValleyNoise(distance, bankWidth, valleyWidth);
//        }
//
//        float riverAlpha = getAlpha(distance, bedWidth, bankWidth);
//        if (riverAlpha < 1.0f) {
//            float level = Math.min(bedLevel, height);
//            height = NoiseUtil.lerp(level, height, riverAlpha);
//            sample.waterNoise *= getRiverNoise(height, baseLevel, bankLevel);
//        }
//
//        sample.heightNoise = height;
//    }
//
//    private float getBaseNoise(TerrainSample sample, NodeSample nodeSample, RiverConfig config, float modifier) {
//        if (nodeSample.isInvalid()) return -1f;
//
//        float distance = nodeSample.distance;
//        float position = nodeSample.position;
//
//        float valleyRadius = config.valleyWidth.at(position);
//        if (distance >= valleyRadius) return -1f;
//
//        float bankRadius = config.bankWidth.at(position);
//        if (distance <= bankRadius) return nodeSample.level * modifier;
//
//        float alpha = (distance - bankRadius) / (valleyRadius - bankRadius);
//        return NoiseUtil.lerp(nodeSample.level, sample.baseNoise, alpha) * modifier;
//    }
//
//    private float getBaseModifier(TerrainSample sample) {
//        float min = ContinentPoints.COAST;
//        float max = 1.0f;
//        return NoiseUtil.map(sample.continentNoise, min, max, max - min);
//    }
//
//    private float getErosionModifier(float erosionNoise, float valleyAlpha) {
//        float erosionFade = 1f - NoiseUtil.map(valleyAlpha, 0.975f, 1.0f, 0.025f);
//
//        return 1.0f - (erosionNoise * erosionFade);
//    }
//
//    private float getValleyNoise(float distance, float bankWidth, float valleyWidth) {
//        // Add offset so the falloff noise doesn't quite reach zero at the top of the river banks
//        float value = BORDER_OFFSET + getAlpha(distance, bankWidth, valleyWidth) / BORDER_RANGE;
//        return MathUtil.clamp(value, 0f, 1f);
//    }
//
//    private float getRiverNoise(float height, float waterLevel, float bankLevel) {
//        // Fall from the top of the river banks to water level
//        float value = getAlpha(height, waterLevel, bankLevel);
//        return MathUtil.clamp(value, 0f, 1f);
//    }
//
//    private static float clipRiverNoise(TerrainSample sample) {
//        // Cut off the river mask when they reach an ocean node
//        return sample.continentNoise < ContinentPoints.BEACH ? 1f : sample.waterNoise;
//    }
//
//    private static float getValleyAlpha(float distance, float bankWidth, float valleyWidth, float baseValue) {
//        float alpha = getAlpha(distance, bankWidth, valleyWidth);
//        float shapeAlpha = getAlpha(baseValue, 0.4f, 0.6f);
//
//        // Lerp between U-shaped falloff and linear
//        return NoiseUtil.lerp(alpha * alpha, alpha, shapeAlpha);
//    }
//
//    private static float getAlpha(float value, float min, float max) {
//        return value <= min ? 0.0f : value >= max ? 1.0f : (value - min) / (max - min);
//    }
//}