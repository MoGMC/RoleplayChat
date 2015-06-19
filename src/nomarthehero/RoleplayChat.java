package nomarthehero;
import java.util.ArrayList;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;


public class RoleplayChat extends JavaPlugin implements Listener, CommandExecutor  {
	
	//Every 5~10 min "Hey <player>, you know you're still talking in roleplay chat?" (async runnable)
	
	private ArrayList<String> roleplayers = new ArrayList<String>();
	private ArrayList<String> staff = new ArrayList<String>();
	
	
	public void onEnable() {
		
		getCommand("roleplay").setExecutor(this);
		getCommand("rp").setExecutor(this);
		getServer().getPluginManager().registerEvents(this, this);
		
	}
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		
		if (command.getName().equalsIgnoreCase("roleplay") || command.getName().equalsIgnoreCase("rp")) {
			
			Player player = (Player)sender;
			String playerName = player.getName();
			
			if (roleplayers.contains(playerName)) {
				
				roleplayers.remove(playerName);
				player.sendMessage(ChatColor.YELLOW + "Disabled roleplay chat!");
				
			}	else {
				
				roleplayers.add(playerName);
				player.sendMessage(ChatColor.YELLOW + "Enabled roleplay chat!");
				player.sendMessage(ChatColor.YELLOW + "Roleplayers will have a yellow chat, and their messages " + ChatColor.RED + "won't" + ChatColor.YELLOW + " be sent in global chat.");
				player.sendMessage(ChatColor.YELLOW + "To disable it, type /roleplay again.");
				
			}
			
			
		}
		
		return true;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onChat(AsyncPlayerChatEvent e) {
		
		if (e.isCancelled() == false) {
			
			Player player = e.getPlayer();
			
			if (isRoleplayer(player)) {
				
				String message = e.getMessage();
				message = ChatColor.YELLOW + message;
				
				e.setCancelled(true);
				
				for (String playerName : roleplayers) {
					
					Bukkit.getPlayer(playerName).sendMessage(ChatColor.YELLOW + "[RP] " + player.getDisplayName() + ChatColor.YELLOW + ": " + message);
					
				}
				
				for (String playerName : staff) {
					
					if (!roleplayers.contains(playerName)) {
						
						Bukkit.getPlayer(playerName).sendMessage(ChatColor.YELLOW + "[RP] " + player.getDisplayName() + ChatColor.YELLOW + ": " + message);
						
					}			
								
				}
				
			}
			
		}
		
	}
	
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		
		Player player = e.getPlayer();
		
		if (player.hasPermission("roleplay.staff")) {
			staff.add(player.getName());
		}
		
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		
		roleplayers.remove(e.getPlayer().getName());
		
	}
	
	private boolean isRoleplayer(Player player) {
		
		if (roleplayers.contains(player.getName())) {
			
			return true;
			
		}
		
		return false;
		
	}
	
	

}
