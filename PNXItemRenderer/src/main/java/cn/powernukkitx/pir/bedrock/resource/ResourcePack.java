package cn.powernukkitx.pir.bedrock.resource;

import cn.powernukkitx.pir.PNXPluginMain;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ResourcePack {
    private final FileSystem zipFileSystem;
    private final Path path;
    private final Map<String, String> id2textureMap = new HashMap<>(256);
    private final Map<String, String> id2geometryMap = new HashMap<>();
    private final Map<String, Map<String, String>> languageMap = new HashMap<>(2);
    private static final ConcurrentHashMap<String, ResourcePack> PARSED_RESOURCE_PACKS = new ConcurrentHashMap<>(2);

    public static ResourcePack getParsedResourcePack(@NotNull String path, @NotNull PIRLogger logger) throws IOException {
        if (PARSED_RESOURCE_PACKS.containsKey(path)) {
            return PARSED_RESOURCE_PACKS.get(path);
        } else {
            var resourcePack = new ResourcePack(Path.of(path));
            PARSED_RESOURCE_PACKS.put(path, resourcePack);
            // parse meta-data
            // 1. parse id and texture map
            {
                var itemTextureJsonPath = resourcePack.getRealPath("textures/item_texture.json");
                try (var reader = PNXPluginMain.GSON.newJsonReader(Files.newBufferedReader(itemTextureJsonPath))) {
                    var textureData = JsonParser.parseReader(reader).getAsJsonObject().get("texture_data").getAsJsonObject();
                    parseTextureData(resourcePack, textureData, logger);
                }
                var terrainTextureJsonPath = resourcePack.getRealPath("textures/terrain_texture.json");
                try (var reader = PNXPluginMain.GSON.newJsonReader(Files.newBufferedReader(terrainTextureJsonPath))) {
                    var textureData = JsonParser.parseReader(reader).getAsJsonObject().get("texture_data").getAsJsonObject();
                    parseTextureData(resourcePack, textureData, logger);
                }
                // the texture path may have no extension name, we need to check and add it
                for (var entry : new HashMap<>(resourcePack.id2textureMap).entrySet()) {
                    var texturePath = entry.getValue();
                    if (!Files.exists(resourcePack.getRealPath(texturePath))) {
                        if (Files.exists(resourcePack.getRealPath(texturePath + ".png"))) {
                            resourcePack.id2textureMap.put(entry.getKey(), texturePath + ".png");
                        } else if (Files.exists(resourcePack.getRealPath(texturePath + ".jpg"))) {
                            resourcePack.id2textureMap.put(entry.getKey(), texturePath + ".jpg");
                        } else if (Files.exists(resourcePack.getRealPath(texturePath + ".jpeg"))) {
                            resourcePack.id2textureMap.put(entry.getKey(), texturePath + ".jpeg");
                        } else {
                            logger.warn("Texture file not found: " + texturePath);
                        }
                    }
                }
            }
            // 2. Parse languages
            // A jsonArray of all available languages files is at texts/languages.json
            // Each xx_XX.lang file contains many lines in key=value, some of them will be pure blank lines
            {
                var languageJsonPath = resourcePack.getRealPath("texts/languages.json");
                try (var reader = PNXPluginMain.GSON.newJsonReader(Files.newBufferedReader(languageJsonPath))) {
                    var languageArray = JsonParser.parseReader(reader).getAsJsonArray();
                    for (var language : languageArray) {
                        var languageCode = language.getAsString();
                        var languageFilePath = resourcePack.getRealPath("texts/" + languageCode + ".lang");
                        var lines = Files.readAllLines(languageFilePath);
                        var languageMap = new HashMap<String, String>(lines.size());
                        for (var line : lines) {
                            if (line.isBlank()) {
                                continue;
                            }
                            var split = line.split("=", 2);
                            if (split.length != 2) {
                                logger.warn("Invalid language line in " + languageFilePath + ": " + line);
                                continue;
                            }
                            languageMap.put(split[0], split[1]);
                        }
                        resourcePack.languageMap.put(languageCode, languageMap);
                    }
                }
            }
            // 3. Parse geometries
            // Geometry files are in models/** folders, which means we need to scan all files recursely under models folder
            // Each file is in the name of xxx.json, the id is field `minecraft:geometry.description.identifier`
            {
                var modelsFolder = resourcePack.getRealPath("models");
                try (var tree = Files.walk(modelsFolder)) {
                    tree.forEach(each -> {
                        var fileName = each.getFileName().toString();
                        if (!fileName.endsWith(".json")) {
                            return;
                        }
                        try (var reader = PNXPluginMain.GSON.newJsonReader(new InputStreamReader(Files.newInputStream(each)))) {
                            var json = JsonParser.parseReader(reader).getAsJsonObject();
                            var identifier = json.get("minecraft:geometry").getAsJsonArray().get(0).getAsJsonObject().get("description").getAsJsonObject().get("identifier").getAsString();
                            resourcePack.id2geometryMap.put(identifier, each.toString());
                        } catch (Exception e) {
                            logger.warn("Failed to parse geometry file: " + each + " " + e.getMessage());
                        }
                    });
                }
            }
            return resourcePack;
        }
    }

    private static void parseTextureData(@NotNull ResourcePack resourcePack, @NotNull JsonObject textureData, @NotNull PIRLogger logger) {
        for (var each : textureData.entrySet()) {
            var id = each.getKey();
            var data = each.getValue();
            if (data instanceof JsonPrimitive) {
                resourcePack.id2textureMap.put(id, data.getAsString());
            } else if (data instanceof JsonObject jsonObject) {
                if (jsonObject.has("textures")) {
                    var texturesEle = jsonObject.get("textures");
                    if (texturesEle instanceof JsonPrimitive primitive) {
                        resourcePack.id2textureMap.put(id, primitive.getAsString());
                    } else if (texturesEle instanceof JsonObject object) {
                        if (object.has("path")) {
                            resourcePack.id2textureMap.put(id, object.get("path").getAsString());
                        } else {
                            logger.warn("Invalid texture data: " + id);
                        }
                    } else if (texturesEle instanceof JsonArray array) {
                        if (array.size() > 0 && array.get(0) instanceof JsonPrimitive primitive && primitive.isString()) {
                            resourcePack.id2textureMap.put(id, primitive.getAsString());
                        } else {
                            logger.warn("Invalid texture data: " + id);
                        }
                    } else {
                        logger.warn("Invalid texture data: " + id);
                    }
                }
            }
        }
    }

    protected ResourcePack(Path path) throws IOException {
        this.path = path;
        this.zipFileSystem = FileSystems.newFileSystem(path);
    }

    public Path getPath() {
        return path;
    }

    public Path getRealPath(String inPackPath) {
        return zipFileSystem.getPath(inPackPath);
    }

    public String getTexturePath(String id) {
        return id2textureMap.get(id);
    }

    public Path getTextureRealPath(String id) {
        return getRealPath(id2textureMap.get(id));
    }

    public String getLanguageText(String languageCode, String key) {
        return languageMap.getOrDefault(languageCode, Collections.emptyMap()).getOrDefault(key, key);
    }

    public String getGeometryPath(String id) {
        return id2geometryMap.get(id);
    }

    public Path getGeometryRealPath(String id) {
        return getRealPath(id2geometryMap.get(id));
    }
}
