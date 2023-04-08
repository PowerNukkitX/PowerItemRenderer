package cn.powernukkitx.pir.object.light;

import cn.powernukkitx.pir.util.MathUtil;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public record AmbientLight(float intensity) implements Light {
    @Override
    public @NotNull LightType lightType() {
        return LightType.AMBIENT;
    }

    @Override
    public void calculateIntensity(float @NotNull [] currentIntensity, @NotNull Vector3f @NotNull [] normalVectors) {
        for (int i = 0; i < currentIntensity.length; i++) {
            currentIntensity[i] = MathUtil.clamp(currentIntensity[i], intensity, 1);
        }
    }
}
