package raccoonman.reterraforged.common.data.preset.settings;

import com.mojang.serialization.Codec;

import net.minecraft.util.StringRepresentable;

public enum SpawnType implements StringRepresentable {
    CONTINENT_CENTER, 
    WORLD_ORIGIN;
	
	public static final Codec<SpawnType> CODEC = StringRepresentable.fromEnum(SpawnType::values);

	@Override
	public String getSerializedName() {
		return this.name();
	}
}