package cn.powernukkitx.pir.object.camera;

import cn.powernukkitx.pir.scene.Scene;
import cn.powernukkitx.pir.util.ColorUtil;
import cn.powernukkitx.pir.worker.RayTraceWorker;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.util.Objects;

public final class SimpleOrthogonalCamera implements OrthogonalCamera {
    private final @NotNull Vector3f position;
    private final @NotNull Vector3f direction;
    private final int pixelWidth;
    private final int pixelHeight;
    private final float sceneWidth;
    private final float sceneHeight;
    private final @NotNull Vector3f up;
    private final @NotNull Vector3f[] rayPositions;
    private final @NotNull Vector3f[] rayDirections;

    public SimpleOrthogonalCamera(
            @NotNull Vector3f position,
            @NotNull Vector3f direction,
            int pixelWidth,
            int pixelHeight,
            float sceneWidth,
            float sceneHeight,
            @NotNull Vector3f up) {
        this.position = position;
        this.direction = direction;
        this.pixelWidth = pixelWidth;
        this.pixelHeight = pixelHeight;
        this.sceneWidth = sceneWidth;
        this.sceneHeight = sceneHeight;
        this.up = up;
        {
            var tmp3f = new Vector3f();
            var tmpPositions = new Vector3f[pixelWidth * pixelHeight];
            var tmpDirections = new Vector3f[pixelWidth * pixelHeight];
            var right = right();
            var dUp = up.mul(sceneHeight / pixelHeight, new Vector3f());
            var dRight = right.mul(sceneWidth / pixelWidth, new Vector3f());
            var startPos = position
                    .sub(dUp.mul(pixelHeight, tmp3f).mul(0.5f))
                    .sub(dRight.mul(pixelWidth, tmp3f).mul(0.5f));
            for (int y = 0; y < pixelHeight; y++) {
                for (int x = 0; x < pixelWidth; x++) {
                    var index = xy2index(x, y);
                    tmpPositions[index] = startPos.add(dUp.mul(y, tmp3f), new Vector3f()).add(dRight.mul(x, tmp3f), new Vector3f());
                    tmpDirections[index] = direction;
                }
            }
            this.rayPositions = tmpPositions;
            this.rayDirections = tmpDirections;
        }
    }

    @Override
    public Vector3f right() {
        return direction.cross(up, new Vector3f()).normalize();
    }

    private int xy2index(int x, int y) {
        return y * pixelWidth + x;
    }

    @Override
    public @NotNull RenderedImage render(@NotNull Scene scene, @NotNull RayTraceWorker rayTraceWorker) {
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

    @Override
    public @NotNull Vector3f position() {
        return position;
    }

    @Override
    public @NotNull Vector3f direction() {
        return direction;
    }

    @Override
    public int pixelWidth() {
        return pixelWidth;
    }

    @Override
    public int pixelHeight() {
        return pixelHeight;
    }

    @Override
    public float sceneWidth() {
        return sceneWidth;
    }

    @Override
    public float sceneHeight() {
        return sceneHeight;
    }

    @Override
    public @NotNull Vector3f up() {
        return up;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (SimpleOrthogonalCamera) obj;
        return Objects.equals(this.position, that.position) &&
                Objects.equals(this.direction, that.direction) &&
                this.pixelWidth == that.pixelWidth &&
                this.pixelHeight == that.pixelHeight &&
                Float.floatToIntBits(this.sceneWidth) == Float.floatToIntBits(that.sceneWidth) &&
                Float.floatToIntBits(this.sceneHeight) == Float.floatToIntBits(that.sceneHeight) &&
                Objects.equals(this.up, that.up);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position, direction, pixelWidth, pixelHeight, sceneWidth, sceneHeight, up);
    }

    @Override
    public String toString() {
        return "SimpleOrthogonalCamera[" +
                "position=" + position + ", " +
                "direction=" + direction + ", " +
                "pixelWidth=" + pixelWidth + ", " +
                "pixelHeight=" + pixelHeight + ", " +
                "sceneWidth=" + sceneWidth + ", " +
                "sceneHeight=" + sceneHeight + ", " +
                "up=" + up + ']';
    }

}
