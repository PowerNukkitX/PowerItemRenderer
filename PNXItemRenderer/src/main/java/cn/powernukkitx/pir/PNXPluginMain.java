package cn.powernukkitx.pir;

import cn.nukkit.plugin.PluginBase;
import cn.powernukkitx.pir.bedrock.render.RenderCommand;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class PNXPluginMain extends PluginBase {
    public static PNXPluginMain MAIN;

    public static final Gson GSON = new GsonBuilder().setLenient().setPrettyPrinting().create();

    @Override
    public void onEnable() {
        MAIN = this;
        this.getServer().getCommandMap().register("pir", new RenderCommand("render"));
    }
}
