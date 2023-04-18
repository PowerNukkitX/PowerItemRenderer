package cn.powernukkitx.pir.bedrock;

import cn.powernukkitx.pir.object.geometry.Cuboid;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.io.InputStream;
import java.io.InputStreamReader;

public final class ModelParser {
    private ModelParser() {
        throw new UnsupportedOperationException();
    }

    @Contract("_ -> new")
    public static @NotNull JsonModel @NotNull [] parse(@NotNull String modelJson) {
        return parse(JsonParser.parseString(modelJson));
    }

    @Contract("_ -> new")
    public static @NotNull JsonModel @NotNull [] parse(@NotNull InputStream modelJson) {
        return parse(JsonParser.parseReader(new InputStreamReader(modelJson)));
    }

    @Contract("_ -> new")
    public static @NotNull JsonModel @NotNull [] parse(@NotNull JsonElement json) {
        var geometries = json.getAsJsonObject().get("minecraft:geometry").getAsJsonArray();
        var models = new JsonModel[geometries.size()];
        for (int k = 0; k < models.length; k++) {
            var geometry = geometries.get(k).getAsJsonObject();
            var description = geometry.get("description");

            var identifier = description.getAsJsonObject().get("identifier").getAsString();
            var textureWidth = description.getAsJsonObject().get("texture_width").getAsInt();
            var textureHeight = description.getAsJsonObject().get("texture_height").getAsInt();
            var visibleBoundsWidth = description.getAsJsonObject().get("visible_bounds_width").getAsFloat();
            var visibleBoundsHeight = description.getAsJsonObject().get("visible_bounds_height").getAsFloat();
            var visibleBoundsOffsetArray = description.getAsJsonObject().get("visible_bounds_offset").getAsJsonArray();
            var visibleBoundsOffset = new Vector3f(
                    visibleBoundsOffsetArray.get(0).getAsFloat(),
                    visibleBoundsOffsetArray.get(1).getAsFloat(),
                    visibleBoundsOffsetArray.get(2).getAsFloat()
            );

            var bonesJsonArray = geometry.get("bones").getAsJsonArray();
            var bones = new JsonModelBone[bonesJsonArray.size()];
            for (int i = 0; i < bonesJsonArray.size(); i++) {
                var boneJson = bonesJsonArray.get(i).getAsJsonObject();
                var name = boneJson.get("name").getAsString();
                var pivotArray = boneJson.get("pivot").getAsJsonArray();
                var pivot = new Vector3f(
                        pivotArray.get(0).getAsFloat(),
                        pivotArray.get(1).getAsFloat(),
                        pivotArray.get(2).getAsFloat()
                );
                // TODO: 2023/4/18 支持旋转
//            var rotationArray = boneJson.get("rotation").getAsJsonArray();
//            var rotation = new Vector3f(
//                    rotationArray.get(0).getAsFloat(),
//                    rotationArray.get(1).getAsFloat(),
//                    rotationArray.get(2).getAsFloat()
//            );
                var cubesJsonArray = boneJson.get("cubes").getAsJsonArray();
                var cubes = new JsonModelCuboid[cubesJsonArray.size()];
                for (int j = 0; j < cubesJsonArray.size(); j++) {
                    var cubeJson = cubesJsonArray.get(j).getAsJsonObject();
                    var originArray = cubeJson.get("origin").getAsJsonArray();
                    var origin = new Vector3f(
                            originArray.get(0).getAsFloat(),
                            originArray.get(1).getAsFloat(),
                            originArray.get(2).getAsFloat()
                    );
                    var sizeArray = cubeJson.get("size").getAsJsonArray();
                    var size = new Vector3f(
                            sizeArray.get(0).getAsFloat(),
                            sizeArray.get(1).getAsFloat(),
                            sizeArray.get(2).getAsFloat()
                    );
                    var uvObject = cubeJson.get("uv").getAsJsonObject();
                    var uvSet = new JsonModelUVSet(makeUVDetail(uvObject.get("north").getAsJsonObject()),
                            makeUVDetail(uvObject.get("east").getAsJsonObject()),
                            makeUVDetail(uvObject.get("south").getAsJsonObject()),
                            makeUVDetail(uvObject.get("west").getAsJsonObject()),
                            makeUVDetail(uvObject.get("up").getAsJsonObject()),
                            makeUVDetail(uvObject.get("down").getAsJsonObject()));
                    cubes[j] = new JsonModelCuboid(origin, size, uvSet);
                }
                bones[i] = new JsonModelBone(name, pivot, cubes);
            }
            models[k] = new JsonModel(identifier, textureWidth, textureHeight, visibleBoundsWidth, visibleBoundsHeight, visibleBoundsOffset, bones);
        }
        return models;
    }

    private static @NotNull Cuboid.UVDetail makeUVDetail(@NotNull JsonObject uvObject) {
        return new Cuboid.UVDetail(
                uvObject.get("uv").getAsJsonArray().get(0).getAsInt(),
                uvObject.get("uv").getAsJsonArray().get(1).getAsInt(),
                uvObject.get("uv_size").getAsJsonArray().get(0).getAsInt(),
                uvObject.get("uv_size").getAsJsonArray().get(1).getAsInt()
        );
    }
}
