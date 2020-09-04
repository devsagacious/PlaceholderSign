package com.sagaciousdevelopment.PlaceholderSign.sign;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.sagaciousdevelopment.PlaceholderSign.Core;
import com.sagaciousdevelopment.PlaceholderSign.util.HexUtil;

import net.md_5.bungee.api.ChatColor;

public class PlaceholderSign {
	
	private String id;
	private String[] linesRaw = new String[] {};
	private int[] linesScroll = new int[] {0,0,0,0};
	private Sign s;
	
	private List<Player> inside = new ArrayList<Player>();
	public boolean deleted = false;

	
	public PlaceholderSign(String id, String[] linesRaw, Sign s) {
		this.id = id;
		this.linesRaw = linesRaw;
		this.s = s;
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Core.getInstance(), new Runnable() {
			public void run() {
				if(deleted) {
					return;
				}
				for(Player p : inside) {
					sendUpdate(p);
				}
			}
		}, 5L, 5L);
	}

	public String getID() {
		return id;
	}
	
	public void save() {
		File f = new File(Core.getInstance().getDataFolder(), "signs/" + id + ".yml");
		Location l = s.getLocation();
		
		if(!f.exists()) {
			
			try {
				f.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			FileConfiguration conf = YamlConfiguration.loadConfiguration(f);
			conf.set("rawLines", new ArrayList<String>(Arrays.asList(linesRaw)));
			conf.set("location", l.getWorld().getName() + "," + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ());
			
			try {
				conf.save(f);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}else {
		
		FileConfiguration conf = YamlConfiguration.loadConfiguration(f);
		conf.set("rawLines", new ArrayList<String>(Arrays.asList(linesRaw)));
		conf.set("location", l.getWorld().getName() + "," + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ());
		
		try {
			conf.save(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		}
	}
	
	public boolean isInside(Player p) {
		if(p.getWorld().equals(s.getWorld())) {
		if(s.getLocation().distance(p.getLocation())<=Core.getInstance().getSignMinimumDistance()) {
			if(!inside.contains(p)) {inside.add(p);}
		   return true;	
		}
		}
		if(inside.contains(p)) {inside.remove(p);}
		return false;
	}
	
	public Sign getSign() {
		return s;
	}
	
	public String[] getLinesRaw(){
		return linesRaw;
	}
	
	public void setLinesRaw(String[] lines) {
		linesRaw=lines;
	}
	
	public void sendUpdate(Player p) {
		if(s.getLocation().getChunk().isLoaded()) {
			
			int scrolled = 0;
			String[] temp = linesRaw;
			List<String> te = new ArrayList<String>();
			
			for(int i = 0; i < temp.length; i++) {
			String t = temp[i];
			t = ChatColor.translateAlternateColorCodes('&', t);
			if(t.contains("%")&&Core.getInstance().hasPAPI()) {
				t = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(p, t);
			}
			if(t.contains("{")&&Core.getInstance().hasMVDW()) {
				t = be.maximvdw.placeholderapi.PlaceholderAPI.replacePlaceholders(p, t);
			}
			
			if(Core.getInstance().hasRGB()) {t = HexUtil.replaceHexColors('&', t);}
			if(t.length()>16) {
				
				int current = linesScroll[scrolled]+1;
				if(current==t.length()-1) {
					current=0;
				}
				linesScroll[scrolled]=current;
				
				int end = current+15;
				if(end>t.length()) {
					end = t.length();
				}
				
				String f = "";
				int currentScroll = current;
				for(int is = 0; is < 16; is++) {
					if(currentScroll>=end) {
						return;
					}
					f+= t.charAt(currentScroll);
					
					currentScroll+=1;
				}
				t=f;	
			}
			scrolled+=1;
			te.add(t);
			}
			
			String[] se = new String[te.size()];
			p.sendSignChange(s.getLocation(), te.toArray(se));
		}
	}
	
	
	
}
