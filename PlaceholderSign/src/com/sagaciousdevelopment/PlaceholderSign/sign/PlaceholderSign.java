package com.sagaciousdevelopment.PlaceholderSign.sign;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import com.sagaciousdevelopment.PlaceholderSign.Core;

public class PlaceholderSign {
	
	private String[] linesRaw = new String[] {};
	private Sign s;
	public List<Integer> toScroll;
	public int scrollSpeed;
	private List<Integer> substringed;
	
	private List<Player> inside = new ArrayList<Player>();
	
	public PlaceholderSign(String[] linesRaw, Sign s, List<Integer> toScroll, int scrollSpeed) {
		this.linesRaw = linesRaw;
		this.s = s;
		this.toScroll = toScroll;
		this.scrollSpeed = scrollSpeed;
		this.substringed = new ArrayList<Integer>(Arrays.asList(0,0,0,0));
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Core.instance, new Runnable() {
			int curr = 1;
			public void run() {
				curr+=1;
				if(scrollSpeed>0&&curr==20/scrollSpeed||curr==20*Core.instance.updateTime) {
					curr=1;
				for(Player p : inside) {
					sendUpdate(p);
				}
				}
			}
		}, 1L, 1L);
	}
	
	public boolean isInside(Player p) {
		if(p.getWorld().equals(s.getWorld())) {
		if(s.getLocation().distance(p.getLocation())<=Core.instance.signMinimumDistance) {
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
		List<String> te = new ArrayList<String>();
		String[] temp = linesRaw;
		List<Integer> newSub = new ArrayList<Integer>();
		for(int i = 0; i < temp.length; i++) {
			int sub = -1;
			if(substringed.size()>i) {
				sub=substringed.get(i);
			}
			String t = temp[i];
			t = ChatColor.translateAlternateColorCodes('&', t);
			if(t.contains("%")&&Core.instance.placeholderapi) {
				t = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(p, t);
			}
			if(t.contains("{")&&Core.instance.mvdwplaceholderapi) {
				t = be.maximvdw.placeholderapi.PlaceholderAPI.replacePlaceholders(p, t);
			}
			if((scrollSpeed<0||!toScroll.contains(i+1))&&sub==-1) {
			te.add(t);
			}else {
		    sub+=1;
		    if(sub>t.length()) {
		    	sub=-10;
		    }
		    if(sub>0) {
		    	t = t.substring(sub);
		    }
		    if(sub>-1) {
		    te.add(t);
		    }else {
		    	te.add("");
		    }
		    newSub.add(sub);
			}
		}
		if(!newSub.isEmpty()) {
			substringed=newSub;
		}
		while(te.size()!=4) {
			te.add("");
		}
		if(!s.getWorld().getBlockAt(s.getLocation()).getType().equals(Material.SIGN)&&!s.getWorld().getBlockAt(s.getLocation()).getType().equals(Material.WALL_SIGN)&&!s.getWorld().getBlockAt(s.getLocation()).getType().equals(Material.SIGN_POST)) {
			Core.instance.sd.removeSignP(this);
			return;
		}
		p.sendSignChange(s.getLocation(), Arrays.copyOf(te.toArray(), te.size(), String[].class));
	}

}
