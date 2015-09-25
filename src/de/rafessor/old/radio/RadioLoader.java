package de.rafessor.old.radio;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.security.CodeSource;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import de.rafessor.api.Language;

public class RadioLoader {

	public RadioLoader(OldRadio plugin, RadioManager manager)
			throws IOException {
		List<PublicRadio> radios = manager.getRadios();
		File folder = new File(plugin.getPlugin().getDataFolder(), "Radios");

		if (!folder.exists()) {
			folder.mkdirs();

			// Sadly i cannot use the resourceloader here because of the radio
			// station name system :/
			CodeSource src = Language.class.getProtectionDomain()
					.getCodeSource();
			if (src != null) {
				URL jar = src.getLocation();
				ZipInputStream zip = new ZipInputStream(jar.openStream());
				while (true) {
					ZipEntry e = zip.getNextEntry();
					if (e == null)
						break;
					String name = e.getName();
					if (name.startsWith("resources/radiostation")
							&& name.endsWith(".nbs")) {
						String[] n = name.split("/");
						String fileName = n[n.length - 1];
						String folderName = n[n.length - 2];
						File radioFolder = new File(folder, folderName);
						radioFolder.mkdirs();
						File target = new File(radioFolder, fileName);
						Files.copy(zip, target.toPath());
					}
				}
			}
		}

		File[] directoryListing = folder.listFiles();

		for (File file : directoryListing) {
			String radioName = file.getName();

			File radioFolder = new File(folder, radioName);
			if (!radioFolder.exists()) {
				radioFolder.mkdirs();
				System.out.println("No songs in " + radioName
						+ ". Radio not loaded!");
				continue;
			}

			radios.add(new PublicRadio(plugin, radioName));
		}
	}
}
