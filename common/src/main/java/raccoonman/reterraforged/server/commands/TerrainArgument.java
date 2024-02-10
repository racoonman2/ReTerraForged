package raccoonman.reterraforged.server.commands;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import raccoonman.reterraforged.client.data.RTFTranslationKeys;
import raccoonman.reterraforged.world.worldgen.terrain.Terrain;
import raccoonman.reterraforged.world.worldgen.terrain.TerrainType;

public class TerrainArgument implements ArgumentType<Terrain> {
    public static final DynamicCommandExceptionType ERROR_INVALID_VALUE = new DynamicCommandExceptionType(input -> Component.translatable(RTFTranslationKeys.TERRAIN_ARGUMENT_INVALID, input));

    private TerrainArgument() {    	
    }
    
    public static TerrainArgument terrain() {
    	return new TerrainArgument();
    }
    
	@Override
	public Terrain parse(StringReader reader) throws CommandSyntaxException {
        String terrainName = reader.readUnquotedString();
        Terrain terrain = TerrainType.get(terrainName);
        if (terrain == null) {
            throw ERROR_INVALID_VALUE.create(terrainName);
        }
        return terrain;
	}

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
        return SharedSuggestionProvider.suggest(getTerrainTypeNames(), suggestionsBuilder);
    }

    @Override
    public Collection<String> getExamples() {
        return getTerrainTypeNames().toList();
    }
    
    private static final List<Terrain> BLACKLIST = ImmutableList.of(TerrainType.NONE, TerrainType.VOLCANO_PIPE);
    
    private static Stream<String> getTerrainTypeNames() {
    	return TerrainType.REGISTRY.stream().filter((type) -> !BLACKLIST.contains(type)).map(Terrain::getName);
    }
    
    public static class Info implements ArgumentTypeInfo<TerrainArgument, Info.Template> {

		@Override
		public void serializeToNetwork(Template template, FriendlyByteBuf byteBuf) {
		}

		@Override
		public Template deserializeFromNetwork(FriendlyByteBuf byteBuf) {
			return new Template();
		}

		@Override
		public void serializeToJson(Template template, JsonObject json) {
		}

		@Override
		public Template unpack(TerrainArgument argument) {
			return new Template();
		}

        public class Template implements ArgumentTypeInfo.Template<TerrainArgument> {

        	@Override
            public TerrainArgument instantiate(CommandBuildContext commandBuildContext) {
                return TerrainArgument.terrain();
            }

            @Override
            public ArgumentTypeInfo<TerrainArgument, ?> type() {
                return Info.this;
            }
        }
    }
}
