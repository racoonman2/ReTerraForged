package raccoonman.reterraforged.world.worldgen.floatproviders;

import com.mojang.serialization.Codec;

import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.FloatProviderType;

@Deprecated
public class LegacyCanyonYScale extends FloatProvider {
	public static final Codec<LegacyCanyonYScale> CODEC = Codec.unit(LegacyCanyonYScale::new);
	
	@Override
	public float sample(RandomSource random) {
		return (random.nextFloat() - 0.5F) * 2.0F / 8.0F;
	}

	@Override
	public float getMinValue() {
		return -1.0F;
	}

	@Override
	public float getMaxValue() {
		return 1.0F;
	}

	@Override
	public FloatProviderType<LegacyCanyonYScale> getType() {
		return RTFFloatProviderTypes.LEGACY_CANYON_Y_SCALE;
	}
}
