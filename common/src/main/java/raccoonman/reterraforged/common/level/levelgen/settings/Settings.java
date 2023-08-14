/*
 * Decompiled with CFR 0.150.
 */
package raccoonman.reterraforged.common.level.levelgen.settings;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record Settings(WorldSettings world, ClimateSettings climate) {
	public static final Settings DEFAULT = new Settings(WorldSettings.DEFAULT, ClimateSettings.DEFAULT);
	
	public static final Codec<Settings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		WorldSettings.CODEC.fieldOf("world").forGetter(Settings::world),
		ClimateSettings.CODEC.fieldOf("climate").forGetter(Settings::climate)
	).apply(instance, Settings::new));
}

