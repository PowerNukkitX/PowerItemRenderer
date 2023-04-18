package cn.powernukkitx.pir;

import cn.nukkit.plugin.PluginBase;

public class PNXPluginMain extends PluginBase {
    public static PNXPluginMain MAIN;

    @Override
    public void onEnable() {
        MAIN = this;
    }
}
