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
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.sagaciousdevelopment.PlaceholderSign.Core;

public class SignListener implements Listener{

	public SignListener() {
		Bukkit.getPluginManager().registerEvents(this, Core.getInstance());
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
		if(e.getFrom().getBlockX()!=e.getTo().getBlockX()||e.getFrom().getBlockZ()!=e.getTo().getBlockZ()) {
			Core.getPSManager().isInside(e.getPlayer());
		}
	}
	
	@EventHandler(priority=EventPriority.LOW)
	public void onTp(PlayerTeleportEvent e) {
			Core.getPSManager().isInside(e.getPlayer());
	}
	
	@EventHandler(priority=EventPriority.LOW)
	public void onRespawn(PlayerRespawnEvent e) {
			Core.getPSManager().isInside(e.getPlayer());
	}
}
