package raccoonman.reterraforged.commands;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator.PackGenerator;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import raccoonman.reterraforged.data.RTFDataGen;
import raccoonman.reterraforged.data.RTFTranslationKeys;
import raccoonman.reterraforged.data.RegistryDataProvider;
import raccoonman.reterraforged.data.RegistryTagProvider;
import raccoonman.reterraforged.platform.ConfigUtil;

public class ExportCommand {
	private static final String PACK_NAME = "WorldData";
//	private static final double DEFAULT_SCALER = 1.0D;
    private static final DynamicCommandExceptionType ERROR_COULDNT_EXPORT = new DynamicCommandExceptionType((arg) -> Component.translatable(RTFTranslationKeys.EXPORT_FAIL, arg));
    
    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher, CommandBuildContext commandBuildContext, Commands.CommandSelection environment) {
    	if(environment == Commands.CommandSelection.INTEGRATED) {
	    	commandDispatcher.register(
	    		Commands.literal("rtf")
	    		.then(
	    			Commands.literal("export")
	    			.then(
		    			Commands.literal("worlddata")
		    			.executes((source) -> {
		    				try {
		    					ConfigUtil.makeChildDirectories(ConfigUtil.DEBUG_PATH);
		    					exportWorldData(source.getSource());
		    					return Command.SINGLE_SUCCESS;
		    			    } catch(Exception exception) {
		    			    	throw ERROR_COULDNT_EXPORT.create(exception.getMessage());
		    			    }
		    			})
		    		)
//	    			.then(
//	    				Commands.literal("densityfunction")
//	    				.then(
//	    					Commands.argument("function", ResourceKeyArgument.key(Registries.DENSITY_FUNCTION))
//	    					.then(
//	    						Commands.argument("min", BlockPosArgument.blockPos())
//		    					.then(
//		    						Commands.argument("max", BlockPosArgument.blockPos())
//		    						.executes((context) -> {
//		    							try {
//		    								CommandSourceStack stack = context.getSource();
//		    								@SuppressWarnings("unchecked")
//		    								ResourceKey<DensityFunction> functionKey = context.getArgument("function", ResourceKey.class);
//		    								Coordinates min = context.getArgument("min", Coordinates.class);
//		    								Coordinates max = context.getArgument("max", Coordinates.class);
//		    								exportDensityFunction(stack, min, max, DEFAULT_SCALER, functionKey);
//		    								return Command.SINGLE_SUCCESS;
//		    							} catch(Exception exception) {
//		    								throw ERROR_COULDNT_EXPORT.create(exception.getMessage());
//		    							}
//		    						})
//		    						.then(
//		    							Commands.argument("scaler", DoubleArgumentType.doubleArg())
//		    							.executes((context) -> {
//		    								try {
//		    									CommandSourceStack stack = context.getSource();
//		    									@SuppressWarnings("unchecked")
//		    									ResourceKey<DensityFunction> functionKey = context.getArgument("function", ResourceKey.class);
//			    								Coordinates min = context.getArgument("min", Coordinates.class);
//			    								Coordinates max = context.getArgument("max", Coordinates.class);
//		    									double scaler = context.getArgument("scaler", double.class);
//			    								exportDensityFunction(stack, min, max, scaler, functionKey);
//		    									return Command.SINGLE_SUCCESS;
//		    								} catch(Exception exception) {
//		    									throw ERROR_COULDNT_EXPORT.create(exception.getMessage());
//		    								}
//		    							})
//		    						)
//		    					)	
//	    					)
//			    		)
//	    			)
	    		)
	        );
    	}
    }
    
    private static void exportWorldData(CommandSourceStack stack) throws IOException {
    	if(stack.getServer() instanceof IntegratedServer server) {
    		Path path = ConfigUtil.DEBUG_PATH.resolve(PACK_NAME + ".zip");

			long start = System.nanoTime();
    		CompletableFuture<HolderLookup.Provider> lookupProvider = CompletableFuture.completedFuture(stack.registryAccess());
    	    RTFDataGen.exportPack(path, (factory) -> {
    	    	PackGenerator packGenerator = factory.apply(PACK_NAME);
    			packGenerator.addProvider((output) -> new RegistryDataProvider(output, lookupProvider.thenApply(ImmutableList::of)));
    			packGenerator.addProvider((output) -> new RegistryTagProvider(output, lookupProvider));
    	    });

    	    long end = System.nanoTime();
			server.minecraft.execute(() -> {
				sendSuccess(server, path, TimeUnit.NANOSECONDS.toSeconds(end - start));
			});
	    }
	}
   
//	  TODO rework/fix this
//    private static void exportDensityFunction(CommandSourceStack stack, Coordinates min, Coordinates max, double scaler, ResourceKey<DensityFunction> functionKey) throws CommandSyntaxException, IOException {
//		BlockPos minPos = min.getBlockPos(stack);
//		BlockPos maxPos = max.getBlockPos(stack);
//		int minX = minPos.getX();
//		int minZ = minPos.getZ();
//		int maxX = maxPos.getX();
//		int maxZ = maxPos.getZ();
//
//		ServerLevel level = stack.getLevel();
//		ServerChunkCache chunkSource = level.getChunkSource();
//		RandomState randomState = chunkSource.randomState();
//		RTFRandomState rtfRandomState = ExtensionUtil.cast(randomState);
//
//    	Holder<DensityFunction> holder = level.registryAccess().lookupOrThrow(Registries.DENSITY_FUNCTION).getOrThrow(functionKey);
//		DensityFunction globalFunction = holder.value().mapAll(rtfRandomState.globalFunctionVisitor());
//		FunctionProvider provider = new FunctionProvider();
//		provider.provide(globalFunction, FunctionProvider.localVisitor(minX, minZ), minX, minZ, maxX, maxZ, RTFCommon.EXECUTOR).thenAccept((function) -> {
//			if(stack.getServer() instanceof IntegratedServer server) {
//				int width = maxX - minX + 1;
//				int height = maxZ - minZ + 1;
//		    	
//				int[] data = new int[width * height];
//				
//				long start = System.nanoTime();
//				MutableFunctionContext ctx = new MutableFunctionContext();
//				for(int blockX = 0; blockX < width; blockX++) {
//					for(int blockZ = 0; blockZ < height; blockZ++) {
//						ctx.at(minX + blockX, 0, minZ + blockZ);
//						
//						int value = (int) (Math.clamp(function.compute(ctx) / scaler, 0.0F, 1.0F) * 255);
//						data[blockZ * width + blockX] = rgb(value, value, value);
//					}
//				}
//				
//				provider.close();
//				server.minecraft.execute(() -> {
//		    		try(NativeImage image = new NativeImage(width, height, false)) {
//		    			for(int x = 0; x < image.getWidth(); x++) {
//		    				for(int y = 0; y < image.getHeight(); y++) {
//		    					int rgb = data[y * width + x];
//		    					image.setPixel(x, image.getHeight() - 1 - y, rgb);
//		    				}
//		    			}
//
//			    		Path path = ConfigUtil.DEBUG_PATH.resolve(functionKey.location().toString().replace(":", File.separator) + ".png");
//			    		PathUtils.createParentDirectories(path);
//			    		image.writeToFile(path);
//		    				
//		    			long end = System.nanoTime();
//		    			sendSuccess(server, path, TimeUnit.NANOSECONDS.toSeconds(end - start));
//		    		} catch (IOException e) {
//		    			e.printStackTrace();
//		    		}
//				});
//	    	}
//		}).exceptionally((err) -> {
//			err.printStackTrace();
//			return null;
//		});
//    }

	private static void sendSuccess(IntegratedServer server, Path path, long time) {
	    // we have to send the result directly since servers can't send open file commands for obvious reasons
		ChatComponent chat = server.minecraft.gui.getChat();
		Component message = Component.translatable(RTFTranslationKeys.EXPORT_SUCCESS, makePathComponent(path), time);
		chat.addMessage(message);
	}
    
    private static Component makePathComponent(Path filePath) {
    	return Component.literal(filePath.toString()).withStyle((s) -> {
    		return s.withClickEvent(new ClickEvent.OpenFile(filePath.getParent().toString()))
    				.withHoverEvent(new HoverEvent.ShowText(Component.translatable(RTFTranslationKeys.EXPORT_DESCRIPTION)))
    				.withColor(ChatFormatting.GREEN);
    	});
    }
}
