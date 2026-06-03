package raccoonman.reterraforged.registry;

import java.util.List;
import java.util.function.BiConsumer;

import com.google.common.collect.ImmutableList;

import net.minecraft.core.Cloner;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.RegistrySetBuilder.PatchedRegistries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.RegistryDataLoader.RegistryData;
import raccoonman.reterraforged.platform.RegistryUtil;

public record RegistryPatcher(RegistrySetBuilder builder, RegistryAccess builtinRegistries, HolderLookup.Provider full, List<HolderLookup.Provider> patches) {
	private static final Cloner.Factory CLONER_FACTORY = new Cloner.Factory(); 
	
	public Result build() {
		PatchedRegistries patched = this.builder.buildPatch(this.builtinRegistries, this.full, CLONER_FACTORY);
		ImmutableList.Builder<HolderLookup.Provider> patches = ImmutableList.builder();
		patches.addAll(this.patches);
		patches.add(patched.patches());
		return new Result(patched.full(), patches.build());
	}
	
	public RegistryPatcher then(BiConsumer<RegistrySetBuilder, HolderLookup.Provider> consumer) {
		Result result = this.build();
		HolderLookup.Provider full = result.full();
		return new RegistryPatcher(makeRegistryBuilder(consumer, full), this.builtinRegistries, full, result.patches());
	}
	
	public static RegistryPatcher of(HolderLookup.Provider lookupProvider, BiConsumer<RegistrySetBuilder, HolderLookup.Provider> consumer) {
		RegistryAccess registries = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
		return new RegistryPatcher(makeRegistryBuilder(consumer, lookupProvider), registries, lookupProvider, ImmutableList.of());
	}
	
	private static RegistrySetBuilder makeRegistryBuilder(BiConsumer<RegistrySetBuilder, HolderLookup.Provider> consumer, HolderLookup.Provider lookupProvider) {
		RegistrySetBuilder builder = new RegistrySetBuilder();
		consumer.accept(builder, lookupProvider);
		return builder;
	}
	
	static {
		for(RegistryData<?> registry : RegistryUtil.getDynamicRegistriesWithDimensions()) {
			registry.runWithArguments(CLONER_FACTORY::addCodec);
		}
	}
	
	public record Result(HolderLookup.Provider full, List<HolderLookup.Provider> patches) {
	}
}
