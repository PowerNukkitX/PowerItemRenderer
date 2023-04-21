package cn.powernukkitx.pir.object.camera;

import cn.powernukkitx.pir.object.ObjectType;
import cn.powernukkitx.pir.object.SceneObject;
import cn.powernukkitx.pir.scene.Scene;
import cn.powernukkitx.pir.worker.RayTraceWorker;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.awt.image.RenderedImage;

public interface Camera extends SceneObject {
    /**
     * @return The position of the camera
     */
    Vector3f position();

    /**
     * @return The direction of the camera
     */
    Vector3f direction();

    /**
     * @return The up vector of the camera
     */
    Vector3f up();

    /**
     * @return The right vector of the camera
     */
    Vector3f right();

    /**
     * @return The pixel pixelWidth of the camera
     */
    int pixelWidth();

    /**
     * @return The pixel pixelHeight of the camera
     */
    int pixelHeight();

    /**
     * @return The aspect ratio of the camera
     */
    default float aspectRatio() {
        return (float) pixelWidth() / pixelHeight();
    }

    /**
     * Render the scene using the given RayTraceWorker with this camera
     * @param scene The scene to render
     * @param rayTraceWorker The RayTraceWorker to use
     * @return The rendered image
     */
    @NotNull RenderedImage render(@NotNull Scene scene, @NotNull RayTraceWorker rayTraceWorker);

    @Override
    @NotNull default ObjectType type() {
        return ObjectType.CAMERA;
    }
}
