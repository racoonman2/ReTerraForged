package raccoonman.reterraforged.mixin;

import java.nio.file.Path;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.google.common.collect.Lists;

import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.server.packs.repository.ServerPacksSource;
import net.minecraft.world.level.validation.DirectoryValidator;
import raccoonman.reterraforged.data.packs.RTFBuiltinPackSource;

@Mixin(ServerPacksSource.class)
class MixinServerPacksSource {
    
	@Redirect(
		method = "createPackRepository",
		at = @At(
			value = "NEW",
			target = "Lnet/minecraft/server/packs/repository/PackRepository;"
		),
		require = 1
	)
    private static PackRepository createPackRepository(RepositorySource[] repositorySources, Path path, DirectoryValidator directoryValidator) {
		List<RepositorySource> sourceList = Lists.newArrayList(repositorySources);
		sourceList.add(new RTFBuiltinPackSource(directoryValidator));
    	return new PackRepository(sourceList.toArray(RepositorySource[]::new));
    }
}
