package com.normarthehero.plugin;

import java.util.HashSet;
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
				sender.sendMessage(ChatColor.YELLOW + "Please specify your subcommand!");
				sender.sendMessage(ChatColor.YELLOW + "Usage: /roleplay <subcommand> <args>. Available sub-commands: list, join, leave, create, lock, unlock. Use " + ChatColor.RED + "/roleplay join" + ChatColor.YELLOW + " by itself to list/join all chats.");
				return true;

			}

			// args.length is either 1 or higher now.

			String subcmd = args[0].toLowerCase();

			if (subcmd.equals("list")) {

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

			if (subcmd.equals("join")) {
				// should never happen if joining via list

				if (roleplayers.contains(sender.getName())) {
					sender.sendMessage(ChatColor.YELLOW + "You're already in a role-play chat! Leave before joining a new one.");
					return true;

				}

				if (rps.size() == 0) {
					sender.sendMessage(ChatColor.YELLOW + "There are no current role-playing chats. Do /roleplay create <name> to create a new one!");
					return true;

				}

				if (args.length == 1) {

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

					sender.sendMessage(ChatColor.YELLOW + "These are all the current role-playing chats. Click one to join!");

					for (RolePlayChat chatlist : rps) {
						chatlist.sendButton(sender);

					}

					return true;
				}

				if (chat.isLocked()) {
					sender.sendMessage(ChatColor.YELLOW + "This group is locked! Ask " + chat.getCreator() + " if you wish to join!");
					return true;

				}

				addRPer(sender.getName());
				chat.add(sender.getName());

				sender.sendMessage(ChatColor.YELLOW + "Enabled roleplay chat!");
				sender.sendMessage(ChatColor.YELLOW + "Roleplayers will have a yellow chat. Their messages " + ChatColor.RED + "won't" + ChatColor.YELLOW + " be sent in global chat.");
				sender.sendMessage(ChatColor.YELLOW + "To disable it, type /roleplay leave.");

				return true;

			}

			if (subcmd.equals("leave")) {
				if (!roleplayers.contains(sender.getName())) {
					sender.sendMessage(ChatColor.YELLOW + "You aren't in a role-playing chat!");
					return true;

				}

				RolePlayChat chat = getRPChat(sender.getName());

				removeRPer(sender.getName(), chat);

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

				if (args.length > 3) {
					sender.sendMessage(ChatColor.YELLOW + "Sorry, but the name of your role-play chat can only be one word! (however, you could do \"unicorn_over_noodles\" or something like that)");
					return true;

				}

				if (getRPChatFromName(args[1]) != null) {
					sender.sendMessage(ChatColor.YELLOW + "A chat with that name already exists!");
					return true;

				}

				RolePlayChat newChat = new RolePlayChat(args[1], sender.getName());

				addRPer(sender.getName());
				rps.add(newChat);

				sender.sendMessage(ChatColor.YELLOW + "Successfully created and added you to the new role-play chat, \"" + args[1] + "\".");

				return true;

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

				if (!chat.getCreator().equals(sender.getName())) {
					sender.sendMessage(ChatColor.YELLOW + "You aren't the creator of this chat! Ask " + chat.getCreator() + " if you wish to lock the group.");
					return true;

				}

				chat.lock();

				chat.chatRaw(ChatColor.YELLOW + "The group has been locked.");

				return true;

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

				if (!chat.getCreator().equals(sender.getName())) {
					sender.sendMessage(ChatColor.YELLOW + "You aren't the creator of this chat! Ask " + chat.getCreator() + " if you wish to unlock the group.");
					return true;

				}

				chat.unlock();

				chat.chatRaw(ChatColor.YELLOW + "The group has been unlocked.");

				return true;

			}

			sender.sendMessage(ChatColor.YELLOW + "Could not find the subcommand you were looking for.");
			sender.sendMessage(ChatColor.YELLOW + "Usage: /roleplay <subcommand> <args>. Available sub-commands: list, join, leave, create, lock, unlock. Use " + ChatColor.RED + "/roleplay join" + ChatColor.YELLOW + " by itself to list/join all chats.");

			return true;

		}

		return false;

	}

	public void removeRPer(String name, RolePlayChat chat) {
		roleplayers.remove(name);
		chat.remove(name);

		if (chat.isEmpty()) {
			rps.remove(chat);
			return;

		}

	}

	public void addRPer(String name) {
		roleplayers.add(name);

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

		// passing the message around

		RolePlayChat chat = getRPChat(e.getPlayer().getName());

		// this should never happen, ever.
		if (chat == null) {
			e.getPlayer().sendMessage(ChatColor.DARK_RED + "Error 1337g0n3wr0n3g. Please report this on the forums.");
			e.setCancelled(false);
			return;

		}

		// make the message and send it.

		StringBuilder msgbuilder = new StringBuilder(ChatColor.YELLOW.toString());
		msgbuilder.append("[");
		msgbuilder.append(chat.getName());
		msgbuilder.append("] ");
		msgbuilder.append(e.getPlayer().getDisplayName());
		msgbuilder.append(ChatColor.YELLOW.toString());
		msgbuilder.append(": ");
		msgbuilder.append(e.getMessage());

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

		String name = e.getPlayer().getName();

		staff.remove(name);

		if (!roleplayers.contains(name)) {
			return;

		}

		// remove from rps

		RolePlayChat chat = getRPChat(name);

		if (chat != null) {
			removeRPer(name, chat);

		}

	}
}
