package raccoonman.reterraforged.client.renderer;

import java.awt.Color;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.function.ToDoubleFunction;

import org.kynosarges.tektosyne.geometry.PointD;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.debug.DebugRenderer.SimpleDebugRenderer;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.RandomState;
import raccoonman.reterraforged.data.preset.registry.RTFLayers;
import raccoonman.reterraforged.extensions.ExtensionUtil;
import raccoonman.reterraforged.extensions.RTFChunk;
import raccoonman.reterraforged.extensions.RTFRandomState;
import raccoonman.reterraforged.registry.RTFRegistries;
import raccoonman.reterraforged.world.worldgen.PosUtil;
import raccoonman.reterraforged.world.worldgen.layer.CacheLayer;
import raccoonman.reterraforged.world.worldgen.layer.Layer;

public class LayerRenderer implements SimpleDebugRenderer {
	private static final RenderPipeline.Snippet MATRICES_PROJECTION_SNIPPET = RenderPipeline.builder()
		.withUniform("DynamicTransforms", UniformType.UNIFORM_BUFFER)
		.withUniform("Projection", UniformType.UNIFORM_BUFFER)
		.buildSnippet();
	public static final RenderPipeline DEBUG_LINES_PIPELINE = RenderPipelines.register(
		RenderPipeline.builder(MATRICES_PROJECTION_SNIPPET)
		.withLocation("pipeline/debug_lines")
		.withVertexShader("core/position_color")
		.withFragmentShader("core/position_color")
		.withCull(false)
		.withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.DEBUG_LINES)
		.build()
	);
	
	public static final RenderPipeline DEBUG_FILLED_TRIANGLES_PIPELINE = RenderPipelines.register(
		RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
			.withLocation("pipeline/debug_filled_triangles")
			.withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.TRIANGLES)
			.build()
	);
	
	public static final RenderType.CompositeRenderType DEBUG_LINES = RenderType.create(
		"debug_lines",
		1536,
		DEBUG_LINES_PIPELINE,
		RenderType.CompositeState.builder()
			.setLineState(new RenderStateShard.LineStateShard(OptionalDouble.empty()))
			.setLayeringState(RenderType.VIEW_OFFSET_Z_LAYERING)
			.setOutputState(RenderType.ITEM_ENTITY_TARGET)
			.createCompositeState(false)
	);
	
	@VisibleForDebug
	public static final RenderType.CompositeRenderType DEBUG_FILLED_TRIANGLES = RenderType.create(
		"debug_filled_triangle",
		1536,
		DEBUG_FILLED_TRIANGLES_PIPELINE,
		RenderType.CompositeState.builder()
			.setLayeringState(RenderType.VIEW_OFFSET_Z_LAYERING)
			.setOutputState(RenderType.ITEM_ENTITY_TARGET)
			.createCompositeState(false)
	);

	private static final Color TRANSPARENT_GREEN = new Color(64, 255, 64, 127);
	private static final Color TRANSPARENT_RED = new Color(255, 64, 64, 127);
	
	@Override
	public void render(PoseStack poseStack, MultiBufferSource source, double cameraX, double cameraY, double cameraZ, DebugValueAccess debugAccess, Frustum frustum) {
		IntegratedServer integratedServer = Minecraft.getInstance().getSingleplayerServer();
        if(integratedServer == null) {
        	return;
        }

        ResourceKey<Level> dimension = integratedServer.minecraft.level.dimension();
        ServerLevel level = integratedServer.getLevel(dimension);
        RandomState randomState = level.getChunkSource().randomState();
        RTFRandomState rtfRandomState = ExtensionUtil.cast(randomState);

	    PoseStack.Pose pose = poseStack.last();
	    
	    int chunkX = SectionPos.blockToSectionCoord((int) cameraX);
	    int chunkZ = SectionPos.blockToSectionCoord((int) cameraZ);
        ChunkAccess chunk = level.getChunk(chunkX, chunkZ, ChunkStatus.EMPTY, false);
        if(chunk instanceof RTFChunk rtfChunk) {
	    	VertexConsumer faceBuffer = source.getBuffer(DEBUG_FILLED_TRIANGLES);
	    	ChunkPos chunkPos = chunk.getPos();
	    	int minBlockX = chunkPos.getMinBlockX();
	    	int minBlockZ = chunkPos.getMinBlockZ();
            int size = SectionPos.sectionToBlockCoord(1);
            rtfChunk.getMaxHeight().ifPresent((maxHeight) -> {
    	    	renderSquare(pose, faceBuffer, cameraX, cameraY, cameraZ, minBlockX, maxHeight, minBlockZ, size, TRANSPARENT_GREEN);
            });
        }

        RegistryAccess registryAccess = level.registryAccess();
	    Registry<Layer.Factory<?>> layerRegistry = registryAccess.lookupOrThrow(RTFRegistries.LAYER);
	    Optional<Layer.Factory<?>> layerFactory = layerRegistry.getOptional(RTFLayers.TERRAIN.location());
	    if(layerFactory.isEmpty()) {
	    	return;
	    }
		    
	    Layer<?> layer = rtfRandomState.getMappedLayer(layerFactory.get());
	    if(layer instanceof CacheLayer<?> cacheLayer) {
	    	int layerSize = layer.size();
	    	
	    	VertexConsumer lineBuffer = source.getBuffer(DEBUG_LINES);
	    	cacheLayer.cache.forEach((key, v) -> {
	    		int layerX = PosUtil.unpackLeft(key);
	    		int layerY = PosUtil.unpackRight(key);
	    		int minBlockX = layer.layerToBlock(layerX);
	    		int minBlockZ = layer.layerToBlock(layerY);
	    		int maxBlockX = layer.layerToBlock(layerX + 1);
	    		int maxBlockZ = layer.layerToBlock(layerY + 1);
	    		renderBoxOutline(pose, lineBuffer, cameraX, cameraY, cameraZ, minBlockX, minBlockZ, maxBlockX, maxBlockZ, 320.0F, Color.BLACK);
	    	});
	    		
	    	VertexConsumer faceBuffer = source.getBuffer(DEBUG_FILLED_TRIANGLES);
	    	cacheLayer.cache.forEach((key, reference) -> {
	    		// this technically isn't thread-safe so the first check here is necessary to prevent NPE's
	    		if(reference == null || reference.future().isDone()) {
	    			return;
	    		}
	    		
	    		int layerX = PosUtil.unpackLeft(key);
	    		int layerY = PosUtil.unpackRight(key);
	    		int minBlockX = layer.layerToBlock(layerX);
	    		int minBlockZ = layer.layerToBlock(layerY);
	    		renderSquare(pose, faceBuffer, cameraX, cameraY, cameraZ, minBlockX, 320.0D, minBlockZ, layerSize, TRANSPARENT_RED);
	    	});
	    }
	}
	
	private static PointD[] box(int minBlockX, int minBlockZ, int maxBlockX, int maxBlockZ) {
		PointD v00 = new PointD(minBlockX, minBlockZ);
		PointD v10 = new PointD(maxBlockX, minBlockZ);
		PointD v01 = new PointD(minBlockX, maxBlockZ);
		PointD v11 = new PointD(maxBlockX, maxBlockZ);
		return new PointD[] {
			v00, v10, v11, v01
		};
	}
	
	private static void renderSquare(PoseStack.Pose pose, VertexConsumer buffer, double cameraX, double cameraY, double cameraZ, double xPos, double yPos, double zPos, double size, Color color) {
    	renderSquare(pose, buffer, cameraX, cameraY, cameraZ, (v) -> yPos, xPos, zPos, size, color);
	}

	private static void renderSquare(PoseStack.Pose pose, VertexConsumer buffer, double cameraX, double cameraY, double cameraZ, ToDoubleFunction<PointD> height, double x, double y, double size, Color color) {
		renderSquare(pose, buffer, cameraX, cameraY, cameraZ, height, x, y, size, size, color);
	}

	private static void renderSquare(PoseStack.Pose pose, VertexConsumer buffer, double cameraX, double cameraY, double cameraZ, ToDoubleFunction<PointD> height, double x, double y, double sizeX, double sizeY, Color color) {
		PointD point00 = new PointD(x, y);
		PointD point10 = new PointD(x + sizeX, y);
		PointD point01 = new PointD(x, y + sizeY);
		PointD point11 = new PointD(x + sizeX, y + sizeY);
		
		renderFace(pose, buffer, cameraX, cameraY, cameraZ, height, PointD.EMPTY, point00, point01, point11, color);
		renderFace(pose, buffer, cameraX, cameraY, cameraZ, height, PointD.EMPTY, point11, point10, point00, color);
		
		renderFace(pose, buffer, cameraX, cameraY, cameraZ, height, PointD.EMPTY, point00, point10, point11, color);
		renderFace(pose, buffer, cameraX, cameraY, cameraZ, height, PointD.EMPTY, point11, point01, point00, color);
	}

	private static void renderFace(PoseStack.Pose pose, VertexConsumer buffer, double cameraX, double cameraY, double cameraZ, ToDoubleFunction<PointD> height, PointD offset, PointD pointA, PointD pointB, PointD pointC, Color color) {
		double heightA = height.applyAsDouble(pointA);
		double heightB = height.applyAsDouble(pointB); 
		double heightC = height.applyAsDouble(pointC);
		int red = color.getRed();
		int green = color.getGreen();
		int blue = color.getBlue();
		int alpha = color.getAlpha();
		buffer.addVertex((float) (-cameraX + pointA.x + offset.x), (float) (-cameraY + heightA), (float) (-cameraZ + pointA.y + offset.y)).setColor(red, green, blue, alpha);
		buffer.addVertex((float) (-cameraX + pointB.x + offset.x), (float) (-cameraY + heightB), (float) (-cameraZ + pointB.y + offset.y)).setColor(red, green, blue, alpha);
		buffer.addVertex((float) (-cameraX + pointC.x + offset.x), (float) (-cameraY + heightC), (float) (-cameraZ + pointC.y + offset.y)).setColor(red, green, blue, alpha);
	}
	
	private static void renderOutline(PoseStack.Pose pose, VertexConsumer buffer, double cameraX, double cameraY, double cameraZ, PointD[] vertices, float height, Color color) {
    	for(int i = 0; i < vertices.length; i++) {
    		PointD origin = vertices[i];
    		PointD destination;
    		if(i == vertices.length - 1) {
    			destination = vertices[0];
    		} else {	
    			destination = vertices[i + 1];
    		}

    		renderLine(pose, buffer, cameraX, cameraY, cameraZ, origin.x, height, origin.y, destination.x, height, destination.y, color, color);
    	}
	}
	
	private static void renderBoxOutline(PoseStack.Pose pose, VertexConsumer buffer, double cameraX, double cameraY, double cameraZ, int minBlockX, int minBlockZ, int maxBlockX, int maxBlockZ, float height, Color color) {
    	PointD[] vertices = box(minBlockX, minBlockZ, maxBlockX, maxBlockZ);
    	renderOutline(pose, buffer, cameraX, cameraY, cameraZ, vertices, height, color);
	}
	
	private static void renderVerticalLine(PoseStack.Pose pose, VertexConsumer buffer, double cameraX, double cameraY, double cameraZ, double lineX, double lineZ, double lineY, double lineHeight, Color colorA, Color colorB) {
		renderLine(pose, buffer, cameraX, cameraY, cameraZ, lineX, lineY, lineZ, lineX, lineY + lineHeight, lineZ, colorA, colorB);
	}
	
	private static void renderLine(PoseStack.Pose pose, VertexConsumer buffer, double cameraX, double cameraY, double cameraZ, double lineX0, double lineY0, double lineZ0, double lineX1, double lineY1, double lineZ1, Color colorA, Color colorB) {
		buffer.addVertex(pose, (float) (-cameraX + lineX0), (float) (-cameraY + lineY0), (float) (-cameraZ + lineZ0)).setColor(colorA.getRed(), colorA.getGreen(), colorA.getBlue(), 255).setNormal(pose, 0, 0, 0);
		buffer.addVertex(pose, (float) (-cameraX + lineX1), (float) (-cameraY + lineY1), (float) (-cameraZ + lineZ1)).setColor(colorB.getRed(), colorB.getGreen(), colorB.getBlue(), 255).setNormal(pose, 0, 0, 0);
	}
	
	private static double distance(double x1, double y1, double x2, double y2) {
		double dx = x2 - x1;
		double dy = y2 - y1;
		return Math.sqrt(dx * dx + dy * dy);		
	}
}
