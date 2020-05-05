package com.sagaciousdevelopment.PlaceholderSign;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Scanner;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;


public class FileUtil {
	
	//Credits: drew6017
	
	public void updateConfig() {
        HashMap<String, Object> newConfig = getConfigVals();
        FileConfiguration c = Core.instance.getConfig();
        for (String var : c.getKeys(false)) {
            newConfig.remove(var);
        }
        if (newConfig.size()!=0) {
            for (String key : newConfig.keySet()) {
                c.set(key, newConfig.get(key));
            }
            try {
                c.set("version", Core.instance.version);
                c.save(new File(Core.instance.getDataFolder(), "config.yml"));
            } catch (IOException e) {}
        }
    }
    public HashMap<String, Object> getConfigVals() {
        HashMap<String, Object> var = new HashMap<>();
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.loadFromString(stringFromInputStream(Core.instance.getResource("config.yml")));
        } catch (InvalidConfigurationException e) {e.printStackTrace();}
        for (String key : config.getKeys(false)) {
            var.put(key, config.get(key));
        }
        return var;
    }
    @SuppressWarnings("resource")
	public String stringFromInputStream(InputStream in) {
        return new Scanner(in).useDelimiter("\\A").next();
    }

}
