package cn.powernukkitx.pir.bedrock.render;

import cn.nukkit.block.Block;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.defaults.VanillaCommand;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.item.Item;
import cn.nukkit.item.customitem.CustomItem;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.powernukkitx.pir.PNXPluginMain;
import cn.powernukkitx.pir.bedrock.resource.PIRLogger;
import cn.powernukkitx.pir.bedrock.resource.ResourcePack;
import com.google.gson.JsonParser;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class RenderCommand extends VanillaCommand {
    public RenderCommand(String name) {
        super(name, "Render items.");
        this.setPermission("pir.all");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newType("namespace", false, CommandParamType.STRING),
                CommandParameter.newType("renderConfig", false, CommandParamType.FILE_PATH)
        });
        this.enableParamTree();
    }

    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        var list = result.getValue();
        String namespace = list.getResult(0);
        String renderConfigPath = list.getResult(1);
        RenderingManifest renderingManifest;
        try (var reader = PNXPluginMain.GSON.newJsonReader(new FileReader(renderConfigPath))) {
            renderingManifest = RenderingManifest.fromJson(JsonParser.parseReader(reader).getAsJsonObject());
        } catch (Exception e) {
            log.addError("Error reading render config: " + e.getMessage());
            return 0;
        }
        Path outputDir;
        if (renderingManifest.outputPathDir == null) {
            outputDir = Path.of(renderConfigPath).getParent();
        } else {
            outputDir = Path.of(renderingManifest.outputPathDir);
        }
        try {
            Files.createDirectories(outputDir);
        } catch (Exception e) {
            log.addError("Error creating output directory: " + e.getMessage());
            return 0;
        }
        var pirLogger = PIRLogger.fromCommandLogger(log);
        // custom items
        for (var each : Item.getCustomItems().entrySet()) {
            if (each.getKey().startsWith(namespace + ":") && !renderingManifest.renderingTaskList.containsKey(each.getKey())) {
                var item = each.getValue().get();
                if (item instanceof CustomItem customItem) {
                    var itemDefinitionNBT = customItem.getDefinition().nbt();

                    var task = new TaskManifest();
                    task.namespaceId = each.getKey();
                    task.texturePackPath = renderingManifest.texturePackPath;
                    ResourcePack resourcePack;
                    try {
                        resourcePack = ResourcePack.getParsedResourcePack(task.texturePackPath, pirLogger);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    var textureManifest = new TextureManifest();
                    var textureId = itemDefinitionNBT.getCompound("components")
                            .getCompound("item_properties")
                            .getCompound("minecraft:icon")
                            .getString("texture");
                    textureManifest.any = resourcePack.getTexturePath(textureId);
                    if (textureManifest.any == null) {
                        pirLogger.warn("Texture of item " + task.namespaceId + " not found: " + textureId);
                        continue;
                    }
                    task.inPackTexturePath = textureManifest;

                    renderingManifest.renderingTaskList.put(each.getKey(), task);
                } else {
                    log.addError("Item " + each.getKey() + " is not a custom item");
                }
            }
        }
        // custom blocks
        for (var customBlock : Block.getCustomBlockMap().values()) {
            var namespaceId = customBlock.getNamespaceId();
            if (namespaceId.startsWith(namespace + ":") && !renderingManifest.renderingTaskList.containsKey(namespaceId)) {
                var blockDefinitionNBT = customBlock.getDefinition().nbt();

                var task = new TaskManifest();
                task.namespaceId = namespaceId;
                task.texturePackPath = renderingManifest.texturePackPath;
                ResourcePack resourcePack;
                try {
                    resourcePack = ResourcePack.getParsedResourcePack(task.texturePackPath, pirLogger);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                var textureManifest = new TextureManifest();
                var materials = blockDefinitionNBT.getCompound("components").getCompound("minecraft:material_instances").getCompound("materials");
                if (materials.isEmpty() && blockDefinitionNBT.contains("permutations")) {
                    var permutations = blockDefinitionNBT.getList("permutations", CompoundTag.class);
                    if (task.permutationIndex > permutations.size()) {
                        pirLogger.warn("Permutation index of block " + task.namespaceId + " is out of range: " + task.permutationIndex);
                        continue;
                    }
                    var permutationCompound = permutations.get(task.permutationIndex);
                    materials = permutationCompound.getCompound("components").getCompound("minecraft:material_instances").getCompound("materials");
                }
                if (materials.isEmpty()) {
                    pirLogger.warn("Materials of block " + task.namespaceId + " not found");
                    continue;
                }
                var isSingleSide = true;
                for (var faceEntry : materials.getTags().entrySet()) {
                    var face = faceEntry.getKey();
                    var material = (CompoundTag) faceEntry.getValue();
                    var textureId = material.getString("texture");
                    var texturePath = resourcePack.getTexturePath(textureId);
                    if (texturePath == null) {
                        pirLogger.warn("Texture of block " + namespaceId + " not found: " + textureId);
                        continue;
                    }
                    var renderMethod = material.getString("render_method");
                    if (isSingleSide && ("alpha_test".equals(renderMethod) || "double_sided".equals(renderMethod))) {
                        isSingleSide = false;
                    }
                    switch (face) {
                        case "up" -> textureManifest.up = texturePath;
                        case "down" -> textureManifest.down = texturePath;
                        case "north" -> textureManifest.north = texturePath;
                        case "south" -> textureManifest.south = texturePath;
                        case "west" -> textureManifest.west = texturePath;
                        case "east" -> textureManifest.east = texturePath;
                        case "*" -> textureManifest.any = texturePath;
                    }
                }
                task.isSingleSide = isSingleSide;
                task.inPackTexturePath = textureManifest;

                // Geometry
                if (blockDefinitionNBT.getCompound("components").contains("minecraft:unit_cube")) {
                    task.inPackModelPath = "unit_cube";
                } else if (blockDefinitionNBT.getCompound("components").contains("minecraft:geometry")) {
                    var modelId = blockDefinitionNBT.getCompound("components").getCompound("minecraft:geometry").getString("value");
                    task.inPackModelPath = resourcePack.getGeometryPath(modelId);
                    if (task.inPackModelPath == null) {
                        pirLogger.warn("Model of block " + namespaceId + " not found: " + modelId);
                        continue;
                    }
                } else {
                    pirLogger.warn("Geometry not found for block: " + namespaceId);
                    continue;
                }

                renderingManifest.renderingTaskList.put(namespaceId, task);
            }
        }
        try (var writer = PNXPluginMain.GSON.newJsonWriter(Files.newBufferedWriter(outputDir.resolve("rendering_manifest.json")))) {
            PNXPluginMain.GSON.toJson(renderingManifest, RenderingManifest.class, writer);
        } catch (Exception e) {
            log.addError("Error writing rendering manifest: " + e.getMessage());
            return 0;
        }
        return 0;
    }
}
