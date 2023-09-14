package raccoonman.reterraforged.common.data.preset;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.StructureSet;

public class StructureSettings {
	public static final Codec<StructureSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.unboundedMap(ResourceKey.codec(Registries.STRUCTURE_SET), StructureSetEntry.CODEC).fieldOf("structures").forGetter((s) -> s.entries)
	).apply(instance, StructureSettings::new));
	
	public Map<ResourceKey<StructureSet>, StructureSetEntry> entries;
	
	public StructureSettings(Map<ResourceKey<StructureSet>, StructureSetEntry> entries) {
		this.entries = new HashMap<>(entries);
	}
	
	public StructureSettings() {
		this(ImmutableMap.of());
	}
	
	public StructureSettings copy() {
		Map<ResourceKey<StructureSet>, StructureSetEntry> entries = new HashMap<>();
		for(Entry<ResourceKey<StructureSet>, StructureSetEntry> entry : this.entries.entrySet()) {
			entries.put(entry.getKey(), entry.getValue().copy());
		}
		return new StructureSettings(entries);
	}
	
	public static StructureSettings makeDefault() {
		return new StructureSettings();
	}
	
	public static class StructureSetEntry {
		public static final Codec<StructureSetEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.INT.fieldOf("spacing").forGetter((s) -> s.spacing),
			Codec.INT.fieldOf("separation").forGetter((s) -> s.separation),
			Codec.INT.fieldOf("salt").forGetter((s) -> s.salt),
			Codec.BOOL.fieldOf("disabled").forGetter((s) -> s.disabled)
		).apply(instance, StructureSetEntry::new));
		
		public int spacing;
		public int separation;
		public int salt;
		public boolean disabled;
		
		public StructureSetEntry(int spacing, int separation, int salt, boolean disabled) {
			this.spacing = spacing;
			this.separation = separation;
			this.salt = salt;
			this.disabled = disabled;
		}
		
		public StructureSetEntry copy() {
			return new StructureSetEntry(this.spacing, this.separation, this.salt, this.disabled);
		}
	}
}
