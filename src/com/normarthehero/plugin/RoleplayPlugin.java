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
	private HashSet<String> staff = new HashSet<String>();
	private HashMap<String, String> rpnames = new HashMap<String, String>();
	private HashSet<RolePlayChat> rps = new HashSet<RolePlayChat>();

	public void onEnable() {

		getServer().getPluginManager().registerEvents(this, this);

	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {

		if (command.getName().equalsIgnoreCase("roleplay")) {

			// list all current rps
			if (args.length == 0) {
				sender.sendMessage(ChatColor.YELLOW + "Do /roleplay ");
				sender.sendMessage("Usage: /roleplay <subcmd> <args>. Available sub-commands: list, name, join, leave, create, lock, unlock. Use /roleplay by itself to list/join all chats.");
				return true;

			}

			// args.length is either 1 or higher now.

			String subcmd = args[0].toLowerCase();

			if (subcmd.equals("list")) {

			}

			if (subcmd.equals("name")) {

				if (roleplayers.contains(sender.getName())) {
					sender.sendMessage(ChatColor.YELLOW + "You're not in a role-play chat! Join one before you change your name.");
					return true;

				}

				if (args.length != 2) {
					sender.sendMessage(ChatColor.YELLOW + "You must specify your name! Usage: /roleplay name <name>");
					return true;

				}

				rpnames.put(sender.getName(), args[1]);
				sender.sendMessage(ChatColor.YELLOW + "Set your role-playing name to " + args[1] + "!");
				return true;

			}

			if (subcmd.equals("join")) {
				// should never happen if joining via list

				if (roleplayers.contains(sender.getName())) {
					sender.sendMessage(ChatColor.YELLOW + "You're already in a role-play chat! Leave before joining a new one.");
					return true;

				}

				if (args.length != 2) {
					if (rps.size() == 0) {
						sender.sendMessage(ChatColor.YELLOW + "There are no current role-playing chats. Do /roleplay create <name> to create a new one!");
						return true;

					}

					sender.sendMessage(ChatColor.YELLOW + "These are all the current role-playing chats. Click one to join!");

					for (RolePlayChat chat : rps) {
						chat.sendButton(sender);

					}

					return true;

				}

				// checks to see if chat exists

				RolePlayChat chat = getRPChatFromName(args[1]);

				if (chat == null) {
					sender.sendMessage(ChatColor.YELLOW + "The chat \"" + args[1] + "\" does not exist.");
					if (rps.size() == 0) {
						sender.sendMessage(ChatColor.YELLOW + "There are no current role-playing chats. Do /roleplay create <name> to create a new one!");
						return true;

					}

					sender.sendMessage(ChatColor.YELLOW + "These are all the current role-playing chats. Click one to join!");

					for (RolePlayChat chatlist : rps) {
						chatlist.sendButton(sender);

					}

					return true;
				}

				chat.add(sender.getName());

				roleplayers.add(sender.getName());

				sender.sendMessage(ChatColor.YELLOW + "Enabled roleplay chat!");
				sender.sendMessage(ChatColor.YELLOW + "Roleplayers will have a yellow chat, and their messages " + ChatColor.RED + "won't" + ChatColor.YELLOW + " be sent in global chat.");
				sender.sendMessage(ChatColor.YELLOW + "To disable it, type /roleplay again.");

			}

			if (subcmd.equals("leave")) {
				if (roleplayers.contains(sender.getName())) {
					sender.sendMessage(ChatColor.YELLOW + "You aren't in a role-playing chat!");
					return true;

				}

				RolePlayChat chat = getRPChat(sender.getName());

				chat.remove(sender.getName());
				roleplayers.remove(sender.getName());

				sender.sendMessage(ChatColor.YELLOW + "Disabled roleplay chat!");

				return true;

			}

			if (subcmd.equals("create")) {

				if (args.length != 2) {
					sender.sendMessage(ChatColor.YELLOW + "You must specify the name of the role-play chat that you want to create! Usage: /roleplay create <name>");
					return true;

				}

				if (roleplayers.contains(sender.getName())) {
					sender.sendMessage(ChatColor.YELLOW + "You are already in a role-playing chat. Leave before creating a chat!");
					return true;

				}

				RolePlayChat newChat = new RolePlayChat(args[1], sender.getName());

				rps.add(newChat);

			}

			if (subcmd.equals("lock")) {
				if (!roleplayers.contains(sender.getName())) {
					sender.sendMessage(ChatColor.YELLOW + "You aren't in a role-play chat!");
					return true;

				}

				RolePlayChat chat = getRPChat(sender.getName());

				if (chat == null) {
					sender.sendMessage(ChatColor.YELLOW + "You aren't in a role-play chat!");
					return true;

				}

				if (chat.getCreator().equals(sender.getName())) {
					sender.sendMessage(ChatColor.YELLOW + "You aren't the creator of this chat! Ask " + chat.getCreator() + " if you wish to lock the chat.");
					return true;

				}

				chat.lock();

			}

			if (subcmd.equals("unlock")) {
				if (!roleplayers.contains(sender.getName())) {
					sender.sendMessage(ChatColor.YELLOW + "You aren't in a role-play chat!");
					return true;

				}

				RolePlayChat chat = getRPChat(sender.getName());

				if (chat == null) {
					sender.sendMessage(ChatColor.YELLOW + "You aren't in a role-play chat!");
					return true;

				}

				if (chat.getCreator().equals(sender.getName())) {
					sender.sendMessage(ChatColor.YELLOW + "You aren't the creator of this chat! Ask " + chat.getCreator() + " if you wish to unlock the chat.");
					return true;

				}

				chat.unlock();

			}

			return true;

		}

		return false;

	}

	// you should use roleplayers.contains()
	@Deprecated
	public boolean isInRP(String playerName) {
		for (RolePlayChat chat : rps) {
			if (chat.isRP(playerName)) {
				return true;

			}

		}

		return false;

	}

	public RolePlayChat getRPChatFromName(String name) {
		for (RolePlayChat chat : rps) {
			if (chat.getName().equalsIgnoreCase(name)) {
				return chat;

			}
		}

		return null;

	}

	public RolePlayChat getRPChat(String playerName) {
		for (RolePlayChat chat : rps) {
			if (chat.isRP(playerName)) {
				return chat;

			}

		}

		return null;

	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onChat(AsyncPlayerChatEvent e) {

		// the chat is blocked, don't do anything.
		if (e.isCancelled()) {
			return;

		}

		// if the player isn't on the rp list
		// this is basically the only reason for roleplayers list...
		// TODO: think of a better way of seeing who's rping?
		if (!roleplayers.contains(e.getPlayer().getName())) {
			return;

		}

		e.setCancelled(true);

		String playerName = e.getPlayer().getName();

		// passing the message around

		RolePlayChat chat = getRPChat(playerName);

		// this should never happen, ever.
		if (chat == null) {
			e.getPlayer().sendMessage(ChatColor.DARK_RED + "Error 1337g0n3wr0n3g. Please report this on the forums.");
			e.setCancelled(false);
			return;

		}

		// make the message and send it.

		StringBuilder msgbuilder = new StringBuilder("[");
		msgbuilder.append(chat.getName());
		msgbuilder.append("] ");
		msgbuilder.append(rpnames.get(playerName));
		msgbuilder.append(":");

		String msg = msgbuilder.toString();

		for (String player : staff) {
			Bukkit.getPlayer(player).sendMessage(msg);

		}

		chat.chatRaw(msg);

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

		staff.remove(e.getPlayer().getName());

		if (!roleplayers.contains(e.getPlayer())) {
			return;

		}

		// remove from rps
		String name = e.getPlayer().getName();

		RolePlayChat chat = getRPChat(name);

		if (chat != null) {
			chat.remove(name);

		}

		roleplayers.remove(name);

	}

}
