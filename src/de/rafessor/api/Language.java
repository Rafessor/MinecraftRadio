package de.rafessor.api;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import lombok.Getter;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;

import de.rafessor.radio.plugin.RadioPlugin;

public class Language {

	@Getter
	private Map<Player, String> language = new WeakHashMap<Player, String>();
	@Getter
	private Map<String, Map<String, String>> languages = new HashMap<String, Map<String, String>>();

	public Language(RadioPlugin plugin) throws FileNotFoundException,
			IOException, InvalidConfigurationException {
		if (!(new File(plugin.getDataFolder(), "langEnglish.txt").exists()))
			plugin.getResourceLoader().load("language", ".txt",
					plugin.getDataFolder());

		File[] directoryListing = plugin.getDataFolder().listFiles();
		for (File file : directoryListing) {
			if (file.getName().startsWith("lang")
					&& file.getName().endsWith(".txt"))
				loadLang(file);
		}
	}

	public String get(Player player, String key, String... args) {
		if (!language.containsKey(player))
			language.put(player, "English");

		if (args == null)
			return get(language.get(player), key);
		else
			return get(language.get(player), key, args);
	}

	public String get(String language, String key, String... args) {
		if (!(languages.containsKey(language) && languages.get(language)
				.containsKey(key)))
			return null;

		String result = languages.get(language).get(key);
		if (args != null) {
			int i = 1;
			for (String s : args) {
				result = result.replace("%" + i + "$s", s);
				i++;
			}
		}
		return result;
	}

	public void loadLang(File langFile) throws FileNotFoundException,
			IOException, InvalidConfigurationException {
		Map<String, String> langMap = new HashMap<String, String>();

		if (!langFile.exists())
			return;

		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(langFile)));
		List<String> lines = new ArrayList<String>();

		try {
			while (reader.ready()) {
				String line = reader.readLine();
				if (line == null)
					break;
				lines.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (String s : lines) {
			String key = "";
			String string = "";

			int i = 0;
			char c = s.charAt(i);
			while (c != ':') {
				c = s.charAt(i);
				if (c != ':')
					key = key + c;
				i++;
			}
			i++;
			while (i < s.length()) {
				c = s.charAt(i);
				string = string + c;
				i++;
			}
			langMap.put(key, string.replace("&", "§"));
		}

		languages.put(langMap.get("language"), langMap);
	}

}
