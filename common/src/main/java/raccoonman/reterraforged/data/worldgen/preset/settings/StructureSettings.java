package raccoonman.reterraforged.data.worldgen.preset.settings;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;

public class StructureSettings {
	public static final Codec<StructureSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.unboundedMap(ResourceKey.codec(Registries.STRUCTURE_SET), StructureSetEntry.CODEC).optionalFieldOf("structure_sets", new HashMap<>()).forGetter((s) -> s.structureSets)
	).apply(instance, StructureSettings::new));
	
	public Optional<StructureSet> strongholdSettings;
	public Map<ResourceKey<StructureSet>, StructureSetEntry> structureSets;
	
	public StructureSettings(Map<ResourceKey<StructureSet>, StructureSetEntry> structureSets) {
		this.structureSets = new HashMap<>(structureSets);
	}
	
	public StructureSettings() {
		this(ImmutableMap.of());
	}
	
	public StructureSettings copy() {
		Map<ResourceKey<StructureSet>, StructureSetEntry> entries = new HashMap<>();
		for(Entry<ResourceKey<StructureSet>, StructureSetEntry> entry : this.structureSets.entrySet()) {
			entries.put(entry.getKey(), entry.getValue().copy());
		}
		return new StructureSettings(entries);
	}
	
	public static class Strongholds {
		
	}
	
	public static class StructureSetEntry {
		public static final Codec<StructureSetEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			StructureSet.DIRECT_CODEC.fieldOf("structure_set").forGetter((s) -> s.structureSet),
			Codec.BOOL.fieldOf("disabled").forGetter((s) -> s.disabled)
		).apply(instance, StructureSetEntry::new));
		
		public StructureSet structureSet;
		public boolean disabled;
		
		public StructureSetEntry(StructureSet structureSet, boolean disabled) {
			this.structureSet = structureSet;
			this.disabled = disabled;
		}
		
		public void setPlacement(StructurePlacement placement) {
			this.structureSet = new StructureSet(this.structureSet.structures(), placement);
		}
		
		public StructureSetEntry copy() {
			return new StructureSetEntry(this.structureSet, this.disabled);
		}
	}
}
