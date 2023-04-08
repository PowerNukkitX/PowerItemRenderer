package cn.powernukkitx.pir.object.light;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class DirectionalLight extends Vector4f implements Light {
    public DirectionalLight(float x, float y, float z, float intensity) {
        super(x, y, z, intensity);
    }

    public DirectionalLight() {
    }

    @Override
    public @NotNull LightType lightType() {
        return LightType.DIRECTIONAL;
    }

    @Override
    public void calculateIntensity(float @NotNull [] currentIntensity, @NotNull Vector3f @NotNull [] normalVectors) {
        var x = -this.x;
        var y = -this.y;
        var z = -this.z;
        // normalize x,y,z
        var length = (float) Math.sqrt(x * x + y * y + z * z);
        x /= length;
        y /= length;
        z /= length;
        // calculate intensity
        for (int i = 0, len1 = currentIntensity.length; i < len1; i++) {
            currentIntensity[i] = Math.min(1, Math.max(currentIntensity[i], Math.max(0, w * normalVectors[i].dot(x, y, z))));
        }
    }
}
