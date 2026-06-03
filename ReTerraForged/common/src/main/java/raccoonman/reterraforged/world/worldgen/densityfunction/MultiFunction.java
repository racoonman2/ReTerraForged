package raccoonman.reterraforged.world.worldgen.densityfunction;

import org.jetbrains.annotations.Nullable;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunction.SimpleFunction;
import raccoonman.reterraforged.world.worldgen.PosUtil;

public abstract class MultiFunction implements SimpleFunction {
	@Deprecated // find a better solution for this
	protected ResourceLocation cacheId;
	protected int fieldCount;
	protected int fieldIndex;

	@Nullable
	private MultiFunction.Data cachedData;
	
	public MultiFunction(ResourceLocation cacheId, int fieldCount, int fieldIndex, MultiFunction.Data cachedData) {
		this.cacheId = cacheId;
		this.fieldCount = fieldCount;
		this.fieldIndex = fieldIndex;
		this.cachedData = cachedData;
	}

	@Override
	public double compute(FunctionContext functionContext) {
		int blockX = functionContext.blockX();
		int blockZ = functionContext.blockZ();
		MultiFunction.Data data = this.cachedData != null ? this.cachedData : this.createData();
		if(data != this.cachedData || data.update(blockX, blockZ)) {
			this.compute(functionContext, data.fields);
		}
		return data.fields[this.fieldIndex];
	}

	@Override
	public double maxValue() {
		return 0.0F;
	}

	@Override
	public double minValue() {
		return 1.0F;
	}
	
	public ResourceLocation cacheId() {
		return this.cacheId;
	}
	
	public int fieldIndex() {
		return this.fieldIndex;
	}
	
	public MultiFunction.Data createData() {
		return new MultiFunction.Data(this.fieldCount);
	}

	public abstract DensityFunction withData(MultiFunction.Data data);
	
	public abstract void compute(FunctionContext ctx, float[] fields);

	public static class Data {
		float[] fields;
		private long lastKey;
		
		public Data(int fieldCount) {
			this.fields = new float[fieldCount];
			this.lastKey = Long.MIN_VALUE;
		}
		
		public boolean update(int blockX, int blockZ) {
			long key = PosUtil.pack(blockX, blockZ);
			if(key == this.lastKey) {
				return false;
			} else {
				this.lastKey = key;
				return true;
			}
		}
	}
}
