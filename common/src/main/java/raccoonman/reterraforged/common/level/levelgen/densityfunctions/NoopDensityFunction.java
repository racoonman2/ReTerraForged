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

package raccoonman.reterraforged.common.level.levelgen.densityfunctions;

import java.util.Arrays;

import com.mojang.serialization.MapCodec;

import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;

public final class NoopDensityFunction implements DensityFunctions.BeardifierOrMarker {
	public static final NoopDensityFunction INSTANCE = new NoopDensityFunction();
    public static final KeyDispatchDataCodec<DensityFunction> CODEC = KeyDispatchDataCodec.of(MapCodec.unit(INSTANCE));

    private NoopDensityFunction() {
    }
    
    @Override
    public double compute(DensityFunction.FunctionContext ctx) {
        return 0.0D;
    }

    @Override
    public void fillArray(double[] array, DensityFunction.ContextProvider ctx) {
        Arrays.fill(array, 0.0D);
    }

	@Override
	public double minValue() {
		return 0.0D;
	}

	@Override
	public double maxValue() {
		return 0.0D;
	}
	
	@Override
	public KeyDispatchDataCodec<? extends DensityFunction> codec() {
		return CODEC;
	}
}
