package cn.powernukkitx.pir.bedrock.render;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class TaskManifest {
    public String namespaceId;
    public String texturePackPath;
    public TextureManifest inPackTexturePath;
    public String inPackModelPath;
    public int permutationIndex = 0;
    public boolean isSingleSide = true;
    public float ambientLight = 0.1f;
    public boolean isIgnored = false;
    @NotNull
    public JsonObject data = new JsonObject();

    public boolean isItem() {
        return inPackModelPath == null;
    }

    public boolean isIncomplete() {
        return namespaceId == null || texturePackPath == null || inPackTexturePath == null;
    }

    public static @NotNull TaskManifest fromJson(@Nullable RenderingManifest renderingManifest, @NotNull JsonObject json) {
        var manifest = new TaskManifest();
        if (json.has("namespaceId")) {
            manifest.namespaceId = json.get("namespaceId").getAsString();
        }
        if (json.has("texturePackPath")) {
            manifest.texturePackPath = json.get("texturePackPath").getAsString();
        } else if (renderingManifest != null) {
            manifest.texturePackPath = renderingManifest.texturePackPath;
        }
        if (json.has("inPackTexturePath")) {
            manifest.inPackTexturePath = TextureManifest.fromJson(json.get("inPackTexturePath"));
        }
        if (json.has("inPackModelPath")) {
            manifest.inPackModelPath = json.get("inPackModelPath").getAsString();
        }
        if (json.has("permutationIndex")) {
            manifest.permutationIndex = json.get("permutationIndex").getAsInt();
        }
        if (json.has("isSingleSide")) {
            manifest.isSingleSide = json.get("isSingleSide").getAsBoolean();
        }
        if (json.has("ambientLight")) {
            manifest.ambientLight = json.get("ambientLight").getAsFloat();
        }
        if (json.has("isIgnored")) {
            manifest.isIgnored = json.get("isIgnored").getAsBoolean();
        }
        if (json.has("data")) {
            manifest.data = json.get("data").getAsJsonObject();
        }
        return manifest;
    }
}
