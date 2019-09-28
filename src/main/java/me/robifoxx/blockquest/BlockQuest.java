package me.robifoxx.blockquest;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class BlockQuest extends JavaPlugin {
    public Config data;
    public void onEnable() {
        String fileName = this.getDescription().getName();
        if(!(new File("plugins/" + fileName + "/config.yml").exists())) {
            getConfig().options().copyDefaults(true);
            saveConfig();
        }

        {
            Config c = new Config("plugins/" + fileName, "data.yml", this);
            c.create();
            c.setDefault("data.yml");
            if(!c.exists()) {
                c.getConfig().options().copyDefaults(true);
                c.saveConfig();
            }
            data = c;
        }
    }
}
