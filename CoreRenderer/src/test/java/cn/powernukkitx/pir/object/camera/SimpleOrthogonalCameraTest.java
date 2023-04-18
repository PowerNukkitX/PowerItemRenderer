package cn.powernukkitx.pir.object.camera;

import cn.powernukkitx.pir.object.geometry.Cube;
import cn.powernukkitx.pir.object.geometry.Cuboid;
import cn.powernukkitx.pir.object.geometry.Triangle;
import cn.powernukkitx.pir.object.light.AmbientLight;
import cn.powernukkitx.pir.object.light.DirectionalLight;
import cn.powernukkitx.pir.scene.Scene;
import cn.powernukkitx.pir.scene.SimpleScene;
import cn.powernukkitx.pir.worker.RayTraceWorker;
import cn.powernukkitx.pir.worker.SimpleRayTraceWorker;
import org.joml.Vector3f;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class SimpleOrthogonalCameraTest {
    Scene scene1WithoutTexture;
    Scene scene1WithTexture;
    Scene scene2WithTexture;
    Scene scene3WithTexture;
    RayTraceWorker worker;

    @BeforeEach
    public void setup() throws IOException {
        if (scene1WithoutTexture == null) {
            var tmpScene = new SimpleScene();
            tmpScene.add(new Triangle(
                    0, 0, 0,
                    0, 1, 0,
                    1, 0, 0
            ));
            tmpScene.add(new Triangle(
                    0, 0, 2,
                    0, 1, 2,
                    1, 0, 2
            ));
            tmpScene.add(new AmbientLight(0.5f));
            scene1WithoutTexture = tmpScene.freeze();
        }
        if (scene1WithTexture == null) {
            var tmpScene = new SimpleScene();
            tmpScene.add(new Triangle(
                    0, 0, 0,
                    1, 0, 0,
                    0, 1, 0,
                    ImageIO.read(Objects.requireNonNull(SimpleOrthogonalCameraTest.class
                            .getResourceAsStream("/textures/leaves.png")))));
            tmpScene.add(new Triangle(
                    0, 0, 1,
                    1.414f, 0, 1,
                    0, 1.414f, 1,
                    ImageIO.read(Objects.requireNonNull(SimpleOrthogonalCameraTest.class
                            .getResourceAsStream("/textures/leaves.png")))));
            tmpScene.add(new AmbientLight(0.3f));
            scene1WithTexture = tmpScene.freeze();
        }
        if (scene2WithTexture == null) {
            var tmpScene = new SimpleScene();
            tmpScene.add(new Cube(0, 0, 0, 1f, ImageIO.read(Objects.requireNonNull(SimpleOrthogonalCameraTest.class
                    .getResourceAsStream("/textures/reactor_core.png")))));
            tmpScene.add(new AmbientLight(0.1f));
            tmpScene.add(new DirectionalLight(-1.5f, -1f, -2.25f, 0.63f));
            scene2WithTexture = tmpScene.freeze();
        }
        if (scene3WithTexture == null) {
            var tmpScene = new SimpleScene();
            tmpScene.add(new Cuboid(0, 0, 0, 1.2f, 1f, 0.4f, ImageIO.read(Objects.requireNonNull(SimpleOrthogonalCameraTest.class
                    .getResourceAsStream("/textures/leaves.png"))), new Cuboid.UVDetail[]{
                    new Cuboid.UVDetail(0, 0, 16, 16),
                    new Cuboid.UVDetail(0, 0, 16, 16),
                    new Cuboid.UVDetail(0, 0, 16, 16),
                    new Cuboid.UVDetail(0, 0, 16, 16),
                    new Cuboid.UVDetail(0, 0, 16, 16),
                    new Cuboid.UVDetail(0, 0, 16, 16)
            }));
            tmpScene.add(new AmbientLight(0.1f));
            tmpScene.add(new DirectionalLight(-1.5f, -1f, -2.25f, 0.63f));
            scene3WithTexture = tmpScene.freeze();
        }
        if (worker == null) {
            worker = new SimpleRayTraceWorker();
        }
    }

    @Test
    public void test1() throws IOException {
        var camera = new SimpleOrthogonalCamera(new Vector3f(0.5f, 0.5f, 3),
                new Vector3f(0f, 0f, -1),
                128, 128, 2f, 2f,
                new Vector3f(-1f, 0, 0));
        var result = camera.render(scene1WithoutTexture, worker);
        var file = new File("target", "scene1_no_texture.png");
        if (file.exists()) {
            file.delete();
        }
        ImageIO.write((RenderedImage) result, "png", file);
    }

    @Test
    public void test2() throws IOException {
        var camera = new SimpleOrthogonalCamera(new Vector3f(4f, 4f, 4f * (float) Math.sqrt(2)),
                new Vector3f(-1f, -1f, (float) (-Math.sqrt(2))),
                128, 128, 2f, 2f,
                new Vector3f(-1f, -1f, (float) (Math.sqrt(2))));
        var result = camera.render(scene1WithTexture, worker);
        var file = new File("target", "scene1_texture.png");
        if (file.exists()) {
            file.delete();
        }
        ImageIO.write((RenderedImage) result, "png", file);
    }

    @Test
    public void test3() throws IOException {
        var fuzzyUp = new Vector3f(0, 0, 1);
        var direction = new Vector3f(-1f, -1f, (float) (-Math.sqrt(2)));
        var camera = new SimpleOrthogonalCamera(new Vector3f(4f, 4f, 4f * (float) Math.sqrt(2)),
                direction,
                1024, 1024, 2f, 2f,
                direction.cross(fuzzyUp.cross(direction), new Vector3f()).normalize()
        );
        var result = camera.render(scene2WithTexture, worker);
        var file = new File("target", "scene2_texture_1.png");
        if (file.exists()) {
            file.delete();
        }
        ImageIO.write((RenderedImage) result, "png", file);
    }

    @Test
    public void test4() throws IOException {
        var fuzzyUp = new Vector3f(0, 0, 1);
        var direction = new Vector3f(-1f, -1f, -1f / 1.27f).normalize();
        var camera = new SimpleOrthogonalCamera(new Vector3f(4.01f, 4f, 4f / 1.27f),
                direction,
                128, 128, 1.62f, 1.61f,
                direction.cross(fuzzyUp.cross(direction), new Vector3f()).normalize()
        );
        var result = camera.render(scene2WithTexture, worker);
        var file = new File("target", "scene2_texture_2.png");
        if (file.exists()) {
            file.delete();
        }
        ImageIO.write((RenderedImage) result, "png", file);
    }

    @Test
    public void test5() throws IOException {
        var fuzzyUp = new Vector3f(0, 0, 1);
        var direction = new Vector3f(-1f, -1f, -1f / 1.27f).normalize();
        var camera = new SimpleOrthogonalCamera(new Vector3f(4.01f, 4f, 4f / 1.27f),
                direction,
                512, 512, 1.62f, 1.61f,
                direction.cross(fuzzyUp.cross(direction), new Vector3f()).normalize()
        );
        var result = camera.render(scene3WithTexture, worker);
        var file = new File("target", "scene3_texture_1.png");
        if (file.exists()) {
            file.delete();
        }
        ImageIO.write((RenderedImage) result, "png", file);
    }


    @Test
    public void test4Performance() {
        var fuzzyUp = new Vector3f(0, 0, 1);
        var direction = new Vector3f(-1f, -1f, -1f / 1.27f).normalize();
        var camera = new SimpleOrthogonalCamera(new Vector3f(4.01f, 4f, 4f / 1.27f),
                direction,
                512, 512, 1.62f, 1.61f,
                direction.cross(fuzzyUp.cross(direction), new Vector3f()).normalize()
        );
        var globalStart = System.currentTimeMillis();
        for (var i = 0; i < 1000; i++) {
            var start = System.currentTimeMillis();
            var result = camera.render(scene2WithTexture, worker);
            System.out.println(System.currentTimeMillis() - start);
        }
        System.out.println("Global time: " + (System.currentTimeMillis() - globalStart));
    }
}
