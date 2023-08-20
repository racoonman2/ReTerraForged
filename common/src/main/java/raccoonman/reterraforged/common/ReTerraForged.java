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

package raccoonman.reterraforged.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import raccoonman.reterraforged.common.level.levelgen.climate.Climate;
import raccoonman.reterraforged.common.level.levelgen.climate.ClimatePreset;
import raccoonman.reterraforged.common.noise.Noise;
import raccoonman.reterraforged.common.registries.RTFBiomeSources;
import raccoonman.reterraforged.common.registries.RTFBuiltInRegistries;
import raccoonman.reterraforged.common.registries.RTFCurveFunctionTypes;
import raccoonman.reterraforged.common.registries.RTFDensityFunctionTypes;
import raccoonman.reterraforged.common.registries.RTFDomainTypes;
import raccoonman.reterraforged.common.registries.RTFFeatures;
import raccoonman.reterraforged.common.registries.RTFNoiseTypes;
import raccoonman.reterraforged.common.registries.RTFRegistries;
import raccoonman.reterraforged.platform.registries.RegistryUtil;

public final class ReTerraForged {
	public static final String MOD_ID = "reterraforged";
	public static final Logger LOGGER = LogManager.getLogger("ReTerraForged");

	public static void bootstrap() {
		RTFBuiltInRegistries.bootstrap();
		// TODO move this somewhere else;
		RegistryUtil.createDataRegistry(RTFRegistries.NOISE, Noise.DIRECT_CODEC);
		RegistryUtil.createDataRegistry(RTFRegistries.CLIMATE, Climate.DIRECT_CODEC);
		RegistryUtil.createDataRegistry(RTFRegistries.CLIMATE_PRESET, ClimatePreset.DIRECT_CODEC);
		RTFCurveFunctionTypes.bootstrap();
		RTFDomainTypes.bootstrap();
		RTFNoiseTypes.bootstrap();
		RTFFeatures.bootstrap();
		RTFDensityFunctionTypes.bootstrap();
		RTFBiomeSources.bootstrap();
	}
	
	// does this belong here?
	public static ResourceLocation resolve(String name) {
		if (name.contains(":")) return new ResourceLocation(name);
		return new ResourceLocation(MOD_ID, name);
	}

	// ^^^^^^^^
	public static <T> ResourceKey<T> resolve(ResourceKey<? extends Registry<T>> registryKey, String valueKey) {
		return ResourceKey.create(registryKey, resolve(valueKey));
	}
}
