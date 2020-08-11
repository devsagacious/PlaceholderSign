package com.sagaciousdevelopment.PlaceholderSign.handler;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import com.sagaciousdevelopment.PlaceholderSign.Core;
import com.sagaciousdevelopment.PlaceholderSign.sign.PlaceholderSign;

public class SignListener implements Listener{

	public SignListener() {
		Bukkit.getPluginManager().registerEvents(this, Core.getInstance());
	}
	
	@EventHandler
	public void chunkUnload(ChunkUnloadEvent e) {
		for(PlaceholderSign ps : Core.getPSManager().getSigns()) {
			if(ps.getSign().getLocation().getChunk().equals(e.getChunk())) {
				ps.chunkLoaded=false;
			}
		}
	}
	
	@EventHandler
	public void chunkLoad(ChunkLoadEvent e) {
		for(PlaceholderSign ps : Core.getPSManager().getSigns()) {
			if(ps.getSign().getLocation().getChunk().equals(e.getChunk())) {
				ps.chunkLoaded=true;
			}
		}
	}
	
	@EventHandler
	public void onSignPlace(SignChangeEvent e) {
			Sign s = (Sign)e.getBlock().getState();
			for(String r : e.getLines()) {
				if(!Core.getPSManager().isPlaceholderSign(s)&&e.getPlayer().hasPermission("placeholdersign.createsign")) {
				if(r.contains("{") || r.contains("%")) {
					Core.getPSManager().createSign(s, new ArrayList<String>(Arrays.asList(e.getLines())));
				}
				}
			}
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		if(e.getBlock().getType().name().contains("SIGN")) {
			
			Sign s = (Sign)e.getBlock().getState();
			if(Core.getPSManager().isPlaceholderSign(s)) {
				
				Core.getPSManager().removeSign(s);
				
			}
			
		}
	}
	
	@EventHandler(priority=EventPriority.LOW)
	public void onMove(PlayerMoveEvent e) {
		if(e.getFrom().getBlockX()!=e.getTo().getBlockX()) {
			Core.getPSManager().isInside(e.getPlayer());
		}
	}
}
