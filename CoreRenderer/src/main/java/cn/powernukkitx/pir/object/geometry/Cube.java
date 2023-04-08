package cn.powernukkitx.pir.object.geometry;

import cn.powernukkitx.pir.util.ImageUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.awt.image.BufferedImage;

public record Cube(@NotNull Vector3f center, float length,
                   int[][] textures, // the first dimension is the face, the second dimension is the texture
                   Vector2i[] textureSizes) implements Polyhedral {
    public Cube(@NotNull Vector3f center, float length) {
        this(center, length, new int[6][], new Vector2i[6]);
    }

    public Cube(@NotNull Vector3f center, float length, @Nullable BufferedImage texture) {
        this(center, length, new int[][]{ImageUtil.getPixels(texture), ImageUtil.getPixels(texture), ImageUtil.getPixels(texture),
                        ImageUtil.getPixels(texture), ImageUtil.getPixels(texture), ImageUtil.getPixels(texture)},
                new Vector2i[]{ImageUtil.getImageSize(texture), ImageUtil.getImageSize(texture), ImageUtil.getImageSize(texture),
                        ImageUtil.getImageSize(texture), ImageUtil.getImageSize(texture), ImageUtil.getImageSize(texture)});
    }

    public Cube(@NotNull Vector3f center, float length, @Nullable BufferedImage @NotNull [] textures) {
        this(center, length, new int[][]{ImageUtil.getPixels(textures[0]), ImageUtil.getPixels(textures[1]), ImageUtil.getPixels(textures[2]),
                        ImageUtil.getPixels(textures[3]), ImageUtil.getPixels(textures[4]), ImageUtil.getPixels(textures[5])},
                new Vector2i[]{ImageUtil.getImageSize(textures[0]), ImageUtil.getImageSize(textures[1]), ImageUtil.getImageSize(textures[2]),
                        ImageUtil.getImageSize(textures[3]), ImageUtil.getImageSize(textures[4]), ImageUtil.getImageSize(textures[5])});
    }

    public Cube(float x, float y, float z, float length) {
        this(new Vector3f(x, y, z), length);
    }

    public Cube(float x, float y, float z, float length, @Nullable BufferedImage texture) {
        this(new Vector3f(x, y, z), length, texture);
    }

    public Cube(float x, float y, float z, float length, @Nullable BufferedImage @NotNull [] textures) {
        this(new Vector3f(x, y, z), length, textures);
    }

    /**
     * @return Triangles of the cube. Order: (Down Right Back Left Front Up) x (Left-down Right-top)
     */
    @Override
    public Triangle[] triangles() {
        float halfLength = length / 2;
        Vector3f[] vertices = new Vector3f[]{
                new Vector3f(center.x - halfLength, center.y - halfLength, center.z - halfLength),
                new Vector3f(center.x + halfLength, center.y - halfLength, center.z - halfLength),
                new Vector3f(center.x + halfLength, center.y + halfLength, center.z - halfLength),
                new Vector3f(center.x - halfLength, center.y + halfLength, center.z - halfLength),
                new Vector3f(center.x - halfLength, center.y - halfLength, center.z + halfLength),
                new Vector3f(center.x + halfLength, center.y - halfLength, center.z + halfLength),
                new Vector3f(center.x + halfLength, center.y + halfLength, center.z + halfLength),
                new Vector3f(center.x - halfLength, center.y + halfLength, center.z + halfLength)
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
}
