package raccoonman.reterraforged.common.util.storage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.util.MathUtil;
import raccoonman.reterraforged.common.util.CodecUtil;

public class WeightMap<T> implements Iterable<T> {
    protected final Object[] values;
    protected final float[] cumulativeWeights;
    protected final float sumWeight;
    protected final float zeroWeight;
    private float[] weights;

    private WeightMap(float[] weights, Object[] values) {
        this.values = Arrays.copyOf(values, values.length);
        this.weights = Arrays.copyOf(weights, weights.length);
        this.cumulativeWeights = getCumulativeWeights(values.length, weights);
        this.zeroWeight = weights.length > 0 ? weights[0] : 0;
        this.sumWeight = MathUtil.sum(weights) * MathUtil.EPSILON;
    }

    @SuppressWarnings("unchecked")
	public Stream<T> streamValues() {
    	return (Stream<T>) Stream.of(this.values);
    }
    
    public boolean isEmpty() {
        return values.length == 0;
    }

    @SuppressWarnings("unchecked")
	public T getValue(float value) {
        value *= sumWeight;

        if (value < zeroWeight) return (T) values[0];

        for (int i = 1; i < cumulativeWeights.length; i++) {
            if (value < cumulativeWeights[i]) {
                return (T) values[i];
            }
        }

        return null;
    }

	@Override
	public Iterator<T> iterator() {
		return new Iterator<>() {
			private int index;
			
			@Override
			public boolean hasNext() {
				return this.index < WeightMap.this.values.length;
			}

			@SuppressWarnings("unchecked")
			@Override
			public T next() {
				return (T) WeightMap.this.values[this.index++];
			}
		};
	}

    public static <T> WeightMap<T> of(float[] weights, T[] values) {
        return new WeightMap<>(weights, values);
    }

    @SuppressWarnings("unchecked")
	public static <T> Codec<WeightMap<T>> codec(Codec<T> valueCodec) {
    	return CodecUtil.forArray(Entry.codec(valueCodec), (size) -> {
    		return (Entry<T>[]) new Entry[size];
    	}).xmap((entries) -> {
    		return new WeightMap<>(getWeights(entries), getValues(entries));
    	}, (map) -> {
    		return getEntries(map.weights, map.values);
    	});
    }
    
    private record Entry<T>(float weight, T value) {
    	
    	public static <T> Codec<Entry<T>> codec(Codec<T> valueCodec) {
    		return RecordCodecBuilder.create(instance -> instance.group(
    			Codec.FLOAT.fieldOf("weight").forGetter(Entry::weight),
    			valueCodec.fieldOf("value").forGetter(Entry::value)
    		).apply(instance, Entry::new));
    	}
    }
    
    private static <T> Object[] getValues(Entry<T>[] entries) {
    	Object[] objects = new Object[entries.length];
    	for(int i = 0; i < objects.length; i++) {
    		objects[i] = entries[i].value;
    	}
    	return objects;
    }
    
    private static <T> float[] getWeights(Entry<T>[] entries) {
    	float[] weights = new float[entries.length];
    	for(int i = 0; i < weights.length; i++) {
    		weights[i] = entries[i].weight;
    	}
    	return weights;
    }
    
    @SuppressWarnings("unchecked")
	private static <T> Entry<T>[] getEntries(float[] weights, Object[] values) {
		Entry<T>[] entries = new Entry[values.length];
    	for(int i = 0; i < entries.length; i++) {
    		entries[i] = new Entry<>(weights[i], (T) values[i]);
    	}
    	return entries;
    }
    
    private static float[] getCumulativeWeights(int len, float[] weights) {
        float[] cumulativeWeights = new float[len];

        float weight = 0F;
        for (int i = 0; i < len; i++) {
            weight += i < weights.length ? weights[i] : 1;
            cumulativeWeights[i] = weight;
        }

        return cumulativeWeights;
    }
    
    public static class Builder<T> {
    	private List<Pair<Float, T>> entries;
    	
    	public Builder() {
    		this.entries = new ArrayList<>();
    	}
    	
		public Builder<T> entry(float weight, T value) {
    		this.entries.add(Pair.of(weight, value));
    		return this;
    	}
		
		public boolean isEmpty() {
			return this.entries.isEmpty();
		}
		
		public List<Pair<Float, T>> entries() {
			return this.entries;
		}

		@SuppressWarnings("unchecked")
		public void addAll(WeightMap<T> map) {
			for(int i = 0; i < map.values.length; i++) {
				this.entry(map.weights[i], (T) map.values[i]);
			}
		}
		
    	public <R extends T> WeightMap<R> build() {
    		int size = this.entries.size();
    		float[] weights = new float[size];
    		Object[] values = new Object[size];
    		for(int i = 0; i < size; i++) {
    			Pair<Float, T> entry = this.entries.get(i);
    			weights[i] = entry.getFirst();
    			values[i] = entry.getSecond();
    		}
    		return new WeightMap<>(weights, values);
    	}
    }
}