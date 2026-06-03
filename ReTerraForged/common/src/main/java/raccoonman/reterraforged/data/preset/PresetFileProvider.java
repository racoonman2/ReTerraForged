package raccoonman.reterraforged.data.preset;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.stream.JsonWriter;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;

import net.minecraft.Util;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.util.GsonHelper;
import raccoonman.reterraforged.preset.Preset;

public record PresetFileProvider(PackOutput output, String presetName, Preset preset, Codec<Preset> codec) implements DataProvider {

	@Override
	public String getName() {
		return "Preset [" + this.presetName + "]";
	}

	@Override
	public CompletableFuture<?> run(CachedOutput cachedOutput) {
		Path path = this.output.getOutputFolder().resolve(this.presetName + ".json");
		DataResult<JsonElement> result = this.codec.encodeStart(JsonOps.INSTANCE, this.preset);
		Optional<DataResult.Error<JsonElement>> error = result.error();
		if(error.isPresent()) {
			return CompletableFuture.failedFuture(new JsonIOException(error.get().message()));
		}
		return saveStable(cachedOutput, result.result().get(), path);
	}

	// don't use DataProvider.saveStable as it doesn't preserve json order
    private static CompletableFuture<?> saveStable(CachedOutput cachedOutput, JsonElement jsonElement, Path path) {
        return CompletableFuture.runAsync(() -> {
            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                HashingOutputStream hashingOutputStream = new HashingOutputStream(Hashing.sha1(), byteArrayOutputStream);
                try (
                	JsonWriter jsonWriter = new JsonWriter(new OutputStreamWriter(hashingOutputStream, StandardCharsets.UTF_8))
                ) {
                    jsonWriter.setSerializeNulls(false);
                    jsonWriter.setIndent("  ");
                    GsonHelper.writeValue(jsonWriter, jsonElement, null);
                }
                cachedOutput.writeIfNeeded(path, byteArrayOutputStream.toByteArray(), hashingOutputStream.hash());
            } catch (IOException iOException) {
                LOGGER.error("Failed to save file to {}", path, iOException);
            }
        }, Util.backgroundExecutor());
    }
}