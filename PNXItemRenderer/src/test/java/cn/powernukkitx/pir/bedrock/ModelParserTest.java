package cn.powernukkitx.pir.bedrock;

import cn.powernukkitx.pir.object.camera.SimpleOrthogonalCamera;
import cn.powernukkitx.pir.object.light.AmbientLight;
import cn.powernukkitx.pir.object.light.DirectionalLight;
import cn.powernukkitx.pir.scene.SimpleScene;
import cn.powernukkitx.pir.worker.SimpleRayTraceWorker;
import org.joml.Vector3f;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class ModelParserTest {
    @Test
    public void testAnvil1() throws IOException {
        var fuzzyUp = new Vector3f(0, 0, 1);
        var direction = new Vector3f(-1f, -1f, -1f / 1.27f).normalize();
        var camera = new SimpleOrthogonalCamera(new Vector3f(4.01f, 4f, 4f / 1.27f),
                direction,
                128, 128, 1.62f, 1.61f,
                direction.cross(fuzzyUp.cross(direction), new Vector3f()).normalize()
        );
        var tmpScene = new SimpleScene();
        tmpScene.add(new AmbientLight(0.1f));
        tmpScene.add(new DirectionalLight(-1.5f, -1f, -2.25f, 0.63f));
        var jsonModels = ModelParser.parse(Objects.requireNonNull(
                ModelParserTest.class.getResourceAsStream("/anvil.geo.json")));
        for (var each : jsonModels) {
            each.applyToScene(tmpScene, ImageIO.read(Objects.requireNonNull(
                    ModelParserTest.class.getResourceAsStream("/annealed_copper_anvil.png"))));
        }
        var scene = tmpScene.freeze();
        var worker = new SimpleRayTraceWorker(true);
        var result = camera.render(scene, worker);
        var file = new File("target", "anvil1.png");
        if (file.exists()) {
            file.delete();
        }
        ImageIO.write((RenderedImage) result, "png", file);
    }

    @Test
    public void testAnvil2() throws IOException {
        var fuzzyUp = new Vector3f(0, 0, 1);
        var direction = new Vector3f(1f, 1f, -1f / 1.27f).normalize();
        var camera = new SimpleOrthogonalCamera(new Vector3f(-4.01f, -4f, 4f / 1.27f),
                direction,
                512, 512, 1.62f * 2, 1.61f * 2,
                direction.cross(fuzzyUp.cross(direction), new Vector3f()).normalize()
        );
        var tmpScene = new SimpleScene();
        tmpScene.add(new AmbientLight(0.1f));
        tmpScene.add(new DirectionalLight(-1.5f, -1f, -2.25f, 0.63f));
        var jsonModels = ModelParser.parse(Objects.requireNonNull(
                ModelParserTest.class.getResourceAsStream("/anvil.geo.json")));
        for (var each : jsonModels) {
            each.applyToScene(tmpScene, ImageIO.read(Objects.requireNonNull(
                    ModelParserTest.class.getResourceAsStream("/annealed_copper_anvil.png"))));
        }
        var scene = tmpScene.freeze();
        var worker = new SimpleRayTraceWorker(true);
        var result = camera.render(scene, worker);
        var file = new File("target", "anvil2.png");
        if (file.exists()) {
            file.delete();
        }
        ImageIO.write((RenderedImage) result, "png", file);
    }

    @Test
    public void testAnvil1Performance() throws IOException {
        var jsonModels = ModelParser.parse(Objects.requireNonNull(
                ModelParserTest.class.getResourceAsStream("/anvil.geo.json")));
        var anvilTexture = ImageIO.read(Objects.requireNonNull(
                ModelParserTest.class.getResourceAsStream("/annealed_copper_anvil.png")));
        for (var i = 0; i < 100; i++) {
            var start = System.currentTimeMillis();
            var fuzzyUp = new Vector3f(0, 0, 1);
            var direction = new Vector3f(-1f, -1f, -1f / 1.27f).normalize();
            var camera = new SimpleOrthogonalCamera(new Vector3f(4.01f, 4f, 4f / 1.27f),
                    direction,
                    128, 128, 1.62f, 1.61f,
                    direction.cross(fuzzyUp.cross(direction), new Vector3f()).normalize()
            );
            var tmpScene = new SimpleScene();
            tmpScene.add(new AmbientLight(0.1f));
            tmpScene.add(new DirectionalLight(-1.5f, -1f, -2.25f, 0.63f));
            for (var each : jsonModels) {
                each.applyToScene(tmpScene, anvilTexture);
            }
            var scene = tmpScene.freeze();
            var worker = new SimpleRayTraceWorker(true);
            var result = camera.render(scene, worker);
            System.out.println("Rendered in " + (System.currentTimeMillis() - start) + "ms");
        }
    }
}
