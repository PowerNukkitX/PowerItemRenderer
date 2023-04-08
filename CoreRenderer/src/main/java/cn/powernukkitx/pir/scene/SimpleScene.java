package cn.powernukkitx.pir.scene;

import cn.powernukkitx.pir.object.Ray;
import cn.powernukkitx.pir.object.SceneObject;
import cn.powernukkitx.pir.worker.RayTraceWorker;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.HashSet;

public class SimpleScene extends HashSet<SceneObject> implements Scene {
    @Override
    public @NotNull Vector4f @NotNull [] rayTrace(@NotNull Ray @NotNull [] ray, @NotNull RayTraceWorker rayTraceWorker) {
        throw new UnsupportedOperationException("You cannot ray trace a scene that is not frozen!");
    }

    @Override
    public @NotNull Vector4f @NotNull [] rayTrace(@NotNull Vector3f @NotNull [] rayPositions, @NotNull Vector3f @NotNull [] rayDirections, @NotNull RayTraceWorker rayTraceWorker) {
        throw new UnsupportedOperationException("You cannot ray trace a scene that is not frozen!");
    }
}
