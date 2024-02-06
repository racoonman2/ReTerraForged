package raccoonman.reterraforged.world.worldgen.surface;

import java.util.BitSet;
import java.util.function.Predicate;

import org.apache.commons.lang3.mutable.MutableObject;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

public record SurfaceMask(Predicate<ResourceKey<Biome>> predicate, MutableObject<BitSet> mask) {
	
	public void setValue(int index, boolean value) {
		this.mask.getValue().set(index, value);
	}
}
