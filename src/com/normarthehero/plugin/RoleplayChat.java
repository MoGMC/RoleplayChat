package com.normarthehero.plugin;

import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.UUID;

public class RoleplayChat {

	private HashSet<UUID> roleplayers = new HashSet<> ();
	private boolean locked = false;
	private String rpname;
	private final UUID CREATOR;
	private BaseComponent[] button;

	private static BaseComponent[] whoIsMsg = new ComponentBuilder ("Click or type ").color (net.md_5.bungee.api.ChatColor.YELLOW)
		.append ("/roleplay info who").color (net.md_5.bungee.api.ChatColor.GOLD).event (RoleplayPlugin.command ("/roleplay info who")).event (RoleplayPlugin.tooltip ("displays who's in the chat!"))
		.append (" to list who is in that chat.").color (net.md_5.bungee.api.ChatColor.YELLOW).create ();

	public RoleplayChat (String name, UUID CREATOR) {

		this.rpname = name;
		this.CREATOR = CREATOR;

		roleplayers.add (CREATOR);

		button = new ComponentBuilder (name).color (net.md_5.bungee.api.ChatColor.YELLOW).event (RoleplayPlugin.command ("/roleplay join " + name)).create ();

	}

	void chatRaw (String msg) {

		for (UUID uuid : roleplayers) {
			Bukkit.getPlayer (uuid).sendMessage (msg);

		}

	}

	void sendInfo (CommandSender.Spigot sender) {
		StringBuilder s = new StringBuilder (ChatColor.YELLOW.toString ());
		s.append (rpname);
		s.append ("'s information:\n");
		s.append (" Creator: ");
		s.append (getDisplayCreator ());
		s.append (ChatColor.YELLOW.toString ());
		s.append ("\n Locked: ");
		s.append (locked);
		s.append ("\n\n ");
		s.append (roleplayers.size ());
		s.append (" player(s) large");
		sender.sendMessage (new TextComponent (s.toString ()));
		sender.sendMessage (whoIsMsg);

	}

	void sendInfoPlayers (CommandSender sender) {
		StringBuilder s = new StringBuilder (ChatColor.YELLOW.toString ());

		s.append ("Players who are currently in \"");
		s.append (rpname);
		s.append ("\":\n ");

		for (UUID uuid : roleplayers) {
			s.append (Bukkit.getPlayer (uuid).getDisplayName ());
			s.append (" ");
			s.append (ChatColor.YELLOW.toString ());

		}

		sender.sendMessage (s.toString ());

	}

	boolean isLocked () {
		return locked;

	}

	UUID getCreator () {
		return CREATOR;

	}

	String getDisplayCreator () {

		OfflinePlayer player = Bukkit.getOfflinePlayer (CREATOR);

		if (player.isOnline ()) {
			return ((Player) player).getDisplayName () + ChatColor.GREEN + " (Online)";

		}
		else {
			return player.getName () + ChatColor.RED + " (Offline)";

		}

	}

	void add (UUID uuid) {

		chatRaw (ChatColor.YELLOW + Bukkit.getPlayer (uuid).getDisplayName () + ChatColor.YELLOW + " has joined the group.");
		roleplayers.add (uuid);

	}

	void kick (UUID uuid) {

		roleplayers.remove (uuid);

		chatRaw (ChatColor.YELLOW + "Kicking " + Bukkit.getPlayer (uuid).getDisplayName () + ChatColor.YELLOW + "...");

	}

	void remove (UUID uuid) {

		roleplayers.remove (uuid);

		if (uuid.equals (CREATOR)) {
			locked = false;

		}

		chatRaw (ChatColor.YELLOW + Bukkit.getPlayer (uuid).getDisplayName () + ChatColor.YELLOW + " has left the group.");

	}

	String getName () {
		return rpname;

	}

	void lock () {
		locked = true;

	}

	void unlock () {
		locked = false;

	}

	boolean contains (UUID uuid) {
		return roleplayers.contains (uuid);

	}

	void sendButton (CommandSender.Spigot sender) {
		sender.sendMessage (button);

	}

	boolean isEmpty () {
		return roleplayers.isEmpty ();

	}

}
