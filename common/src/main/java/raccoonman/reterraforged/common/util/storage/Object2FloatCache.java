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

import java.util.IdentityHashMap;
import java.util.Map;

public class Object2FloatCache<T> {
    protected final Value[] values;
    protected final Map<T, Value> map;

    public Object2FloatCache(int size) {
        this.values = new Value[size];
        this.map = new IdentityHashMap<>();
        for (int i = 0; i < values.length; i++) {
            values[i] = new Value();
        }
    }

    public void clear() {
        map.clear();
    }

    public void put(T t, float value) {
        int index = map.size();

        var holder = values[index];
        holder.value = value;

        map.put(t, holder);
    }

    public float get(T t) {
        var holder = map.get(t);
        if (holder == null) return Float.NaN;
        return holder.value;
    }

    protected static class Value {
        protected float value;
    }
}
