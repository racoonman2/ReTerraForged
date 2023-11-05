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

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.registries.RTFBuiltInRegistries;
import raccoonman.reterraforged.common.registries.RTFCurveFunctionTypes;
import raccoonman.reterraforged.common.registries.RTFDensityFunctionTypes;
import raccoonman.reterraforged.common.registries.RTFDomainTypes;
import raccoonman.reterraforged.common.registries.RTFNoiseTypes;
import raccoonman.reterraforged.common.registries.RTFPlacementModifierTypes;
import raccoonman.reterraforged.common.registries.RTFRegistries;
import raccoonman.reterraforged.common.registries.RTFSurfaceConditionTypes;
import raccoonman.reterraforged.common.registries.RTFSurfaceRuleTypes;
import raccoonman.reterraforged.common.worldgen.data.preset.Preset;
import raccoonman.reterraforged.platform.RegistryUtil;

public final class ReTerraForged {
	public static final String MOD_ID = "reterraforged";
	public static final String LEGACY_MOD_ID = "terraforged";
	public static final Logger LOGGER = LogManager.getLogger("ReTerraForged");

	public static void bootstrap() {
		RTFBuiltInRegistries.bootstrap();
		RTFCurveFunctionTypes.bootstrap();
		RTFDomainTypes.bootstrap();
		RTFNoiseTypes.bootstrap();
		RTFPlacementModifierTypes.bootstrap();
		RTFDensityFunctionTypes.bootstrap();
		RTFSurfaceRuleTypes.bootstrap();
		RTFSurfaceConditionTypes.bootstrap();
		
		RegistryUtil.createDataRegistry(RTFRegistries.NOISE, Noise.DIRECT_CODEC);
		RegistryUtil.createDataRegistry(RTFRegistries.PRESET, Preset.DIRECT_CODEC);
	}
}
