package raccoonman.reterraforged.data.worldgen.preset;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.biome.Climate.ParameterPoint;
import net.minecraft.world.level.biome.OverworldBiomeBuilder;

public enum SpawnType implements StringRepresentable {
    CONTINENT_CENTER("CONTINENT_CENTER") {
    	
		@Override
		public List<ParameterPoint> getParameterPoints() {
			//FIXME this will spawn us on coasts as well
			return new OverworldBiomeBuilder().spawnTarget();
		}
	}, 
    WORLD_ORIGIN("WORLD_ORIGIN") {
		
		@Override
		public List<ParameterPoint> getParameterPoints() {
			return ImmutableList.of();
		}
	};
	
	public static final Codec<SpawnType> CODEC = StringRepresentable.fromEnum(SpawnType::values);

	private String name;
	
	private SpawnType(String name) {
		this.name = name;
	}
	
	@Override
	public String getSerializedName() {
		return this.name;
	}
	
	public abstract List<ParameterPoint> getParameterPoints();
}