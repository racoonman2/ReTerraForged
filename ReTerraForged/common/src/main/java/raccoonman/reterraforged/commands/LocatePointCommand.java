package raccoonman.reterraforged.commands;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.datafixers.util.Either;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceOrTagArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.QuartPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.phys.Vec3;
import raccoonman.reterraforged.commands.arguments.ParameterArgument;
import raccoonman.reterraforged.data.RTFTranslationKeys;
import raccoonman.reterraforged.extensions.compat.TBClimateSampler;
import raccoonman.reterraforged.registry.RTFRegistries;
import raccoonman.reterraforged.world.ParameterSlice;
import raccoonman.reterraforged.world.PointFinder;
import raccoonman.reterraforged.world.worldgen.ContinentPoints;
import raccoonman.reterraforged.world.worldgen.ErosionPoints;
import raccoonman.reterraforged.world.worldgen.HumidityPoints;
import raccoonman.reterraforged.world.worldgen.TemperaturePoints;
import raccoonman.reterraforged.world.worldgen.WeirdnessPoints;

public class LocatePointCommand {
	private static final Map<String, Climate.Parameter> BUILTIN_CONTINENTALNESS_PARAMETERS = ImmutableMap.of(
		"MUSHROOM_FIELDS", Climate.Parameter.span(ContinentPoints.MUSHROOM_FIELDS, ContinentPoints.DEEP_OCEAN),
		"DEEP_OCEAN", Climate.Parameter.span(ContinentPoints.DEEP_OCEAN, ContinentPoints.OCEAN),
		"OCEAN", Climate.Parameter.span(ContinentPoints.OCEAN, ContinentPoints.COAST),
		"COAST", Climate.Parameter.span(ContinentPoints.COAST, ContinentPoints.NEAR_INLAND),
		"NEAR_INLAND", Climate.Parameter.span(ContinentPoints.NEAR_INLAND, ContinentPoints.MID_INLAND),
		"MID_INLAND", Climate.Parameter.span(ContinentPoints.MID_INLAND, ContinentPoints.FAR_INLAND),
		"FAR_INLAND", Climate.Parameter.span(ContinentPoints.FAR_INLAND, ContinentPoints.MAX),
		"INLAND", Climate.Parameter.span(ContinentPoints.COAST, ContinentPoints.MAX)
	);
	
	private static final Map<String, Climate.Parameter> BUILTIN_TEMPERATURE_PARAMETERS = ImmutableMap.<String, Climate.Parameter>builder()
		.put("FROZEN", Climate.Parameter.span(TemperaturePoints.FROZEN, TemperaturePoints.COLD))
		.put("COLD", Climate.Parameter.span(TemperaturePoints.COLD, TemperaturePoints.TEMPERATE))
		.put("TEMPERATE", Climate.Parameter.span(TemperaturePoints.TEMPERATE, TemperaturePoints.WARM))
		.put("WARM", Climate.Parameter.span(TemperaturePoints.WARM, TemperaturePoints.HOT))
		.put("HOT", Climate.Parameter.span(TemperaturePoints.HOT, TemperaturePoints.MAX))
		.build();
	
	private static final Map<String, Climate.Parameter> BUILTIN_HUMIDITY_PARAMETERS = ImmutableMap.<String, Climate.Parameter>builder()
		.put("ARID", Climate.Parameter.span(HumidityPoints.ARID, HumidityPoints.DRY))
		.put("DRY", Climate.Parameter.span(HumidityPoints.DRY, HumidityPoints.MILD))
		.put("MILD", Climate.Parameter.span(HumidityPoints.MILD, HumidityPoints.HUMID))
		.put("HUMID", Climate.Parameter.span(HumidityPoints.HUMID, HumidityPoints.WET))
		.put("WET", Climate.Parameter.span(HumidityPoints.WET, 1.5F))
		.build();
	
	private static final Map<String, Climate.Parameter> BUILTIN_EROSION_PARAMETERS = ImmutableMap.of(
		"LEVEL_0", Climate.Parameter.span(ErosionPoints.LEVEL_0, ErosionPoints.LEVEL_1),
		"LEVEL_1", Climate.Parameter.span(ErosionPoints.LEVEL_1, ErosionPoints.LEVEL_2),
		"LEVEL_2", Climate.Parameter.span(ErosionPoints.LEVEL_2, ErosionPoints.LEVEL_3),
		"LEVEL_3", Climate.Parameter.span(ErosionPoints.LEVEL_3, ErosionPoints.LEVEL_4),
		"LEVEL_4", Climate.Parameter.span(ErosionPoints.LEVEL_4, ErosionPoints.LEVEL_5),
		"LEVEL_5", Climate.Parameter.span(ErosionPoints.LEVEL_5, ErosionPoints.LEVEL_6),
		"LEVEL_6", Climate.Parameter.span(ErosionPoints.LEVEL_6, ErosionPoints.MAX)
	);
	
	private static final Map<String, Climate.Parameter> BUILTIN_WEIRDNESS_PARAMETERS = builtinWeirdnessParameters();
	
    private static final SimpleCommandExceptionType ERROR_ALREADY_SEARCHING = new SimpleCommandExceptionType(Component.translatable(RTFTranslationKeys.ALREADY_SEARCHING));
    private static final SimpleCommandExceptionType ERROR_NO_SEARCH = new SimpleCommandExceptionType(Component.translatable(RTFTranslationKeys.NO_SEARCH));
    private static final AtomicReference<Boolean> IS_SEARCHING = new AtomicReference<>();
    
    private static final int STEP = 128;
    private static final int MAX_DISTANCE = 24000;
    private static final long TIMEOUT = 30L;
    
    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher, CommandBuildContext commandBuildContext, Commands.CommandSelection environment) {
    	// clear any previous tasks
    	IS_SEARCHING.set(null);
    	
    	commandDispatcher.register(
    		Commands.literal("rtf")
    			.requires((stack) -> stack.hasPermission(2))
    			.then(
    				Commands.literal("locate")
    				.then(
    					Commands.literal("cancel")
    					.executes((ctx) -> {
    						return cancelSearch(ctx.getSource());
    	    			})
    	    		)
    				.then(
    					Commands.argument("terrain", ResourceOrTagArgument.resourceOrTag(commandBuildContext, RTFRegistries.PARAMETER_SLICE))
    					.executes((ctx) -> {
    						List<Climate.ParameterPoint> points = getPoints(ctx);
    						return findPoint(points, ctx.getSource(), 0);
    					})
    					.then(
    						Commands.argument("minRadius", IntegerArgumentType.integer(STEP))
    						.executes((ctx) -> {
        						CommandSourceStack stack = ctx.getSource();
        						List<Climate.ParameterPoint> points = getPoints(ctx);
    							int minRadius = ctx.getArgument("minRadius", int.class);
        						return findPoint(points, stack, minRadius);
        					})
    					)
    					.then(
    						Commands.argument("biome", ResourceOrTagArgument.resourceOrTag(commandBuildContext, Registries.BIOME))
	    					.executes((ctx) -> {
	    						CommandSourceStack stack = ctx.getSource();
	    						List<Climate.ParameterPoint> points = getPoints(ctx);
        						@SuppressWarnings("unchecked")
	    						ResourceOrTagArgument.Result<Biome> biomePredicate = ctx.getArgument("biome", ResourceOrTagArgument.Result.class);
	    						return findPointWithBiome(stack, points, biomePredicate, 0);
	    					})
	    					.then(
	    						Commands.argument("minRadius", IntegerArgumentType.integer(STEP))
	    						.executes((ctx) -> {
	    							CommandSourceStack stack = ctx.getSource();
		    						List<Climate.ParameterPoint> points = getPoints(ctx);
	    							@SuppressWarnings("unchecked")
	    							ResourceOrTagArgument.Result<Biome> biomePredicate = ctx.getArgument("biome", ResourceOrTagArgument.Result.class);
	    							int minRadius = ctx.getArgument("minRadius", int.class);
	    							return findPointWithBiome(stack, points, biomePredicate, minRadius);
	    	    				})
	    	    			)
    					)
    				)
    				.then(
    					Commands.argument("continentalness", ParameterArgument.parameter(BUILTIN_CONTINENTALNESS_PARAMETERS))
    					.then(
    		    			Commands.argument("erosion", ParameterArgument.parameter(BUILTIN_EROSION_PARAMETERS))
    		    			.then(
    		    				Commands.argument("weirdness", ParameterArgument.parameter(BUILTIN_WEIRDNESS_PARAMETERS))
    		    				.then(
    		    					Commands.argument("temperature", ParameterArgument.parameter(BUILTIN_TEMPERATURE_PARAMETERS))
    		    					.then(
    		    						Commands.argument("humidity", ParameterArgument.parameter(BUILTIN_HUMIDITY_PARAMETERS))
    		    						.executes((ctx) -> {
    		    							CommandSourceStack stack = ctx.getSource();
    		    							Climate.Parameter continentalness = ctx.getArgument("continentalness", Climate.Parameter.class);
    		    							Climate.Parameter temperature = ctx.getArgument("temperature", Climate.Parameter.class);
    		    							Climate.Parameter humidity = ctx.getArgument("humidity", Climate.Parameter.class);
    		    							Climate.Parameter erosion = ctx.getArgument("erosion", Climate.Parameter.class);
    		    							Climate.Parameter weirdness = ctx.getArgument("weirdness", Climate.Parameter.class);
    		    							Climate.Parameter depth = Climate.Parameter.point(0.0F);
    		    							Climate.ParameterPoint point = Climate.parameters(temperature, humidity, continentalness, erosion, depth, weirdness, 0.0F);
    		    							return findPoint(ImmutableList.of(point), stack, 0);
    		    						})
    		    						.then(
    		    	    					Commands.argument("minRadius", IntegerArgumentType.integer(STEP))
    		    	    					.executes((ctx) -> {
        		    							CommandSourceStack stack = ctx.getSource();
        		    							Climate.Parameter continentalness = ctx.getArgument("continentalness", Climate.Parameter.class);
        		    							Climate.Parameter temperature = ctx.getArgument("temperature", Climate.Parameter.class);
        		    							Climate.Parameter humidity = ctx.getArgument("humidity", Climate.Parameter.class);
        		    							Climate.Parameter erosion = ctx.getArgument("erosion", Climate.Parameter.class);
        		    							Climate.Parameter weirdness = ctx.getArgument("weirdness", Climate.Parameter.class);
        		    							Climate.Parameter depth = Climate.Parameter.point(0.0F);
        		    							Climate.ParameterPoint point = Climate.parameters(temperature, humidity, continentalness, erosion, depth, weirdness, 0.0F);
        		    							int minRadius = ctx.getArgument("minRadius", int.class);
        		    							return findPoint(ImmutableList.of(point), stack, minRadius);
        		    						})
    		    	    				)
    		    					)
    		    				)
    		    			)
    	    			)
    				)
    			)
    	);
    }
    
    private static int findPointWithBiome(CommandSourceStack stack, List<Climate.ParameterPoint> points, ResourceOrTagArgument.Result<Biome> biomePredicate, int minRadius) throws CommandSyntaxException {
    	ServerChunkCache chunkSource = stack.getLevel().getChunkSource();
		BiomeSource biomeSource = chunkSource.getGenerator().getBiomeSource();
		Climate.Sampler sampler = chunkSource.randomState().sampler();
		
		// make sure depth is always 0 so we don't end up sampling cave biomes
		Climate.Sampler surfaceSampler = new Climate.Sampler(
			sampler.temperature(),
			sampler.humidity(), 
			sampler.continentalness(), 
			sampler.erosion(), 
			DensityFunctions.zero(), 
			sampler.weirdness(), 
			sampler.spawnTarget()
		);
		TBClimateSampler.copy(sampler, surfaceSampler);
		
		int result = findPoint((pos) -> {
    		int quartPosX = QuartPos.fromBlock(pos.getX());
    		int quartPosZ = QuartPos.fromBlock(pos.getZ());
    	    Holder<Biome> biome = biomeSource.getNoiseBiome(quartPosX, 0, quartPosZ, surfaceSampler);
    	    return PointFinder.fitness(sampler, points, pos) == 0L && biomePredicate.test(biome);
    	}, stack, points, minRadius);
		return result;
    }
    
    private static int findPoint(List<Climate.ParameterPoint> points, CommandSourceStack stack, int minRadius) throws CommandSyntaxException {
		Climate.Sampler sampler = stack.getLevel().getChunkSource().randomState().sampler();
    	return findPoint((pos) -> PointFinder.fitness(sampler, points, pos) == 0, stack, points, minRadius);
    }
    
    private static int findPoint(Predicate<BlockPos> test, CommandSourceStack stack, List<Climate.ParameterPoint> points, int minRadius) throws CommandSyntaxException {
    	Boolean status = IS_SEARCHING.get();
    	if(status == null) {
        	CompletableFuture.runAsync(() -> {
        		IS_SEARCHING.set(true);

        		stack.sendSystemMessage(Component.translatable(RTFTranslationKeys.SEARCHING)
        			.append(" ")
        			.append(ComponentUtils.wrapInSquareBrackets(Component.translatable(RTFTranslationKeys.CANCEL_SEARCH))
        				.withStyle(style -> style.withColor(ChatFormatting.GOLD)
        					.withClickEvent(new ClickEvent.RunCommand("/rtf locate cancel"))
        					.withHoverEvent(new HoverEvent.ShowText(Component.translatable(RTFTranslationKeys.CANCEL_SEARCH_DESCRIPTION)))
        				)
        			)
        		);
        		
            	Vec3 origin = stack.getPosition();
            	BlockPos result = PointFinder.of(STEP, minRadius, MAX_DISTANCE, TIMEOUT)
            		.findPoint(origin, test, IS_SEARCHING::get)
            		.orElse(null);
            	
            	if(result != null) {
                	BlockPos originPos = BlockPos.containing(origin);
                	int originX = originPos.getX();
                	int originY = originPos.getY();
                	int originZ = originPos.getZ();
                	int resultX = result.getX();
                	int resultZ = result.getZ();
                	int distance = Mth.floor(dist(originX, originZ, resultX, resultZ));
                	stack.sendSuccess(() -> Component.translatable(RTFTranslationKeys.POINT_FOUND, createTeleportMessage(resultX, originY, resultZ), distance), false);
            	} else if (IS_SEARCHING.get()) {
            		stack.sendFailure(Component.translatable(RTFTranslationKeys.POINT_NOT_FOUND));
            	}
            	IS_SEARCHING.set(null);
        	});
        	return Command.SINGLE_SUCCESS;
    	} else {
    		throw ERROR_ALREADY_SEARCHING.create();
    	}
    }
    
    private static int cancelSearch(CommandSourceStack stack) throws CommandSyntaxException {
    	Boolean status = IS_SEARCHING.get();
		if(status == null) {
    		throw ERROR_NO_SEARCH.create();
		} else if(status) {
			IS_SEARCHING.set(false);
			stack.sendSuccess(() -> Component.translatable(RTFTranslationKeys.SEARCH_CANCELLED), false);
		}
		return Command.SINGLE_SUCCESS;
    }
    
    private static Component createTeleportMessage(int x, int y, int z) {
        return ComponentUtils.wrapInSquareBrackets(Component.translatable("chat.coordinates", x, y, z)).withStyle(s -> s
        	.withColor(ChatFormatting.GREEN)
        	.withClickEvent(new ClickEvent.SuggestCommand("/tp @s " + x + " " + y + " " + z))
        	.withHoverEvent(new HoverEvent.ShowText(Component.translatable("chat.coordinates.tooltip")))
        );
    }
    
	//FIXME this can overflow with large distances
    private static double dist(int x0, int z0, int x1, int z1) {
    	int distX = x1 - x0;
    	int distZ = z1 - z0;
    	return Mth.sqrt(distX * distX + distZ * distZ);
	}
    
	@SuppressWarnings("unchecked")
    private static List<Climate.ParameterPoint> getPoints(CommandContext<?> ctx) throws CommandSyntaxException {
		ResourceOrTagArgument.Result<ParameterSlice> result = ctx.getArgument("terrain", ResourceOrTagArgument.Result.class);
    	Either<? extends Holder<ParameterSlice>, ? extends HolderSet<ParameterSlice>> unwrapped = result.unwrap();
    	return unwrapped.map((holder) -> holder.value().points(), (holderSet) -> holderSet.stream().flatMap((holder) -> {
    		return holder.value().points().stream();
    	}).toList());
    }
	
	private static Map<String, Climate.Parameter> builtinWeirdnessParameters() {
		Map<String, Climate.Parameter> parameters = ImmutableMap.of(
			"VALLEY", Climate.Parameter.span(WeirdnessPoints.VALLEY, WeirdnessPoints.LOW_SLICE),
			"LOW", Climate.Parameter.span(WeirdnessPoints.LOW_SLICE, WeirdnessPoints.MID_SLICE),
			"MID", Climate.Parameter.span(WeirdnessPoints.MID_SLICE, WeirdnessPoints.HIGH_SLICE),
			"HIGH", Climate.Parameter.span(WeirdnessPoints.HIGH_SLICE, WeirdnessPoints.PEAK),
			"PEAK", Climate.Parameter.span(WeirdnessPoints.PEAK, WeirdnessPoints.HIGH_SLICE_VARIANT),
			"HIGH_SLICE_VARIANT", Climate.Parameter.span(WeirdnessPoints.HIGH_SLICE_VARIANT, WeirdnessPoints.MID_SLICE_VARIANT),
			"MID_SLICE_VARIANT", Climate.Parameter.span(WeirdnessPoints.MID_SLICE_VARIANT, WeirdnessPoints.MAX)
		);
		
		ImmutableMap.Builder<String, Climate.Parameter> builder = ImmutableMap.builder();
		parameters.forEach((name, point) -> {
			builder.put("-" + name, new Climate.Parameter(-point.max(), -point.min()));
		});
		builder.putAll(parameters);
		return builder.build();
	}
}