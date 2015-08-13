package com.normarthehero.plugin;

import java.util.HashSet;

import mkremins.fanciful.FancyMessage;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class RolePlayChat {

	private HashSet<String> roleplayers = new HashSet<String>();
	private boolean locked = false;
	private String rpname;
	private final String CREATOR;
	private FancyMessage button;

	private static FancyMessage whoIsMsg = new FancyMessage("Click or type ").color(ChatColor.YELLOW).then("/roleplay info who").color(ChatColor.GOLD).command("/roleplay info who").tooltip("displays who's in the chat!").then(" to list who is in that chat.").color(ChatColor.YELLOW);

	public RolePlayChat(String name, String CREATOR) {
		this.rpname = name;
		button = new FancyMessage(name).color(ChatColor.YELLOW).command("/roleplay join " + name);
		this.CREATOR = CREATOR;

		roleplayers.add(CREATOR);

	}

	// should not exist
	@Deprecated
	public void chat(String msg) {

	}

	public void chatRaw(String msg) {
		for (String player : roleplayers) {
			Bukkit.getPlayer(player).sendMessage(msg);

		}

	}

	public void sendInfo(CommandSender sender) {
		StringBuilder s = new StringBuilder(ChatColor.YELLOW.toString());
		s.append(rpname);
		s.append("'s information:\n");
		s.append(" Creator: ");
		s.append(CREATOR);
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

		for (String player : roleplayers) {
			s.append(player);
			s.append(" ");

		}

		sender.sendMessage(s.toString());

	}

	public boolean isLocked() {
		return locked;

	}

	public String getCreator() {
		return CREATOR;

	}

	public void add(String name) {
		roleplayers.add(name);

	}

	public void kick(String name) {
		roleplayers.remove(name);

		chatRaw(ChatColor.YELLOW + name + " has been kicked from the group.");

	}

	public void remove(String name) {
		roleplayers.remove(name);

		if (name.equals(CREATOR)) {
			locked = false;

		}

		chatRaw(ChatColor.YELLOW + name + " has left the group.");

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

	public boolean isRP(String name) {
		return roleplayers.contains(name);

	}

	public void sendButton(CommandSender sender) {
		button.send(sender);

	}

	public boolean isEmpty() {
		return roleplayers.isEmpty();

	}

}
