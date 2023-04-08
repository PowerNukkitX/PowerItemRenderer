package cn.powernukkitx.pir.object;

import org.joml.Vector3f;

public record Ray(Vector3f pos, Vector3f direction) {
    public Ray(float x, float y, float z, float dx, float dy, float dz) {
        this(new Vector3f(x, y, z), new Vector3f(dx, dy, dz));
    }
}
