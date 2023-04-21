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
import cn.powernukkitx.pir.bedrock.ModelParser;
import cn.powernukkitx.pir.bedrock.resource.PIRLogger;
import cn.powernukkitx.pir.bedrock.resource.ResourcePack;
import cn.powernukkitx.pir.object.camera.SimpleOrthogonalCamera;
import cn.powernukkitx.pir.object.geometry.Cube;
import cn.powernukkitx.pir.object.light.AmbientLight;
import cn.powernukkitx.pir.object.light.DirectionalLight;
import cn.powernukkitx.pir.scene.SimpleScene;
import cn.powernukkitx.pir.util.ImageUtil;
import cn.powernukkitx.pir.worker.SimpleRayTraceWorker;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.FileReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class RenderCommand extends VanillaCommand {
    public RenderCommand(String name) {
        super(name, "Render items.");
        this.setPermission("pir.all");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newType("namespace", false, CommandParamType.STRING),
                CommandParameter.newEnum("mode", true, new String[]{"manifest", "image", "mcmod"}),
                CommandParameter.newType("renderConfig", false, CommandParamType.FILE_PATH)
        });
        this.enableParamTree();
    }

    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        // parse args
        var list = result.getValue();
        String namespace = list.getResult(0);
        String mode = list.size() == 2 ? "manifest" : list.getResult(1);
        String renderConfigPath = list.getResult(list.size() - 1);
        var start = System.currentTimeMillis();
        // read and parse render config
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
        if (renderingManifest.texturePackPath != null) {
            try {
                ResourcePack.getParsedResourcePack(renderingManifest.texturePackPath, pirLogger);
            } catch (IOException e) {
                log.addError("Error loading texture pack: " + e.getMessage());
                return 0;
            }
        }
        log.addSuccess("Parsed render config and resource packs in " + (System.currentTimeMillis() - start) + "ms").output();
        start = System.currentTimeMillis();
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
                        throw new UncheckedIOException(e);
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
            if (namespaceId.startsWith(namespace + ":")) {
                TaskManifest task;
                if (renderingManifest.renderingTaskList.containsKey(namespaceId)) {
                    var tmpTask = renderingManifest.renderingTaskList.get(namespaceId);
                    if (tmpTask.isIgnored) {
                        continue;
                    }
                    if (tmpTask.isIncomplete()) {
                        task = tmpTask;
                    } else {
                        continue;
                    }
                } else {
                    task = new TaskManifest();
                    task.namespaceId = namespaceId;
                    task.texturePackPath = renderingManifest.texturePackPath;
                }

                var blockDefinitionNBT = customBlock.getDefinition().nbt();

                ResourcePack resourcePack;
                try {
                    resourcePack = ResourcePack.getParsedResourcePack(task.texturePackPath, pirLogger);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
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
        log.addSuccess("Collected " + renderingManifest.renderingTaskList.size() + " custom items and blocks in " +
                (System.currentTimeMillis() - start) + "ms").output();
        start = System.currentTimeMillis();
        if ("manifest".equals(mode)) {
            try (var writer = PNXPluginMain.GSON.newJsonWriter(Files.newBufferedWriter(outputDir.resolve("rendering_manifest.json")))) {
                PNXPluginMain.GSON.toJson(renderingManifest, RenderingManifest.class, writer);
            } catch (Exception e) {
                log.addError("Error writing rendering manifest: " + e.getMessage());
                return 0;
            }
        }
        if ("image".equals(mode)) {
            var rendered32Images = renderImage(32, 32, renderingManifest, pirLogger);
            log.addSuccess("Rendered " + rendered32Images.size() + " 32x32 images in " + (System.currentTimeMillis() - start) + "ms").output();
            start = System.currentTimeMillis();
            var rendered128Images = renderImage(128, 128, renderingManifest, pirLogger);
            log.addSuccess("Rendered " + rendered128Images.size() + " 128x128 images in " + (System.currentTimeMillis() - start) + "ms").output();
            // parallely write them into disk
            for (var entry : rendered128Images.entrySet()) {
                var task = entry.getKey();
                var image = entry.getValue();
                var output = outputDir.resolve(task.replace(':', '-') + "-128x" + ".png");
                try {
                    ImageIO.write((RenderedImage) image, "png", Files.newOutputStream(output, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING));
                } catch (IOException e) {
                    pirLogger.warn("Error writing image: " + e.getMessage());
                } catch (Exception e) {
                    pirLogger.warn("Bad image: " + e.getMessage());
                }
            }
            log.addSuccess("Wrote " + rendered128Images.size() + " 128x128 images in " +
                    (System.currentTimeMillis() - start) + "ms").output();
            start = System.currentTimeMillis();
            for (var entry : rendered32Images.entrySet()) {
                var task = entry.getKey();
                var image = entry.getValue();
                var output = outputDir.resolve(task.replace(':', '-') + "-32x" + ".png");
                try {
                    ImageIO.write((RenderedImage) image, "png", Files.newOutputStream(output, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING));
                } catch (IOException e) {
                    pirLogger.warn("Error writing image: " + e.getMessage());
                } catch (Exception e) {
                    pirLogger.warn("Bad image: " + e.getMessage());
                }
            }
            log.addSuccess("Wrote " + rendered32Images.size() + " 32x32 images in " +
                    (System.currentTimeMillis() - start) + "ms").output();
        }
        return 0;
    }

    public static @NotNull Map<String, Image> renderImage(int width, int height,
                                                          @NotNull RenderingManifest manifest,
                                                          @NotNull PIRLogger logger) {
        var fuzzyUp = new Vector3f(0, 0, 1);
        var direction = new Vector3f(-1f, 1f, -1f / 1.27f).normalize();
        var camera = new SimpleOrthogonalCamera(new Vector3f(4.01f, -4f, 4f / 1.27f),
                direction,
                width, height, 1.62f, 1.61f,
                direction.cross(fuzzyUp.cross(direction), new Vector3f()).normalize()
        );
        return manifest.renderingTaskList.values().parallelStream().map(task -> {
            if (task.isIgnored) return null;
            try {
                var resourcePack = ResourcePack.getParsedResourcePack(task.texturePackPath, logger);
                if (task.isItem()) {
                    var texture = ImageIO.read(Files.newInputStream(resourcePack.getRealPath(task.inPackTexturePath.any)));
                    var image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                    var graphics = image.createGraphics();
                    graphics.drawImage(texture, 0, 0, width, height, null);
                    graphics.dispose();
                    return new AbstractMap.SimpleImmutableEntry<>(task.namespaceId, image);
                } else {
                    var scene = new SimpleScene();
                    var rayTraceWorker = new SimpleRayTraceWorker(task.isSingleSide);
                    // handle scene
                    {
                        scene.add(new AmbientLight(task.ambientLight));
                        scene.add(new DirectionalLight(-1f, 1.5f, -2.25f, 0.63f));
                    }
                    // handle model
                    if ("unit_cube".equals(task.inPackModelPath)) {
                        // down, north, east, south, west, up
                        scene.add(new Cube(0, 0, 0, 1, new BufferedImage[]{
                                ImageUtil.readImage(resourcePack.getRealPath(task.inPackTexturePath.down())),
                                ImageUtil.readImage(resourcePack.getRealPath(task.inPackTexturePath.north())),
                                ImageUtil.readImage(resourcePack.getRealPath(task.inPackTexturePath.west())),
                                ImageUtil.readImage(resourcePack.getRealPath(task.inPackTexturePath.south())),
                                ImageUtil.readImage(resourcePack.getRealPath(task.inPackTexturePath.east())),
                                ImageUtil.readImage(resourcePack.getRealPath(task.inPackTexturePath.up()))
                        }));
                    } else {
                        var modelText = Files.readString(resourcePack.getRealPath(task.inPackModelPath));
                        var model = ModelParser.parse(modelText);
                        var modelTexture = ImageUtil.readImage(resourcePack.getRealPath(task.inPackTexturePath.any));
                        for (var each : model) {
                            each.applyToScene(scene, modelTexture);
                        }
                    }
                    // render
                    var image = camera.render(scene.freeze(), rayTraceWorker);
                    return new AbstractMap.SimpleEntry<>(task.namespaceId, image);
                }
            } catch (Exception e) {
                logger.warn("Error rendering image for " + task.namespaceId + ", " + e.getMessage());
                e.printStackTrace();
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
