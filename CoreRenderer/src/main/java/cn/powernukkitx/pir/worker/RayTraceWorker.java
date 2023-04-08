package cn.powernukkitx.pir.worker;

import cn.powernukkitx.pir.object.Ray;
import cn.powernukkitx.pir.object.geometry.Shape;
import cn.powernukkitx.pir.object.geometry.Triangle;
import cn.powernukkitx.pir.object.light.Light;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.joml.Vector4f;

public interface RayTraceWorker {
    @NotNull
    Vector4f @NotNull [] rayTrace(@NotNull Ray @NotNull [] rays,
                                  @NotNull Triangle @NotNull [] triangles,
                                  @NotNull Shape @NotNull [] otherShapes,
                                  @NotNull Light @NotNull [] lights,
                                  @NotNull Vector4f defaultColor);

    @NotNull
    Vector4f @NotNull [] rayTrace(@NotNull Vector3f @NotNull [] rayPositions,
                                  @NotNull Vector3f @NotNull [] rayDirections,
                                  @NotNull Triangle @NotNull [] triangles,
                                  @NotNull Shape @NotNull [] otherShapes,
                                  @NotNull Light @NotNull [] lights,
                                  @NotNull Vector4f defaultColor);
}
