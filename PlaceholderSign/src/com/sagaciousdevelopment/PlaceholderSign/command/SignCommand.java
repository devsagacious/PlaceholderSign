package com.sagaciousdevelopment.PlaceholderSign.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
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
import com.sagaciousdevelopment.PlaceholderSign.util.HexUtil;

public class SignCommand implements CommandExecutor, Listener{

	public SignCommand() {
		Core.getInstance().getCommand("editsign").setExecutor(this);
		Core.getInstance().getCommand("editsign").setAliases(new ArrayList<String>(Arrays.asList("es")));
		Bukkit.getPluginManager().registerEvents(this, Core.getInstance());
	}
	
	private class UserData {
		public Player p;
		public String txt;
		public int line;
		
		public UserData(Player p, String txt, int line) {
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
			sender.sendMessage("§cInvalid command usage, try /editsign <line> <message>");
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
			String txt = args[1];
			for(int i = 2; i < args.length; i++) {
				txt = txt + " " + args[i];
			}
			if(Core.getInstance().hasRGB()) {txt = HexUtil.replaceHexColors('&', txt);}
			Player p = (Player)sender;
			if(getData(p)!=null) {
				p.sendMessage("§cAborted last action...");
				ud.remove(getData(p));
				return true;
			}
			ud.add(new UserData(p, txt, s));
			sender.sendMessage("§aClick on a sign to edit it's content.");
			return true;
		}
	}

	@EventHandler
	public void onClickSign(PlayerInteractEvent e) {
		if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
			if(e.getClickedBlock().getType().name().contains("SIGN")) {
				Player p = e.getPlayer();
				if(getData(p)!=null) {
				Sign s = (Sign)e.getClickedBlock().getState();
				PlaceholderSign ps = Core.getPSManager().getPlaceholderSign(s);
				if(ps==null) {
					List<String> temp = new ArrayList<String>(Arrays.asList(s.getLines()));
					while(temp.size()!=4) {
						temp.add("");
					}
					Core.getPSManager().createSign(s, temp);
					temp.clear();
					ps = Core.getPSManager().getPlaceholderSign(s);
				}
				s.setLine(getData(p).line-1, getData(p).txt);
				List<String> txt = new ArrayList<String>();
				for(int i = 0; i < 4; i++) {
					if(i==getData(p).line-1) {
						txt.add(getData(p).txt);
					}else {
						txt.add(ps.getLinesRaw()[i]);
					}
				}
				ps.setLinesRaw(Arrays.copyOf(txt.toArray(), txt.size(), String[].class));
				txt.clear();
				e.getPlayer().sendMessage("§aUpdated message!");
				ud.remove(getData(p));
				s.update();
				}
			}
		}
	}
}
