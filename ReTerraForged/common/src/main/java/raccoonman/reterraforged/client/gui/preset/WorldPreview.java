package raccoonman.reterraforged.client.gui.preset;

import java.awt.Color;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.kynosarges.tektosyne.geometry.RectD;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import raccoonman.reterraforged.RTFCommon;

public class WorldPreview extends AbstractWidget implements AutoCloseable {
	public static final int RADIUS = 1;
	public static final int LENGTH = RADIUS * 2 + 1;
	public static final int RESOLUTION = 128;
	public static final int IMAGE_SIZE = LENGTH * RESOLUTION;
	
	private static final int KILOMETER = 1000;
	private static final Vector3f LIGHT_ANGLE = new Vector3f(0.0F, 1.0F, 0.48F);
	private static final float DIRECTIONAL_LIGHT = 0.3F;
	private static final float ELEVATION_LIGHT = 0.1F;
	private static final float TRAVERSAL_SPEED = 0.625F; //0.5F;
	private static final Color RIVER_COLOR = new Color(40, 140, 200);

	private ResourceLocation textureId;
	private DynamicTexture texture;
	
	protected float scale;
	private float centerX;
	private float centerY;
	private int maxHeight;
	
	public WorldPreview(int x, int y, int width, int height, @Nullable WorldPreview oldPreview) {
		super(x, y, width, height, Component.empty());
		
		if(oldPreview != null) {
			this.textureId = oldPreview.textureId;
			this.texture = oldPreview.texture;
			this.scale = oldPreview.scale;
			this.centerX = oldPreview.centerX;
			this.centerY = oldPreview.centerY;
			this.maxHeight = oldPreview.maxHeight;
		} else {
			this.textureId = RTFCommon.location("preview/" + System.nanoTime());
			this.texture = new DynamicTexture(this.textureId.toString(), WorldPreview.IMAGE_SIZE, WorldPreview.IMAGE_SIZE, true);
			Minecraft minecraft = Minecraft.getInstance();
			minecraft.getTextureManager().register(this.textureId, this.texture);
			
			this.scale = 1.0F;
		}
	}

	public DynamicTexture getTexture() {
		return this.texture;
	}
	
	public void setScale(float scale) {
		this.scale = scale;
	}
	
	public void upload() {
		this.texture.upload();
	}

//	public void fill(Terrain terrain, int maxHeight, int layerX, int layerY) {
//		this.maxHeight = maxHeight;
//
//		DataArray.Int cellArray = terrain.provide(Terrain.CELL);
//		DataArray.Float continentalnessArray = terrain.provide(Terrain.CONTINENTALNESS);
//		DataArray.Float heightArray = terrain.provide(Terrain.HEIGHT);
//		
//		NativeImage texture = this.texture.getPixels();
//		TerrainRenderer.renderTerrain(terrain, (faceA, _) -> {
//			int color = this.computeCellColor(terrain, cellArray, continentalnessArray, heightArray, faceA.cellIndex());
//			int cellIndex = faceA.cellIndex();
//			int cellX = terrain.cellX(cellIndex);
//			int cellY = terrain.cellY(cellIndex);
//			texture.setPixel(cellX + RESOLUTION * (layerX + 1), texture.getHeight() - 1 - cellY - RESOLUTION * (layerY + 1), color);
//		});
//	}
	
//	@Deprecated
//	private int computeCellColor(CellGrid terrain, DataArray.Int cellArray, DataArray.Float continentalnessArray, DataArray.Float heightArray, int cellIndex) {
//		int cell = cellArray.get(cellIndex);
//		float continentalness = continentalnessArray.get(cellIndex);
//		return continentalness > ContinentPoints.OCEAN && TerrainCell.isChannel(cell) ? RIVER_COLOR.getRGB() : 
//			TerrainRenderer.computeElevationColor(terrain, heightArray, cellIndex,
//				TerrainRenderer.computeCellColor(terrain, continentalnessArray, heightArray, cellIndex), 
//				this.maxHeight, 
//				ELEVATION_LIGHT
//			);
//	}
	
	@Override
	protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
//		if(this.currentTerrain == null) {
//			return;
//		}
		
//    	if(this.isHovered()) {
//    		this.renderHoverTooltip(graphics, mouseX, mouseY);
//    	}

		int x1 = this.getX();
		int y1 = this.getY();
		int x2 = x1 + this.width;
		int y2 = y1 + this.height;
		float u0 = 0.0F;
		float v0 = 0.0F;
		float u1 = 1.0F;
		float v1 = 1.0F;
		
	    graphics.blit(this.textureId, x1, y1, x2, y2, u0, u1, v0, v1);
//    	float scale = this.computeScale();
//    	for(int x = -1; x <= 1; x++) {
//    		for(int y = -1; y <= 1; y++) {
//    			this.renderTerrain(graphics, scale, x, y);
//        	}
//    	}
//    	this.renderScale(graphics);
	}
	
	@Override
	public void onClick(MouseButtonEvent mouseButtonEvent, boolean bl) {
		super.onClick(mouseButtonEvent, bl);
	
//		if(!mouseButtonEvent.hasControlDown() || this.currentTerrain == null) {
//			return;
//		}
		
//		int widgetX = this.getX();
//    	int widgetY = this.getY();
//    	int mouseX = Mth.floor(mouseButtonEvent.x());
//    	int mouseY = Mth.floor(mouseButtonEvent.y());
//    	float previewX = this.pixelToPreviewCoord(mouseX, widgetX);
//    	float previewY = 1.0F - this.pixelToPreviewCoord(mouseY, widgetY);
//    	int blockX = this.previewToBlockCoord(previewX, this.centerX);
//    	int blockZ = this.previewToBlockCoord(previewY, this.centerY);
//
//		LayerProvider layerProvider = this.currentTerrain.getProvider();
//		int cellX = layerProvider.blockToCell(blockX);
//    	int cellY = layerProvider.blockToCell(blockZ);
//    	if(!layerProvider.contains(cellX, cellY)) {
//    		return;
//    	}
//    	
//    	int cellIndex = layerProvider.cellIndex(cellX, cellY);
//    	int blockY = Mth.floor(this.currentTerrain.getHeight(cellIndex)) + 1;
//    	
//		StringBuilder clipboard = new StringBuilder();
//		clipboard.append(blockX);
//		clipboard.append(" ");
//		clipboard.append(blockY);
//		clipboard.append(" ");
//		clipboard.append(blockZ);
//		
//		Minecraft mc = Minecraft.getInstance();
//		mc.keyboardHandler.setClipboard(clipboard.toString());
	}
	
	@Override
	public boolean mouseScrolled(double d, double e, double f, double g) {
//		this.scale = Math.clamp(this.scale + (int) g, 1, this.layers.size() * 2);
		return super.mouseScrolled(d, e, f, g);
	}
	
	// TODO prevent tile scheduling when traversing too quickly
	@Override
	protected void onDrag(MouseButtonEvent mouseButtonEvent, double dx, double dy) {
		double deltaX = (dx * (TRAVERSAL_SPEED / this.scale)) / this.width;
		double deltaY = (dy * (TRAVERSAL_SPEED / this.scale)) / this.height;
		this.centerX += deltaX;
		this.centerY += deltaY;
	}

	@Override	
	public void playDownSound(SoundManager soundManager) {
	}
	
	@Override
	protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
		//TODO 
	}

	@Override
	public void close() {
		if(this.texture != null) {
			Minecraft minecraft = Minecraft.getInstance();
			minecraft.getTextureManager().release(this.textureId);
			this.texture = null;
		}
	}
	
	private void renderTerrain(GuiGraphics graphics, float scale, int layerX, int layerY) {
		int centerX = (int) (this.centerX * this.width);
		int centerY = (int) (this.centerY * this.height);
		RectD bounds = new RectD(layerX * this.width + centerX, layerY * this.height + centerY, layerX * this.width + this.width + centerX, layerY * this.height + this.height + centerY);
		if(!bounds.intersectsWith(new RectD(0, 0, this.width, this.height))) {
			return;
		}

    	int widgetX = this.getX();
    	int widgetY = this.getY();
		int tileX = layerX + 1;
		int tileY = layerY + 1;
    	int tileWidgetX = widgetX + Math.floorMod(centerX + tileX * this.width, this.width * LENGTH) - this.width;
		int tileWidgetY = widgetY + Math.floorMod(centerY + tileY * this.height, this.height * LENGTH) - this.height;
		int marginWidth = (int) (scale * this.width);
		int marginHeight = (int) (scale * this.height);
    	
    	int textureX = tileWidgetX - marginWidth;
    	int textureY = tileWidgetY - marginHeight;
    	int textureWidth = this.width + marginWidth * 2;
    	int textureHeight = this.height + marginHeight * 2;

    	int x1 = Math.max(widgetX, textureX);
    	int y1 = Math.max(widgetY, textureY);
    	int x2 = Math.min(textureX + textureWidth, widgetX + this.width);
    	int y2 = Math.min(textureY + textureHeight, widgetY + this.height);
    	
    	if(x2 - x1 <= 0 || y2 - y1 <= 0) {
    		return;
    	}

    	float u0 = (tileX - Math.min(this.centerX + layerX, 0.0F)) / 3.0F;
    	float v0 = (tileY - Math.min(this.centerY + layerY, 0.0F)) / 3.0F;
    	float u1 = (tileX + 1.0F - Math.max(this.centerX + layerX, 0.0F)) / 3.0F;
    	float v1 = (tileY + 1.0F - Math.max(this.centerY + layerY, 0.0F)) / 3.0F;
	    graphics.blit(this.textureId, x1, y1, x2, y2, u0, u1, v0, v1);
	 }
	
//	private void renderHoverTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
//		Minecraft mc = Minecraft.getInstance();
//
//		int widgetX = this.getX();
//    	int widgetY = this.getY();
//    	float margin = this.computeScale();
//    	int blockX = this.pixelToBlock(((float) mouseX - widgetX) / this.width - margin, this.centerX);
//    	int blockZ = this.pixelToBlock(1.0F - ((float) mouseY - widgetY) / this.height, this.centerY);
//    	int cellX = this.currentTerrain.blockToCell(blockX);
//    	int cellY = this.currentTerrain.blockToCell(blockZ);
//
//    	int blockY;
//    	if(this.currentTerrain.contains(cellX, cellY)) {
//    		DataArray.Float heightArray = this.currentTerrain.provide(Terrain.HEIGHT);
//    	
//    		int cellIndex = this.currentTerrain.cellIndex(cellX, cellY);
//    		blockY = Mth.floor(heightArray.get(cellIndex) / this.currentTerrain.distanceScale());
//    	} else {
//    		blockY = Integer.MIN_VALUE;
//    	}
//    	
//    	ImmutableList.Builder<Component> tooltipBuilder = ImmutableList.builder();
//    	tooltipBuilder.add(Component.literal("x: " + blockX + ", y: " + (blockY == Integer.MIN_VALUE ? "?" : blockY) + ", z: " + blockZ));
//    	tooltipBuilder.add(Component.empty());
//    	if(blockY > this.maxHeight) {
//        	tooltipBuilder.add(Component.translatable(RTFTranslationKeys.WORLD_PREVIEW_HEIGHT_LIMIT_WARNING).withStyle(ChatFormatting.RED));
//        	tooltipBuilder.add(Component.empty());
//    	}
//    	tooltipBuilder.add(Component.empty());
//    	tooltipBuilder.add(InputTooltip.make(RTFTranslationKeys.GUI_ACTION_DRAG, RTFTranslationKeys.GUI_INPUT_LEFT_CLICK));
//    	tooltipBuilder.add(InputTooltip.make(RTFTranslationKeys.GUI_ACTION_COPY, RTFTranslationKeys.GUI_INPUT_LEFT_CLICK, RTFTranslationKeys.GUI_INPUT_LEFT_CONTROL));
//    	graphics.setTooltipForNextFrame(mc.font, tooltipBuilder.build(), Optional.empty(), mouseX, mouseY);
//	}
//	
//	private void renderScale(GuiGraphics graphics) {
//		Minecraft mc = Minecraft.getInstance();
//
//		int widgetX = this.getX();
//    	int widgetY = this.getY();
//    	
//    	float scaleSize = 0.25F;
//    	int scaleWidth = (int) (this.width * scaleSize);
//    	int horizontalPadding = 8;
//    	int verticalPadding = 10;
//    	
//    	int paddedX = widgetX + horizontalPadding;
//    	int paddedY = widgetY - verticalPadding + this.height;
//    	
//		TooltipRenderUtil.renderTooltipBackground(graphics, paddedX, paddedY, scaleWidth, 4, null);
//		
//    	String text = this.getScaleText(scaleSize);
//    	graphics.drawCenteredString(mc.font, text, paddedX + scaleWidth / 2, widgetY + this.height - 11, -2039584);
//	}
	
//	private int pixelToBlock(float previewCoord, float center) {
//		int sizeBlocks = this.currentTerrain.sizeBlocks();
//		return Mth.floor(previewCoord * sizeBlocks + center * sizeBlocks);
//	}
//	
//	private String getScaleText(float scaleSize) {
//		int sizeBlocks = this.currentTerrain.sizeBlocks();
//    	float sizeInMeters = sizeBlocks * scaleSize;
//		return sizeInMeters > KILOMETER ? String.format("%.1f", sizeInMeters / KILOMETER) + " km" : Mth.ceil(sizeInMeters) + " m";
//	}

	private float computeScale() {
		return (this.scale - 1.0F) * 0.15F;
	}

//	private Optional<BlockPos> findSpawnPoint() {
//		HolderGetter<Modifier> modifiers = this.lookupProvider.lookupOrThrow(LithostitchedRegistryKeys.WORLDGEN_MODIFIER);
//		Climate.Sampler climateSampler = this.randomState.sampler();
//		return modifiers.get(RTFWorldGenModifierData.SET_SPAWN_FINDER).flatMap((holder) -> {
//			if(holder.value() instanceof SetSpawnFinderModifier spawnModifier) {
//				PointFinder spawnFinder = spawnModifier.spawnFinder();
//				return spawnFinder.findPoint(climateSampler.spawnTarget(), climateSampler);
//			}
//			return Optional.empty();
//		});
//	}
}