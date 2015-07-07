package com.normarthehero.plugin;

import java.util.ArrayList;
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

public class RoleplayChat extends JavaPlugin implements Listener {

	// Every 5~10 min
	// "Hey <player>, you know you're still talking in roleplay chat?" (async
	// runnable)

	private ArrayList<String> roleplayers = new ArrayList<String>();

	public void onEnable() {

		getServer().getPluginManager().registerEvents(this, this);

	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {

		if (command.getName().equalsIgnoreCase("roleplay")) {

			if (sender.hasPermission("roleplay.staff")) {

				return true;

			}

			if (roleplayers.contains(sender.getName())) {

				roleplayers.remove(sender.getName());
				sender.sendMessage(ChatColor.YELLOW + "Disabled roleplay chat!");

			} else {

				roleplayers.add(sender.getName());
				sender.sendMessage(ChatColor.YELLOW + "Enabled roleplay chat!");
				sender.sendMessage(ChatColor.YELLOW + "Roleplayers will have a yellow chat, and their messages " + ChatColor.RED + "won't" + ChatColor.YELLOW + " be sent in global chat.");
				sender.sendMessage(ChatColor.YELLOW + "To disable it, type /roleplay again.");

			}

			return true;

		}

		return false;
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
			roleplayers.add(player.getName());

		}

	}

	@EventHandler
	public void onLeave(PlayerQuitEvent e) {

		Player player = e.getPlayer();

		roleplayers.remove(player.getName());

		if (player.hasPermission("roleplay.staff"))
			staff.remove(player.getName());

	}

	private boolean isRoleplayer(Player player) {

		if (roleplayers.contains(player.getName())) {

			return true;

		}

		return false;

	}

}
