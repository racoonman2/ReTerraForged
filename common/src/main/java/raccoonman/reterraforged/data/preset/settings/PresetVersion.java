package raccoonman.reterraforged.data.preset.settings;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.StringRepresentable;

//note: we can't store the direct codecs in a field or we get dependency issues with the Preset codec 
public enum PresetVersion implements StringRepresentable {
	TERRAFORGED("terraforged") {

		@Override
		public Codec<Preset> codec() {
			return RecordCodecBuilder.create(instance -> instance.group(
				PresetVersion.CODEC.optionalFieldOf("version", PresetVersion.TERRAFORGED).forGetter(Preset::version),
				WorldSettings.CODEC.fieldOf("world").forGetter(Preset::world),
				SurfaceSettings.CODEC.optionalFieldOf("surface", new SurfaceSettings(new SurfaceSettings.Erosion(30, 140, 40, 95, 95, 0.65F, 0.475F, 0.4F, 0.45F, 6.0F, 3.0F))).forGetter(Preset::surface),
				CaveSettings.CODEC.optionalFieldOf("caves", new CaveSettings(1.5625F, 0.0F, 1.0F, 1.0F, 1.0F, 0.14285715F, 0.07F, 0.02F, true, false)).forGetter(Preset::caves),
				ClimateSettings.CODEC.fieldOf("climate").forGetter(Preset::climate),
				TerrainSettings.CODEC.fieldOf("terrain").forGetter(Preset::terrain),
				RiverSettings.CODEC.fieldOf("rivers").forGetter(Preset::rivers),
				FilterSettings.CODEC.fieldOf("filters").forGetter(Preset::filters),
				MiscellaneousSettings.CODEC.fieldOf("miscellaneous").forGetter(Preset::miscellaneous)
			).apply(instance, Preset::new));
		}
	},
	RETERRAFORGED("reterraforged") {

		@Override
		public Codec<Preset> codec() {
			return RecordCodecBuilder.create(instance -> instance.group(
				PresetVersion.CODEC.optionalFieldOf("version", PresetVersion.TERRAFORGED).forGetter(Preset::version),
				WorldSettings.CODEC.fieldOf("world").forGetter(Preset::world),
				SurfaceSettings.CODEC.optionalFieldOf("surface", new SurfaceSettings(new SurfaceSettings.Erosion(30, 140, 40, 95, 95, 0.65F, 0.475F, 0.4F, 0.45F, 6.0F, 3.0F))).forGetter(Preset::surface),
				CaveSettings.CODEC.optionalFieldOf("caves", new CaveSettings(1.5625F, 0.0F, 1.0F, 1.0F, 1.0F, 0.14285715F, 0.07F, 0.02F, true, false)).forGetter(Preset::caves),
				ClimateSettings.CODEC.fieldOf("climate").forGetter(Preset::climate),
				TerrainSettings.CODEC.fieldOf("terrain").forGetter(Preset::terrain),
				RiverSettings.CODEC.fieldOf("rivers").forGetter(Preset::rivers),
				FilterSettings.CODEC.fieldOf("filters").forGetter(Preset::filters),
				MiscellaneousSettings.CODEC.fieldOf("miscellaneous").forGetter(Preset::miscellaneous)
			).apply(instance, Preset::new));
		}
	};
	
	public static final Codec<PresetVersion> CODEC = StringRepresentable.fromEnum(PresetVersion::values);

	private String name;
	
	private PresetVersion(String name) {
		this.name = name;
	}
	
	@Override
	public String getSerializedName() {
		return this.name;
	}
	
	public abstract Codec<Preset> codec();
}
