package com.normarthehero.plugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

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

import com.monkeygamesmc.plugin.playerdata.PlayerData;
import com.monkeygamesmc.plugin.playerdata.PlayerDataPlugin;

import mkremins.fanciful.FancyMessage;

public class RoleplayPlugin extends JavaPlugin implements Listener {

		private HashMap<String, RoleplayChat> roleplays = new HashMap<String, RoleplayChat>();
		private HashMap<UUID, String> roleplayers = new HashMap<UUID, String>();

		private HashSet<UUID> spy = new HashSet<UUID>();

		private PlayerDataPlugin database;

		final String SPY_KEY = "rpchat.spy";

		public void onEnable() {

			getServer().getPluginManager().registerEvents(this, this);

			database = Bukkit.getServicesManager().load(PlayerDataPlugin.class);

		}

		// TODO: seperate subcmds into classes

		@Override
		public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

			if (!(sender instanceof Player)) {
					return false;

			}

			// list all current rps
			if (args.length < 1) {
					sender.sendMessage(ChatColor.YELLOW + "Please specify your subcommand!");
					usage(sender);
					return true;

			}

			// args.length is either 1 or higher now.

			String subcmd = args[0].toLowerCase();

			if (subcmd.equals("list")) {

					if (roleplays.values().size() == 0) {
						sender.sendMessage(ChatColor.YELLOW + "There are currently no role-playing chats. Do " + ChatColor.WHITE + "/roleplay create <name>" + ChatColor.YELLOW + " to create a new one!");
						return true;

					}

					sender.sendMessage(ChatColor.YELLOW + "These are all the current role-playing chats. Click one to join!");

					for (RoleplayChat chat : roleplays.values()) {
						chat.sendButton(sender);

					}

					return true;

			}

			Player player = (Player) sender;

			if (subcmd.equals("join")) {
					// should never happen if joining via list

					if (roleplayers.containsKey(player.getUniqueId())) {
						sender.sendMessage(ChatColor.YELLOW + "You're already in a role-play chat! Leave before joining a new one.");
						return true;

					}

					if (roleplays.size() == 0) {
						sender.sendMessage(ChatColor.YELLOW + "There are no current role-playing chats. Do /roleplay create <name> to create a new one!");
						return true;

					}

					if (args.length == 1) {

						sender.sendMessage(ChatColor.YELLOW + "These are all the current role-playing chats. Click one to join!");

						for (RoleplayChat chat : roleplays.values()) {
								chat.sendButton(sender);

						}

						return true;

					}

					// checks to see if chat exists

					if (!roleplays.containsKey(args[1])) {

						sender.sendMessage(ChatColor.YELLOW + "The chat '" + args[1] + "' does not exist.");
						sender.sendMessage(ChatColor.YELLOW + "These are all the current role-playing chats. Click one to join!");

						for (RoleplayChat chatlist : roleplays.values()) {
								chatlist.sendButton(sender);

						}

						return true;
					}

					RoleplayChat chat = roleplays.get(args[1]);

					if (chat.isLocked()) {
						sender.sendMessage(ChatColor.YELLOW + "This roleplay is locked! Ask " + chat.getCreator() + " if you wish to join!");
						return true;

					}

					addRPer(player.getUniqueId(), chat.getName());

					chat.add(player.getUniqueId());

					sender.sendMessage(ChatColor.YELLOW + "Enabled roleplay chat!");
					sender.sendMessage(ChatColor.YELLOW + "Roleplayers will have a yellow chat. Their messages " + ChatColor.RED + "won't" + ChatColor.YELLOW + " be sent in global chat.");
					sender.sendMessage(ChatColor.YELLOW + "To disable it, type /roleplay leave.");

					return true;

			}

			if (subcmd.equals("leave")) {

					if (!isInRp(player)) {
						return true;

					}

					clearPlayer(player.getUniqueId());

					sender.sendMessage(ChatColor.YELLOW + "Disabled roleplay chat!");

					return true;

			}

			if (subcmd.equals("info")) {

					if (!isInRp(player)) {
						return true;

					}

					RoleplayChat chat = getChat(player.getUniqueId());

					if (args.length == 2) {
						if (args[1].equalsIgnoreCase("who")) {
								chat.sendInfoPlayers(sender);
								return true;

						}
					}

					chat.sendInfo(sender);

					return true;

			}

			if (subcmd.equals("create")) {

					if (args.length != 2) {
						sender.sendMessage(ChatColor.YELLOW + "You must specify the name of the role-play chat that you want to create! Usage: /roleplay create <name>");
						return true;

					}

					if (roleplayers.containsKey(player.getUniqueId())) {
						sender.sendMessage(ChatColor.YELLOW + "You are already in a role-playing chat. Leave before creating a chat!");
						return true;

					}

					if (args.length > 3) {
						sender.sendMessage(ChatColor.YELLOW + "Sorry, but the name of your role-play chat can only be one word! (however, you could do 'unicorn_over_noodles' or something like that)");
						return true;

					}

					if (roleplays.get(args[1]) != null) {
						sender.sendMessage(ChatColor.YELLOW + "A chat with that name already exists!");
						return true;

					}

					RoleplayChat newChat = new RoleplayChat(args[1], player.getUniqueId());

					addRPer(player.getUniqueId(), newChat.getName());
					roleplays.put(newChat.getName(), newChat);

					sender.sendMessage(ChatColor.YELLOW + "Successfully created and added you to the new role-play chat, '" + args[1] + "'.");

					return true;

			}

			if (subcmd.equals("lock")) {

					if (!isInRp(player)) {
						return true;

					}

					RoleplayChat chat = getChat(player.getUniqueId());

					if (!chat.getCreator().equals(sender.getName())) {
						sender.sendMessage(ChatColor.YELLOW + "You aren't the creator of this chat! Ask " + chat.getCreator() + " if you wish to lock the group.");
						return true;

					}

					chat.lock();

					chat.chatRaw(ChatColor.YELLOW + "The group has been locked.");

					return true;

			}

			if (subcmd.equals("unlock")) {

					if (!isInRp(player)) {
						return true;

					}

					RoleplayChat chat = getChat(player.getUniqueId());

					if (!chat.getCreator().equals(sender.getName())) {
						sender.sendMessage(ChatColor.YELLOW + "You aren't the creator of this chat! Ask " + chat.getCreator() + " if you wish to unlock the group.");
						return true;

					}

					chat.unlock();

					chat.chatRaw(ChatColor.YELLOW + "The group has been unlocked.");

					return true;

			}

			if (subcmd.equals("kick")) {

					if (!isInRp(player)) {
						return true;

					}

					if (args.length < 2) {
						sender.sendMessage(ChatColor.YELLOW + "Please specify a player to kick!");
						return true;

					}

					RoleplayChat chat = getChat(player.getUniqueId());

					if (chat.getCreator() != sender.getName()) {
						sender.sendMessage(ChatColor.YELLOW + "You aren't the creator of this chat! Ask " + chat.getCreator() + " if you wish to kick a player.");
						return true;

					}

					if (args[1] == sender.getName()) {
						sender.sendMessage(ChatColor.YELLOW + "You can't kick yourself!");
						return true;

					}

					Player target = Bukkit.getPlayer(args[1]);

					if (target == null) {
						sender.sendMessage(ChatColor.YELLOW + "Could not find player, '" + args[1] + "'");
						return true;

					}

					chat.kick(target.getUniqueId());

					target.sendMessage(ChatColor.YELLOW + "You have been kicked from " + chat.getName() + ".");

					roleplayers.remove(target.getName());

					return true;

			}

			if (subcmd.equals("spy")) {

					if (!sender.hasPermission("roleplay.staff")) {
						sender.sendMessage(ChatColor.YELLOW + "Nice try, space cowboy.");
						return true;

					}

					if (spy.contains(player.getUniqueId())) {
						disableSpy(player.getUniqueId());

					} else {
						enableSpy(player.getUniqueId());

					}

					return true;

			}

			sender.sendMessage(ChatColor.YELLOW + "Could not find the subcommand you were looking for.");
			usage(sender);

			return true;

		}

		FancyMessage subcmds = new FancyMessage("Available sub-commands: ").color(ChatColor.YELLOW).then("info, ").color(ChatColor.GOLD).tooltip("gives info about the rp you're in (/rp info)").then("join, ").color(ChatColor.GOLD).tooltip("join a rp (/rp join <rp name>)").then("list, ").color(ChatColor.GOLD).tooltip("list all the current rps (/rp list)").then("create, ").color(ChatColor.GOLD).tooltip("creates a new rp (/rp create <rp name>").then("leave, ").color(ChatColor.GOLD).tooltip("leaves the rp you're in (/rp leave)").then("kick, ").color(ChatColor.GOLD).tooltip("[owner tool] kicks someone from your rp (/rp kick <player>)").then("lock, ").color(ChatColor.GOLD).tooltip("[owner tool] locks your rp so no one can join (/rp lock)").then("unlock").color(ChatColor.GOLD).tooltip("[owner tool] unlocks your rp so people can join (/rp unlock)");

		FancyMessage joininfo = new FancyMessage("Use ").color(ChatColor.YELLOW).then("/roleplay join").color(ChatColor.RED).command("/roleplay join").tooltip("clicking this will list all current chats.").then(" by itself to list/join chats.").color(ChatColor.YELLOW);

		public void usage(CommandSender sender) {
			sender.sendMessage(ChatColor.YELLOW + "Usage: " + ChatColor.GOLD + "/roleplay <subcommand> <args>");
			subcmds.send(sender);
			joininfo.send(sender);
			sender.sendMessage(ChatColor.YELLOW + "Need to say something in global chat? Simply add a " + ChatColor.RED + "-g" + ChatColor.YELLOW + " at the beginning of your message. (example: \"-g hello!\"");

		}

		// gets chat that player is in
		public RoleplayChat getChat(UUID uuid) {
			return roleplays.get(roleplayers.get(uuid));

		}

		public void addRPer(UUID uuid, String chat) {
			roleplayers.put(uuid, chat);

		}

		boolean isInRp(Player player) {

			if (roleplayers.containsKey(player.getUniqueId())) {
					return true;

			}

			player.sendMessage(ChatColor.YELLOW + "You aren't in a roleplaying chat!");

			return false;

		}

		@EventHandler(priority = EventPriority.HIGH)
		public void onChat(AsyncPlayerChatEvent e) {

			// the chat is blocked, don't do anything.
			if (e.isCancelled()) {
					return;

			}

			// if the player isn't on the rp list
			// this is basically the only reason for roleplayers list...
			// TODO: think of a better way of seeing who's rping?
			if (!roleplayers.containsKey(e.getPlayer().getUniqueId())) {
					return;

			}

			String oMsg = e.getMessage();

			if (oMsg.startsWith("-g ") || oMsg.startsWith("-G ")) {
					e.setMessage(oMsg.replaceFirst("(?i)-g ", ""));
					return;

			}

			e.setCancelled(true);

			// passing the message around

			RoleplayChat chat = getChat(e.getPlayer().getUniqueId());

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
			msgbuilder.append(oMsg);

			String msg = msgbuilder.toString();

			for (UUID uuid : spy) {

					if (chat.contains(uuid)) {
						continue;

					}

					Bukkit.getPlayer(uuid).sendMessage(msg);

			}

			chat.chatRaw(msg);

		}

		@EventHandler
		public void onJoin(PlayerJoinEvent e) {

			// since there's not going to be a lot of players won't be really effective to define a variable

			PlayerData data = database.getPlayerData(e.getPlayer().getUniqueId());

			if (data.isSet(SPY_KEY)) {
					spy.add(e.getPlayer().getUniqueId());

			}

		}

		public void enableSpy(UUID uuid) {

			database.setData(uuid, SPY_KEY, "true");

			spy.add(uuid);

		}

		public void disableSpy(UUID uuid) {

			database.unsetData(uuid, SPY_KEY);

			spy.remove(uuid);

		}

		@EventHandler
		public void onLeave(PlayerQuitEvent e) {

			UUID uuid = e.getPlayer().getUniqueId();

			spy.remove(uuid);

			clearPlayer(uuid);

		}

		public void clearPlayer(UUID uuid) {

			if (roleplayers.containsKey(uuid)) {

					clearRoleplayer(uuid);

			}

		}

		public void clearRoleplayer(UUID uuid) {

			RoleplayChat chat = getChat(uuid);

			chat.remove(uuid);

			roleplayers.remove(uuid);

		}

}
