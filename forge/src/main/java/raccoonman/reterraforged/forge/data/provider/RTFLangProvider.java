package raccoonman.reterraforged.forge.data.provider;

import net.minecraft.data.PackOutput;
import raccoonman.reterraforged.common.client.data.RTFTranslationKeys;

// TODO add more languages
public final class RTFLangProvider {
	
	public static final class EnglishUS extends LanguageProvider {

		public EnglishUS(PackOutput output, String modid) {
			super(output, modid, "en_us");
		}

		@Override
		protected void addTranslations() {
			this.add(RTFTranslationKeys.CUSTOMIZE_WORLD_CREATE, "Create Preset");
			this.add(RTFTranslationKeys.CUSTOMIZE_WORLD_SAVE, "Save Preset");
			this.add(RTFTranslationKeys.CUSTOMIZE_WORLD_RESET, "Reset Preset");
			this.add(RTFTranslationKeys.CUSTOMIZE_WORLD_DELETE, "Delete Preset");
			this.add(RTFTranslationKeys.CUSTOMIZE_WORLD_PAGE_PRESETS, "Presets & Defaults");
			this.add(RTFTranslationKeys.CUSTOMIZE_WORLD_PAGE_WORLD, "World Settings");
			this.add(RTFTranslationKeys.CUSTOMIZE_WORLD_PAGE_CLIMATE, "Climate Settings");
			this.add(RTFTranslationKeys.CUSTOMIZE_WORLD_PAGE_TERRAIN, "Terrain Settings");
			this.add(RTFTranslationKeys.CUSTOMIZE_WORLD_PAGE_RIVER, "River Settings");
			this.add(RTFTranslationKeys.CUSTOMIZE_WORLD_PAGE_FILTERS, "Filter Settings");
			this.add(RTFTranslationKeys.CUSTOMIZE_WORLD_PAGE_STRUCTURES, "Structure Settings");
			this.add(RTFTranslationKeys.CUSTOMIZE_WORLD_PAGE_MISCELLANEOUS, "Miscellaneous Settings");
		}
	}
}
