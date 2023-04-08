package cn.powernukkitx.pir.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

import java.awt.image.BufferedImage;
import java.util.concurrent.ConcurrentHashMap;

public final class ImageUtil {
    private ImageUtil() {
        throw new UnsupportedOperationException();
    }

    public static final Vector2i FALLBACK_TEXTURE_SIZE = new Vector2i(1, 1);
    public static final int[] FALLBACK_TEXTURE = new int[]{0x80808080};

    private static final ConcurrentHashMap<BufferedImage, int[]> PIXEL_CACHE = new ConcurrentHashMap<>();

    @Contract("null -> !null; !null -> !null")
    public static int[] getPixels(@Nullable BufferedImage image) {
        if (image == null) return FALLBACK_TEXTURE;
        return PIXEL_CACHE.computeIfAbsent(image, img -> {
            int width = img.getWidth();
            int height = img.getHeight();
            int[] pixels = new int[width * height];
            img.getRGB(0, 0, width, height, pixels, 0, width);
            return pixels;
        });
    }

    private static final ConcurrentHashMap<int[], int[]> TRANSPOSE_CACHE = new ConcurrentHashMap<>();

    @Contract(pure = true)
    public static int @NotNull [] transpose(int[] pixels, int width, int height) {
        return TRANSPOSE_CACHE.computeIfAbsent(pixels, p -> {
            int[] result = new int[pixels.length];
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    result[i * height + j] = pixels[j * width + i];
                }
            }
            return result;
        });
    }

    @Contract(pure = true)
    public static int @NotNull [] transpose(int[] pixels, @NotNull Vector2i size) {
        return transpose(pixels, size.x, size.y);
    }

    @Contract("null ->!null; !null -> !null")
    public static Vector2i getImageSize(@Nullable BufferedImage image) {
        if (image == null) return FALLBACK_TEXTURE_SIZE;
        return new Vector2i(image.getWidth(), image.getHeight());
    }

    private static final ConcurrentHashMap<int[], int[]> CENTRAL_SYMMETRY_CACHE = new ConcurrentHashMap<>();

    @Contract(pure = true)
    public static int @NotNull [] centralSymmetry(int[] pixels, int width, int height) {
        return CENTRAL_SYMMETRY_CACHE.computeIfAbsent(pixels, p -> {
            int[] result = new int[pixels.length];
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    result[i * height + j] = pixels[(width - i - 1) * height + (height - j - 1)];
                }
            }
            return result;
        });
    }

    @Contract(pure = true)
    public static int @NotNull [] centralSymmetry(int[] pixels, @NotNull Vector2i size) {
        return centralSymmetry(pixels, size.x, size.y);
    }
}
