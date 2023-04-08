package cn.powernukkitx.pir.scene;

import cn.powernukkitx.pir.object.Ray;
import cn.powernukkitx.pir.object.SceneObject;
import cn.powernukkitx.pir.util.ColorUtil;
import cn.powernukkitx.pir.worker.RayTraceWorker;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.Set;

public interface Scene extends Set<SceneObject> {
    /**
     * @return is this scene frozen.
     */
    default boolean isFrozen() {
        return false;
    }

    /**
     * Freeze a delegate of this scene.
     * Once a scene is frozen, it cannot be modified.
     * If a scene is not frozen, it cannot be rendered.
     *
     * @return A delegate of this scene. The delegate cannot be modified.
     */
    @Contract("-> new")
    default @NotNull Scene freeze() {
        return new FrozenScene(this);
    }

    @NotNull
    Vector4f @NotNull [] rayTrace(@NotNull Ray @NotNull [] ray, @NotNull RayTraceWorker rayTraceWorker);

    @NotNull
    Vector4f @NotNull [] rayTrace(@NotNull Vector3f @NotNull [] rayPositions,
                                  @NotNull Vector3f @NotNull [] rayDirections,
                                  @NotNull RayTraceWorker rayTraceWorker);

    @NotNull
    default Vector4f getDefaultColor() {
        return ColorUtil.TRANSPARENT_VECTOR_FLOAT;
    }
}
