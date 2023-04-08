package cn.powernukkitx.pir.object.camera;

import cn.powernukkitx.pir.scene.Scene;
import cn.powernukkitx.pir.util.ColorUtil;
import cn.powernukkitx.pir.worker.RayTraceWorker;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.awt.*;
import java.awt.image.BufferedImage;

public record SimpleOrthogonalCamera(
        @NotNull Vector3f position,
        @NotNull Vector3f direction,
        int pixelWidth,
        int pixelHeight,
        float sceneWidth,
        float sceneHeight,
        @NotNull Vector3f up) implements OrthogonalCamera {

    @Override
    public Vector3f right() {
        return direction.cross(up, new Vector3f()).normalize();
    }

    private int xy2index(int x, int y) {
        return y * pixelWidth + x;
    }

    @Override
    public @NotNull Image render(@NotNull Scene scene, @NotNull RayTraceWorker rayTraceWorker) {
        var tmp3f = new Vector3f();
        var rayPositions = new Vector3f[pixelWidth * pixelHeight];
        var rayDirections = new Vector3f[pixelWidth * pixelHeight];
        var right = right();
        var dUp = up.mul(sceneHeight / pixelHeight, new Vector3f());
        var dRight = right.mul(sceneWidth / pixelWidth, new Vector3f());
        var startPos = position
                .sub(dUp.mul(pixelHeight, tmp3f).mul(0.5f))
                .sub(dRight.mul(pixelWidth, tmp3f).mul(0.5f));
        for (int y = 0; y < pixelHeight; y++) {
            for (int x = 0; x < pixelWidth; x++) {
                var index = xy2index(x, y);
                rayPositions[index] = startPos.add(dUp.mul(y, tmp3f), new Vector3f()).add(dRight.mul(x, tmp3f), new Vector3f());
                rayDirections[index] = direction;
            }
        }
        var colors = scene.rayTrace(rayPositions, rayDirections, rayTraceWorker);
        var image = new BufferedImage(pixelWidth, pixelHeight, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < pixelHeight; y++) {
            for (int x = 0; x < pixelWidth; x++) {
                // In Java, images are processed in a left-handed format,
                // but in PIR, rendering is processed in a right-handed format.
                // Here requires conversion
                var color = colors[xy2index(x, y)];
                image.setRGB(x, pixelHeight - y - 1, ColorUtil.toInt(color));
            }
        }
        return image;
    }
}
