package com.sagaciousdevelopment.PlaceholderSign;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.sagaciousdevelopment.PlaceholderSign.handler.ScrollSignCommand;
import com.sagaciousdevelopment.PlaceholderSign.handler.SignCommand;
import com.sagaciousdevelopment.PlaceholderSign.handler.SignListener;

public class Core extends JavaPlugin{
	public static Core instance;
	
	public String version = "1.1";
	
	public boolean placeholderapi;
	public boolean mvdwplaceholderapi;
	public SignData sd;
	
	public int updateTime;
	public int signMinimumDistance;
	public String priority;

	@Override
	public void onEnable() {
		instance = this;
		setupConfig();
		if (!getConfig().getString("version").equals(version)) {
            getLogger().info("Your configuration file was not up to date. Updating it now...");
            new FileUtil().updateConfig();
            getLogger().info("Configuration file updated.");
        }
		placeholderapi = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
		mvdwplaceholderapi = Bukkit.getPluginManager().isPluginEnabled("MVdWPlaceholderAPI");
		updateTime = getConfig().getInt("update-time");
		signMinimumDistance = getConfig().getInt("sign-minimum-distance");
		priority = getConfig().getString("priority");
		sd = new SignData();
		new SignCommand();
		new SignListener();
		new ScrollSignCommand();
	}
	
	@Override
	public void onDisable() {
		sd.save();
	}
	
	
	private File dataFolder;
	private File config;
	
	private void setupConfig() {
		dataFolder = getDataFolder();
		if(!dataFolder.exists()) {
			dataFolder.mkdir();
		}
		config = new File(dataFolder, "config.yml");
		if(!config.exists()) {
			try(InputStream in = Core.instance.getResource("config.yml")){
			    Files.copy(in, config.toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
