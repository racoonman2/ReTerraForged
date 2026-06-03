package raccoonman.reterraforged.client.gui.preset;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationContext;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.RandomState;
import raccoonman.reterraforged.client.gui.InputTooltip;
import raccoonman.reterraforged.client.gui.widget.EditBox2;
import raccoonman.reterraforged.client.gui.widget.Label;
import raccoonman.reterraforged.client.gui.widget.Slider;
import raccoonman.reterraforged.client.gui.widget.Slider.FloatSlider;
import raccoonman.reterraforged.data.RTFDataGen;
import raccoonman.reterraforged.data.RTFTranslationKeys;
import raccoonman.reterraforged.preset.Preset;
import raccoonman.reterraforged.preset.option.Page;

public class PresetEditorGui extends PresetGui<AbstractWidget, AbstractWidget> implements PackChangeListener {
	private int pageIndex;
	private PresetListGui parentGui;
	private PresetListGui.Entry presetEntry;

	private FloatSlider zoomSlider;

	private WorldPreview worldPreview;
 	private RandomState randomState;
	private HolderLookup.Provider lookupProvider;
	
	private CompletableFuture<?> currentTask;
	private boolean updateRegistries;
	private long lastTickMillis;
	private int updateDelay;
	private boolean upload;
	
	public PresetEditorGui(PresetListGui parentGui, PresetListGui.Entry presetEntry) {
		super(parentGui.screen);
		
		this.parentGui = parentGui;
		this.presetEntry = presetEntry;
		
		this.currentTask = CompletableFuture.completedFuture(null);
		this.lastTickMillis = Integer.MIN_VALUE;
		this.updateDelay = Integer.MIN_VALUE;
	}

	@Override
	public void init(ScreenRectangle area) {
		super.init(area);
		
		this.right.add((x, y, width, height) -> {
			WorldCreationUiState uiState = this.screen.getUiState();
			String seed = String.valueOf(uiState.getSettings().options().seed());
			
			EditBox2 seedBox = new EditBox2(this.screen.font, x, y, width, height, Component.translatable(RTFTranslationKeys.WORLD_PREVIEW_SEED), this::onRandomizeSeed);
			seedBox.setText(seed, true);
			seedBox.setResponder(this::onUpdateSeed);
			
			Component tooltip = InputTooltip.make(RTFTranslationKeys.GUI_ACTION_RANDOMIZE, RTFTranslationKeys.GUI_INPUT_LEFT_CONTROL, RTFTranslationKeys.GUI_INPUT_LEFT_CLICK);
			seedBox.setTooltip(Tooltip.create(tooltip));
			return seedBox;
		});
		this.zoomSlider = this.right.add((x, y, width, height) -> {
			return FloatSlider.make(x, y, width, height, Component.translatable(RTFTranslationKeys.WORLD_PREVIEW_ZOOM), this.zoomSlider != null ? this.zoomSlider.getValue() : 1.0F, 1.0F, 4.0F, (v, value) -> {
				this.worldPreview.setScale(value);
				return false;
			}, Slider.Formatter.literal());
		});
		this.worldPreview = this.right.add((x, y, width, v) -> {
			return new PresetEditorGui.Preview(x, y, width, width, this.worldPreview);
		});
		
		if(this.randomState == null) {
			this.schedulePreviewUpdate(true, 0, true);
		}

		this.selectPage(0);
	}
	
	@Override
	public void tick() {
		long timeMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
		long lastTickMillis = this.lastTickMillis;
		this.lastTickMillis = timeMillis;

		if(this.upload) {
			this.worldPreview.upload();
			this.upload = false;
		}
		
		if(lastTickMillis != Integer.MIN_VALUE && (this.updateDelay == Integer.MIN_VALUE || (this.updateDelay -= (timeMillis - lastTickMillis)) > 0)) {
			return;
		}
		
		if(this.updateRegistries) {
			this.lookupProvider = RTFDataGen.buildPatch(
				this.presetEntry.getPreset(), 
				this.parentGui.dimensionRegistries(),
				this.parentGui.structurePlacements()
			).full();
		}

		WorldCreationContext settings = this.screen.getUiState().getSettings();
		//TODO don't hardcode overworld settings here
		this.randomState = RandomState.create(
			this.lookupProvider.lookupOrThrow(Registries.NOISE_SETTINGS).getOrThrow(NoiseGeneratorSettings.OVERWORLD).value(),
			this.lookupProvider.lookupOrThrow(Registries.NOISE),
			settings.options().seed()
		);
		
		this.currentTask = this.currentTask.thenCompose((v) -> this.schedulePreviewFuture());
		
		this.updateRegistries = false;
		this.updateDelay = Integer.MIN_VALUE;
	}
	
	@Override
	public void close() {
		try {
			this.presetEntry.save();
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.worldPreview.close();
		this.parentGui.close();
	}

	@Override
	public Component getTitle() {
		Page currentPage = this.getCurrentPage();
		return currentPage != null ? currentPage.displayName() : Component.empty();
	}

	@Override
	public void onApply() {
		try {
			this.presetEntry.save();
			this.presetEntry.apply();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onLast() {
		if(this.pageIndex != 0 && !this.screen.minecraft.hasControlDown()) {
			this.selectPage(-1);
			return;
		}

		try {
			this.presetEntry.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.parentGui.setGuiCallback().accept(this.parentGui);
	}

	@Override
	public void onNext() {
		this.selectPage(1);
	}
	
	@Override
	public void onPackUpdated() {
		this.parentGui.onPackUpdated();
	}
	
	private void onRandomizeSeed(EditBox2 box) {
		if(PresetEditorGui.this.screen.minecraft.hasControlDown()) {
			ThreadLocalRandom random = ThreadLocalRandom.current();
			box.setText(String.valueOf(random.nextLong()), true);
		}
	}
	
	private void onUpdateSeed(String seed) {
		WorldCreationUiState uiState = this.screen.getUiState();
		if(seed.isBlank() || seed.equals(uiState.getSeed())) {
			return;
		}
		
		for(Tab tab : this.screen.tabNavigationBar.getTabs()) {
			if(!(tab instanceof CreateWorldScreen.WorldTab worldTab)) {
				continue;
			}
			worldTab.seedEdit.setValue(seed);
		}
		this.schedulePreviewUpdate(false, 400, true);
	}
	
	@Nullable
	private Page getCurrentPage() {
		List<Page> pages = this.parentGui.pages();
		return pages.isEmpty() ? null : pages.get(this.pageIndex);
	}

	private void selectPage(int offset) {
		this.pageIndex += offset;
		this.nextButton.active = this.pageIndex + 1 < this.parentGui.pages().size();
		
		this.title.setMessage(this.presetEntry.getMessage().copy().append("  |  ").append(this.getTitle()));
		this.left.clearEntries();
		this.left.setScrollAmount(0.0D);

		Page currentPage = this.getCurrentPage();
		if(currentPage == null) {
			return;
		}
		
		Preset preset = this.presetEntry.getPreset();

		List<OptionWidgets.ChangeListener> optionListeners = new ArrayList<>();
		currentPage.categories().forEach((category) -> {
			this.left.add((x, y, width, height) -> {
				return new Label(x, y, width, height, category.displayName(), false);
			});
			category.options().forEach((option) -> {
				this.left.add((x, y, width, height) -> {
					return OptionWidgets.makeWidget(preset, x, y, width, height, option, optionListeners);
				});
			});
		});
		
		optionListeners.forEach(OptionWidgets.ChangeListener::onChange);
		optionListeners.add(() -> {
			this.schedulePreviewUpdate(true, 300, true);
		});
	}

	private void schedulePreviewUpdate(boolean updateRegistries, int delay, boolean upload) {
		this.updateRegistries |= updateRegistries;
		this.updateDelay = Math.max(delay, this.updateDelay);
		this.upload |= upload;
	}
	
	private CompletableFuture<?> schedulePreviewFuture() {
//		long start = System.nanoTime();
//		
//		RTFRandomState rtfRandomState = ExtensionUtil.cast(this.randomState);
//		ResourceKey<Layer.Factory<Reference<Terrain>>> layerKey = LayerKeys.TERRAIN_SIMULATION.apply(LevelStem.OVERWORLD.location());
//		
//		Holder<Layer.Factory<Reference<Terrain>>> layerFactory = RegistryUtil.lookupTyped(this.lookupProvider, layerKey);
//		Layer<Reference<Terrain>> layer = rtfRandomState.getMappedLayer(layerFactory);
//
//		NoiseGeneratorSettings generatorSettings = RegistryUtil.lookupTyped(this.lookupProvider, NoiseGeneratorSettings.OVERWORLD).value();
//		NoiseSettings noiseSettings = generatorSettings.noiseSettings();
//		int maxHeight = noiseSettings.minY() + noiseSettings.height();
		
//		List<CompletableFuture<?>> futures = new ArrayList<>();
//		for(int layerX = -WorldPreview.RADIUS; layerX <= WorldPreview.RADIUS; layerX++) {
//			for(int layerY = -WorldPreview.RADIUS; layerY <= WorldPreview.RADIUS; layerY++) {
//				final int fX = layerX;
//				final int fY = layerY;
//				CompletableFuture<Void> future = layer.provide(layerX, layerY, Util.backgroundExecutor()).future().thenAcceptAsync((terrain) -> {
//					this.worldPreview.fill(terrain, maxHeight, fX, fY);
//					this.schedulePreviewUpdate(false, Integer.MIN_VALUE, true);
//				}, Util.backgroundExecutor());
//				futures.add(future);
//			}
//		}
//		return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).thenRun(() -> System.out.println("finished: " + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start)));
		
		DynamicTexture texture = this.worldPreview.getTexture();
//		MacroTerrainLayer layer = new MacroTerrainLayer(3, 128, 4096, 1, 7, 15);
//		NativeImage image = texture.getPixels();
//		return layer.provide(0, 0, Util.backgroundExecutor()).future().thenAccept((macroTerrain) -> {
//			for(int terrainCellIndex = 0; terrainCellIndex < macroTerrain.cellCount(); terrainCellIndex++) {
//				int terrainCellX = macroTerrain.cellX(terrainCellIndex);
//				int terrainCellY = macroTerrain.cellY(terrainCellIndex);
//				MacroTerrain.Cell terrainCell = macroTerrain.getCell(terrainCellIndex);
//				int basePixelX = terrainCellX * terrainCell.size();
//				int basePixelY = terrainCellY * terrainCell.size();
//				float[] heightArray = terrainCell.queue(Simulation.HEIGHT);
//				int[] cellArray = terrainCell.queue(Simulation.CELL);
//				for(int cellIndex = 0; cellIndex < terrainCell.cellCount(); cellIndex++) {
//					float height = Math.clamp(heightArray[cellIndex] / 9000.0F, 0.0F, 1.0F);
//					int cellX = basePixelX + terrainCell.cellX(cellIndex);
//					int cellY = basePixelY + terrainCell.cellY(cellIndex);
//					image.setPixel(cellX, cellY, TerrainCell.isChannel(cellArray[cellIndex]) ? Color.CYAN.getRGB() : new Color(height, height, height).getRGB());
//				}
//				
//				for(int drain : terrainCell.getDrains()) {
//					int cellX = basePixelX + terrainCell.cellX(drain);
//					int cellY = basePixelY + terrainCell.cellY(drain);
//					image.setPixel(cellX, cellY, Color.BLUE.getRGB());
//				}
//			}
//		}).thenRunAsync(texture::upload, this.screen.minecraft).handle((v, e) -> {if(e != null) e.printStackTrace(); return v;});
		return CompletableFuture.completedFuture(null);
	}
	
	private class Preview extends WorldPreview {

		public Preview(int x, int y, int width, int height, @Nullable WorldPreview oldPreview) {
			super(x, y, width, height, oldPreview);
		}

		@Override
		public boolean mouseScrolled(double d, double e, double f, double g) {
			boolean result = super.mouseScrolled(d, e, f, g);
			PresetEditorGui.this.zoomSlider.setValue(this.scale);
			return result;
		}
	}
}
