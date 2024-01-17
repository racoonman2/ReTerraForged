package raccoonman.reterraforged.data.packs;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.VanillaPackResources;
import net.minecraft.server.packs.VanillaPackResourcesBuilder;
import net.minecraft.server.packs.repository.BuiltInPackSource;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.Pack.ResourcesSupplier;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.level.validation.DirectoryValidator;
import raccoonman.reterraforged.RTFCommon;

public class RTFBuiltinPackSource extends BuiltInPackSource {
	private static final ResourceLocation PACKS_DIR = RTFCommon.location("datapacks");
	
	public RTFBuiltinPackSource(DirectoryValidator directoryValidator) {
		super(PackType.SERVER_DATA, createRTFPackSource(), PACKS_DIR, directoryValidator);
	}

	@Nullable
	@Override
	protected Pack createVanillaPack(PackResources packResources) {
		return null;
	}

	@Override
	protected Component getPackTitle(String title) {
		return Component.literal(title);
	}

	@Override
	protected Pack createBuiltinPack(String title, ResourcesSupplier resourceSupplier, Component description) {
        return Pack.readMetaAndCreate(title, description, false, resourceSupplier, PackType.SERVER_DATA, Pack.Position.TOP, PackSource.FEATURE);
	}

	private static VanillaPackResources createRTFPackSource() {
		VanillaPackResourcesBuilder builder = new VanillaPackResourcesBuilder().exposeNamespace(RTFCommon.MOD_ID);
		PackType packType = PackType.SERVER_DATA;
		String root = "/" + packType.getDirectory() + "/";
		URL uRL = RTFCommon.class.getResource(root);
		if (uRL == null) {
			RTFCommon.LOGGER.error("File {} does not exist in classpath", root);
		} else {
			try {
				URI uRI = uRL.toURI();
				String uriSchema = uRI.getScheme();
				if (!"jar".equals(uriSchema) && !"file".equals(uriSchema)) {
					RTFCommon.LOGGER.warn("Assets URL '{}' uses unexpected schema", uRI);
				}
				Path path = safeGetPath(uRI);
				builder.pushAssetPath(packType, path);
			} catch (Exception exception) {
				RTFCommon.LOGGER.error("Couldn't resolve path to assets", exception);
			}	
		}
        return builder.applyDevelopmentConfig().build();
	}

    private static Path safeGetPath(URI uRI) throws IOException {
        try {
            return Paths.get(uRI);
        } catch (FileSystemNotFoundException fileSystemNotFoundException) {
        } catch (Throwable throwable) {
        	RTFCommon.LOGGER.warn("Unable to get path for: {}", uRI, throwable);
        }
        try {
            FileSystems.newFileSystem(uRI, Collections.emptyMap());
        } catch (FileSystemAlreadyExistsException fileSystemAlreadyExistsException) {
            // empty catch block
        }
        return Paths.get(uRI);
    }
}
