package cn.powernukkitx.pir.bedrock.render;

import cn.powernukkitx.pir.PNXPluginMain;
import com.google.gson.JsonElement;

public final class TextureManifest {
    public String up;
    public String down;
    public String east;
    public String west;
    public String north;
    public String south;
    public String any;

    public String up() {
        return up == null ? any : up;
    }

    public String down() {
        return down == null ? any : down;
    }

    public String east() {
        return east == null ? any : east;
    }

    public String west() {
        return west == null ? any : west;
    }

    public String north() {
        return north == null ? any : north;
    }

    public String south() {
        return south == null ? any : south;
    }

    public String any() {
        return any;
    }

    public static TextureManifest fromJson(JsonElement json) {
        if (json.isJsonPrimitive()) {
            var manifest = new TextureManifest();
            manifest.any = json.getAsString();
            return manifest;
        } else if (json.isJsonArray()) {
            var manifest = new TextureManifest();
            var array = json.getAsJsonArray();
            if (array.size() == 1) {
                manifest.any = array.get(0).getAsString();
            } else {
                if (array.size() > 0) manifest.up = array.get(0).getAsString();
                if (array.size() > 1) manifest.down = array.get(1).getAsString();
                if (array.size() > 2) manifest.north = array.get(2).getAsString();
                if (array.size() > 3) manifest.south = array.get(3).getAsString();
                if (array.size() > 4) manifest.west = array.get(4).getAsString();
                if (array.size() > 5) manifest.east = array.get(5).getAsString();
            }
            return manifest;
        } else if (json.isJsonObject()) {
            var manifest = new TextureManifest();
            var obj = json.getAsJsonObject();
            if (obj.has("up")) manifest.up = obj.get("up").getAsString();
            if (obj.has("down")) manifest.down = obj.get("down").getAsString();
            if (obj.has("east")) manifest.east = obj.get("east").getAsString();
            if (obj.has("west")) manifest.west = obj.get("west").getAsString();
            if (obj.has("north")) manifest.north = obj.get("north").getAsString();
            if (obj.has("south")) manifest.south = obj.get("south").getAsString();
            if (obj.has("any")) manifest.any = obj.get("any").getAsString();
            return manifest;
        }
        throw new IllegalArgumentException("Invalid texture manifest: " + PNXPluginMain.GSON.toJson(json));
    }
}
