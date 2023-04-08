package cn.powernukkitx.pir.object.light;

import cn.powernukkitx.pir.object.ObjectType;
import cn.powernukkitx.pir.object.SceneObject;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public interface Light extends SceneObject {
    @NotNull LightType lightType();

    @Override
    @NotNull default ObjectType type() {
        return ObjectType.LIGHT;
    }

    void calculateIntensity(float @NotNull [] currentIntensity, @NotNull Vector3f @NotNull [] normalVectors);
}
