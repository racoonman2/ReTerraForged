package raccoonman.reterraforged.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup.RegistryLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import raccoonman.reterraforged.registries.RTFRegistries;
import raccoonman.reterraforged.world.worldgen.structure.rule.StructureRule;

@Mixin(SinglePoolElement.class)
public class MixinSinglePoolElement {

	@Redirect(
		method = "place",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate;placeInWorld(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructurePlaceSettings;Lnet/minecraft/util/RandomSource;I)Z"
		)
	)
    public boolean placeInWorld(StructureTemplate template, ServerLevelAccessor serverLevelAccessor, BlockPos blockPos, BlockPos blockPos2, StructurePlaceSettings structurePlaceSettings, RandomSource randomSource, int i) {
		if(serverLevelAccessor instanceof WorldGenLevel level) {
			RegistryAccess registry = level.registryAccess();
			RegistryLookup<StructureRule> structureRules = registry.lookupOrThrow(RTFRegistries.STRUCTURE_RULE);
			for(StructureRule structureRule : structureRules.listElements().map(Holder::value).toList()) {
				if(!structureRule.test(level, blockPos, template.getBoundingBox(structurePlaceSettings, blockPos))) {
					return false;
				}
			}
		}
		return template.placeInWorld(serverLevelAccessor, blockPos, blockPos2, structurePlaceSettings, randomSource, i);
	}
}
