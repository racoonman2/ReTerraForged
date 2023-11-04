package raccoonman.reterraforged.common.level.levelgen.density;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.levelgen.DensityFunction;
import raccoonman.reterraforged.common.level.levelgen.test.cell.Cell;
import raccoonman.reterraforged.common.level.levelgen.test.concurrent.Resource;
import raccoonman.reterraforged.common.level.levelgen.test.world.heightmap.WorldLookup;
import raccoonman.reterraforged.common.worldgen.data.preset.Preset;

public record CellSampler(Channel channel, WorldLookup worldLookup) implements DensityFunction.SimpleFunction {

	@Override
	public double compute(FunctionContext ctx) {
        try (Resource<Cell> resource = Cell.getResource()) {
            Cell cell = resource.get();
            this.worldLookup.applyCell(cell, ctx.blockX(), ctx.blockZ());
            return this.channel.read(cell);
        }
	}

	@Override
	public DensityFunction mapAll(Visitor visitor) {
		return visitor.apply(this);
	}

	@Override
	public double minValue() {
		return 0.0D;
	}

	@Override
	public double maxValue() {
		return 1.0D;
	}

	@Override
	public KeyDispatchDataCodec<CellSampler> codec() {
		throw new UnsupportedOperationException();
	}
	
	public record Marker(Channel channel, Holder<Preset> preset) implements DensityFunction.SimpleFunction {
		public static final Codec<CellSampler.Marker> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Channel.CODEC.fieldOf("channel").forGetter(CellSampler.Marker::channel),
			Preset.CODEC.fieldOf("preset").forGetter(CellSampler.Marker::preset)
		).apply(instance, CellSampler.Marker::new));
		
		@Override
		public double compute(FunctionContext ctx) {
			return 0.0D;
		}

		@Override
		public DensityFunction mapAll(Visitor visitor) {
			return visitor.apply(this);
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
		public KeyDispatchDataCodec<CellSampler.Marker> codec() {
			return new KeyDispatchDataCodec<>(CODEC);
		}
	}
	
	public class Cached implements DensityFunction.SimpleFunction {
		private Channel channel;
		private Cell[] cache;
		private int firstX;
		private int firstZ;
		private int width;
		
        public Cached(Channel channel, Cell[] cache, int firstX, int firstZ, int width) {
        	this.channel = channel;
        	this.cache = cache;
        	this.firstX = firstX;
        	this.firstZ = firstZ;
        	this.width = width;
        }

        @Override
        public double compute(DensityFunction.FunctionContext functionContext) {
        	int x = functionContext.blockX() - this.firstX;
        	int z = functionContext.blockZ() - this.firstZ;
            if (x >= 0 && z >= 0 && x < this.width && z < this.width) {
            	return this.channel.read(this.cache[x * this.width + z]);
            }
            return CellSampler.this.compute(functionContext);
        }
		
		@Override
		public double minValue() {
			return 0.0D;
		}

		@Override
		public double maxValue() {
			return 1.0D;
		}

		@Override
		public KeyDispatchDataCodec<CellSampler.Cached> codec() {
			throw new UnsupportedOperationException();
		}
	}
	
	public enum Channel implements StringRepresentable {
		HEIGHT {
			
			@Override
			public float read(Cell cell) {
				return cell.value;
			}
		},
		TEMPERATURE {
			
			@Override
			public float read(Cell cell) {
				return cell.temperatureLevel;
			}
		},
		MOISTURE {
			
			@Override
			public float read(Cell cell) {
				return cell.moistureLevel;
			}
		},
		CONTINENT {
			
			@Override
			public float read(Cell cell) {
				return cell.continentLevel;
			}
		},
		EROSION {

			@Override
			public float read(Cell cell) {
				return cell.erosionLevel;
			}
			
		},
		RIDGE {

			@Override
			public float read(Cell cell) {
				return cell.ridgeLevel;
			}
		},
		STEEPNESS {

			@Override
			public float read(Cell cell) {
				return cell.gradient;
			}
		},
		SEDIMENT {

			@Override
			public float read(Cell cell) {
				return cell.sediment;
			}
		};

		public static final Codec<Channel> CODEC = StringRepresentable.fromEnum(Channel::values);
		
		public abstract float read(Cell cell);
		
		@Override
		public String getSerializedName() {
			return this.name();
		}
	}
}
