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

package raccoonman.reterraforged.common.util.storage;

import java.util.function.IntFunction;
import java.util.function.Supplier;

public class ObjectMap<T> {
    private final Index index;
    private final T[] data;

    public ObjectMap(IntFunction<T[]> constructor) {
        index = Index.CHUNK;
        data = constructor.apply(16 * 16);
    }

    public ObjectMap(int border, IntFunction<T[]> constructor) {
        int size = 16 + border * 2;
        this.index = Index.borderedChunk(border);
        this.data = constructor.apply(size * size);
    }

    public Index getIndex() {
        return index;
    }

    public T get(int x, int z) {
        return get(index.of(x, z));
    }

    public void set(int x, int z, T value) {
        set(index.of(x, z), value);
    }

    public T get(int index) {
        return data[index];
    }

    public void set(int index, T value) {
        data[index] = value;
    }

    public void fill(Supplier<T> supplier) {
        for (int i = 0; i < data.length; i++) {
            data[i] = supplier.get();
        }
    }
}
