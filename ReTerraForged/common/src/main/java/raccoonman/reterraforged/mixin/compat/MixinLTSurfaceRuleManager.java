package raccoonman.reterraforged.mixin.compat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.google.common.collect.ImmutableList;

import dev.worldgen.lithostitched.Lithostitched;
import dev.worldgen.lithostitched.registry.LithostitchedRegistryKeys;
import dev.worldgen.lithostitched.worldgen.modifier.AddSurfaceRuleModifier;
import dev.worldgen.lithostitched.worldgen.modifier.Modifier;
import dev.worldgen.lithostitched.worldgen.surface.SurfaceRuleManager;
import dev.worldgen.lithostitched.worldgen.surface.rule.TransientMergedRule;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.SurfaceRules;
import raccoonman.reterraforged.mixin.accessors.NoiseBasedChunkGeneratorAccessor;
import raccoonman.reterraforged.world.worldgen.modifier.AppendSurfaceRuleModifier;

@Deprecated // this could probably be moved to AppendSurfaceRuleModifier
@Mixin(SurfaceRuleManager.class)
class MixinLTSurfaceRuleManager {
	
	// lets hope noone injects into this
	@Overwrite(remap = false)
    public static void applySurfaceRules(MinecraftServer server) {
        RegistryAccess registryAccess = server.registryAccess();
        Registry<Modifier> modifiers = registryAccess.lookupOrThrow(LithostitchedRegistryKeys.WORLDGEN_MODIFIER);

        Map<ResourceLocation, List<SurfaceRules.RuleSource>> prependRules = new HashMap<>();
        Map<ResourceLocation, List<SurfaceRules.RuleSource>> appendRules = new HashMap<>();
        modifiers.listElements().forEach((holder) -> {
        	Modifier modifier = holder.value();
        	if(modifier instanceof AddSurfaceRuleModifier prepend) {
                prepend.levels().forEach(levelStemResourceKey -> prependRules.computeIfAbsent(levelStemResourceKey.location(), v -> new ArrayList<>()).add(prepend.surfaceRule()));
        	}
        	
        	if(modifier instanceof AppendSurfaceRuleModifier append) {
                append.levels().forEach(levelStemResourceKey -> appendRules.computeIfAbsent(levelStemResourceKey.location(), v -> new ArrayList<>()).add(append.surfaceRule()));
        	}
        });

        Registry<LevelStem> dimensions = registryAccess.lookupOrThrow(Registries.LEVEL_STEM);
        for (Map.Entry<ResourceKey<LevelStem>, LevelStem> entry : dimensions.entrySet()) {
            ResourceLocation location = entry.getKey().location();
            List<SurfaceRules.RuleSource> prepend = prependRules.getOrDefault(location, ImmutableList.of());
            List<SurfaceRules.RuleSource> append = appendRules.getOrDefault(location, ImmutableList.of());
            if (!prepend.isEmpty() || !append.isEmpty()) {
                ChunkGenerator chunkGenerator = entry.getValue().generator();
                if (!(chunkGenerator instanceof NoiseBasedChunkGenerator)) continue;
                NoiseGeneratorSettings settings = ((NoiseBasedChunkGenerator) chunkGenerator).generatorSettings().value();
                SurfaceRules.RuleSource oldRules = settings.surfaceRule();
                // Noise generator settings must be rebuilt due to Forge not allowing surface rules to be directly modified.
                ((NoiseBasedChunkGeneratorAccessor) chunkGenerator).setSettings(Holder.direct(new NoiseGeneratorSettings(
                    settings.noiseSettings(),
                    settings.defaultBlock(),
                    settings.defaultFluid(),
                    settings.noiseRouter(),
                    buildModdedSurfaceRules(prepend, append, oldRules),
                    settings.spawnTarget(),
                    settings.seaLevel(),
                    settings.disableMobGeneration(),
                    settings.isAquifersEnabled(),
                    settings.oreVeinsEnabled(),
                    settings.useLegacyRandomSource()
                )));

                Lithostitched.LOGGER.info("Applied " + prepend.size() + " surface rule additions for '" + location + "' dimension");
            }
        }
    }

    private static SurfaceRules.RuleSource buildModdedSurfaceRules(List<SurfaceRules.RuleSource> prependList, List<SurfaceRules.RuleSource> appendList, SurfaceRules.RuleSource originalSource) {
        List<SurfaceRules.RuleSource> newRuleSourceList = new ArrayList<>();
        newRuleSourceList.addAll(prependList);
        newRuleSourceList.add(originalSource);
        newRuleSourceList.addAll(appendList);
        if (originalSource instanceof TransientMergedRule merged) {
            merged.sequence().addAll(newRuleSourceList);
            return originalSource;
        } else {
            return new TransientMergedRule(newRuleSourceList, originalSource);
        }
    }
}
