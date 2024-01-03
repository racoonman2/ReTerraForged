package raccoonman.reterraforged.world.worldgen.noise.function;

import com.mojang.serialization.Codec;

import net.minecraft.util.StringRepresentable;

public enum EdgeFunction implements StringRepresentable {
	DISTANCE_2("DISTANCE_2") {

		@Override
		public float apply(float distance, float distance2) {
			return distance2 - 1.0F;
		}

		@Override
		public float max() {
			return 1.0F;
		}

		@Override
		public float min() {
			return -1.0F;
		}

		@Override
		public float range() {
			return 2.0F;
		}
	},
	DISTANCE_2_ADD("DISTANCE_2_ADD") {

		@Override
		public float apply(float distance, float distance2) {
			return distance2 + distance - 1.0F;
		}

		@Override
		public float max() {
			return 1.6F;
		}

		@Override
		public float min() {
			return -1.0F;
		}

		@Override
		public float range() {
			return 2.6F;
		}
	},
	DISTANCE_2_SUB("DISTANCE_2_SUB") {

		@Override
		public float apply(float distance, float distance2) {
			return distance2 - distance - 1.0F;
		}

		@Override
		public float max() {
			return 0.8F;
		}

		@Override
		public float min() {
			return -1.0F;
		}

		@Override
		public float range() {
			return 1.8F;
		}
	},
	DISTANCE_2_MUL("DISTANCE_2_MUL") {

		@Override
		public float apply(float distance, float distance2) {
			return distance2 * distance - 1.0F;
		}

		@Override
		public float max() {
			return 0.7F;
		}

		@Override
		public float min() {
			return -1.0F;
		}

		@Override
		public float range() {
			return 1.7F;
		}
	},
	DISTANCE_2_DIV("DISTANCE_2_DIV") {

		@Override
		public float apply(float distance, float distance2) {
			return distance / distance2 - 1.0F;
		}

		@Override
		public float max() {
			return 0.0F;
		}

		@Override
		public float min() {
			return -1.0F;
		}

		@Override
		public float range() {
			return 1.0F;
		}
	};

	public static final Codec<EdgeFunction> CODEC = StringRepresentable.fromEnum(EdgeFunction::values);

	private String name;

	private EdgeFunction(String name) {
		this.name = name;
	}

	@Override
	public String getSerializedName() {
		return this.name;
	}

	public abstract float apply(float distance, float distance2);

	public abstract float max();

	public abstract float min();

	public abstract float range();
}
