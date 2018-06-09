package com.normarthehero.plugin;

import java.util.HashSet;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
			s.append(getDisplayCreator());
			s.append(ChatColor.YELLOW.toString());
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
					s.append(ChatColor.YELLOW.toString());

			}

			sender.sendMessage(s.toString());

		}

		public boolean isLocked() {
			return locked;

		}

		public UUID getCreator() {
			return CREATOR;

		}

		public String getDisplayCreator() {

			OfflinePlayer player = Bukkit.getOfflinePlayer(CREATOR);

			if (player.isOnline()) {
					return ((Player) player).getDisplayName() + ChatColor.GREEN + " (Online)";

			} else {
					return player.getName() + ChatColor.RED + " (Offline)";

			}

		}

		public void add(UUID uuid) {

			chatRaw(ChatColor.YELLOW + Bukkit.getPlayer(uuid).getDisplayName() + ChatColor.YELLOW + " has joined the group.");
			roleplayers.add(uuid);

		}

		public void kick(UUID uuid) {

			roleplayers.remove(uuid);

			chatRaw(ChatColor.YELLOW + "Kicking " + Bukkit.getPlayer(uuid).getDisplayName() + ChatColor.YELLOW + "...");

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
