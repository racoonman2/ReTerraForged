package raccoonman.reterraforged.forge.data.provider;

import net.minecraft.data.PackOutput;

public final class RTFLangProvider {
	public static final String GENERATOR = "generator.reterraforged.reterraforged";
	
	public static final class EnglishUS extends LanguageProvider {

		public EnglishUS(PackOutput output, String modid) {
			super(output, modid, "en_us");
		}

		@Override
		protected void addTranslations() {
			this.add(GENERATOR, "ReTerraForged");
		}	
	}
}
