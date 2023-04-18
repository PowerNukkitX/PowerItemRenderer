package cn.powernukkitx.pir.bedrock;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public record JsonModelCuboid(
        @NotNull Vector3f origin,
        @NotNull Vector3f size,
        @NotNull JsonModelUVSet uvSet
) {
}
