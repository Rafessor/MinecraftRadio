package de.rafessor.old.radio;

import java.io.File;
import java.io.IOException;

import lombok.Getter;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.UnknownDependencyException;

import de.rafessor.api.Language;
import de.rafessor.api.ResourceLoader;
import de.rafessor.radio.plugin.AutoUpdater;
import de.rafessor.radio.plugin.RadioPlugin;

public class OldRadio implements CommandExecutor {

	@Getter
	private RadioPlugin plugin;
	@Getter
	private String version;

	@Getter
	private Language language;
	@Getter
	private RadioManager radioManager;
	@Getter
	private ResourceLoader resourceLoader;

	public OldRadio(RadioPlugin plugin) {
		this.plugin = plugin;
		this.version = "v1.2";
		
		new AutoUpdater(plugin, version).updateCheck();

		this.resourceLoader = plugin.getResourceLoader();
		this.language = plugin.getLanguage();
		if (plugin.getServer().getPluginManager().getPlugin("NoteBlockAPI") == null) {
			try {
				initNoteBlockAPI();
			} catch (IOException | UnknownDependencyException
					| InvalidPluginException | InvalidDescriptionException e) {
				e.printStackTrace();
			}
		}

		plugin.getCommand("radio").setExecutor(this);

		try {
			this.radioManager = new RadioManager(this);

			File info = new File(plugin.getDataFolder(), "info.txt");
			if (info.exists())
				info.delete();
			resourceLoader.load("info", ".txt", plugin.getDataFolder());
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("--=<>=---==---===<(O)>===---==---=<>=--");
		System.out.println("       Radio " + version + " plugin loaded");
		System.out.println("          Made by Rafessor:");
		System.out.println("    https://twitter.com/TheRafessor");
		System.out.println("--=<>=---==---===<(O)>===---==---=<>=--");
	}

	@SuppressWarnings("deprecation")
	public void onDisable() {
		for (Player player : plugin.getServer().getOnlinePlayers())
			radioManager.setRadio(player, null);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("§cThis command is only available for Players!");
			return true;
		}
		Player p = (Player) sender;

		if (cmd.getName().equalsIgnoreCase("radio")) {
			if (args.length > 0) {
				if (args[0].equalsIgnoreCase("lang")) {
					if (args.length > 1) {
						if (language.getLanguages().keySet().contains(args[1])) {
							language.getLanguage().put(p, args[1]);
							plugin.getConfig().set("lang." + p.getName(),
									args[1]);
							plugin.saveConfig();
							radioManager.updateRadioItem(p);
							p.sendMessage("§2"
									+ language.get(p, "setlanguage", args[1]));
						} else {
							p.sendMessage("§2"
									+ language.get(p, "availablelangs") + ":");
							for (String l : language.getLanguages().keySet())
								p.sendMessage("§a" + l);
						}
					} else {
						p.sendMessage("§2" + language.get(p, "availablelangs"));
						for (String l : language.getLanguages().keySet())
							p.sendMessage("§a" + l);
					}
				} else if (args[0].equalsIgnoreCase("skipsong")) {
					if (p.hasPermission("radio.skipsong")) {
						if (radioManager.getRadio(p) != null)
							radioManager.getRadio(p).nextSong();
					} else
						p.sendMessage("§c"
								+ language.get(p, "nopermission",
										"radio.skipsong"));
				} else if (args[0].equalsIgnoreCase("give")) {
					if (p.hasPermission("radio.own"))
						radioManager.updateRadioItem(p);
					else
						p.sendMessage("§c"
								+ language.get(p, "nopermission", "radio.own"));
				} else {
					p.sendMessage("§c/radio lang <language>");
					p.sendMessage("§c/radio skipsong");
				}
			} else {
				p.sendMessage("§c/radio lang <language>");
				p.sendMessage("§c/radio skipsong");
			}
			return true;
		}

		return false;
	}

	public void initNoteBlockAPI() throws IOException,
			UnknownDependencyException, InvalidPluginException,
			InvalidDescriptionException {
		File file = resourceLoader.load("api", ".jar", plugin.getDataFolder()
				.getParentFile());
		PluginManager manager = plugin.getServer().getPluginManager();
		manager.enablePlugin(manager.loadPlugin(file));
	}

}
