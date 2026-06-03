package raccoonman.reterraforged.mixin; 

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseRouter;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.SurfaceSystem;
import raccoonman.reterraforged.extensions.ExtensionUtil;
import raccoonman.reterraforged.extensions.RTFRandomState;
import raccoonman.reterraforged.world.Ticking;
import raccoonman.reterraforged.world.worldgen.densityfunction.GlobalFunctionVisitor;
import raccoonman.reterraforged.world.worldgen.layer.Layer;
import raccoonman.reterraforged.world.worldgen.layer.RequiredLayer;

@Mixin(RandomState.class)
class MixinRandomState implements RTFRandomState {
	@Shadow
	@Final
    public PositionalRandomFactory random;
    @Shadow
    @Final
	private NoiseRouter router;
	@Shadow
	@Final
	private Climate.Sampler sampler;
	@Shadow
	@Final
    private SurfaceSystem surfaceSystem;

	private Map<Layer.Factory<?>, Layer<?>> layers;
	private Set<Ticking> managedLayers;
	private Set<RequiredLayer> requiredLayers;
	private GlobalFunctionVisitor globalFunctionVisitor;
	
	@Override
	public void initialize(long seed) {
		RandomState self = ExtensionUtil.cast(this);
		this.layers = new HashMap<>();
		this.managedLayers = new ObjectArraySet<>();
		this.requiredLayers = new ObjectArraySet<>();
		this.globalFunctionVisitor = new GlobalFunctionVisitor(self, seed);
	}

	@Override
	public GlobalFunctionVisitor globalFunctionVisitor() {
		return this.globalFunctionVisitor;
	}

	@Override
	public Set<Ticking> getManagedLayers() {
		return this.managedLayers;
	}
	
	@Override
	public Set<RequiredLayer> getRequiredLayers() {
		return this.requiredLayers;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <A> Layer<A> getOrCreateLayer(Layer.Factory<A> factory) {
		Layer<A> layer = (Layer<A>) this.layers.get(factory);
		if(layer != null) {
			return layer;
		}
		layer = factory.createLayer(this);
		this.layers.put(factory, layer);
		
		if(layer instanceof Ticking managedLayer) {
			this.managedLayers.add(managedLayer);
		}
		return layer;
	}

	@Mixin(targets = "net.minecraft.world.level.levelgen.RandomState$1NoiseWiringHelper")
	public static abstract class Mixin1NoiseWiringHelper implements DensityFunction.Visitor, Layer.Visitor {
		@Shadow
		@Final
		private long val$seed;
		@Shadow
		@Final
		private RandomState field_38266;
		
		@Inject(
			method = "<init>",
			at = @At("TAIL")
		)
	    private void init(CallbackInfo callback) {
			RTFRandomState rtfRandomState = ExtensionUtil.cast(this.field_38266);
			rtfRandomState.initialize(this.val$seed);
	    }
		
		@Inject(
			method = "wrapNew",
			at = @At("TAIL"),
			cancellable = true
		)
		private void wrapNew(DensityFunction function, CallbackInfoReturnable<DensityFunction> callback) {
			RTFRandomState rtfRandomState = ExtensionUtil.cast(this.field_38266);
			callback.setReturnValue(rtfRandomState.globalFunctionVisitor().apply(function));
		}

		@Override
		public <A> Layer.Factory<A> apply(Layer.Factory<A> factory) {
			RTFRandomState rtfRandomState = ExtensionUtil.cast(this.field_38266);
			return rtfRandomState.globalFunctionVisitor().apply(factory);
		}
	}
}
