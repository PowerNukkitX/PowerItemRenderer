package cn.powernukkitx.pir.bedrock;

import cn.powernukkitx.pir.scene.Scene;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.awt.image.BufferedImage;

public record JsonModel(
        @NotNull String identifier,
        int textureWidth,
        int textureHeight,
        float visibleBoundsWidth,
        float visibleBoundsHeight,
        @NotNull Vector3f visibleBoundsOffset,
        @NotNull JsonModelBone @NotNull [] bones
) {
    public void applyToScene(@NotNull Scene scene, @NotNull BufferedImage texture) {
        for (var bone : bones) {
            bone.applyToScene(scene, texture);
        }
    }
}
