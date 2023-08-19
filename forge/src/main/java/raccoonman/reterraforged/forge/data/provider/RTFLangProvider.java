package raccoonman.reterraforged.forge.data.provider;

import net.minecraft.data.PackOutput;

// TODO more translations?
public final class RTFLangProvider {
	
	public static final class EnglishUS extends LanguageProvider {

		public EnglishUS(PackOutput output, String modid) {
			super(output, modid, "en_us");
		}

		@Override
		protected void addTranslations() {
			this.add("generator.reterraforged.reterraforged", "ReTerraForged");
			this.add("createWorld.customize.reterraforged.world_preview.layer", "Layer");
			this.add("createWorld.customize.reterraforged.world_preview.zoom", "Zoom");
			this.add("createWorld.customize.reterraforged.title", "Noise Viewer");
		}
	}
}
