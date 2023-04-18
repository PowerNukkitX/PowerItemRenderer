package cn.powernukkitx.pir.object.geometry;

import cn.powernukkitx.pir.util.ImageUtil;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.awt.image.BufferedImage;

public record Cuboid(
        @NotNull Vector3f center, float xLength, float yLength, float zLength,
        int[][] textures,
        // the first dimension is the face, the second dimension is the texture, Down Right Back Left Front Up
        Vector2i[] textureSizes
) implements Polyhedral {
    @Override
    public Triangle[] triangles() {
        float halfXLength = xLength / 2;
        float halfYLength = yLength / 2;
        float halfZLength = zLength / 2;
        var vertices = new Vector3f[]{
                new Vector3f(center.x - halfXLength, center.y - halfYLength, center.z - halfZLength),
                new Vector3f(center.x + halfXLength, center.y - halfYLength, center.z - halfZLength),
                new Vector3f(center.x + halfXLength, center.y + halfYLength, center.z - halfZLength),
                new Vector3f(center.x - halfXLength, center.y + halfYLength, center.z - halfZLength),
                new Vector3f(center.x - halfXLength, center.y - halfYLength, center.z + halfZLength),
                new Vector3f(center.x + halfXLength, center.y - halfYLength, center.z + halfZLength),
                new Vector3f(center.x + halfXLength, center.y + halfYLength, center.z + halfZLength),
                new Vector3f(center.x - halfXLength, center.y + halfYLength, center.z + halfZLength)
        };
        return new Triangle[]{
                new Triangle(vertices[0], vertices[3], vertices[1], ImageUtil.centralSymmetry(textures[0], textureSizes[0]), textureSizes[0]),
                new Triangle(vertices[2], vertices[1], vertices[3], textures[0], textureSizes[0]),
                new Triangle(vertices[2], vertices[3], vertices[6], ImageUtil.centralSymmetry(textures[1], textureSizes[1]), textureSizes[1]),
                new Triangle(vertices[7], vertices[6], vertices[3], textures[1], textureSizes[1]),
                new Triangle(vertices[3], vertices[0], vertices[7], ImageUtil.centralSymmetry(textures[2], textureSizes[2]), textureSizes[2]),
                new Triangle(vertices[4], vertices[7], vertices[0], textures[2], textureSizes[2]),
                new Triangle(vertices[0], vertices[1], vertices[4], ImageUtil.centralSymmetry(textures[3], textureSizes[3]), textureSizes[3]),
                new Triangle(vertices[5], vertices[4], vertices[1], textures[3], textureSizes[3]),
                new Triangle(vertices[1], vertices[2], vertices[5], ImageUtil.centralSymmetry(textures[4], textureSizes[4]), textureSizes[4]),
                new Triangle(vertices[6], vertices[5], vertices[2], textures[4], textureSizes[4]),
                new Triangle(vertices[4], vertices[5], vertices[7], ImageUtil.centralSymmetry(textures[5], textureSizes[5]), textureSizes[5]),
                new Triangle(vertices[6], vertices[7], vertices[5], textures[5], textureSizes[5])
        };
    }

    public record UVDetail(int x, int y, int w, int h) {
    }

    public Cuboid(@NotNull Vector3f center, float xLength, float yLength, float zLength) {
        this(center, xLength, yLength, zLength, new int[6][], new Vector2i[6]);
    }

    public Cuboid(float x, float y, float z, float xLength, float yLength, float zLength, @NotNull BufferedImage texture,
                  @NotNull UVDetail @NotNull [] uvDetails) {
        this(new Vector3f(x, y, z), xLength, yLength, zLength, new int[][]{
                ImageUtil.slice(texture, uvDetails[0].x, uvDetails[0].y, uvDetails[0].w, uvDetails[0].h),
                ImageUtil.slice(texture, uvDetails[1].x, uvDetails[1].y, uvDetails[1].w, uvDetails[1].h),
                ImageUtil.slice(texture, uvDetails[2].x, uvDetails[2].y, uvDetails[2].w, uvDetails[2].h),
                ImageUtil.slice(texture, uvDetails[3].x, uvDetails[3].y, uvDetails[3].w, uvDetails[3].h),
                ImageUtil.slice(texture, uvDetails[4].x, uvDetails[4].y, uvDetails[4].w, uvDetails[4].h),
                ImageUtil.slice(texture, uvDetails[5].x, uvDetails[5].y, uvDetails[5].w, uvDetails[5].h)
        }, new Vector2i[]{
                new Vector2i(uvDetails[0].w, uvDetails[0].h),
                new Vector2i(uvDetails[1].w, uvDetails[1].h),
                new Vector2i(uvDetails[2].w, uvDetails[2].h),
                new Vector2i(uvDetails[3].w, uvDetails[3].h),
                new Vector2i(uvDetails[4].w, uvDetails[4].h),
                new Vector2i(uvDetails[5].w, uvDetails[5].h)
        });
    }
}
