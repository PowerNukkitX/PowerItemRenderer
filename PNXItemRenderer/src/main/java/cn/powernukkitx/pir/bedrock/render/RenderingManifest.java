package cn.powernukkitx.pir.bedrock.render;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public final class RenderingManifest {
    public String texturePackPath;
    public String outputPathDir;
    public Map<String, TaskManifest> renderingTaskList = new HashMap<>();

    public static @NotNull RenderingManifest fromJson(@NotNull JsonObject json) {
        var manifest = new RenderingManifest();
        if (json.has("texturePackPath")) {
            manifest.texturePackPath = json.get("texturePackPath").getAsString();
        }
        if (json.has("outputPathDir")) {
            manifest.outputPathDir = json.get("outputPathDir").getAsString();
        }
        if (json.has("renderingTaskList")) {
            var obj = json.get("renderingTaskList").getAsJsonObject();
            for (var entry : obj.entrySet()) {
                var task = TaskManifest.fromJson(manifest, entry.getValue().getAsJsonObject());
                if (task.namespaceId == null) {
                    task.namespaceId = entry.getKey();
                }
                manifest.renderingTaskList.put(entry.getKey(), task);
            }
        }
        return manifest;
    }
}
