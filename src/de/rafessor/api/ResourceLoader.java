package de.rafessor.api;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.security.CodeSource;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.bukkit.plugin.Plugin;

public class ResourceLoader {

	private Plugin plugin;

	public ResourceLoader(Plugin plugin) {
		this.plugin = plugin;
	}

	public File load(String folder, String ending, File targetFolder)
			throws IOException {
		File result = null;
		CodeSource src = ResourceLoader.class.getProtectionDomain()
				.getCodeSource();
		if (src != null) {
			URL jar = src.getLocation();
			ZipInputStream zip = new ZipInputStream(jar.openStream());
			while (true) {
				ZipEntry e = zip.getNextEntry();
				if (e == null)
					break;
				String name = e.getName();
				if (name.startsWith("resources/" + folder)
						&& name.endsWith(ending)) {
					String[] n = name.split("/");
					String fileName = n[n.length - 1];
					plugin.getDataFolder().mkdirs();
					File target = new File(targetFolder, fileName);
					Files.copy(zip, target.toPath());
					result = target;
				}
			}
		}
		return result;
	}
}
