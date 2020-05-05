package com.sagaciousdevelopment.PlaceholderSign.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.sagaciousdevelopment.PlaceholderSign.Core;
import com.sagaciousdevelopment.PlaceholderSign.sign.PlaceholderSign;

public class ScrollSignCommand implements CommandExecutor, Listener{

	public ScrollSignCommand() {
		Core.instance.getCommand("scrollsign").setExecutor(this);
		Core.instance.getCommand("scrollsign").setAliases(new ArrayList<String>(Arrays.asList("ss")));
		Bukkit.getPluginManager().registerEvents(this, Core.instance);
	}
	
	private class UserData {
		public Player p;
		public int txt;
		public int line;
		
		public UserData(Player p, int txt, int line) {
			this.p=p;
			this.txt=txt;
			this.line=line;
		}
	}
	private List<UserData> ud = new ArrayList<UserData>();
	public UserData getData(Player p) {
		for(UserData u : ud) {
			if(u.p==p){
				return u;
			}
		}
		return null;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage("§cYou can only perform this command as a player");
			return true;
		}
		if(!sender.hasPermission("placeholdersign.editsign")) {
			sender.sendMessage("§cYou don't have access to this command!");
			return true;
		}
		if(args.length < 2) {
			sender.sendMessage("§cInvalid command usage, try /scrollsign <line> <speed> (speed will apply to all lines)");
			return true;
		}else {
			int s = 0;
			try {
				s=Integer.parseInt(args[0]);
			}catch(NumberFormatException e) {}
			if(s<1||s>4) {
			sender.sendMessage("§cInvalid number input, valid numbers are: 1, 2, 3, 4"); 
			return true;
			}
			int txt = 0;
			try {
				txt=Integer.parseInt(args[1]);
			}catch(NumberFormatException e) {sender.sendMessage("§cInvalid number input"); return true;}
			Player p = (Player)sender;
			if(getData(p)!=null) {
				p.sendMessage("§cAborted last action...");
				ud.remove(getData(p));
				return true;
			}
			ud.add(new UserData(p, txt, s));
			sender.sendMessage("§aClick on a sign to enable/disable scrolling.");
			return true;
		}
	}
	
	@EventHandler
	public void onClickSign(PlayerInteractEvent e) {
		if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
			if(e.getClickedBlock().getType().equals(Material.SIGN) || e.getClickedBlock().getType().equals(Material.SIGN_POST) || e.getClickedBlock().getType().equals(Material.WALL_SIGN)) {
				Player p = e.getPlayer();
				if(getData(p)!=null) {
					UserData d = getData(p);
				Sign s = (Sign)e.getClickedBlock().getState();
				PlaceholderSign ps = Core.instance.sd.getPlaceholderSign(s);
				if(ps==null) {
					List<String> temp = new ArrayList<String>(Arrays.asList(s.getLines()));
					while(temp.size()!=4) {
						temp.add("");
					}
					Core.instance.sd.createSign(s, temp);
					temp.clear();
					ps = Core.instance.sd.getPlaceholderSign(s);
				}
				if(ps.toScroll.contains(d.line)) {
					ps.toScroll.remove(d.line);
					e.getPlayer().sendMessage("§aDisabled scrolling!");
					ud.remove(d);
					return;
				}
				ps.toScroll.add(d.line);
				ps.scrollSpeed = d.txt;
				e.getPlayer().sendMessage("§aEnabled scrolling!");
				ud.remove(d);
				}
			}
		}
	}
}
