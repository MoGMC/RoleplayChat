package com.normarthehero.plugin;

import java.util.HashSet;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import mkremins.fanciful.FancyMessage;

public class RoleplayChat {

		private HashSet<UUID> roleplayers = new HashSet<UUID>();
		private boolean locked = false;
		private String rpname;
		private final UUID CREATOR;
		private FancyMessage button;

		private static FancyMessage whoIsMsg = new FancyMessage("Click or type ").color(ChatColor.YELLOW).then("/roleplay info who").color(ChatColor.GOLD).command("/roleplay info who").tooltip("displays who's in the chat!").then(" to list who is in that chat.").color(ChatColor.YELLOW);

		public RoleplayChat(String name, UUID CREATOR) {

			this.rpname = name;
			this.CREATOR = CREATOR;

			roleplayers.add(CREATOR);

			button = new FancyMessage(name).color(ChatColor.YELLOW).command("/roleplay join " + name);

		}

		public void chatRaw(String msg) {

			for (UUID uuid : roleplayers) {
					Bukkit.getPlayer(uuid).sendMessage(msg);

			}

		}

		public void sendInfo(CommandSender sender) {
			StringBuilder s = new StringBuilder(ChatColor.YELLOW.toString());
			s.append(rpname);
			s.append("'s information:\n");
			s.append(" Creator: ");
			s.append(getCreator());
			s.append("\n Locked: ");
			s.append(locked);
			s.append("\n\n ");
			s.append(roleplayers.size());
			s.append(" player(s) large");
			sender.sendMessage(s.toString());
			whoIsMsg.send(sender);

		}

		public void sendInfoPlayers(CommandSender sender) {
			StringBuilder s = new StringBuilder(ChatColor.YELLOW.toString());

			s.append("Players who are currently in \"");
			s.append(rpname);
			s.append("\":\n ");

			for (UUID uuid : roleplayers) {
					s.append(Bukkit.getPlayer(uuid).getDisplayName());
					s.append(" ");

			}

			sender.sendMessage(s.toString());

		}

		public boolean isLocked() {
			return locked;

		}

		public String getCreator() {

			Player player = Bukkit.getPlayer(CREATOR);

			if (player == null) {
					return "Offline";

			} else {
					return player.getDisplayName();

			}

		}

		public void add(UUID uuid) {
			roleplayers.add(uuid);

		}

		public void kick(UUID uuid) {

			roleplayers.remove(uuid);

			chatRaw(ChatColor.YELLOW + Bukkit.getPlayer(uuid).getDisplayName() + ChatColor.YELLOW + " has been kicked from the group.");

		}

		public void remove(UUID uuid) {

			roleplayers.remove(uuid);

			if (uuid.equals(CREATOR)) {
					locked = false;

			}

			chatRaw(ChatColor.YELLOW + Bukkit.getPlayer(uuid).getDisplayName() + ChatColor.YELLOW + " has left the group.");

		}

		public String getName() {
			return rpname;

		}

		public void lock() {
			locked = true;

		}

		public void unlock() {
			locked = false;

		}

		public boolean contains(UUID uuid) {
			return roleplayers.contains(uuid);

		}

		public void sendButton(CommandSender sender) {
			button.send(sender);

		}

		public boolean isEmpty() {
			return roleplayers.isEmpty();

		}

}
