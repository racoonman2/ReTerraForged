package raccoonman.reterraforged.client.gui.preset;

import java.util.Optional;
import java.util.function.Consumer;

import com.mojang.serialization.Lifecycle;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationContext;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.server.RegistryLayer;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.PrimaryLevelData;
import raccoonman.reterraforged.client.gui.Gui;
import raccoonman.reterraforged.client.gui.widget.ColumnAlignment;
import raccoonman.reterraforged.client.gui.widget.Label;
import raccoonman.reterraforged.client.gui.widget.WidgetList;
import raccoonman.reterraforged.data.RTFTranslationKeys;

abstract class PresetGui<A extends AbstractWidget, B extends AbstractWidget> implements Gui {
	protected CreateWorldScreen screen;
	private float leftColumnSize;
	private float rightColumnSize;
	protected Button lastButton;
	protected Button nextButton;
	protected Button newWorldButton;
	protected Button applyButton;
	protected WidgetList<A> left;
	protected WidgetList<B> right;
	protected Label title;

	public PresetGui(CreateWorldScreen screen) {
		this(screen, 0.7F, 0.3F);
	}
	
	public PresetGui(CreateWorldScreen screen, float leftColumnSize, float rightColumnSize) {
		this.screen = screen;
		this.leftColumnSize = leftColumnSize;
		this.rightColumnSize = rightColumnSize;
	}
	
	public abstract Component getTitle();
	
	public void onLast() {
	}

	public void onNext() {
	}

	public void onApply() {
	}
	
	@Override
	public void init(ScreenRectangle area) {
		int buttonsCenter = this.screen.width / 2;
        int buttonWidth = 50;
        int bottomButtonWidth = 65;
        int buttonHeight = 20;
        int buttonPad = 2;
        int buttonsRow = this.screen.height - 25;

		this.lastButton = Button.builder(Component.literal("<<"), (v) -> {
			this.onLast();
		}).bounds(buttonsCenter - (bottomButtonWidth + buttonWidth + (buttonPad * 3)), buttonsRow, buttonWidth, buttonHeight).build();
		this.nextButton = Button.builder(Component.literal(">>"), (v) -> {
			this.onNext();
		}).bounds(buttonsCenter + bottomButtonWidth + (buttonPad * 3), buttonsRow, buttonWidth, buttonHeight).build();
		this.applyButton = Button.builder(Component.translatable(RTFTranslationKeys.GUI_APPLY), (v) -> {
			this.onApply();
		}).bounds(buttonsCenter - bottomButtonWidth - buttonPad, buttonsRow, bottomButtonWidth, buttonHeight).tooltip(
			createTooltip(RTFTranslationKeys.GUI_APPLY_TOOLTIP)
		).build();
		this.newWorldButton = Button.builder(Component.translatable(RTFTranslationKeys.GUI_NEW_WORLD), (v) -> {
			this.openWorld();
		}).bounds(buttonsCenter + buttonPad, buttonsRow, bottomButtonWidth, buttonHeight).tooltip(
			createTooltip(RTFTranslationKeys.GUI_NEW_WORLD_TOOLTIP)
		).build();
		
		this.addColumns(area);
	}
	
	@Override
	public void visitChildren(Consumer<AbstractWidget> visitor) {
		visitor.accept(this.title);
		visitor.accept(this.lastButton);
		visitor.accept(this.nextButton);
		visitor.accept(this.applyButton);
		visitor.accept(this.newWorldButton);
		visitor.accept(this.left);
		visitor.accept(this.right);
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		//NOOP
	}
	
	protected void addColumns(ScreenRectangle area) {
		ColumnAlignment alignment = new ColumnAlignment(area.width(), area.height(), 4, 10);
		this.left = alignment.addColumn(this.leftColumnSize, this::createColumn);
		this.right = alignment.addColumn(this.rightColumnSize, this::createColumn);
		this.title = new Label(16, 23, 300, 20, this.getTitle(), false);
	}
	
	private <C extends AbstractWidget> WidgetList<C> createColumn(int left, int width, int height) {
		return new WidgetList<>(this.screen.minecraft, left, 40, width, height - 30, 25);
	}
	
	private void openWorld() {
		CreateWorldScreen.queueLoadScreen(this.screen.minecraft, CreateWorldScreen.PREPARING_WORLD_DATA);
		this.onApply(); 
		WorldCreationContext settings = this.screen.getUiState().getSettings();
		WorldDimensions.Complete dimensions = settings.selectedDimensions().bake(settings.datapackDimensions());
		LayeredRegistryAccess<RegistryLayer> registryAccess = settings.worldgenRegistries().replaceFrom(RegistryLayer.DIMENSIONS, dimensions.dimensionsRegistryAccess());
		Lifecycle experimentalLifecycle = FeatureFlags.isExperimental(settings.dataConfiguration().enabledFeatures()) ? Lifecycle.experimental() : Lifecycle.stable();
		Lifecycle compositeLifecycle = registryAccess.compositeAccess().allRegistriesLifecycle();
		Lifecycle registryLifecycle = compositeLifecycle.add(experimentalLifecycle);
		this.createNewWorld(dimensions.specialWorldProperty(), registryAccess, registryLifecycle, settings);
    }

    private void createNewWorld(PrimaryLevelData.SpecialWorldProperty propertyData, LayeredRegistryAccess<RegistryLayer> registryAccess, Lifecycle lifecycle, WorldCreationContext settings) {
		String s = this.screen.getUiState().getTargetFolder();
    	Optional<LevelStorageSource.LevelStorageAccess> storageAccess = CreateWorldScreen.createNewWorldDirectory(this.screen.minecraft, s, this.screen.tempDataPackDir);
        if (storageAccess.isEmpty()) {
            return;
        }
        this.screen.removeTempDataPackDir();
        boolean isDebug = propertyData == PrimaryLevelData.SpecialWorldProperty.DEBUG;
        LevelSettings levelSettings = this.screen.createLevelSettings(isDebug);
        PrimaryLevelData worldData = new PrimaryLevelData(levelSettings, settings.options(), propertyData, lifecycle);
        this.screen.minecraft.createWorldOpenFlows().createLevelFromExistingSettings(storageAccess.get(), settings.dataPackResources(), registryAccess, worldData);
    }
	
	private static Tooltip createTooltip(String key) {
		return Tooltip.create(Component.translatable(key));
	}
}