package raccoonman.reterraforged.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.client.renderer.debug.DebugRenderer.SimpleDebugRenderer;
import net.minecraft.util.debug.DebugValueAccess;
import raccoonman.reterraforged.client.renderer.LayerRenderer;

@Mixin(DebugRenderer.class)
public class MixinDebugRenderer {
	private SimpleDebugRenderer layerRenderer;
//	private SimpleDebugRenderer chunkRenderer;
	
	@Inject(
		method = "<init>", 
		at = @At("TAIL")
	)
	public void DebugRenderer(CallbackInfo callback) {
    	this.layerRenderer = new LayerRenderer();
//    	this.chunkRenderer = new ChunkRenderer();
    }
	
	@Inject(
		method = "render", 
		at = @At("TAIL"),
		locals = LocalCapture.CAPTURE_FAILHARD
	)
    public void render(PoseStack poseStack, Frustum frustum, MultiBufferSource.BufferSource bufferSource, double x, double y, double z, boolean bl, CallbackInfo callback, Minecraft minecraft, DebugValueAccess debugAccess) {
    	this.layerRenderer.render(poseStack, bufferSource, x, y, z, debugAccess, frustum);
//    	this.chunkRenderer.render(poseStack, bufferSource, x, y, z, debugAccess, frustum);
    }	
}
