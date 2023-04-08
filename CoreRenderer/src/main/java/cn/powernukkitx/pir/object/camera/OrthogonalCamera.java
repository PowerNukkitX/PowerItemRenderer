package cn.powernukkitx.pir.object.camera;

public interface OrthogonalCamera extends Camera {
    /**
     * @return The real width of the camera in the scene
     */
    float sceneWidth();

    /**
     * @return The real height of the camera in the scene
     */
    float sceneHeight();
}
