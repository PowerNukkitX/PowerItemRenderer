package cn.powernukkitx.pir.bedrock;

import cn.powernukkitx.pir.object.geometry.Cuboid;
import org.jetbrains.annotations.NotNull;

public record JsonModelUVSet(
        @NotNull Cuboid.UVDetail north,
        @NotNull Cuboid.UVDetail east,
        @NotNull Cuboid.UVDetail south,
        @NotNull Cuboid.UVDetail west,
        @NotNull Cuboid.UVDetail up,
        @NotNull Cuboid.UVDetail down
) {
    public @NotNull Cuboid.UVDetail @NotNull [] toArray() {
        // Down Right Back Left Front Up
        // 正确的右手系逻辑顺序：down, north, west, south, east, up
        // 但是sb mojang非得tm用左手系，我们不得不做出这种神奇的事情
        return new Cuboid.UVDetail[]{down, north, east, south, west, up};
    }
}
