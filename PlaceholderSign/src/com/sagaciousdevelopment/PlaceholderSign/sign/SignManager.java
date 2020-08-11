package com.sagaciousdevelopment.PlaceholderSign.sign;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.sagaciousdevelopment.PlaceholderSign.Core;

public class SignManager {

	private List<PlaceholderSign> ps = new ArrayList<PlaceholderSign>();
	
	public SignManager() {
		File f = new File(Core.getInstance().getDataFolder(), "signs");
		if(!f.exists()) {
			f.mkdir();
		}
		
		for(File s : f.listFiles()) {
			
			FileConfiguration conf = YamlConfiguration.loadConfiguration(s);
			String[] l = conf.getString("location").split(",");
			String[] q = new String[conf.getStringList("rawLines").size()];
			
			Location signLoc = new Location(Bukkit.getWorld(l[0]), Double.valueOf(l[1]), Double.valueOf(l[2]), Double.valueOf(l[3]));
			if(signLoc!=null&&signLoc.getWorld()!=null) {
			
				BlockState temp = signLoc.getWorld().getBlockAt(signLoc).getState();
				if(temp!=null) {
					if(temp.getType().name().contains("SIGN")) {
						ps.add(new PlaceholderSign(s.getName().replace(".yml", ""), conf.getStringList("rawLines").toArray(q), (Sign)temp));
					}
				}
					
			}
		}
	}
	
	public List<PlaceholderSign> getSigns(){
		return ps;
	}
	
	private Random r = new Random();
	private String genID() {
		String f = "";
		for(int i = 0; i < 14; i++) {
			f+=""+r.nextInt(9);
		}
		return f;
	}
	
	public void createSign(Sign s, List<String> lines) {
		if(!isPlaceholderSign(s)) {
		    while(lines.size()!=4) {
		    	lines.add("");
		    }
			ps.add(new PlaceholderSign(genID(), Arrays.copyOf(lines.toArray(), lines.size(), String[].class), s));
		}
	}
	
	public boolean isPlaceholderSign(Sign s) {
		return getPlaceholderSign(s)!=null;
	}
	
	public PlaceholderSign getPlaceholderSign(Sign s) {
		for(PlaceholderSign p : ps) {
			if(p.getSign().getLocation().equals(s.getLocation())) {
				return p;
			}
		}
		return null;
	}
	
	public void saveAll() {
		for(PlaceholderSign p : ps) {
			p.save();
		}
	}
	
	public void isInside(Player p) {
		for(PlaceholderSign pz : ps) {
			pz.isInside(p);
		}
	}
	
	public void removeSign(Sign s) {
		if(isPlaceholderSign(s)) {
			PlaceholderSign signs = getPlaceholderSign(s);
			signs.deleted = true;
			ps.remove(signs);
			
			File f = new File(Core.getInstance().getDataFolder(), "signs/" + signs.getID() + ".yml");
			f.delete();
		}
	}
}
