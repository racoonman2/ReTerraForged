package raccoonman.reterraforged.world.worldgen.surface.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.SurfaceRules.Context;
import raccoonman.reterraforged.platform.ModLoaderUtil;

record ModCondition(String modId) implements SurfaceRules.ConditionSource {
	public static final Codec<ModCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.STRING.fieldOf("mod_id").forGetter(ModCondition::modId)
	).apply(instance, ModCondition::new));
	
	@Override
	public SurfaceRules.Condition apply(Context t) {
		boolean isModLoaded = ModLoaderUtil.isLoaded(this.modId);
		return () -> isModLoaded;
	}

	@Override
	public KeyDispatchDataCodec<ModCondition> codec() {
		return new KeyDispatchDataCodec<>(CODEC);
	}
}
