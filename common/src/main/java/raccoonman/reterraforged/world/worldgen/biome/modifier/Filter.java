package raccoonman.reterraforged.world.worldgen.biome.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.biome.Biome;

public record Filter(HolderSet<Biome> biomes, Behavior behavior) {
	public static final Codec<Filter> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Biome.LIST_CODEC.fieldOf("biomes").forGetter(Filter::biomes),
		Behavior.CODEC.fieldOf("behavior").forGetter(Filter::behavior)
	).apply(instance, Filter::new));
	
	public boolean test(Holder<Biome> biome) {
		return this.behavior.test(this.biomes, biome);
	}
	
	public enum Behavior implements StringRepresentable {
		WHITELIST("whitelist") {
			
			@Override
			public boolean test(HolderSet<Biome> biomes, Holder<Biome> biome) {
				return biomes.contains(biome);
			}
		},
		BLACKLIST("blacklist") {

			@Override
			public boolean test(HolderSet<Biome> biomes, Holder<Biome> biome) {
				return !biomes.contains(biome);
			}
		};
		
		public static final Codec<Behavior> CODEC = StringRepresentable.fromEnum(Behavior::values);
		
		private String name;
		
		private Behavior(String name) {
			this.name = name;
		}

		@Override
		public String getSerializedName() {
			return this.name;
		}
		
		public abstract boolean test(HolderSet<Biome> biomes, Holder<Biome> biome);
	}
}
