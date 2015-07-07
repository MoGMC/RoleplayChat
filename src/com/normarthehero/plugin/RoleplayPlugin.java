package com.normarthehero.plugin;

import java.util.HashSet;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class RoleplayPlugin extends JavaPlugin implements Listener {

	// Every 5~10 min
	// "Hey <player>, you know you're still talking in roleplay chat?" (async
	// runnable)

	private HashSet<String> roleplayers = new HashSet<String>();
	private HashMap<String, String> rpnames = new HashMap<String, String>();

	public void onEnable() {

		getServer().getPluginManager().registerEvents(this, this);

	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {

		if (command.getName().equalsIgnoreCase("roleplay")) {

			// person wants to change name
			if (args.length != 0) {
				rpnames.put(sender.getName(), args[0]);
				sender.sendMessage(ChatColor.YELLOW + "Set your role-playing name to " + args[0] + "!");
				return true;

			}

			// don't let staff members un-roleplay
			if (sender.hasPermission("roleplay.staff")) {
				sender.sendMessage(ChatColor.YELLOW + "Huh? You trying to escape your responsibilities of staff?");
				return true;

			}

			if (roleplayers.contains(sender.getName())) {

				roleplayers.remove(sender.getName());
				sender.sendMessage(ChatColor.YELLOW + "Disabled roleplay chat!");

				return true;

			}

			roleplayers.add(sender.getName());
			sender.sendMessage(ChatColor.YELLOW + "Enabled roleplay chat!");
			sender.sendMessage(ChatColor.YELLOW + "Roleplayers will have a yellow chat, and their messages " + ChatColor.RED + "won't" + ChatColor.YELLOW + " be sent in global chat.");
			sender.sendMessage(ChatColor.YELLOW + "To disable it, type /roleplay again.");

			return true;

		}

		return false;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onChat(AsyncPlayerChatEvent e) {

		// the chat is blocked, don't do anything.
		if (e.isCancelled()) {
			return;

		}

		// if the player isn't on the rp list
		if (!roleplayers.contains(e.getPlayer().getName())) {
			return;

		}

		e.setCancelled(true);

		String message = ChatColor.YELLOW + e.getMessage();

		// TODO: should we really use Bukkit.getPlayer every time?
		for (String playerName : roleplayers) {
			Bukkit.getPlayer(playerName).sendMessage(ChatColor.YELLOW + "[RP] " + rpnames.get(playerName) + ": " + message);

		}

	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {

		Player player = e.getPlayer();

		if (player.hasPermission("roleplay.staff")) {
			roleplayers.add(player.getName());

		}

	}

	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		Player player = e.getPlayer();

		roleplayers.remove(player.getName());

	}

	private void addRP(String name) {
		roleplayers.add(name);

	}

	private void removeRP() {

	}

}
