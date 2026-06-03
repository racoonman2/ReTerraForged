package raccoonman.reterraforged.data;

import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonObject;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.GameRules;
import raccoonman.reterraforged.RTFCommon;
import raccoonman.reterraforged.preset.option.Category;
import raccoonman.reterraforged.preset.option.Option;
import raccoonman.reterraforged.preset.option.Page;

public abstract class LanguageProvider implements DataProvider {
    private final Map<String, String> data = new TreeMap<>();
    private final PackOutput output;
    private final String locale;

    public LanguageProvider(PackOutput output, String locale) {
        this.output = output;
        this.locale = locale;
    }

    protected abstract void addTranslations();
    
	protected void add(Page page, String text) {
		this.add(RTFTranslationKeys.prependModId(page.name()), text);
	}

	protected void add(Category category, String text) {
		this.add(translationKey(category.displayName()).orElse(category.name()), text);
	}
	
	protected void add(Category category, Option<?> option, String text, String tooltip) {
		this.add(translationKey(category.displayName(), option.name()), text, tooltip);
	}
	
	protected void add(String translationKey, String text, String tooltip) {
		this.add(translationKey, text);
		this.add(RTFTranslationKeys.tooltipKey(translationKey), tooltip);
	}
	
	protected void add(ResourceKey<?> key, String text) {
		this.add(key.location().toString(), text);
	}
	
	protected void add(GameRules.Key<?> rule, String text, String description) {
		String descriptionId = rule.getDescriptionId();
		this.add(descriptionId, text);
		this.add(descriptionId + ".description", description);
	}

    private CompletableFuture<?> save(CachedOutput cache, Path target) {
        JsonObject json = new JsonObject();
        this.data.forEach(json::addProperty);
        return DataProvider.saveStable(cache, json, target);
    }
    
    public void add(String key, String value) {
        if (this.data.put(key, value) != null) {
            throw new IllegalStateException("Duplicate translation key " + key);
        }
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
      	this.addTranslations();

        if (!this.data.isEmpty()) {
            return this.save(cache, this.output.getOutputFolder(PackOutput.Target.RESOURCE_PACK).resolve(RTFCommon.MOD_ID).resolve("lang").resolve(this.locale + ".json"));
        }
            
        return CompletableFuture.allOf();
    }

    @Override
    public String getName() {
        return "Languages: " + this.locale;
    }
    
    public static String translationKey(Component component, String key) {
    	return translationKey(component).map((text) -> text + "." + key).orElse(key);
    }
    
    public static Optional<String> translationKey(Component component) {
    	if(component.getContents() instanceof TranslatableContents translatable) {
			return Optional.of(translatable.getKey());
		}
    	return Optional.empty();
    }
    
    public static class EnglishUS extends LanguageProvider {

		public EnglishUS(PackOutput output) {
			super(output, "en_us");
		}

		//TODO improve option descriptions
		@Override
		protected void addTranslations() {
			// Metadata
			this.add(RTFTranslationKeys.RESOURCE_PACK_METADATA_DESCRIPTION, "ReTerraForged resources");
			this.add(RTFTranslationKeys.DATA_PACK_METADATA_DESCRIPTION, "ReTerraForged preset");

			this.add(RTFTranslationKeys.MISSING_ERROR_MESSAGE, "{No error message}");

			// Commands
			this.add(RTFTranslationKeys.MIN_GREATER_THAN_MAX, "Min (%s) must be less than max (%s)");
			
			this.add(RTFTranslationKeys.POINT_FOUND, "Found point at %s (%s blocks away)");
			this.add(RTFTranslationKeys.POINT_NOT_FOUND, "Could not find that point within a reasonable distance");
			this.add(RTFTranslationKeys.SEARCHING, "Searching for point");
			this.add(RTFTranslationKeys.CANCEL_SEARCH, "Cancel");
			this.add(RTFTranslationKeys.CANCEL_SEARCH_DESCRIPTION, "Cancels the search");
			this.add(RTFTranslationKeys.ALREADY_SEARCHING, "Already searching");
			this.add(RTFTranslationKeys.SEARCH_CANCELLED, "Cancelled search");
			this.add(RTFTranslationKeys.NO_SEARCH, "Not currently searching");
			
			// Gui
			this.add(RTFTranslationKeys.WORLD_PREVIEW_SEED, "Seed");
			this.add(RTFTranslationKeys.WORLD_PREVIEW_ZOOM, "Zoom");
			this.add(RTFTranslationKeys.WORLD_PREVIEW_RESOLUTION, "Resolution");
			this.add(RTFTranslationKeys.WORLD_PREVIEW_HEIGHT_LIMIT_WARNING, "Y level exceeds height limit");
			
			this.add(RTFTranslationKeys.GUI_ACTION_DRAG, "to drag");
			this.add(RTFTranslationKeys.GUI_ACTION_COPY, "to copy");
			this.add(RTFTranslationKeys.GUI_ACTION_RESET, "to reset");
			this.add(RTFTranslationKeys.GUI_ACTION_RANDOMIZE, "to randomize");
			this.add(RTFTranslationKeys.GUI_INPUT_LEFT_CLICK, "Left click");
			this.add(RTFTranslationKeys.GUI_INPUT_LEFT_CONTROL, "Left ctrl");
			
			this.add(RTFTranslationKeys.GUI_TAB_TITLE, "ReTerraForged");
			this.add(RTFTranslationKeys.GUI_TRUE, "true");
			this.add(RTFTranslationKeys.GUI_FALSE, "false");
			this.add(RTFTranslationKeys.GUI_APPLY, "Apply");
			this.add(RTFTranslationKeys.GUI_APPLY_TOOLTIP, "Loads the currently selected preset's datapack");
			this.add(RTFTranslationKeys.GUI_NEW_WORLD, "New World");
			this.add(RTFTranslationKeys.GUI_NEW_WORLD_TOOLTIP, "Creates a world with the currently selected preset");
			
			this.add(RTFTranslationKeys.GUI_EXPORT_PRESET_SUCCESS, "Successfully exported preset");
			
			// Select preset
			this.add(RTFTranslationKeys.SELECT_PRESET_TITLE, "Select Preset");
			
			this.add(RTFTranslationKeys.CREATE_PRESET_PROMPT, "Type here to create a preset");
			this.add(RTFTranslationKeys.CREATE_PRESET, "Create");
			this.add(RTFTranslationKeys.COPY_PRESET, "Copy");
			this.add(RTFTranslationKeys.RENAME_PRESET, "Rename");
			this.add(RTFTranslationKeys.DELETE_PRESET, "Delete");
			this.add(RTFTranslationKeys.EXPORT_PRESET, "Export as datapack");
			this.add(RTFTranslationKeys.OPEN_PRESET_FOLDER, "Open preset folder");
			this.add(RTFTranslationKeys.OPEN_EXPORT_FOLDER, "Open export folder");
			
			this.add(RTFTranslationKeys.EXPERIMENTAL, "Experimental");
			this.add(RTFTranslationKeys.MEDIUM_PERFORMANCE_IMPACT, "Medium performance impact");
			this.add(RTFTranslationKeys.HEAVY_PERFORMANCE_IMPACT, "Heavy performance impact");
			
			this.add(RTFTranslationKeys.EXPORT_SUCCESS, "Exported to %s in %s seconds");
			this.add(RTFTranslationKeys.EXPORT_FAIL, "Error exporting, %s");
			this.add(RTFTranslationKeys.EXPORT_DESCRIPTION, "Click to open");
		}
	}
}
