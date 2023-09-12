package raccoonman.reterraforged.common.data.provider;

import net.minecraft.data.PackOutput;
import raccoonman.reterraforged.client.data.RTFTranslationKeys;
import raccoonman.reterraforged.common.ReTerraForged;

// TODO add some more languages
public final class RTFLangProvider {
	
	public static final class EnglishUS extends LanguageProvider {

		public EnglishUS(PackOutput output) {
			super(output, ReTerraForged.MOD_ID, "en_us");
		}

		@Override
		protected void addTranslations() {
			this.add(RTFTranslationKeys.METADATA_DESCRIPTION, "ReTerraForged resources");
			this.add(RTFTranslationKeys.PRESET_METADATA_DESCRIPTION, "ReTerraForged preset");
			this.add(RTFTranslationKeys.NO_ERROR_MESSAGE, "{No error message}");
			this.add(RTFTranslationKeys.GUI_SELECT_PRESET_MISSING_LEGACY_PRESETS, "Couldn't find any legacy presets");
			this.add(RTFTranslationKeys.GUI_SELECT_PRESET_TITLE, "Presets & Defaults");
		}
	}
}
