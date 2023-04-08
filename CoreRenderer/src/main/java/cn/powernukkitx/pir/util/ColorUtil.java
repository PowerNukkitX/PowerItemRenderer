package cn.powernukkitx.pir.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector4f;
import org.joml.Vector4i;

import java.awt.*;

public final class ColorUtil {
    public static final Vector4f TRANSPARENT_VECTOR_FLOAT = new Vector4f(0, 0, 0, 0);

    private ColorUtil() {
        throw new UnsupportedOperationException();
    }

    @Contract(pure = true)
    public static int toInt(int r, int g, int b) {
        return ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
    }

    @Contract(pure = true)
    public static int toInt(int r, int g, int b, int a) {
        return ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
    }

    @Contract(pure = true)
    public static int toInt(float r, float g, float b) {
        return toInt((int) (r * 255 + 0.5), (int) (g * 255 + 0.5), (int) (b * 255 + 0.5));
    }

    @Contract(pure = true)
    public static int toInt(float r, float g, float b, float a) {
        return toInt((int) (r * 255 + 0.5), (int) (g * 255 + 0.5), (int) (b * 255 + 0.5), (int) (a * 255 + 0.5));
    }

    @Contract(pure = true)
    public static int toInt(@NotNull Vector4i colorVector) {
        return toInt(colorVector.x, colorVector.y, colorVector.z, colorVector.w);
    }

    @Contract(pure = true)
    public static int toInt(@NotNull Vector4f colorVector) {
        return toInt(colorVector.x, colorVector.y, colorVector.z, colorVector.w);
    }

    @Contract(pure = true)
    public static int toInt(@NotNull Color color) {
        return toInt(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull Color toColor(int color) {
        return new Color(color, true);
    }

    @Contract(pure = true, value = "_, _ -> param2")
    public static @NotNull Vector4f toColorVectorFloat(int color, @NotNull Vector4f ref) {
        ref.x = ((color >> 16) & 0xFF) / 255f;
        ref.y = ((color >> 8) & 0xFF) / 255f;
        ref.z = (color & 0xFF) / 255f;
        ref.w = ((color >> 24) & 0xFF) / 255f;
        return ref;
    }

    @Contract(pure = true, value = "_, _ -> param2")
    public static @NotNull Vector4i toColorVectorInt(int color, @NotNull Vector4i ref) {
        ref.x = (color >> 16) & 0xFF;
        ref.y = (color >> 8) & 0xFF;
        ref.z = color & 0xFF;
        ref.w = (color >> 24) & 0xFF;
        return ref;
    }

    @Contract(pure = true, value = "_ -> new")
    public static @NotNull Vector4f toColorVectorFloat(int color) {
        return toColorVectorFloat(color, new Vector4f());
    }

    @Contract(pure = true, value = "_ -> new")
    public static @NotNull Vector4i toColorVectorInt(int color) {
        return toColorVectorInt(color, new Vector4i());
    }

    public static void combineRefractionColor(@NotNull Vector4f outputColor, @NotNull Vector4f refractionColor) {
        // if outputColor is transparent, return refractionColor
        // if outputColor is not transparent at all, return color
        // if outputColor is partially transparent, return a mix of outputColor and refractionColor, outputColor is the major
        // if outputColor is partially transparent but refractionColor is not transparent at all, the alpha of the mix of two colors should be 1
        if (outputColor.w == 0) {
            outputColor.set(refractionColor);
        } else if (outputColor.w < 1) {
            if (refractionColor.w == 1) {
                outputColor.w = 1;
            }
            outputColor.x = outputColor.x * outputColor.w + refractionColor.x * refractionColor.w * (1 - outputColor.w);
            outputColor.y = outputColor.y * outputColor.w + refractionColor.y * refractionColor.w * (1 - outputColor.w);
            outputColor.z = outputColor.z * outputColor.w + refractionColor.z * refractionColor.w * (1 - outputColor.w);
            outputColor.w = outputColor.w + refractionColor.w * (1 - outputColor.w);
        }
    }

    public static @NotNull Vector4f toColorVectorFloat(@NotNull Color color, @NotNull Vector4f ref) {
        ref.x = color.getRed() / 255f;
        ref.y = color.getGreen() / 255f;
        ref.z = color.getBlue() / 255f;
        ref.w = color.getAlpha() / 255f;
        return ref;
    }

    public static @NotNull Vector4f toColorVectorFloat(@NotNull Color color) {
        return toColorVectorFloat(color, new Vector4f());
    }

    public static @NotNull Vector4i toColorVectorInt(@NotNull Color color, @NotNull Vector4i ref) {
        ref.x = color.getRed();
        ref.y = color.getGreen();
        ref.z = color.getBlue();
        ref.w = color.getAlpha();
        return ref;
    }

    public static @NotNull Vector4i toColorVectorInt(@NotNull Color color) {
        return toColorVectorInt(color, new Vector4i());
    }

    public static @NotNull Vector4f toColorVectorFloat(@NotNull Vector4i colorVector, @NotNull Vector4f ref) {
        ref.x = colorVector.x / 255f;
        ref.y = colorVector.y / 255f;
        ref.z = colorVector.z / 255f;
        ref.w = colorVector.w / 255f;
        return ref;
    }

    public static @NotNull Vector4f toColorVectorFloat(@NotNull Vector4i colorVector) {
        return toColorVectorFloat(colorVector, new Vector4f());
    }

    public static @NotNull Vector4i toColorVectorInt(@NotNull Vector4f colorVector, @NotNull Vector4i ref) {
        ref.x = (int) (colorVector.x * 255);
        ref.y = (int) (colorVector.y * 255);
        ref.z = (int) (colorVector.z * 255);
        ref.w = (int) (colorVector.w * 255);
        return ref;
    }

    public static @NotNull Vector4i toColorVectorInt(@NotNull Vector4f colorVector) {
        return toColorVectorInt(colorVector, new Vector4i());
    }

    public static @NotNull Color toColor(@NotNull Vector4i colorVector) {
        return new Color(colorVector.x, colorVector.y, colorVector.z, colorVector.w);
    }

    public static @NotNull Color toColor(@NotNull Vector4f colorVector) {
        return new Color(colorVector.x, colorVector.y, colorVector.z, colorVector.w);
    }

    public static void setLightIntensityInHSB(@NotNull Vector4f colorVectorInRGBA, float lightIntensity) {
        setLightIntensityInHSB(colorVectorInRGBA, lightIntensity, new float[3]);
    }

    public static void setLightIntensityInHSB(@NotNull Vector4f colorVectorInRGBA, float lightIntensity, float[] hsbTmpBuffer) {
        float[] hsb = Color.RGBtoHSB((int) (colorVectorInRGBA.x * 255 + 0.5f), (int) (colorVectorInRGBA.y * 255 + 0.5f), (int) (colorVectorInRGBA.z * 255 + 0.5f), hsbTmpBuffer);
        hsb[2] = Math.min(1, hsb[2] * lightIntensity * 2);
        int rgb = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
        colorVectorInRGBA.x = ((rgb >> 16) & 0xFF) / 255f;
        colorVectorInRGBA.y = ((rgb >> 8) & 0xFF) / 255f;
        colorVectorInRGBA.z = (rgb & 0xFF) / 255f;
    }
}
