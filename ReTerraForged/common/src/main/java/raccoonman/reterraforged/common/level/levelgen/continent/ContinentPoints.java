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

package raccoonman.reterraforged.common.level.levelgen.continent;

import raccoonman.reterraforged.common.level.levelgen.continent.shape.FalloffPoint;
import raccoonman.reterraforged.common.level.levelgen.settings.ControlPoints;
import raccoonman.reterraforged.common.util.MathUtil;

public interface ContinentPoints {
    float DEEP_OCEAN = 0.1f;
    float SHALLOW_OCEAN = 0.25f;
    float BEACH = 0.5f;
    float COAST = 0.55f;
    float INLAND = 0.6f;
    
    static FalloffPoint[] getFalloff(ControlPoints controlPoints) {
        return new FalloffPoint[] {
        	new FalloffPoint(controlPoints.inland, 1.0F, 1.0F),
        	new FalloffPoint(controlPoints.coast, ContinentPoints.COAST, 1.0F),
        	new FalloffPoint(controlPoints.beach, ContinentPoints.BEACH, ContinentPoints.COAST),
        	new FalloffPoint(controlPoints.shallowOcean, ContinentPoints.SHALLOW_OCEAN, ContinentPoints.BEACH),
        	new FalloffPoint(controlPoints.deepOcean, ContinentPoints.DEEP_OCEAN, ContinentPoints.SHALLOW_OCEAN),
        };
    }

    static float getFalloff(float continentNoise, FalloffPoint[] falloffCurve) {
        float previous = 1.0F;

        for (var falloff : falloffCurve) {

            if (continentNoise >= falloff.controlPoint()) {
                return MathUtil.map(continentNoise, falloff.controlPoint(), previous, falloff.min(), falloff.max());
            }

            previous = falloff.controlPoint();
        }

        return MathUtil.map(continentNoise, 0.0F, previous, 0.0F, ContinentPoints.DEEP_OCEAN);
    }
}
