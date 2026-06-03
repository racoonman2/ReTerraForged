package raccoonman.reterraforged.extensions;

import java.util.Set;

import net.minecraft.core.Holder;
import raccoonman.reterraforged.world.Ticking;
import raccoonman.reterraforged.world.worldgen.densityfunction.GlobalFunctionVisitor;
import raccoonman.reterraforged.world.worldgen.layer.Layer;
import raccoonman.reterraforged.world.worldgen.layer.RequiredLayer;

public interface RTFRandomState extends Layer.Provider {
	void initialize(long seed);
	
	GlobalFunctionVisitor globalFunctionVisitor();
	
	Set<Ticking> getManagedLayers();
	
	Set<RequiredLayer> getRequiredLayers();
	
	default <A> Layer<A> getMappedLayer(Layer.Factory<A> factory) {
		Layer.Factory<A> mappedFactory = factory.mapAll(this.globalFunctionVisitor());
		return this.getOrCreateLayer(mappedFactory);
	}
	
	default <A> Layer<A> getMappedLayer(Holder<Layer.Factory<A>> holder) {
		return this.getMappedLayer(holder.value());
	}
}
