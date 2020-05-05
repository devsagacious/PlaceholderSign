package com.sagaciousdevelopment.PlaceholderSign;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.sagaciousdevelopment.PlaceholderSign.sign.PlaceholderSign;

public class SignData {
	
	private List<PlaceholderSign> ps = new ArrayList<PlaceholderSign>();
	private FileConfiguration conf;
	
	public SignData() {
		conf = Core.instance.getConfig();
		for(String s : conf.getStringList("signs")) {
			String[] f = s.split(",,");
			String[] q = s.split("-_q");
			Sign s1 = (Sign)Bukkit.getWorld(f[0]).getBlockAt(Integer.parseInt(f[1]), Integer.parseInt(f[2]), Integer.parseInt(f[3])).getState();
			if(s1!=null) {
				List<Integer> z = new ArrayList<Integer>();
				if(q.length==3) {
				for(String e : q[2].split("~~.")) {
					if(!e.equals("")) {
						z.add(Integer.valueOf(e));
					}
				}
				}
				int sp = Integer.valueOf(q[1]);
			ps.add(new PlaceholderSign(new String[] {f.length>4?f[4]:"", f.length>5?f[5]:"", f.length>6?f[6]:"", f.length>7?f[7]:""}, s1, z, sp));
			}
		}
	}
	
	public void save() {
		List<String> s = new ArrayList<String>();
		for(PlaceholderSign p : ps) {
			Location w = p.getSign().getLocation();
			String f = "";
			for(int i = 0; i < p.toScroll.size(); i++) {
				f = f + (!f.equals("")?"~~.":"") + p.toScroll.get(i);
			}
			s.add(w.getWorld().getName() + ",," + w.getBlockX() + ",," + w.getBlockY() + ",," + w.getBlockZ() + ",," + p.getLinesRaw()[0] + ",," + p.getLinesRaw()[1] + ",," + p.getLinesRaw()[2] + ",," + p.getLinesRaw()[3] +
					"-_q" + p.scrollSpeed + "-_q" + f);
			
		}
		conf.set("signs", s);
		try {
			conf.save(new File(Core.instance.getDataFolder(), "config.yml"));
		} catch (IOException e) {
			e.printStackTrace();
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
	
	public void createSign(Sign s, List<String> lines) {
		if(!isPlaceholderSign(s)) {
		    while(lines.size()!=4) {
		    	lines.add("");
		    }
			ps.add(new PlaceholderSign(Arrays.copyOf(lines.toArray(), lines.size(), String[].class), s, new ArrayList<Integer>(), -1));
		}
	}
	
	public void isInside(Player p) {
		for(PlaceholderSign pz : ps) {
			pz.isInside(p);
		}
	}
	
	public void removeSign(Sign s) {
		if(isPlaceholderSign(s)) {
			ps.remove(getPlaceholderSign(s));
		}
	}
	
	public void removeSignP(PlaceholderSign p) {
			ps.remove(p);
	}


}
