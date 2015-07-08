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

	public boolean isLocked() {
		return locked;

	}

	public String getCreator() {
		return CREATOR;

	}

	public void add(String name) {
		roleplayers.add(name);

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
