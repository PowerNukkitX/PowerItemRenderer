package cn.powernukkitx.pir.bedrock;

import cn.powernukkitx.pir.object.geometry.Cuboid;
import cn.powernukkitx.pir.scene.Scene;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.awt.image.BufferedImage;
import java.util.Arrays;

public record JsonModelBone(
        @NotNull String name,
        @NotNull Vector3f pivot,
        @NotNull JsonModelCuboid @NotNull [] cubes
) {
    public void applyToScene(@NotNull Scene scene, @NotNull BufferedImage texture) {
        var modelHeight = (float) (Arrays.stream(cubes).mapToDouble(each -> each.origin().y + each.size().y).max().orElse(0)
                - Arrays.stream(cubes).mapToDouble(each -> each.origin().y).min().orElse(0));
        for (var cuboidData : cubes) {
            var centerX = cuboidData.origin().x + cuboidData.size().x / 2;
            var centerZ = (cuboidData.origin().y + cuboidData.size().y / 2) - modelHeight / 2f;
            var centerY = -(cuboidData.origin().z + cuboidData.size().z / 2);
            scene.add(new Cuboid(centerX / 16, centerY / 16, centerZ / 16,
                    cuboidData.size().x / 16, cuboidData.size().z / 16, cuboidData.size().y / 16,
                    texture, cuboidData.uvSet().toArray()));
        }
    }
}
