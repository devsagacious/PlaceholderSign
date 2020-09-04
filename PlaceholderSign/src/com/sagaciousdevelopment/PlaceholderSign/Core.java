package com.sagaciousdevelopment.PlaceholderSign;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.sagaciousdevelopment.PlaceholderSign.command.SignCommand;
import com.sagaciousdevelopment.PlaceholderSign.handler.SignListener;
import com.sagaciousdevelopment.PlaceholderSign.sign.SignManager;

public class Core extends JavaPlugin{

	private static Core instance;
	public static Core getInstance() {
		return instance;
	}
	
	private static SignManager sm;
	public static SignManager getPSManager() {
		return sm;
	}

	private String version = "1.1";
	public String getVersion() {
		return version;
	}

	
	private boolean mvdw = false;
	private boolean papi = false;
	private boolean rgb = false;
	public boolean hasMVDW() {
		return mvdw;
	}
	
	public boolean hasPAPI() {
		return papi;
	}
	
	public boolean hasRGB() {
		return rgb;
	}
	
	
	private int signMinimumDistance;
	public int getSignMinimumDistance() {
		return signMinimumDistance;
	}
	
	private int scrollSign;
	public int getScrollSign() {
		return scrollSign;
	}
	
	
	@Override
	public void onEnable() {
		instance=this;
		
		getConfig().options().copyDefaults(true);
		saveDefaultConfig();
		if (!getConfig().getString("version").equals(version)) {
            getLogger().info("Your configuration file was not up to date. Updating it now...");
            new FileUtil().updateConfig("config.yml");
            getLogger().info("Configuration file updated.");
        }
		
		papi = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
		mvdw = Bukkit.getPluginManager().isPluginEnabled("MVdWPlaceholderAPI");
		rgb = Bukkit.getVersion().contains("1.16");if(rgb) {getLogger().info("Hex Colors are supported in 1.16");}
		signMinimumDistance = getConfig().getInt("sign-minimum-distance");
		scrollSign = getConfig().getInt("scroll-time");
		
		sm = new SignManager();
		new SignCommand();
		new SignListener();
	}
	
	
	@Override
	public void onDisable() {
		sm.saveAll();
	}
}
