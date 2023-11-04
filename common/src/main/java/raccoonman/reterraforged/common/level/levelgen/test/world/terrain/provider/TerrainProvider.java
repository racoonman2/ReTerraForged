package raccoonman.reterraforged.common.level.levelgen.test.world.terrain.provider;

import java.util.List;
import java.util.function.Consumer;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.test.cell.Populator;
import raccoonman.reterraforged.common.level.levelgen.test.world.terrain.LandForms;
import raccoonman.reterraforged.common.level.levelgen.test.world.terrain.Terrain;
import raccoonman.reterraforged.common.level.levelgen.test.world.terrain.populator.TerrainPopulator;
import raccoonman.reterraforged.common.worldgen.data.preset.TerrainSettings;

public interface TerrainProvider {
	LandForms getLandforms();

	List<Populator> getPopulators();

	int getVariantCount(Terrain terrain);

	default Populator getPopulator(Terrain terrain) {
		return this.getPopulator(terrain, 0);
	}

	Populator getPopulator(Terrain terrain, int id);

	default void forEach(Consumer<TerrainPopulator> consumer) {
	}

	default Terrain getTerrain(String name) {
		return null;
	}

	void registerMixable(TerrainPopulator tp);

	void registerUnMixable(TerrainPopulator tp);

	default void registerMixable(Terrain type, Noise base, Noise variance, Noise ridge, Noise erosion, TerrainSettings.Terrain settings) {
		this.registerMixable(TerrainPopulator.of(type, base, variance, ridge, erosion, settings));
	}

	default void registerUnMixable(Terrain type, Noise base, Noise variance, Noise ridge, Noise erosion, TerrainSettings.Terrain settings) {
		this.registerUnMixable(TerrainPopulator.of(type, base, variance, ridge, erosion, settings));
	}
}
