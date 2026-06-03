package raccoonman.reterraforged.world.worldgen.modifier;

import java.util.Set;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.LevelStem;
import raccoonman.reterraforged.extensions.ExtensionUtil;
import raccoonman.reterraforged.extensions.RTFChunkGenerator;
import raccoonman.reterraforged.world.PointFinder;

public record SetSpawnFinderModifier(Set<ResourceKey<LevelStem>> levels, PointFinder spawnFinder) implements LevelModifier {
	public static final MapCodec<SetSpawnFinderModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> LevelModifier.addFields(instance).and(
		PointFinder.CODEC.fieldOf("spawn_finder").forGetter(SetSpawnFinderModifier::spawnFinder)
	).apply(instance, SetSpawnFinderModifier::new));
	
	@Override
	public void applyModifier(LevelStem level, RegistryAccess registryAccess) {
		RTFChunkGenerator rtfChunkGenerator = ExtensionUtil.cast(level.generator());
		rtfChunkGenerator.setSpawnFinder(this.spawnFinder);
	}
	
	@Override
	public MapCodec<SetSpawnFinderModifier> codec() {
		return CODEC;
	}

	@Override
	public int priority() {
		return 0;
	}
}
