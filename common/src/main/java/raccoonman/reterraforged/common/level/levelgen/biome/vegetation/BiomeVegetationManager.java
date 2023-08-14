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

package raccoonman.reterraforged.common.level.levelgen.biome.vegetation;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;

public class BiomeVegetationManager {
    private final HolderSet<VegetationConfig> configs;
    private final Map<Holder<Biome>, BiomeVegetation> vegetation = new ConcurrentHashMap<>();

    public BiomeVegetationManager(HolderSet<VegetationConfig> configs) {
        this.configs = configs;
    }

    public BiomeVegetation getVegetation(Holder<Biome> biome) {
        return vegetation.computeIfAbsent(biome, this::compute);
    }

    /**
     * Note: Must be lazily computed because tags load after world-gen
     */
    private BiomeVegetation compute(Holder<Biome> biome) {
        var config = getConfig(biome, configs);
        var features = VegetationFeatures.create(biome.value(), config);
        return new BiomeVegetation(config, features);
    }

    private static Optional<VegetationConfig> getConfig(Holder<Biome> biome, HolderSet<VegetationConfig> configs) {
        for (var config : configs) {
        	var value = config.value();
            if (biome.is(value.biomes())) {
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }
}
