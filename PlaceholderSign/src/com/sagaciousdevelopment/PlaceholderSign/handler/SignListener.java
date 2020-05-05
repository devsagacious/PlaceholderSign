package com.sagaciousdevelopment.PlaceholderSign.handler;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;

import com.sagaciousdevelopment.PlaceholderSign.Core;

public class SignListener implements Listener{
	private EventPriority prio = null;
	
	private class CustomPriority extends RegisteredListener {
		private RegisteredListener list;

		public CustomPriority(RegisteredListener list) {
			super(null, null, null, null, false);
			this.list = list;
		}
		
		@Override
	    public Listener getListener()
	    {
	        return list.getListener();
	    }

	    @Override
	    public Plugin getPlugin()
	    {
	        return list.getPlugin();
	    }

	    @Override
	    public EventPriority getPriority()
	    {
	        return prio;
	    }

	    @Override
	    public void callEvent(Event event) throws EventException
	    {
	        list.callEvent(event);
	    }

	    @Override
	    public boolean isIgnoringCancelled()
	    {
	        return list.isIgnoringCancelled();
	    }
	}
	
	public SignListener() {
		Bukkit.getPluginManager().registerEvents(this, Core.instance);
		prio = EventPriority.valueOf(Core.instance.priority);
        for(RegisteredListener rl : PlayerMoveEvent.getHandlerList().getRegisteredListeners()){
        	if(rl.getPlugin().getName().equalsIgnoreCase("PlaceholderSign")) {
            PlayerMoveEvent.getHandlerList().unregister(rl);
            PlayerMoveEvent.getHandlerList().register(new CustomPriority(rl));
        	}
        }
	}
	
	@EventHandler
	public void onSignPlace(SignChangeEvent e) {
			Sign s = (Sign)e.getBlock().getState();
			for(String r : e.getLines()) {
				if(!Core.instance.sd.isPlaceholderSign(s)) {
				if(r.contains("{") || r.contains("%")) {
					Core.instance.sd.createSign(s, new ArrayList<String>(Arrays.asList(e.getLines())));
				}
				}
			}
	}

	@EventHandler(priority=EventPriority.LOW)
	public void onMove(PlayerMoveEvent e) {
		if(e.getFrom().getBlockX()!=e.getTo().getBlockX()) {
			Core.instance.sd.isInside(e.getPlayer());
		}
	}
}
